package etithespirit.etimod.common.tile.light;

import java.util.Random;

import etithespirit.etimod.energy.ILightEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

/**
 * An extension to Forge's EnergyStorage that binds to a TileEntity and automatically modifies its NBT and marks it as dirty.
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
	protected boolean allowFlux;
	protected boolean allowRFConversion;
	protected final Runnable markDirty;
	protected long iteration;
	protected Random rng;
	
	/**
	 * If set, this string will be used to save this container's energy to NBT. If left alone, then {@link #ENERGY_KEY} will be used.
	 */
	public String energyKeyOverride = ENERGY_KEY;

	public PersistentLightEnergyStorage(Runnable markDirty, double capacity) {
		this(markDirty, capacity, capacity, capacity, false, false, 0);
	}
	
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxTransfer) {
		this(markDirty, capacity, maxTransfer, maxTransfer, false, false, 0);
	}
	
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxTransfer, boolean allowRFConversion) {
		this(markDirty, capacity, maxTransfer, maxTransfer, false, allowRFConversion, 0);
	}
	
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxReceive, double maxExtract) {
		this(markDirty, capacity, maxReceive, maxExtract, false, false, 0);
	}
	
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxReceive, double maxExtract, boolean allowRFConversion) {
		this(markDirty, capacity, maxReceive, maxExtract, false, allowRFConversion, 0);
	}

	/**
	 * Construct a new storage with the given TE's markDirty() method, the given maximum capacity, maximum transfer amounts, and starting energy.
	 * @param markDirty The markDirty method associated with a TileEntity, or null if this is being used outside of the context of a TileEntity.
	 * @param capacity The maximum amount of energy this storage can containe
	 * @param maxReceive The maximum amount of energy that can be stored in a single action.
	 * @param maxExtract The maximum amount of energy that can be extracted in a single action.
	 * @param allowFlux Whether or not this storage is subject to environmental flux.
	 * @param allowRFConversion Whether or not this storage can convert to and from RF
	 * @param energy The amount of energy within this storage at first.
	 */
	public PersistentLightEnergyStorage(Runnable markDirty, double capacity, double maxReceive, double maxExtract, boolean allowFlux, boolean allowRFConversion, double energy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = Math.max(0, Math.min(capacity, energy));
		this.allowFlux = allowFlux;
		this.allowRFConversion = allowRFConversion;
		this.markDirty = markDirty;
		
		this.rng = new Random();
		this.iteration = rng.nextInt();
		this.rng = new Random(this.iteration);
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this appends the energy data to the entity.
	 * @param state
	 * @param nbt
	 */
	public void readFromNBT(BlockState state, CompoundNBT nbt) {
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
	public boolean subjectToFlux() {
		return allowFlux;
	}

	@Override
	public boolean acceptsConversion() {
		return allowRFConversion;
	}

	@Override
	public double applyEnvFlux(double minGen, double maxGen, boolean simulate) {
		if (!subjectToFlux()) return 0;
		if (maxGen < minGen) {
			double oldMax = maxGen;
			maxGen = minGen;
			minGen = oldMax;
		}
		rng.setSeed(iteration);
		double range = maxGen - minGen;
		double amount = (rng.nextDouble() * range) - minGen;
		if (!simulate) {
			if (amount > 0) {
				if (!canReceiveLight()) amount = 0;
			} else if (amount < 0) {
				if (!canExtractLight()) amount = 0;
			}
			iteration++;
			if (amount != 0 && markDirty != null) markDirty.run();
		}
		return amount;
	}

}
