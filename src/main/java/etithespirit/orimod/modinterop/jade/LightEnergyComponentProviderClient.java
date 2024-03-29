package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.chat.ExtendedChatColors;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.energy.ILightEnergyGenerator;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.util.TruncateNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum LightEnergyComponentProviderClient implements IBlockComponentProvider {
	INSTANCE;
	
	private static final ResourceLocation ID = new ResourceLocation(OriMod.MODID, "show_energy");
	
	private static String floatToRoundedInt(float f) {
		if ((long)f == f) {
			return String.valueOf((long)f);
		}
		return String.valueOf(f);
	}
	
	private static boolean isClientSneaking() {
		Player plr = Minecraft.getInstance().player;
		if (plr == null) return false;
		return plr.isCrouching();
	}
	
	/**
	 * On the current line: "X Luxen" or "Y Lum" or "X Luxen/tick" or "Y Lum/tick"
	 * @param tooltip
	 * @param lux
	 * @return True if it had to be scaled into lum, false if it remained as luxen
	 */
	private static boolean appendLuxAmountToExisting(ITooltip tooltip, float lux, boolean asRate) {
		if (lux < 1) {
			tooltip.append(
				Component.translatable(
					"waila.orimod.energy.lum_amount",
					TruncateNumber.truncateNumber(lux * ILightEnergyStorage.LUM_PER_LUX, 2)
				).withStyle(ExtendedChatColors.LUXEN_PAIR.normal)
			);
			if (asRate) {
				tooltip.append(Component.literal("/").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
				tooltip.append(Component.translatable("waila.orimod.energy.word_tick").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
			}
			return true;
		} else {
			tooltip.append(
				Component.translatable(
					"waila.orimod.energy.luxen_amount",
					TruncateNumber.truncateNumber(lux, 2)
				).withStyle(ExtendedChatColors.LUXEN_PAIR.normal)
			);
			if (asRate) {
				tooltip.append(Component.literal("/").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
				tooltip.append(Component.translatable("waila.orimod.energy.word_tick").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
			}
			return false;
		}
	}
	
	/**
	 * On the current line: " (X RF)" or " (X RF/tick)"<br/>
	 * Shows nothing if {@link #isClientSneaking()} returns false.
	 * @param tooltip
	 * @param lux
	 */
	private static void appendLuxToExistingAsRF(ITooltip tooltip, float lux, boolean asRate) {
		if (!isClientSneaking()) return;
		tooltip.append(Component.literal(" (").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
		tooltip.append(
			Component.translatable(
				"waila.orimod.energy.rf_amount",
				TruncateNumber.truncateNumber(ILightEnergyStorage.luxenToRedstoneFlux(lux), 2)
			).withStyle(ExtendedChatColors.RF_PAIR.normal)
		);
		if (asRate) {
			tooltip.append(Component.literal("/").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
			tooltip.append(Component.translatable("waila.orimod.energy.word_tick").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
		}
		tooltip.append(Component.literal(")").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
	}
	
	/**
	 * On the current line: "X Luxen (Y RF)" or "X Luxen/tick (Y RF/tick)" or "X Lum/tick (Y RF/tick)"<br/>
	 * Excludes RF if {@link #isClientSneaking()} returns false.
	 * @param tooltip
	 * @param lux
	 * @param asRate
	 * @returns True if it had to be scaled into lum, false if it remained as luxen
	 */
	private static boolean appendLuxAndRF(ITooltip tooltip, float lux, boolean asRate) {
		boolean retn = appendLuxAmountToExisting(tooltip, lux, asRate);
		appendLuxToExistingAsRF(tooltip, lux, asRate);
		return retn;
	}
	
	private static void addOverloadWarning(ITooltip tooltip) {
		tooltip.add(Component.translatable("waila.orimod.energy.word_warning").withStyle(ExtendedChatColors.SWARM_PINK_PAIR.normal));
		tooltip.append(Component.literal(": ").withStyle(ExtendedChatColors.DUSTY_RED_PAIR.normal));
		tooltip.append(Component.translatable("waila.orimod.energy.overload").withStyle(ExtendedChatColors.DUSTY_RED_PAIR.normal));
	}
	
	private static void addConversionNotice(ITooltip tooltip) {
		tooltip.add(Component.translatable("waila.orimod.energy.word_conversion").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
		tooltip.append(Component.literal(": ").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
		tooltip.append(
			Component.translatable(
				"waila.orimod.energy.lum_amount",
				floatToRoundedInt(ILightEnergyStorage.LUM_PER_LUX)
			).withStyle(ExtendedChatColors.LUXEN_PAIR.dark)
		);
		tooltip.append(Component.literal(" = ").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
		tooltip.append(
			Component.translatable(
				"waila.orimod.energy.luxen_amount",
				"1"
			).withStyle(ExtendedChatColors.LUXEN_PAIR.dark)
		);
	}
	
	private static void setupConsumerTip(LightEnergyHandlingTile tile, ITooltip tooltip, CompoundTag serverData) {
		float consumptionPerTick = serverData.getFloat("Consumed");
		boolean isOverdrawn = serverData.getBoolean("IsOverdrawn");
		
		tooltip.add(Component.translatable("waila.orimod.energy.word_draw").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
		tooltip.append(Component.literal(": ").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
		boolean neededLum = appendLuxAndRF(tooltip, consumptionPerTick, true);
		if (neededLum && isClientSneaking()) addConversionNotice(tooltip);
		if (isOverdrawn) addOverloadWarning(tooltip);
	}
	
	private static void setupGeneratorTip(LightEnergyHandlingTile tile, ITooltip tooltip, CompoundTag serverData) {
		float generationPerTick = serverData.getFloat("Generated");
		boolean isOverdrawn = serverData.getBoolean("IsOverdrawn");
		
		tooltip.add(Component.translatable("waila.orimod.energy.word_output").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
		tooltip.append(Component.literal(": ").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
		boolean neededLum = appendLuxAndRF(tooltip, generationPerTick, true);
		if (neededLum && isClientSneaking()) addConversionNotice(tooltip);
		if (isOverdrawn) addOverloadWarning(tooltip);
	}
	
	private static void setupStorageTip(LightEnergyHandlingTile tile, ITooltip tooltip, CompoundTag serverData) {
		tooltip.add(Component.translatable("waila.orimod.energy.word_storage").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
		tooltip.append(Component.literal(": ").withStyle(ExtendedChatColors.GRAY_PAIR.normal));
		boolean neededLum = appendLuxAndRF(tooltip, serverData.getFloat("Light"), false);
		if (neededLum && isClientSneaking()) addConversionNotice(tooltip);
	}
	
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
		BlockEntity ent = blockAccessor.getBlockEntity();
		if (ent instanceof LightEnergyHandlingTile storageTile) {
			CompoundTag srvData = blockAccessor.getServerData();
			boolean isConsumer = storageTile instanceof ILightEnergyConsumer;
			boolean isGenerator = storageTile instanceof ILightEnergyGenerator;
			if (isConsumer && isGenerator) {
				throw new UnsupportedOperationException();
			}
			if (isConsumer) {
				setupConsumerTip(storageTile, tooltip, srvData);
			} else if (isGenerator) {
				setupGeneratorTip(storageTile, tooltip, srvData);
			} else if (srvData.contains("Light")) {
				setupStorageTip(storageTile, tooltip, srvData);
			}
		}
	}
	
	@Override
	public ResourceLocation getUid() {
		return ID;
	}
}
