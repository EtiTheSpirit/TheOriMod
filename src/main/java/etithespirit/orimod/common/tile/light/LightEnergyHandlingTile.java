package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.energy.ILightEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * A close relative to {@link LightEnergyTile}, this is the same thing save for the fact that it also works with the power directly in some way.
 * Use the interfaces {@link ILightEnergyStorage}, {@link etithespirit.orimod.energy.ILightEnergyConsumer}, and {@link etithespirit.orimod.energy.ILightEnergyGenerator} to
 * determine the behavior and valid methods to interface with this handler.
 */
public abstract class LightEnergyHandlingTile extends LightEnergyTile {
	
	private final AABB renderAABB;
	
	/** A cache of every connected, valid storage unit connected to this one, including this one. This is a reference so that it may be shared (and cleared) across all instances of this BE in a world. */
	private ArrayContainer<LightEnergyHandlingTile> allValidOtherStorages = new ArrayContainer<>();
	
	protected LightEnergyHandlingTile(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		Vec3 pos = new Vec3(pWorldPosition.getX() + 0.5D, pWorldPosition.getY() + 0.5D, pWorldPosition.getZ() + 0.5D);
		renderAABB = AABB.ofSize(pos, 1, 1, 1);
	}
	
	private void clearNeighborsCommon(Set<LightEnergyHandlingTile> excluding) {
		if (allValidOtherStorages != null && excluding.add(this)) {
			LightEnergyHandlingTile[] thisArray = allValidOtherStorages.array;
			if (thisArray != null) {
				for (LightEnergyHandlingTile tile : thisArray) {
					if (tile.allValidOtherStorages != allValidOtherStorages) {
						if (excluding.add(tile)) {
							// OriMod.LOG.warn("BlockEntity {} found a connected tech block using a different array to cache the connected parts. This has caused a parity fault, which will now be fixed.", tile);
							tile.clearNeighborsCommon(excluding);
						}
					}
				}
			}
			allValidOtherStorages.array = null;
			allValidOtherStorages = null;
		}
	}
	
	private void clearNeighborsCommon() {
		HashSet<LightEnergyHandlingTile> excluding = new HashSet<>();
		clearNeighborsCommon(excluding);
	}
	
	@Override
	public void markLastKnownNeighborsDirty() {
		this.clearNeighborsCommon();
		super.markLastKnownNeighborsDirty();
	}
	
	@Override
	protected void markLastKnownNeighborsDirty(Set<BlockPos> excluding) {
		this.clearNeighborsCommon();
		super.markLastKnownNeighborsDirty(excluding);
	}
	
	private void iterateOver(HashSet<LightEnergyHandlingTile> known, HashSet<LightEnergyTile> knownJunctions, int remainingIterations, LightEnergyTile toCheckAround) {
		if (remainingIterations <= 0) return;
		remainingIterations--;
		
		LightEnergyTile[] others = toCheckAround.tryGetOtherHubs();
		if (others == null) return;
		
		for (LightEnergyTile other : others) {
			if (other.isRemoved() || !other.hasLevel()) continue;
			
			if (knownJunctions.add(other)) {
				if (other instanceof LightEnergyHandlingTile storage) {
					known.add(storage);
					// return; // Stop here!
				}
				iterateOver(known, knownJunctions, remainingIterations, other); // If this happens, we hit a T/+ joint. Continue searching.
				if (remainingIterations <= 0) return;
			}
		}
	}
	
	public LightEnergyHandlingTile[] getAllConnectedHandlers(int remainingIter) {
		if (allValidOtherStorages == null || allValidOtherStorages.array == null) {
			neighbors(); // Get neighbors.
			HashSet<LightEnergyHandlingTile> known = new HashSet<>(remainingIter);
			HashSet<LightEnergyTile> knownJunctions = new HashSet<>(remainingIter);
			
			known.add(this); // Prevent it from iterating back over itself
			iterateOver(known, knownJunctions, remainingIter, this);
			
			allValidOtherStorages = new ArrayContainer<>();
			allValidOtherStorages.array = known.toArray(new LightEnergyHandlingTile[0]);
			for (int index = 0; index < allValidOtherStorages.array.length; index++) {
				LightEnergyHandlingTile tile = allValidOtherStorages.array[index];
				tile.allValidOtherStorages = allValidOtherStorages; // Make them share the reference
			}
			
		}
		return allValidOtherStorages.array;
	}
	
	@Override
	public void setLevel(Level pLevel) {
		super.setLevel(pLevel);
		if (allValidOtherStorages != null) {
			allValidOtherStorages.array = null;
		}
		allValidOtherStorages = null;
		super.markLastKnownNeighborsDirty();
	}
	
	@Override
	public AABB getRenderBoundingBox() {
		return renderAABB;
	}
}
