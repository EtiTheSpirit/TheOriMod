package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.common.tile.AbstractLightEnergyAnchor;
import etithespirit.etimod.common.tile.IWorldUpdateListener;
import etithespirit.etimod.energy.FluxBehavior;
import etithespirit.etimod.registry.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraftforge.common.util.Constants;

public class TileEntityLightCapacitor extends AbstractLightEnergyAnchor implements IWorldUpdateListener {
	
	private boolean hasZeroEnergy;

	private boolean TEMP_hasTicked = false;
	
	public TileEntityLightCapacitor() {
		super(TileEntityRegistry.LIGHT_CAPACITOR.get());
		this.storage = new PersistentLightEnergyStorage(this::setChanged, 10000, 20, 20, FluxBehavior.DISABLED, false, 10000);
		hasZeroEnergy = storage.getLightStored() == 0;
	}
	
	/**
	 * Executes when energy changes from nonzero to zero, or zero to nonzero.
	 */
	private void energyZeroStateChanged() {
		for (ILightEnergyConduit conduit : connected) {
			conduit.refresh(); // lol
		}
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		return storage.writeToNBT(super.save(nbt));
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt); // Explicitly call load here.
		storage.readFromNBT(nbt); // THEN let the storage do its thing.
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = storage.writeToNBT(new CompoundNBT());
		return new SUpdateTileEntityPacket(getBlockPos(), -1, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager mgr, SUpdateTileEntityPacket packet) {
		CompoundNBT nbt = packet.getTag();
		storage.readFromNBT(nbt);
	}

	@Override
	public double receiveLight(double maxReceive, boolean simulate) {
		double amount = storage.receiveLight(maxReceive, simulate);
		if (amount != 0) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		if (!simulate) {
			if (hasZeroEnergy && getLightStored() != 0) {
				hasZeroEnergy = false;
				energyZeroStateChanged();
			}
		}
		return amount;
	}

	@Override
	public double extractLight(double maxExtract, boolean simulate) {
		double amount = storage.extractLight(maxExtract, simulate);
		if (amount != 0) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		if (!simulate) {
			if (!hasZeroEnergy && getLightStored() == 0) {
				hasZeroEnergy = true;
				energyZeroStateChanged();
			}
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
	public boolean acceptsConversion() {
		return storage.acceptsConversion();
	}

	@Override
	public FluxBehavior getFluxBehavior() {
		return storage.getFluxBehavior();
	}

	@Override
	public double applyEnvFlux(boolean simulate) {
		double amount = storage.applyEnvFlux(simulate);
		if (amount != 0) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
		return amount;
	}
}
