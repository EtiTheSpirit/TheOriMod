package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.util.TruncateNumber;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum LightEnergyComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {
	INSTANCE;
	
	private static final ResourceLocation ID = new ResourceLocation(OriMod.MODID, "show_energy");
	
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
		BlockEntity ent = blockAccessor.getBlockEntity();
		if (ent instanceof LightEnergyStorageTile storageTile) {
			CompoundTag srvData = blockAccessor.getServerData();
			if (srvData.contains("Light")) {
				double stored = srvData.getDouble("Light");
				if (storageTile.acceptsConversion()) {
					tooltip.add(Component.translatable("waila.orimod.energy_rf", TruncateNumber.truncateNumber(stored, 2), TruncateNumber.truncateNumber(ILightEnergyStorage.luxenToRedstoneFlux(stored), 2)));
				} else {
					tooltip.add(Component.translatable("waila.orimod.energy", stored));
				}
			}
		}
	}
	
	@Override
	public void appendServerData(CompoundTag data, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
		data.putDouble("Light", ((LightEnergyStorageTile)blockEntity).getLightStored());
	}
	
	
	@Override
	public ResourceLocation getUid() {
		return ID;
	}
}
