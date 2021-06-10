package etithespirit.etimod.common.tile.rf;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityRFStorage extends TileEntity implements IEnergyStorage {
	
	private PersistentEnergyStorage storage = new PersistentEnergyStorage(this::setChanged, 1000000, 100, 100, 1000000);

	public TileEntityRFStorage(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		storage.writeToNBT(nbt);
		return nbt;
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		storage.readFromNBT(state, nbt);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return storage.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return storage.canExtract();
	}

	@Override
	public boolean canReceive() {
		return storage.canReceive();
	}

}
