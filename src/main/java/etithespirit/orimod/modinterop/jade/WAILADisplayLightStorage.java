package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.light.LightCapacitorBlock;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.util.TruncateNumber;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * <em>Hell now I oughta join in on it, make "SRWAILAASFSGDI" for "Stop Remaking WAILA And Settle For Something God Damn It"</em> -- Eti<br/>
 * All joking aside, this registers Light-based tech blocks to have their Light-energy readings put on WAILA
 * @author Eti
 *
 */
public final class WAILADisplayLightStorage implements IComponentProvider {
	
	private static final ResourceLocation SHOW_ENERGY = new ResourceLocation(OriMod.MODID, "show_energy");
	
	public void initialize(IRegistrar hwyla) {
		hwyla.addConfig(SHOW_ENERGY, true);
		hwyla.registerComponentProvider(this, TooltipPosition.BODY, LightCapacitorBlock.class);
	}
	
	
	@Override
	public void appendTooltip(ITooltip info, BlockAccessor blockAccessor, IPluginConfig config) {
		if (blockAccessor.getBlockEntity() instanceof AbstractLightEnergyHub container) {
			final double stored = container.getLightStored();
			// Every other parameter (conversion, conv ratio, flux) is constant.
			
			if (config.get(SHOW_ENERGY)) {
				if (container.acceptsConversion()) {
					addComponent(info, "waila.orimod.energy_rf", TruncateNumber.truncateNumber(stored, 2), TruncateNumber.truncateNumber(stored * OriModConfigs.LUX_TO_RF_RATIO.get(), 2));
				} else {
					addComponent(info, "waila.orimod.energy", TruncateNumber.truncateNumber(stored, 2));
				}
			}
		}
	}
	
	private void addComponent(ITooltip info, String trsKey, Object... args) {
		info.add(new TranslatableComponent(trsKey, args));
	}
	
}
