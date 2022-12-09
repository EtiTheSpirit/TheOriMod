package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.OriMod;
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

public abstract class LightEnergyStorageTile extends LightEnergyTile implements ILightEnergyStorage {
	
	/** A container used to store energy. */
	protected final @Nonnull PersistentLightEnergyStorage storage;
	
	/** A cache of every connected, valid storage unit connected to this one, including this one. This is a reference so that it may be shared (and cleared) across all instances of this BE in a world. */
	private Container<LightEnergyStorageTile> allValidOtherStorages = new Container<>();
	
	/** This is used to know when to send an update message out to all neighboring tiles to redraw conduits in their active (energized) or inactive state. */
	protected boolean lastHadEnergy = false;
	
	protected LightEnergyStorageTile(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, @Nonnull PersistentLightEnergyStorage storageProvider) {
		super(pType, pWorldPosition, pBlockState);
		storage = storageProvider;
		storage.markDirty = this::setChanged;
	}
	
	private void clearNeighborsCommon(Set<LightEnergyStorageTile> excluding) {
		if (allValidOtherStorages != null && excluding.add(this)) {
			LightEnergyStorageTile[] thisArray = allValidOtherStorages.array;
			if (thisArray != null) {
				for (LightEnergyStorageTile tile : thisArray) {
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
		HashSet<LightEnergyStorageTile> excluding = new HashSet<>();
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
	
	private void iterateOver(HashSet<LightEnergyStorageTile> known, HashSet<LightEnergyTile> knownJunctions, int remainingIterations, LightEnergyTile toCheckAround) {
		if (remainingIterations <= 0) return;
		remainingIterations--;
		
		LightEnergyTile[] others = toCheckAround.tryGetOtherHubs();
		if (others == null) return;
		
		for (LightEnergyTile other : others) {
			if (other.isRemoved() || !other.hasLevel()) continue;
			
			if (knownJunctions.add(other)) {
				if (other instanceof LightEnergyStorageTile storage) {
					known.add(storage);
					// return; // Stop here!
				}
				iterateOver(known, knownJunctions, remainingIterations, other); // If this happens, we hit a T/+ joint. Continue searching.
				if (remainingIterations <= 0) return;
			}
		}
	}
	
	public LightEnergyStorageTile[] getAllConnectedStorage(int remainingIter) {
		if (allValidOtherStorages == null || allValidOtherStorages.array == null) {
			neighbors(); // Get neighbors.
			HashSet<LightEnergyStorageTile> known = new HashSet<>(remainingIter);
			HashSet<LightEnergyTile> knownJunctions = new HashSet<>(remainingIter);
			
			known.add(this); // Prevent it from iterating back over itself
			iterateOver(known, knownJunctions, remainingIter, this);
			
			allValidOtherStorages = new Container<>();
			allValidOtherStorages.array = known.toArray(new LightEnergyStorageTile[0]);
			for (int index = 0; index < allValidOtherStorages.array.length; index++) {
				LightEnergyStorageTile tile = allValidOtherStorages.array[index];
				tile.allValidOtherStorages = allValidOtherStorages; // Make them share the reference
			}
			
		}
		return allValidOtherStorages.array;
	}
	
	@Override
	public void setLevel(Level pLevel) {
		super.setLevel(pLevel);
		allValidOtherStorages.array = null;
		allValidOtherStorages = null;
		super.markLastKnownNeighborsDirty();
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		storage.writeToNBT(tag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		storage.readFromNBT(tag);
	}
	
	@Override
	public AABB getRenderBoundingBox() {
		BlockPos posI = getBlockPos();
		Vec3 pos = new Vec3(posI.getX() + 0.5D, posI.getY() + 0.5D, posI.getZ() + 0.5D);
		return AABB.ofSize(pos, 1, 1, 1);
	}
	
	@Override
	public double receiveLight(double maxReceive, boolean simulate) {
		return storage.receiveLight(maxReceive, simulate);
	}
	
	@Override
	public double extractLightFrom(double maxExtract, boolean simulate) {
		return storage.extractLightFrom(maxExtract, simulate);
	}
	
	@Override
	public double getLightStored() {
		return storage.getLightStored();
	}
	
	@Override
	public double getMaxLightStored() {
		return storage.getMaxLightStored();
	}
	
	@Override
	public boolean canReceiveLight() {
		return storage.canReceiveLight();
	}
	
	@Override
	public boolean canExtractLightFrom() {
		return storage.canExtractLightFrom();
	}
	
	@Override
	public boolean acceptsConversion() {
		return storage.acceptsConversion();
	}
	
}
