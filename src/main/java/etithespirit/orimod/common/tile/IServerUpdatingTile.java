package etithespirit.orimod.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An extension that causes the tile ticker to update on this specific block.
 */
public interface IServerUpdatingTile {
	
	void updateServer(Level inLevel, BlockPos at, BlockState current);
	
}
