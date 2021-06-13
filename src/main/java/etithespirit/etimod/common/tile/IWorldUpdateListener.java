package etithespirit.etimod.common.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Allows TileEntities to see block updates in the world.
 * <strong>For this to work, the block associated with this TE must set it up.</strong>
 */
@SuppressWarnings("deprecation")
public interface IWorldUpdateListener {
	
	/**
	 * If the given {@link TileEntity} is an instance of {@link IWorldUpdateListener}, this will return it cast to an instance of this class.
	 * Otherwise, this will return null.
	 * @param tile The {@link TileEntity} that might be (and ideally is) an {@link IWorldUpdateListener}
	 * @return An instance of {@link IWorldUpdateListener}, or null if {@code tile} could not be cast.
	 */
	static IWorldUpdateListener from(TileEntity tile) {
		if (tile instanceof IWorldUpdateListener) {
			return (IWorldUpdateListener)tile;
		}
		return null;
	}
	
	/**
	 * Identical to {@link Block#neighborChanged(BlockState, World, BlockPos, Block, BlockPos, boolean)}.
	 * @param state The state of this block.
	 * @param world The world this change occurred in.
	 * @param at Where in the world this change occurred.
	 * @param replacedBlock The block that was replaced (before its replacement).
	 * @param changedAt Where the change occurred.
	 * @param isMoving Whether or not this change occurred due to a piston push.
	 */
	void neighborChanged(BlockState state, World world, BlockPos at, Block replacedBlock, BlockPos changedAt, boolean isMoving);
	
}
