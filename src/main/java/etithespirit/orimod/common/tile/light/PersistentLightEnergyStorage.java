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
	
	protected double energy;
	protected double capacity;
	protected double maxReceive;
	protected double maxExtract;
	protected boolean allowRFConversion;
	Runnable markDirty; // For telling attached block entities that they have changed.
	
	/**
	 * If set, this string will be used to save this container's energy to NBT. If left alone, then {@link #ENERGY_KEY} will be used.
	 */
	public String energyKeyOverride = ENERGY_KEY;
	
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, double capacity) {
		this(markDirty, capacity, capacity, capacity, false, 0);
	}
	
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, double capacity, double maxTransfer) {
		this(markDirty, capacity, maxTransfer, maxTransfer, false, 0);
	}
	
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, double capacity, double maxTransfer, boolean allowRFConversion) {
		this(markDirty, capacity, maxTransfer, maxTransfer, allowRFConversion, 0);
	}
	
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, double capacity, double maxReceive, double maxExtract) {
		this(markDirty, capacity, maxReceive, maxExtract, false, 0);
	}
	
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, double capacity, double maxReceive, double maxExtract, boolean allowRFConversion) {
		this(markDirty, capacity, maxReceive, maxExtract, allowRFConversion, 0);
	}
	
	/**
	 * Construct a new storage with the given TE's markDirty() method, the given maximum capacity, maximum transfer amounts, and starting energy.
	 * @param markDirty The markDirty method associated with a TileEntity. This will be automatically set if needed.
	 * @param capacity The maximum amount of energy this storage can containe
	 * @param maxReceive The maximum amount of energy that can be stored in a single action.
	 * @param maxExtract The maximum amount of energy that can be extracted in a single action.
	 * @param allowRFConversion Whether or not this storage can convert to and from RF
	 * @param energy The amount of energy within this storage at first.
	 */
	public PersistentLightEnergyStorage(@Nullable Runnable markDirty, double capacity, double maxReceive, double maxExtract, boolean allowRFConversion, double energy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = Math.max(0, Math.min(capacity, energy));
		this.allowRFConversion = allowRFConversion;
		this.markDirty = markDirty;
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this appends the energy data to the entity.
	 * @param nbt The NBT tag to read from.
	 */
	public void readFromNBT(CompoundTag nbt) {
		this.energy = Math.max(Math.min(capacity, nbt.getDouble(energyKeyOverride)), 0);
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this writes the energy data to NBT.
	 * @param nbt The NBT tag to write to.
	 * @return The modified NBT tag.
	 */
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt.putDouble(energyKeyOverride, energy);
		return nbt;
	}
	
	@Override
	public double receiveLight(double maxReceive, boolean simulate) {
		if (!canReceiveLight()) return 0;
		
		double energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate) {
			energy += energyReceived;
			if (energyReceived > 0 && markDirty != null) markDirty.run();
		}
		return energyReceived;
	}
	
	@Override
	public double extractLight(double maxExtract, boolean simulate) {
		if (!canExtractLight()) return 0;
		
		double energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate) {
			energy -= energyExtracted;
			if (energyExtracted > 0 && markDirty != null) markDirty.run();
		}
		return energyExtracted;
	}
	
	@Override
	public double getLightStored() {
		return energy;
	}
	
	@Override
	public double getMaxLightStored() {
		return capacity;
	}
	
	@Override
	public boolean canExtractLight() {
		return maxExtract > 0;
	}
	
	@Override
	public boolean canReceiveLight() {
		return maxReceive > 0;
	}
	
	@Override
	public boolean acceptsConversion() {
		return allowRFConversion;
	}
	
}
