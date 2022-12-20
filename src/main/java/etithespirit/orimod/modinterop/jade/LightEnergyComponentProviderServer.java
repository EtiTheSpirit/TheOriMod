package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.energy.ILightEnergyGenerator;
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
		if (blockEntity instanceof ILightEnergyStorage storage) {
			data.putFloat("Light", storage.getLightStored());
		}
		if (blockEntity instanceof ILightEnergyGenerator generator) {
			data.putFloat("Generated", generator.getMaximumGeneratedAmountForDisplay());
			data.putBoolean("IsOverdrawn", generator.hadTooMuchDrawLastForDisplay());
		} else if (blockEntity instanceof ILightEnergyConsumer consumer) {
			data.putFloat("Consumed", consumer.getMaximumDrawnAmountForDisplay());
			data.putBoolean("IsOverdrawn", consumer.hadTooLittlePowerLastForDisplay());
		}
	}
	
	
	@Override
	public ResourceLocation getUid() {
		return ID;
	}
}