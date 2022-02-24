package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Interopability for WAILA: Allows displaying the automatic connection behavior of light tech blocks.
 *
 * @author Eti
 */
public final class WAILADisplayConnectableLightTechBlock implements IComponentProvider {
	
	private static final ResourceLocation SHOW_AUTO = new ResourceLocation(OriMod.MODID, "show_auto");
	
	public void initialize(IRegistrar hwyla) {
		hwyla.addConfig(SHOW_AUTO, true);
		hwyla.registerComponentProvider(this, TooltipPosition.BODY, ConnectableLightTechBlock.class);
	}
	
	@Override
	public void appendTooltip(ITooltip info, BlockAccessor accessor, IPluginConfig config) {
		if (accessor.getBlock() instanceof ConnectableLightTechBlock) {
			// This should be
			if (config.get(SHOW_AUTO)) {
				boolean isAuto = accessor.getBlockState().getValue(ConnectableLightTechBlock.AUTO);
				addComponentYN(info, "waila.orimod.autoconnect", isAuto);
			}
		}
	}
	
	private void addComponentYN(ITooltip info, String trsKeyPrefix, boolean state) {
		info.add(new TranslatableComponent(trsKeyPrefix + (state ? "true" : "false")));
	}
	
}
