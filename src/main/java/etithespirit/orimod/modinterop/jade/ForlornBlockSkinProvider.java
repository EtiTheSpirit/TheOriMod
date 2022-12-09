package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ForlornBlockSkinProvider implements IBlockComponentProvider {
	INSTANCE;
	
	private static final ResourceLocation ID = new ResourceLocation(OriMod.MODID, "show_forlorn_skin");
	
	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
		Block forlornBlockCheck = blockAccessor.getBlock();
		if (forlornBlockCheck instanceof IForlornBlueOrangeBlock forlornBlock) {
			boolean isBlue = forlornBlock.isBlue(blockAccessor.getLevel(), blockAccessor.getPosition());
			iTooltip.add(Component.translatable("waila.orimod.forlorn_blue"));
			iTooltip.append(Component.translatable("waila.orimod.forlorn_blue." + (isBlue ? "blue" : "orange")));
		}
	}
	
	@Override
	public ResourceLocation getUid() {
		return ID;
	}
}
