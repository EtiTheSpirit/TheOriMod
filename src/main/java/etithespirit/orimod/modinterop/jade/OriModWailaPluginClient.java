package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.common.block.light.LightCapacitorBlock;
import etithespirit.orimod.common.block.light.LightToRFGeneratorBlock;
import etithespirit.orimod.common.block.light.LightToRedstoneSignalBlock;
import etithespirit.orimod.common.block.light.connection.LightConduitBlock;
import etithespirit.orimod.common.block.light.connection.SolidLightConduitBlock;
import etithespirit.orimod.common.block.light.creative.InfiniteSourceLightBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornStoneLineBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornStoneOmniBlock;
import etithespirit.orimod.common.block.light.generation.SolarGeneratorBlock;
import etithespirit.orimod.common.block.light.interaction.LightRepairBoxBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class OriModWailaPluginClient implements IWailaPlugin {
	
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(LightEnergyComponentProviderClient.INSTANCE, LightCapacitorBlock.class);
		registration.registerBlockComponent(LightEnergyComponentProviderClient.INSTANCE, InfiniteSourceLightBlock.class);
		registration.registerBlockComponent(LightEnergyComponentProviderClient.INSTANCE, SolarGeneratorBlock.class);
		registration.registerBlockComponent(LightEnergyComponentProviderClient.INSTANCE, LightToRedstoneSignalBlock.class);
		registration.registerBlockComponent(LightEnergyComponentProviderClient.INSTANCE, LightToRFGeneratorBlock.class);
		registration.registerBlockComponent(LightEnergyComponentProviderClient.INSTANCE, LightRepairBoxBlock.class);
		
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, LightCapacitorBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, ForlornStoneOmniBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, ForlornStoneLineBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, LightConduitBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, SolidLightConduitBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, LightToRFGeneratorBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, LightToRedstoneSignalBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, InfiniteSourceLightBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProviderClient.INSTANCE, LightRepairBoxBlock.class);
		// registration.registerBlockComponent(ForlornBlockSkinProvider.INSTANCE, SolidLightConduitBlock.class);
	}
}
