package etithespirit.etimod.srwailaasfsgdi;

import java.util.List;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.tile.AbstractLightEnergyTileEntity;
import etithespirit.etimod.energy.ILightEnergyStorage;
import etithespirit.etimod.util.TruncateNumber;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.TooltipPosition;
import net.darkhax.wawla.lib.Feature;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

/**
 * <em>hell now i oughta join in on it, make "SRWAILAASFSGDI" for "Stop Remaking WAILA And Settle For Something God Damn It"</em> -- Eti<br/>
 * All joking aside, this registers Light-based tech blocks to have their Light-energy readings put on WAILA+friends
 * @author Eti
 *
 */
public class HWYLAHook extends Feature implements IComponentProvider, IServerDataProvider<TileEntity> {
	
	private static final ResourceLocation SHOW_ENERGY = new ResourceLocation(EtiMod.MODID, "show_energy");
	private static final ResourceLocation SHOW_FLUX = new ResourceLocation(EtiMod.MODID, "show_flux");

	@Override
	public void initialize(IRegistrar hwyla) {
		hwyla.addConfig(SHOW_ENERGY, true);
		hwyla.registerComponentProvider(this, TooltipPosition.BODY, ILightEnergyStorage.class);
		hwyla.registerBlockDataProvider(this, ILightEnergyStorage.class);
	}
	
	@Override
	public void appendBody(List<ITextComponent> info, IDataAccessor accessor, IPluginConfig config) {
		if (accessor.getTileEntity() instanceof AbstractLightEnergyTileEntity) {
			final AbstractLightEnergyTileEntity container = (AbstractLightEnergyTileEntity)accessor.getTileEntity();
			
			final CompoundNBT nbt = accessor.getServerData();
			final double stored = nbt.getDouble("storedLux"); // This is the only one that needs server-dominant data.
			// Every other parameter (conversion, conv ratio, flux) is constant.
			
			if (config.get(SHOW_ENERGY)) {
				if (container.acceptsConversion()) {
					this.addInfo(info, "waila.etimod.energy_rf", TruncateNumber.truncateNumber(stored, 2), TruncateNumber.truncateNumber(stored * container.getLightToRFConversionRatio(), 2));
				} else {
					this.addInfo(info, "waila.etimod.energy", TruncateNumber.truncateNumber(stored, 2));
				}
			}
			if (config.get(SHOW_FLUX) && container.subjectToFlux()) {
				this.addInfo(info, "waila.etimod.env_flux");
			}
		}
	}
	
	@Override
	public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, TileEntity te) {
		if (te instanceof AbstractLightEnergyTileEntity) {
			final AbstractLightEnergyTileEntity container = (AbstractLightEnergyTileEntity)te;
			data.putDouble("storedLux", container.getLightStored());
		}
	}
	

}
