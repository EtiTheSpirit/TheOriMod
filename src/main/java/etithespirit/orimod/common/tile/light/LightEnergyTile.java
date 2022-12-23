package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.aos.ConnectionHelper;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IFirstTickListener;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.energy.ILightEnergyGenerator;
import etithespirit.orimod.energy.ILightEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The Light Tile class represents any and all tile entities that handle Light energy in some way.
 *
 * Unlike previous iterations of the mod, there is no longer a (static) distinction between hubs and conduits. Any block can now switch between being a hub
 * and a conduit at any time.
 */
public abstract class LightEnergyTile extends BlockEntity implements IFirstTickListener {
	
	private BlockPos[] lastKnownValidNeighbors = null;
	private boolean init = false;
	
	protected LightEnergyTile(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
	}
	
	@Override
	public boolean needsInit() {
		return !init;
	}
	
	@Override
	public void tellInitComplete() {
		init = true;
	}
	
	@Override
	public void firstTick(Level inWorld, BlockPos at, BlockState state) { }
	
	/**
	 * To optimize functions like {@link #tryGetNext(LightEnergyTile)} and {@link #tryGetOtherHubs()}, the neighbors of this tile are cached. This clears the cache.
	 */
	public void markLastKnownNeighborsDirty() {
		// OriMod.LOG.debug("Marked all neighbors dirty for the assembly around {}.", this);
		HashSet<BlockPos> skip = new HashSet<>(256);
		markLastKnownNeighborsDirty(skip);
	}
	
	protected void markLastKnownNeighborsDirty(Set<BlockPos> excluding) {
		if (lastKnownValidNeighbors != null) {
			if (excluding.add(worldPosition)) {
				for (BlockPos neighbor : lastKnownValidNeighbors) {
					LightEnergyTile tile = at(neighbor);
					if (tile != null) {
						tile.markLastKnownNeighborsDirty(excluding);
					}
				}
			}
		}
		lastKnownValidNeighbors = null;
	}
	
	/**
	 * Only safe to call when this has the Powered property. This sets the block at the current position to the desired power state.
	 * This does not check if the state is already set, nor does it check if the Powered property is actually valid. Handle with care!
	 * @param desiredPower The desired Powered state.
	 */
	protected void utilSetPoweredStateTo(boolean desiredPower) {
		if (level != null) {
			level.setBlock(worldPosition, getBlockState().setValue(ForlornAppearanceMarshaller.POWERED, desiredPower), StaticData.REPLICATE_CHANGE | StaticData.DO_NOT_NOTIFY_NEIGHBORS | StaticData.DO_NOT_MAKE_NEIGHBORS_DROP);
		}
	}
	
	/**
	 * Updates the state of this block based on whether or not it is powered.
	 */
	public void updateVisualPoweredAppearance() {
		BlockState currentState = getBlockState();
		if (currentState.hasProperty(ForlornAppearanceMarshaller.POWERED) && level != null) {
			boolean currentPower = currentState.getValue(ForlornAppearanceMarshaller.POWERED);
			
			if (this instanceof ILightEnergyStorage storage) {
				boolean desiredPower = storage.getLightStored() > 0;
				if (desiredPower != currentPower) {
					utilSetPoweredStateTo(desiredPower);
				}
			} else if (this instanceof ILightEnergyGenerator generator) {
				boolean desiredPower = generator.takeGeneratedEnergy(Float.POSITIVE_INFINITY, true) > 0;
				if (desiredPower != currentPower) {
					utilSetPoweredStateTo(desiredPower);
				}
			} else if (this instanceof ILightEnergyConsumer consumer) {
				boolean desiredPower = consumer.consumeEnergy(Float.POSITIVE_INFINITY, true) > 0;
				if (desiredPower != currentPower) {
					utilSetPoweredStateTo(desiredPower);
				}
			}
		}
	}
	
	@Override
	public void setRemoved() {
		markLastKnownNeighborsDirty();
		super.setRemoved();
	}
	
	/**
	 * @return The cached neighbors of this conduit or hub. Rebuilds the cache if needed.
	 */
	protected BlockPos[] neighbors() {
		if (lastKnownValidNeighbors == null) {
			lastKnownValidNeighbors = ConnectionHelper.getDirectionsWithMutualConnections(level, worldPosition, true);
		}
		return lastKnownValidNeighbors;
	}
	
