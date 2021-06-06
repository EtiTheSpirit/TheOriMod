package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.common.tile.AbstractLightEnergyTileEntity;
import etithespirit.etimod.registry.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraftforge.common.util.Constants;

public class TileEntityLightCapacitor extends AbstractLightEnergyTileEntity {
	
	protected PersistentLightEnergyStorage storage = null;

	public TileEntityLightCapacitor() {
		super(TileEntityRegistry.LIGHT_CAPACITOR.get());
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		return storage.writeToNBT(super.write(nbt));
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		storage.readFromNBT(nbt);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = storage.writeToNBT(new CompoundNBT());
		return new SUpdateTileEntityPacket(getPos(), -1, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager mgr, SUpdateTileEntityPacket packet) {
		CompoundNBT nbt = packet.getNbtCompound();
		storage.readFromNBT(nbt);
	}

	@Override
	public double receiveLight(double maxReceive, boolean simulate) {
		double amount = storage.receiveLight(maxReceive, simulate);
		if (amount != 0) {
			world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		return amount;
	}

	@Override
	public double extractLight(double maxExtract, boolean simulate) {
		double amount = storage.extractLight(maxExtract, simulate);
		if (amount != 0) {
			world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		return amount;
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
		double amount = storage.applyEnvFlux(minGen, maxGen, simulate);
		if (amount != 0) {
			world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		return amount;
	}

}
