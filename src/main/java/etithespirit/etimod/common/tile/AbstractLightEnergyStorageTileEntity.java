package etithespirit.etimod.common.tile;

import com.google.common.collect.ImmutableSet;
import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.common.block.light.connection.ConnectionHelper;
import etithespirit.etimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.etimod.energy.ILightEnergyStorage;
import etithespirit.etimod.info.coordinate.Cardinals;
import etithespirit.etimod.util.collection.CachedImmutableSetProvider;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.ArrayList;

/** A superclass representing all Light-based energy blocks' TEs.
 *  @author Eti
 */
@SuppressWarnings("unused")
public abstract class AbstractLightEnergyStorageTileEntity extends TileEntity implements ILightEnergyStorage, ITickableTileEntity {
	
	/**
	 * The maximum amount of successful neighbors that will be scanned. Failing neighbors do not count to this limit.<br/>
	 * <br/>
	 * A successful neighbor counts as a neighbor that is an instance of {@link ILightEnergyConduit}
	 */
	public static final int MAX_SUCCESSFUL_TILE_COUNT = 512;
	
	/**
	 * A container used to store energy.
	 */
	protected PersistentLightEnergyStorage storage;
	
	/**
	 * Every connected instance of {@link ILightEnergyConduit}. This has no particular order to it.
	 */
	protected final CachedImmutableSetProvider<ILightEnergyConduit> connected = new CachedImmutableSetProvider<>(MAX_SUCCESSFUL_TILE_COUNT, true);
	private final CachedImmutableSetProvider<AbstractLightEnergyStorageTileEntity> connectedAnchors = new CachedImmutableSetProvider<>(true);
	private boolean hasPopulatedAnchorArrayFromRecursion = false;
	
	private final ArrayList<BlockPos> tested = new ArrayList<>();
	private boolean hasPopulatedConnectedArray = false;
	private int numTestedSuccessfully = 0;
	
	public AbstractLightEnergyStorageTileEntity(TileEntityType<?> tileEntityTypeIn) {
		this(tileEntityTypeIn, null);
	}
	
	public AbstractLightEnergyStorageTileEntity(TileEntityType<?> tileEntityTypeIn, PersistentLightEnergyStorage storage) {
		super(tileEntityTypeIn);
		this.storage = storage;
		
	}
	
	@Override
	public void tick() {
		// TODO: Figure out how to avoid this.
		// ^ Assuming it even SHOULD be avoided.
		if (!hasPopulatedConnectedArray) {
			repopulateConnectedArray();
		}
	}
	
	@Override
	public void setLevelAndPosition(World world, BlockPos pos) {
		super.setLevelAndPosition(world, pos);
		hasPopulatedConnectedArray = false;
	}
	
	@Override
	public void setPosition(BlockPos pos) {
		super.setPosition(pos);
		hasPopulatedConnectedArray = false;
	}
	
	/**
	 * Intended to be called by an instance of {@link ILightEnergyConduit}, it registers that instance as connected to this storage entity.<br/>
	 * <strong>This should be called AFTER the conduit registers this instance as an anchor, otherwise a desynchronization can occur.</strong>
	 * @param conduit The {@link ILightEnergyConduit} to register as connected.
	 * @throws IllegalArgumentException If the given {@link ILightEnergyConduit} is already connected.
	 */
	public final void registerConnectedElement(ILightEnergyConduit conduit) throws IllegalArgumentException {
		connected.add(conduit);
		
		if (hasPopulatedAnchorArrayFromRecursion) {
			// Only add it to the array if we've done the big build first.
			for (AbstractLightEnergyStorageTileEntity anchor : conduit.getAnchors()) {
				if (!connectedAnchors.contains(anchor)) {
					connectedAnchors.add(anchor);
				}
			}
		}
	}
	
	/**
	 * Intended to be called by an instance of {@link ILightEnergyConduit}, it registers that instance as no longer connected to this storage entity.<br/>
	 * <strong>This should be called AFTER the conduit registers this instance as an anchor, otherwise a desynchronization can occur.</strong>
	 * @param conduit The {@link ILightEnergyConduit} to register as no longer connected.
	 * @throws IllegalArgumentException If the given {@link ILightEnergyConduit} is not connected.
	 */
	public final void unregisterConnectedElement(ILightEnergyConduit conduit) throws IllegalArgumentException {
		connected.remove(conduit);
		
		if (hasPopulatedAnchorArrayFromRecursion) {
			// Only remove it from the array if we've done the big build first.
			for (AbstractLightEnergyStorageTileEntity anchor : conduit.getAnchors()) {
				if (connectedAnchors.contains(anchor)) {
					connectedAnchors.remove(anchor);
				}
			}
		}
	}
	
	/**
	 * Returns a read-only set of the connected elements.
	 * Use {@link #registerConnectedElement(ILightEnergyConduit)} and {@link #unregisterConnectedElement(ILightEnergyConduit)}
	 * to modify this set.
	 * @return A read-only set of the connected elements.
	 */
	public final ImmutableSet<ILightEnergyConduit> getConnectedConduits() {
		return connected.immutable();
	}
	
	/**
	 * Returns whether or not the given {@link ILightEnergyConduit} is connected to this at the time of calling.
	 * @param conduit The {@link ILightEnergyConduit} to check.
	 * @return Whether or not the given {@link ILightEnergyConduit} is connected.
	 */
	public final boolean isConduitConnected(ILightEnergyConduit conduit) {
		return connected.contains(conduit);
	}
	
