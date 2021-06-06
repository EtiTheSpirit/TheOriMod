package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.energy.ILightEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityLightCapacitor extends TileEntity implements ILightEnergyStorage {
	
	protected PersistentLightEnergyStorage storage = null;

	public TileEntityLightCapacitor(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		return storage.writeToNBT(super.write(nbt));
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		storage.readFromNBT(state, nbt);
	}

	@Override
	public double receiveLight(double maxReceive, boolean simulate) {
		return storage.receiveLight(maxReceive, simulate);
	}

	@Override
	public double extractLight(double maxExtract, boolean simulate) {
		return storage.extractLight(maxExtract, simulate);
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
	public boolean canExtractLight() {
		return storage.canExtractLight();
	}

	@Override
	public boolean canReceiveLight() {
		return storage.canReceiveLight();
	}

	@Override
	public boolean subjectToFlux() {
		return storage.subjectToFlux();
	}

	@Override
	public boolean acceptsConversion() {
		return storage.acceptsConversion();
	}

	@Override
	public double applyEnvFlux(double minGen, double maxGen, boolean simulate) {
		return storage.applyEnvFlux(minGen, maxGen, simulate);
	}

}
