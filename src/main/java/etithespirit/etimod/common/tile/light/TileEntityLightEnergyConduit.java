package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.registry.TileEntityRegistry;
import net.minecraft.tileentity.TileEntity;

/**
 * A {@link TileEntity} associated with {@link LightConduitBlock} that is able to store a reference to all connected storage points.
 */
public class TileEntityLightEnergyConduit extends AbstractLightEnergyLink {
	
	public TileEntityLightEnergyConduit() {
		super(TileEntityRegistry.LIGHT_CONDUIT.get());
	}
	
	
	
}
