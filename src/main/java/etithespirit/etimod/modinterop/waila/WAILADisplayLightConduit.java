package etithespirit.etimod.modinterop.waila;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.common.tile.AbstractLightEnergyTileEntity;
import etithespirit.etimod.util.TruncateNumber;
import mcp.mobius.waila.api.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public final class WAILADisplayLightConduit implements IComponentProvider {
	
	private static final ResourceLocation SHOW_AUTO = new ResourceLocation(EtiMod.MODID, "show_auto_conduit");
	
	public void initialize(IRegistrar hwyla) {
		hwyla.addConfig(SHOW_AUTO, true);
		hwyla.registerComponentProvider(this, TooltipPosition.BODY, LightConduitBlock.class);
	}
	
	@Override
	public void appendBody(List<ITextComponent> info, IDataAccessor accessor, IPluginConfig config) {
		if (accessor.getBlock() instanceof LightConduitBlock) {
			// This should be
			if (config.get(SHOW_AUTO)) {
				boolean isAuto = accessor.getBlockState().getValue(LightConduitBlock.AUTO);
				addComponentYN(info, "waila.etimod.isautoconduit", isAuto);
			}
		}
	}
	
	private void addComponentYN(List<ITextComponent> info, String trsKey, boolean state) {
		TextFormatting color = state ? TextFormatting.GREEN : TextFormatting.RED;
		String yn = state ? "gui.yes" : "gui.no"; // vanilla keys
		info.add(new TranslationTextComponent(trsKey).append(new TranslationTextComponent(yn).withStyle(color)));
	}
	
}
