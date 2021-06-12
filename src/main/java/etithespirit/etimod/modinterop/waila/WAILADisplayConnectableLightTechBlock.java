package etithespirit.etimod.modinterop.waila;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.common.tile.AbstractLightEnergyTileEntity;
import etithespirit.etimod.util.TruncateNumber;
import mcp.mobius.waila.api.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public final class WAILADisplayConnectableLightTechBlock implements IComponentProvider {
	
	private static final ResourceLocation SHOW_AUTO = new ResourceLocation(EtiMod.MODID, "show_auto");
	
	public void initialize(IRegistrar hwyla) {
		hwyla.addConfig(SHOW_AUTO, true);
		hwyla.registerComponentProvider(this, TooltipPosition.BODY, ConnectableLightTechBlock.class);
	}
	
	@Override
	public void appendBody(List<ITextComponent> info, IDataAccessor accessor, IPluginConfig config) {
		if (accessor.getBlock() instanceof ConnectableLightTechBlock) {
			// This should be
			if (config.get(SHOW_AUTO)) {
				boolean isAuto = accessor.getBlockState().getValue(ConnectableLightTechBlock.AUTO);
				addComponentYN(info, "waila.etimod.autoconnect", isAuto);
			}
		}
	}
	
	private void addComponentYN(List<ITextComponent> info, String trsKeyPrefix, boolean state) {
		info.add(new TranslationTextComponent(trsKeyPrefix + (state ? "true" : "false")));
	}
	
}
