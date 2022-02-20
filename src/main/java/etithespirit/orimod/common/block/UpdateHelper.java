package etithespirit.orimod.common.block;

import etithespirit.orimod.common.tile.IWorldUpdateListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.world.BlockEvent;

public final class UpdateHelper {
	
	public static void onBlockChanged(BlockEvent.NeighborNotifyEvent evt) {
		LevelAccessor world = evt.getWorld();
		BlockPos at = evt.getPos();
		BlockEntity tile = world.getBlockEntity(at);
		IWorldUpdateListener listener = IWorldUpdateListener.from(tile);
		if (listener != null) {
			listener.changed(world, at);
		}
	}
	
}
