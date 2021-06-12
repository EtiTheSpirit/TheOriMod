package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.common.tile.AbstractLightEnergyTileEntity;
import etithespirit.etimod.common.tile.IWorldUpdateListener;
import etithespirit.etimod.energy.FluxBehavior;
import etithespirit.etimod.registry.TileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class TileEntityLightCapacitor extends AbstractLightEnergyTileEntity implements IWorldUpdateListener {
	
	public static final int MAX_BRANCH_RANGE = 64;
	public static final int MAX_RECURSION_DEPTH = 16;

	public TileEntityLightCapacitor() {
		super(TileEntityRegistry.LIGHT_CAPACITOR.get());
		this.storage = new PersistentLightEnergyStorage(this::setChanged, 10000, 20, 20, FluxBehavior.DISABLED, false, 10000);
		
		
	}
	
	@Override
	public void tick() {
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos at, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
	
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
		return amount;
	}

	@Override
	public double extractLight(double maxExtract, boolean simulate) {
		double amount = storage.extractLight(maxExtract, simulate);
		if (amount != 0) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
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
