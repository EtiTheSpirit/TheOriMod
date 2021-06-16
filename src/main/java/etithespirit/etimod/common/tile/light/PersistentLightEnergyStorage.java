package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.energy.FluxBehavior;
import etithespirit.etimod.energy.ILightEnergyStorage;
import net.minecraft.nbt.CompoundNBT;

/**
 * An extension to Forge's EnergyStorage that binds to a TileEntity and automatically modifies its NBT and marks it as dirty. This variant is specifically
 * designed for ILightEnergyStorage rather than the standard IEnergyStorage
 * @author Eti
 *
 */
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
	protected final Runnable markDirty;
	protected FluxBehavior flux;
	
	/**
	 * If set, this string will be used to save this container's energy to NBT. If left alone, then {@link #ENERGY_KEY} will be used.
	 */
	public String energyKeyOverride = ENERGY_KEY;

	public PersistentLightEnergyStorage(Runnable markDirty, double capacity) {
		this(markDirty, capacity, capacity, capacity, null, false, 0);
	}
	
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxTransfer) {
		this(markDirty, capacity, maxTransfer, maxTransfer, null, false, 0);
	}
	
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxTransfer, boolean allowRFConversion) {
		this(markDirty, capacity, maxTransfer, maxTransfer, null, allowRFConversion, 0);
	}
	
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxReceive, double maxExtract) {
		this(markDirty, capacity, maxReceive, maxExtract, null, false, 0);
	}
	
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxReceive, double maxExtract, boolean allowRFConversion) {
		this(markDirty, capacity, maxReceive, maxExtract, null, allowRFConversion, 0);
	}

	/**
	 * Construct a new storage with the given TE's markDirty() method, the given maximum capacity, maximum transfer amounts, and starting energy.
	 * @param markDirty The markDirty method associated with a TileEntity, or null if this is being used outside of the context of a TileEntity.
	 * @param capacity The maximum amount of energy this storage can containe
	 * @param maxReceive The maximum amount of energy that can be stored in a single action.
	 * @param maxExtract The maximum amount of energy that can be extracted in a single action.
	 * @param flux A NumberRange representing the possible environmental flux. NumberRange.ZERO will disable flux. Note that this is cloned for internal use.
	 * @param allowRFConversion Whether or not this storage can convert to and from RF
	 * @param energy The amount of energy within this storage at first.
	 */
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxReceive, double maxExtract, FluxBehavior flux, boolean allowRFConversion, double energy) {
		if (flux == null) flux = FluxBehavior.DISABLED;
		
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = Math.max(0, Math.min(capacity, energy));
		this.allowRFConversion = allowRFConversion;
		this.markDirty = markDirty;
		this.flux = flux;
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this appends the energy data to the entity.
	 * @param nbt
	 */
	public void readFromNBT(CompoundNBT nbt) {
		this.energy = Math.max(Math.min(capacity, nbt.getDouble(energyKeyOverride)), 0);
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this writes the energy data to NBT.
	 * @param nbt
	 * @return
	 */
	public CompoundNBT writeToNBT(CompoundNBT nbt) {
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

	@Override
	public FluxBehavior getFluxBehavior() {
		return flux;
	}

	@Override
	public double applyEnvFlux(boolean simulate) {
		double amount = getFluxBehavior().getNextEnvFlux(canExtractLight(), canReceiveLight(), simulate);
		
		if (amount > 0) {
			amount = receiveLight(amount, simulate);
		} else if (amount < 0) {
			amount = extractLight(amount, simulate);
		}
		
		if (amount != 0 && markDirty != null && !simulate) markDirty.run();
		return amount;
	}

}
