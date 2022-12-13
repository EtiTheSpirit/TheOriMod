package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.energy.ILightEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IServerDataProvider;

public enum LightEnergyComponentProviderServer implements IServerDataProvider<BlockEntity> {
	INSTANCE;
	
	private static final ResourceLocation ID = new ResourceLocation(OriMod.MODID, "show_energy");
	
	@Override
	public void appendServerData(CompoundTag data, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
		ILightEnergyStorage storage = (LightEnergyStorageTile)blockEntity;
		data.putFloat("Light", storage.getLightStored());
		if (storage instanceof LightEnergyStorageTile.ILuxenGenerator generator) {
			data.putFloat("Generated", generator.getLuxGeneratedPerTick());
		}
		if (storage instanceof LightEnergyStorageTile.ILuxenConsumer generator) {
			data.putFloat("Consumed", generator.getLuxConsumedPerTick());
			data.putBoolean("IsOverdrawn", generator.isOverdrawn());
		}
	}
	
	
	@Override
	public ResourceLocation getUid() {
		return ID;
	}
}