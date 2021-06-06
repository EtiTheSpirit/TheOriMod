package etithespirit.etimod.common.tile.rf;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.EnergyStorage;

/**
 * An extension to Forge's EnergyStorage that binds to a TileEntity and automatically modifies its NBT and marks it as dirty.
 * @author Eti
 *
 */
public class PersistentEnergyStorage extends EnergyStorage {
	
	/**
	 * The default key used to save the energy stored in this instance to NBT.
	 */
	public static final String ENERGY_KEY = "rfEnergy";
	
	protected final Runnable markDirty;
	
	/**
	 * If set, this string will be used to save this container's energy to NBT. If left alone, then {@link #ENERGY_KEY} will be used.
	 */
	public String energyKeyOverride = ENERGY_KEY;

	/**
	 * Construct a new storage with the given TE's markDirty() method, and the given maximum capacity. It can transfer up to that much energy at once, and starts with 0 energy stored.
	 * @param markDirty
	 * @param capacity
	 */
	public PersistentEnergyStorage(Runnable markDirty, int capacity) {
		this(markDirty, capacity, capacity, capacity, 0);
	}

	/**
	 * Construct a new storage with the given TE's markDirty() method, the given maximum capacity, and the maximum amount of energy that can be extracted or received at once. It will have 0 energy stored.
	 * @param markDirty
	 * @param capacity
	 * @param maxTransfer
	 */
	public PersistentEnergyStorage(Runnable markDirty, int capacity, int maxTransfer) {
		this(markDirty, capacity, maxTransfer, maxTransfer, 0);
	}

	/**
	 * Construct a new storage with the given TE's markDirty() method, the given maximum capacity, and the given maximum amounts of energy that can be extracted or received. It will have 0 energy stored.
	 * @param markDirty
	 * @param capacity
	 * @param maxReceive
	 * @param maxExtract
	 */
	public PersistentEnergyStorage(Runnable markDirty, int capacity, int maxReceive, int maxExtract) {
		this(markDirty, capacity, maxReceive, maxExtract, 0);
	}

	/**
	 * Construct a new storage with the given TE's markDirty() method, the given maximum capacity, maximum transfer amounts, and starting energy.
	 * @param markDirty
	 * @param capacity
	 * @param maxReceive
	 * @param maxExtract
	 * @param energy
	 */
	public PersistentEnergyStorage(Runnable markDirty, int capacity, int maxReceive, int maxExtract, int energy) {
		super(capacity, maxReceive, maxExtract, energy);
		this.markDirty = markDirty;
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this appends the energy data to the entity.
	 * @param state
	 * @param nbt
	 */
	public void readFromNBT(BlockState state, CompoundNBT nbt) {
		this.energy = Math.max(Math.min(capacity, nbt.getInt(energyKeyOverride)), 0);
	}
	
	/**
	 * Should be called from the TileEntity that instantiated this instance, this writes the energy data to NBT.
	 * @param nbt
	 * @return
	 */
	public CompoundNBT writeToNBT(CompoundNBT nbt) {
		nbt.putInt(energyKeyOverride, energy);
		return nbt;
	}
	
	/**
	 * Receive the given amount of energy (or the maximum amount that this can receive, whichever is less).<br/>
	 * If the energy transferred is not zero, and if simulate is false, then the markDirty() method is called on the corresponding TE.
	 */
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int amount = super.receiveEnergy(maxReceive, simulate);
		if (amount != 0 && !simulate && markDirty != null) markDirty.run();
		return amount;
	}

	/**
	 * Extract the given amount of energy (or the maximum amount that this can give, whichever is less).<br/>
	 * If the energy transferred is not zero, and if simulate is false, then the markDirty() method is called on the corresponding TE.
	 */
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int amount = super.extractEnergy(maxExtract, simulate);
		if (amount != 0 && !simulate && markDirty != null) markDirty.run();
		return amount;
	}

}
