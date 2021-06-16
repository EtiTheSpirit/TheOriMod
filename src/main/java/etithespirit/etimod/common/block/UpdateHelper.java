package etithespirit.etimod.common.block;

import etithespirit.etimod.common.tile.IWorldUpdateListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;

public final class UpdateHelper {
	
	public static void onBlockChanged(BlockEvent.NeighborNotifyEvent evt) {
		IWorld world = evt.getWorld();
		BlockPos at = evt.getPos();
		TileEntity tile = world.getBlockEntity(at);
		IWorldUpdateListener listener = IWorldUpdateListener.from(tile);
		if (listener != null) {
			listener.changed(world, at);
		}
	}
	
}
