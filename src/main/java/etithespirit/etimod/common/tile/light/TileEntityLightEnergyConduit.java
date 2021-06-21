package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.common.tile.IWorldUpdateListener;
import etithespirit.etimod.registry.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A {@link TileEntity} associated with {@link LightConduitBlock} that is able to store a reference to all connected storage points.
 */
public class TileEntityLightEnergyConduit extends AbstractLightEnergyLink {
	
	public TileEntityLightEnergyConduit() {
		super(TileEntityRegistry.LIGHT_CONDUIT.get());
	}
	
	
	
}
