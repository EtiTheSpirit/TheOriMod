package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.common.tile.light.implementations.LightCapacitorTile;
import etithespirit.orimod.common.tile.light.implementations.LightInfiniteSourceTile;
import etithespirit.orimod.common.tile.light.implementations.LightRepairBoxTile;
import etithespirit.orimod.common.tile.light.implementations.LightToRFTile;
import etithespirit.orimod.common.tile.light.implementations.LightToRedstoneSignalTile;
import etithespirit.orimod.common.tile.light.implementations.SolarGeneratorTile;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class OriModWailaPluginServer implements IWailaPlugin {
	
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(LightEnergyComponentProviderServer.INSTANCE, LightCapacitorTile.class);
		registration.registerBlockDataProvider(LightEnergyComponentProviderServer.INSTANCE, LightInfiniteSourceTile.class);
		registration.registerBlockDataProvider(LightEnergyComponentProviderServer.INSTANCE, SolarGeneratorTile.class);
		registration.registerBlockDataProvider(LightEnergyComponentProviderServer.INSTANCE, LightToRedstoneSignalTile.class);
		registration.registerBlockDataProvider(LightEnergyComponentProviderServer.INSTANCE, LightToRFTile.class);
		registration.registerBlockDataProvider(LightEnergyComponentProviderServer.INSTANCE, LightRepairBoxTile.class);
	}
	
}
