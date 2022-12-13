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

public abstract class LightEnergyStorageTile extends LightEnergyTile implements ILightEnergyStorage {
	
	/** A container used to store energy. */
	private final @Nonnull PersistentLightEnergyStorage storage;
	
	/** A cache of every connected, valid storage unit connected to this one, including this one. This is a reference so that it may be shared (and cleared) across all instances of this BE in a world. */
	private ArrayContainer<LightEnergyStorageTile> allValidOtherStorages = new ArrayContainer<>();
	
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
			
			allValidOtherStorages = new ArrayContainer<>();
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
		if (allValidOtherStorages != null) {
			allValidOtherStorages.array = null;
		}
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
	public float receiveLight(float maxReceive, boolean simulate) {
		return storage.receiveLight(maxReceive, simulate);
	}
	
	@Override
	public float extractLightFrom(float maxExtract, boolean simulate) {
		return storage.extractLightFrom(maxExtract, simulate);
	}
	
	@Override
	public float getLightStored() {
		return storage.getLightStored();
	}
	
	@Override
	public float getMaxLightStored() {
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
	
	/**
	 * Attempts to remove the given amount of energy from this storage (with the intent of using or "spending" this energy).<br/>
	 * Most notably, this bypasses {@link #canExtractLightFrom()}, and is intended for when a device uses its own energy rather
	 * than in the transfer of energy. To transfer energy, use {@link #extractLightFrom(float, boolean)}.<br/>
	 * <br/>
	 * This respects the maximum extraction rate, unless it is 0 from which no rate limit is imposed. Attempting to extract
	 * an amount larger than the maximum extraction rate will fail.
	 * @param amount The amount to try to remove.
	 * @param simulate If true, the energy is not actually taken out.
	 * @return True if the desired amount could be taken, false if not.
	 */
	public boolean trySpendEnergy(float amount, boolean simulate) {
		return storage.trySpendEnergy(amount, simulate);
	}
	
	/**
	 * The opposite of {@link #trySpendEnergy(float, boolean)}, this method attempts to create energy out of nothing with the intent
	 * of a block generating it (you know, like a generator does). This bypasses the receiver limits, and as such, should not be used for
	 * transfer. To transfer energy, use {@link #receiveLight(float, boolean)}.
	 * @param amount The amount to try to generate.
	 * @param simulate If true, the energy is not actually added.
	 * @return The actual amount of energy that was added to the machine.
	 */
	public float generateEnergy(float amount, boolean simulate) {
		return storage.generateEnergy(amount, simulate);
	}
	
	/**
	 * For use in generators, this value describes the maximum power output per tick.
	 * @return The maximum power output per tick.
	 */
	public float getMaxPowerDraw() {
		return storage.getMaxPowerDraw();
	}
	
	/**
	 * For use in generators, this value describes the maximum power input per tick.
	 * @return The maximum power input per tick.
	 */
	public float getMaxChargeRate() {
		return storage.getMaxChargeRate();
	}
	
	/**
	 * If true, the Powered state will not be managed by the ticker and must be manually set by the implementor.
	 * @return False to automatically handle the Powered state based on if there is energy incoming, True to manually handle it per implementor.
	 */
	public boolean skipAutomaticPoweredBlockstate() {
		return false;
	}
	
	/** Denotes the inherited class as a generator, which changes how it displays in Jade */
	public interface ILuxenGenerator {
		
		float getLuxGeneratedPerTick();
		
	}
	
	
	/** Denotes the inherited class as a consumer, which changes how it displays in Jade */
	public interface ILuxenConsumer {
		
		float getLuxConsumedPerTick();
		
		/**
		 * Returns whether or not this block should be consuming more power than it has available.
		 * @return
		 */
		boolean isOverdrawn();
		
	}
}
