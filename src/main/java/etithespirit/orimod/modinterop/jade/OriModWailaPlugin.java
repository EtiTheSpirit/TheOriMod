package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.common.block.light.LightCapacitorBlock;
import etithespirit.orimod.common.block.light.LightConduitBlock;
import etithespirit.orimod.common.block.light.SolidLightConduitBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornStoneLineBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornStoneOmniBlock;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tile.light.implementations.LightCapacitorTile;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class OriModWailaPlugin implements IWailaPlugin {
	
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(LightEnergyComponentProvider.INSTANCE, LightCapacitorTile.class);
	}
	
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(LightEnergyComponentProvider.INSTANCE, LightCapacitorBlock.class);
		
		registration.registerBlockComponent(ForlornBlockSkinProvider.INSTANCE, LightCapacitorBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProvider.INSTANCE, ForlornStoneOmniBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProvider.INSTANCE, ForlornStoneLineBlock.class);
		registration.registerBlockComponent(ForlornBlockSkinProvider.INSTANCE, LightConduitBlock.class);
		// registration.registerBlockComponent(ForlornBlockSkinProvider.INSTANCE, SolidLightConduitBlock.class);
	}
}
