package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.chat.ExtendedChatColors;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ForlornBlockSkinProviderClient implements IBlockComponentProvider {
	INSTANCE;
	
	private static final ResourceLocation ID = new ResourceLocation(OriMod.MODID, "show_forlorn_skin");
	
	private static boolean isClientSneaking() {
		Player plr = Minecraft.getInstance().player;
		if (plr == null) return false;
		return plr.isCrouching();
	}
	
	/**
	 * On a new line: "Skin: Blue" or "Skin: Orange"
	 * @param tooltip
	 * @param isBlue
	 */
	private static void addSkin(ITooltip tooltip, boolean isBlue) {
		tooltip.add(Component.translatable("waila.orimod.forlorn.word_skin").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
		tooltip.append(Component.literal(": ").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
		if (isBlue) {
			tooltip.append(Component.translatable("waila.orimod.forlorn.word_blue").withStyle(ExtendedChatColors.FORLORN_BLUE_PAIR.dark));
		} else {
			tooltip.append(Component.translatable("waila.orimod.forlorn.word_orange").withStyle(ExtendedChatColors.FORLORN_ORANGE_PAIR.dark));
		}
	}
	
	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
		Block forlornBlockCheck = blockAccessor.getBlock();
		if (forlornBlockCheck instanceof IForlornBlueOrangeBlock forlornBlock && isClientSneaking()) {
			boolean isBlue = forlornBlock.isBlue(blockAccessor.getLevel(), blockAccessor.getPosition());
			addSkin(iTooltip, isBlue);
		}
	}
	
	@Override
	public ResourceLocation getUid() {
		return ID;
	}
}
