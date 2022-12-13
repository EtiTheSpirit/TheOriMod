package etithespirit.orimod.common.tile.light;


import etithespirit.orimod.energy.ILightEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

/**
 * An extension to Forge's EnergyStorage that binds to a TileEntity and automatically modifies its NBT and marks it as dirty. This variant is specifically
 * designed for ILightEnergyStorage rather than the standard IEnergyStorage
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public class PersistentLightEnergyStorage implements ILightEnergyStorage {
	
	/**
	 * The default key used to save the energy stored in this instance to NBT.
	 */
	public static final String ENERGY_KEY = "lightEnergy";
	
	protected float energy;
	protected float capacity;
	protected float maxReceive;
	protected float maxExtract;
	Runnable markDirty; // For telling attached block entities that they have changed.
	
	/**
	 * If set, this string will be used to save this container's energy to NBT. If left alone, then {@link #ENERGY_KEY} will be used.
	 */
	public String energyKeyOverride = ENERGY_KEY;
	
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, float capacity) {
		this(markDirty, capacity, capacity, capacity, 0);
	}
	
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, float capacity, float maxTransfer) {
		this(markDirty, capacity, maxTransfer, maxTransfer, 0);
	}
	
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, float capacity, float maxReceive, float maxExtract) {
		this(markDirty, capacity, maxReceive, maxExtract, 0);
	}
	
	/**
	 * Construct a new storage with the given TE's markDirty() method, the given maximum capacity, maximum transfer amounts, and starting energy.
	 * @param markDirty The markDirty method associated with a TileEntity. This will be automatically set if needed.
	 * @param capacity The maximum amount of energy this storage can containe
	 * @param maxReceive The maximum amount of energy that can be stored in a single action.
	 * @param maxExtract The maximum amount of energy that can be extracted in a single action.
	 * @param energy The amount of energy within this storage at first.
	 */
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, float capacity, float maxReceive, float maxExtract, float energy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = Math.max(0, Math.min(capacity, energy));
		this.markDirty = markDirty;
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this appends the energy data to the entity.
	 * @param nbt The NBT tag to read from.
	 */
	public void readFromNBT(CompoundTag nbt) {
		this.energy = Math.max(Math.min(capacity, nbt.getFloat(energyKeyOverride)), 0);
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this writes the energy data to NBT.
	 * @param nbt The NBT tag to write to.
	 * @return The modified NBT tag.
	 */
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt.putFloat(energyKeyOverride, energy);
		return nbt;
	}
	
	@Override
	public float receiveLight(float maxReceive, boolean simulate) {
		if (!canReceiveLight()) return 0;
		
		float energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate) {
			energy += energyReceived;
			if (energyReceived > 0 && markDirty != null) markDirty.run();
		}
		return energyReceived;
	}
	
	@Override
	public float extractLightFrom(float maxExtract, boolean simulate) {
		if (!canExtractLightFrom()) return 0;
		
		float energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate) {
			energy -= energyExtracted;
			if (energyExtracted > 0 && markDirty != null) markDirty.run();
		}
		return energyExtracted;
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
		if (amount < 0) return false;
		if (canExtractLightFrom()) {
			if (amount > maxExtract) return false;
		}
		if (energy < amount) return false;
		if (simulate) return true;
		energy -= amount;
		if (markDirty != null) markDirty.run();
		return true;
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
		if (amount < 0) return 0;
		if (canReceiveLight()) {
			if (amount > maxReceive) amount = maxReceive;
		}
		float possible = capacity - energy;
		if (amount > possible) amount = possible;
		if (possible <= 0) return 0;
		if (simulate) return amount;
		energy += amount;
		if (markDirty != null) markDirty.run();
		return amount;
	}
	
	@Override
	public float getLightStored() {
		return energy;
	}
	
	@Override
	public float getMaxLightStored() {
		return capacity;
	}
	
	@Override
	public boolean canExtractLightFrom() {
		return maxExtract > 0;
	}
	
	@Override
	public boolean canReceiveLight() {
		return maxReceive > 0;
	}
	
	/**
	 * For use in generators, this value describes the maximum power output per tick.
	 * @return The maximum power output per tick.
	 */
	public float getMaxPowerDraw() {
		return maxExtract;
	}
	
	/**
	 * For use in machinery, this value describes the maximum power input per tick.
	 * @return The maximum power input per tick.
	 */
	public float getMaxChargeRate() {
		return maxReceive;
	}
	
}
