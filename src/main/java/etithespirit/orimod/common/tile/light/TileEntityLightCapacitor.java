package etithespirit.orimod.common.tile.light;


import etithespirit.orimod.client.audio.LoopingLightEnergyBlockSound;
import etithespirit.orimod.client.audio.StartLoopEndBlockSound;
import etithespirit.orimod.common.tile.IAmbientSoundEmitter;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityLightCapacitor extends AbstractLightEnergyHub implements IAmbientSoundEmitter {
	
	private final StartLoopEndBlockSound sound;
	
	public TileEntityLightCapacitor(BlockPos at, BlockState state) {
		super(TileEntityRegistry.LIGHT_CAPACITOR.get(), at, state, new PersistentLightEnergyStorage(null, 100, 50, 50, false, 100));
		this.sound = new StartLoopEndBlockSound(
			SoundRegistry.get("tile.light_tech.generic.activate"),
			new LoopingLightEnergyBlockSound(this, SoundRegistry.get("tile.light_tech.generic.active_loop")),
			SoundRegistry.get("tile.light_tech.generic.deactivate")
		);
		sound.loop.playStartupOnLoad = true;
	}
	
	@Override
	public void startSound() {
		if (this.hasLevel()) {
			Level level = this.getLevel();
			if (level.isClientSide) {
				sound.enqueue();
			}
		}
	}
	
	@Override
	public boolean soundShouldBePlaying() {
		return getLightStored() > 0;
	}
	
	@Override
	public void onLoad() {
		startSound();
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
		if (level == null) return 0;
		double amount = storage.receiveLight(maxReceive, simulate);
		if (amount != 0) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
		}
		return amount;
	}
	
	@Override
	public double extractLight(double maxExtract, boolean simulate) {
		if (level == null) return 0;
		double amount = storage.extractLight(maxExtract, simulate);
		if (amount != 0) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
		}
		return amount;
	}
	
	@Override
	public StartLoopEndBlockSound getSoundInstance() {
		return sound;
	}
}
