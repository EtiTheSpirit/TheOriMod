package etithespirit.etimod.srwailaasfsgdi;

import java.util.List;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.tile.AbstractLightEnergyTileEntity;
import etithespirit.etimod.util.TruncateNumber;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
/**
 * <em>Hell now I oughta join in on it, make "SRWAILAASFSGDI" for "Stop Remaking WAILA And Settle For Something God Damn It"</em> -- Eti<br/>
 * All joking aside, this registers Light-based tech blocks to have their Light-energy readings put on WAILA
 * @author Eti
 *
 */
public class WAILADisplayLightStorage implements IComponentProvider {
	
	private static final ResourceLocation SHOW_ENERGY = new ResourceLocation(EtiMod.MODID, "show_energy");
	private static final ResourceLocation SHOW_FLUX = new ResourceLocation(EtiMod.MODID, "show_flux");

	public void initialize(IRegistrar hwyla) {
		hwyla.addConfig(SHOW_ENERGY, true);
		hwyla.addConfig(SHOW_FLUX, true);
		hwyla.registerComponentProvider(this, TooltipPosition.BODY, AbstractLightEnergyTileEntity.class);
	}
	
	@Override
	public void appendBody(List<ITextComponent> info, IDataAccessor accessor, IPluginConfig config) {
		if (accessor.getTileEntity() instanceof AbstractLightEnergyTileEntity) {
			final AbstractLightEnergyTileEntity container = (AbstractLightEnergyTileEntity)accessor.getTileEntity();
			final double stored = container.getLightStored();
			// Every other parameter (conversion, conv ratio, flux) is constant.
			
			if (config.get(SHOW_ENERGY)) {
				if (container.acceptsConversion()) {
					addComponent(info, "waila.etimod.energy_rf", TruncateNumber.truncateNumber(stored, 2), TruncateNumber.truncateNumber(stored * container.getLightToRFConversionRatio(), 2));
				} else {
					addComponent(info, "waila.etimod.energy", TruncateNumber.truncateNumber(stored, 2));
				}
			}
			if (config.get(SHOW_FLUX) && container.getFluxBehavior().isEnabled()) {
				addComponent(info, "waila.etimod.env_flux");
			}
		}
	}
	
	private void addComponent(List<ITextComponent> info, String trsKey, Object... args) {
		info.add(new TranslationTextComponent(trsKey, args));
	}
}
