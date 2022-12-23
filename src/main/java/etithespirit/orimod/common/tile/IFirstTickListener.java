package etithespirit.orimod.common.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface allows BEs to listen to and run code on the first tick.
 */
public interface IFirstTickListener {
	
	boolean needsInit();
	
	void tellInitComplete();
	
	void firstTick(Level inWorld, BlockPos at, BlockState state);
	
}