	/**
	 * @return a read-only set of all connected instances of {@link AbstractLightEnergyStorageTileEntity} that are connected
	 * to this in some way via {@link ILightEnergyConduit} instances.
	 */
	public final ImmutableSet<AbstractLightEnergyStorageTileEntity> getAllConnectedStorage() {
		if (hasPopulatedAnchorArrayFromRecursion) {
			return connectedAnchors.immutable();
		}
		
		// TODO: Cluster all conduits into up to six clusters representing branches off of a given surface of this block.
		// TODO: ^ This can help because then I can just check the FIRST conduit of each branch for all anchors, and that's all I need.
		// TODO: ^ With this change, there is no need to iterate through every single conduit.
		// ^^^ For now, just cache this result. It'll help but it's not perfect.
		
		for (ILightEnergyConduit conduit : connected) {
			for (AbstractLightEnergyStorageTileEntity anchor : conduit.getAnchors()) {
				if (!connectedAnchors.contains(anchor)) {
					connectedAnchors.add(anchor);
				}
			}
		}
		
		hasPopulatedAnchorArrayFromRecursion = true;
		return connectedAnchors.immutable();
	}
	
	/**
	 * @return Whether or not the array of connected neighbors (recursively) has been populated.
	 */
	public final boolean hasAcquiredConnectedNeighbors() {
		return hasPopulatedConnectedArray;
	}
	
	/**
	 * <strong>Note: This method may be expensive to call.</strong><br/>
	 * Recalculate all connected instances of {@link ILightEnergyConduit}.
	 */
	public final void repopulateConnectedArray() {
		if (!hasLevel()) throw new NullPointerException("This TileEntity does not have an associated world!");
		connected.clear();
		tested.clear();
		recurse(getBlockPos(), getAllNeighbors(getBlockPos()), true);
		for (ILightEnergyConduit conduit : connected) {
			if (!conduit.isAnchor(this)) {
				conduit.registerAnchor(this);
			}
		}
		hasPopulatedConnectedArray = true;
	}
	
	/**
	 * Recurses through all of the neighbors of the given origin block.
	 * @param origin The block with the neighbors surrounding it.
	 * @param neighbors The neighbors of this block, which may be null if they are invalid.
	 * @param isDepth1 Whether or not this is the first level of recursion, which decides whether or not to anticipate connections.
	 * @return True if <strong>all</strong> recursion should continue, false if it should not.
	 */
	private boolean recurse(BlockPos origin, BlockPos[] neighbors, boolean isDepth1) {
		for (int idx = 0; idx < neighbors.length; idx++) {
			BlockPos validNeighbor = neighbors[idx];
			if (validNeighbor == null) continue;
			
			BlockState state = level.getBlockState(validNeighbor);
			
			Direction dir = ConnectionHelper.fromBlockPos(origin, validNeighbor, true);
			if (ConnectionHelper.hasMutualConnectionInDirection(level, origin, dir, isDepth1)) {
				TileEntity tile = level.getBlockEntity(validNeighbor);
				if (tile instanceof ILightEnergyConduit) {
					connected.add((ILightEnergyConduit)tile);
					numTestedSuccessfully++;
				} else {
					neighbors[idx] = null; // This is a storage point, not a conduit. Stop this one.
				}
			} else {
				neighbors[idx] = null;
				// This neighbor is not valid because it is not actually connected to this.
				// Skip it.
			}
			
			if (numTestedSuccessfully >= MAX_SUCCESSFUL_TILE_COUNT) return false;
		}
		
		for (BlockPos validNeighbor : neighbors) {
			if (validNeighbor == null) continue;
			
			if (!recurse(validNeighbor, getAllNeighbors(validNeighbor), false)) return false;
		}
		
		return true;
	}
	
	/**
	 * Returns all six neighbors around the given BlockPos.<br/>
	 * <strong>SOME INDICES MAY BE NULL.</strong> Any instances of {@link BlockPos} that have already been tested or are not {@link ILightEnergyConduit} instances will be skipped.
	 * @param around The block to get all adjacent blocks to.
	 * @return An array of six {@link BlockPos} instances that are situated around the given position.
	 */
	private BlockPos[] getAllNeighbors(BlockPos around) {
		BlockPos[] validAdjacent = new BlockPos[6];
		if (!tested.contains(around)) tested.add(around);
		
		for (int idx = 0; idx < Cardinals.ADJACENTS_IN_ORDER.length; idx++) {
			Vector3i adj = Cardinals.ADJACENTS_IN_ORDER[idx];
			
			BlockPos toTest = around.offset(adj);
			if (tested.contains(toTest)) {
				validAdjacent[idx] = null;
				continue;
			}
			
			BlockState state = level.getBlockState(toTest);
			if (!state.hasTileEntity()) {
				validAdjacent[idx] = null;
				tested.add(toTest);
				continue;
			}
			
			/*
			TileEntity neighbor = this.level.getBlockEntity(toTest);
			if (neighbor instanceof ILightEnergyConduit && !(neighbor instanceof AbstractLightEnergyStorageTileEntity)) {
				validAdjacent[idx] = toTest;
			} else {
				validAdjacent[idx] = null;
			}
			*/
			
			// Fallback: Just test the block itself, don't bother with TEs quite yet.
			if (state.getBlock() instanceof ConnectableLightTechBlock) {
				validAdjacent[idx] = toTest;
			} else {
				validAdjacent[idx] = null;
			}
			tested.add(toTest);
		}
		return validAdjacent;
	}
}
