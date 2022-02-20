package etithespirit.orimod.common.tile.light;


import etithespirit.orimod.common.tile.IWorldUpdateListener;
import etithespirit.orimod.energy.FluxBehavior;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.network.NetworkInstance;
import org.jetbrains.annotations.Nullable;

public class TileEntityLightCapacitor extends AbstractLightEnergyHub implements IWorldUpdateListener {
	
	public TileEntityLightCapacitor(BlockPos at, BlockState state) {
		super(TileEntityRegistry.LIGHT_CAPACITOR.get(), at, state);
		this.storage = new PersistentLightEnergyStorage(this::setChanged, 10000, 20, 20, FluxBehavior.DISABLED, false, 10000);
	}
	
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		storage.writeToNBT(nbt);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt); // Explicitly call load here.
		storage.readFromNBT(nbt); // THEN let the storage do its thing.
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this, (blockEntity) -> {
			CompoundTag tag = getNBTForUpdatePacket(new CompoundTag());
			tag = storage.writeToNBT(tag);
			return tag;
		});
	}
	
	@Override
	public void onDataPacket(Connection mgr, ClientboundBlockEntityDataPacket packet) {
		CompoundTag nbt = packet.getTag();
		storage.readFromNBT(nbt);
	}
	
	@Override
	public double receiveLight(double maxReceive, boolean simulate) {
		double amount = storage.receiveLight(maxReceive, simulate);
		if (amount != 0) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
		}
		return amount;
	}
	
	@Override
	public double extractLight(double maxExtract, boolean simulate) {
		double amount = storage.extractLight(maxExtract, simulate);
		if (amount != 0) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
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
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
		}
		return amount;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos at, BlockState state) {
		return new TileEntityLightCapacitor(at, state);
	}
}