	/**
	 * @param pos The position to search.
	 * @return The BlockEntity at {@code pos} iff this tile has a level and the BlockEntity at the given position is an instance of {@link LightEnergyTile}.
	 */
	protected @Nullable LightEnergyTile at(BlockPos pos) {
		if (isRemoved() || (level == null)) return null;
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof LightEnergyTile lightBlock) return lightBlock;
		return null;
	}
	
	/**
	 * A specialized helper function that, assuming this tile is a straight line or elbow joint (two connections), and given the previous tile in said line,
	 * this will return the next tile in the line. If this has any amount of connections other than two, this returns null.
	 * @param previous The previous tile in the line; the tile before this one in a sequence.
	 * @return The next tile in the line; the tile after this one in a sequence
	 */
	public @Nullable LightEnergyTile tryGetNext(LightEnergyTile previous) {
		if (isRemoved() || !hasLevel()) return null;
		if (!isFunctioningAsLine()) return null;
		BlockPos[] neighbors = neighbors();
		
		BlockPos otherPos = previous.getBlockPos();
		LightEnergyTile neighborEnt;
		if (neighbors[0].equals(otherPos)) {
			neighborEnt = at(neighbors[1]);
		} else {
			neighborEnt = at(neighbors[0]);
		}
		return neighborEnt;
	}
	
	/**
	 * A specialized helper function that has multiple behaviors depending on the context of this line.
	 * <ul>
	 *     <li>If {@link #isFunctioningAsHub()}, this returns up to six connecting hubs.</li>
	 *     <li>If {@link #isDeadEnd()}, this returns up to one hub which is the origin of this line.</li>
	 *     <li>If {@link #isFunctioningAsLine()}, this returns up to two hubs which are the hubs that this line links together.</li>
	 *     <li>If this block entity has been removed (or is scheduled for removal), this returns null.</li>
	 * </ul>
	 * The returned array will never include this hub, however it may include duplicates if more than one route directly connecting this to another hub is present.
	 * @return The other hub(s), or null if this block entity is not valid.
	 * @throws IllegalStateException This tile is not functioning as a hub.
	 */
	public @Nullable LightEnergyTile[] tryGetOtherHubs() throws IllegalStateException {
		if (isRemoved() || !hasLevel()) return null;
		ArrayList<LightEnergyTile> tiles = new ArrayList<>(6);
		if (isFunctioningAsHub() || isFunctioningAsLine()) {
			for (BlockPos neighborPos : lastKnownValidNeighbors) {
				LightEnergyTile next = at(neighborPos);
				LightEnergyTile current = this;
				
				while (next != null && next != this) { // next != this would be caused by someone making a square.
					LightEnergyTile nextBuf = next.tryGetNext(current);
					current = next;
					next = nextBuf;
				}
				if (current != this) {
					tiles.add(current);
				}
			}
		} else if (isDeadEnd()) {
			if (lastKnownValidNeighbors.length == 1) {
				LightEnergyTile current = this;
				LightEnergyTile next = at(lastKnownValidNeighbors[0]);
				while (next != null && next != this) {
					LightEnergyTile nextBuf = next.tryGetNext(current);
					current = next;
					next = nextBuf;
				}
				if (current != this) {
					tiles.add(current);
				}
			}
		}
		return tiles.toArray(new LightEnergyTile[0]);
	}
	
	
	/**
	 * @return Whether or not this tile functions as a hub, or a location where more than two source/destination of energy connects; it is not a straight line, but instead a T joint or + joint.
	 */
	public boolean isFunctioningAsHub() {
		return neighbors().length > 2;
	}
	
	/**
	 * @return Whether or not this tile is a dead end (it has 0 or 1 connection(s)).
	 */
	public boolean isDeadEnd() {
		return neighbors().length < 2;
	}
	
	/**
	 * @return Whether or not this tile is a line (it has exactly two connections).
	 */
	public boolean isFunctioningAsLine() {
		return neighbors().length == 2;
	}
	
	
	
	protected static class ArrayContainer<T extends LightEnergyTile> {
		
		public T[] array = null;
		
	}
}
