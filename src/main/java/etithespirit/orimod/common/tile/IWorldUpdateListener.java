package etithespirit.orimod.common.tile;

import etithespirit.orimod.info.coordinate.Cardinals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IWorldUpdateListener {
	
	/**
	 * If the given {@link BlockEntity} is an instance of {@link IWorldUpdateListener}, this will return it cast to an instance of this class.
	 * Otherwise, this will return null.
	 * @param tile The {@link BlockEntity} that might be (and ideally is) an {@link IWorldUpdateListener}
	 * @return An instance of {@link IWorldUpdateListener}, or null if {@code tile} could not be cast.
	 */
	static IWorldUpdateListener from(BlockEntity tile) {
		if (tile instanceof IWorldUpdateListener) {
			return (IWorldUpdateListener)tile;
		}
		return null;
	}
	
	/**
	 * Occurs when a {@link IWorldUpdateListener} adjacent to this {@link BlockEntity} is added, replaced, or removed.
	 * @param state The state of this block.
	 * @param world The world this change occurred in.
	 * @param at Where in the world this change occurred.
	 * @param replacedTile The previous TileEntity at the block. It will be missing core data, so reference the {@code world} and {@code changedAt} parameters to get the world and location.
	 * @param changedAt Where the change occurred.
	 * @param isMoving Whether or not this change occurred due to a piston push.
	 */
	void neighborAddedOrRemoved(BlockState state, Level world, BlockPos at, BlockPos changedAt, BlockEntity replacedTile, boolean isMoving);
	
	/**
	 * Occurs when <strong>this</strong> block changes (as opposed to the typical setup where neighbors have changed).
	 * @param world The world the change occurred in.
	 * @param at The location the change occurred in.
	 */
	void changed(LevelAccessor world, BlockPos at);
	
	/**
	 * Tells all neighbors of {@code changedAt} that this TE was added, replaced, or removed. Fires only for neighbors with TEs that are instances of {@link IWorldUpdateListener}.
	 * @param state The state of the block receiving the change signal.
	 * @param world The world the signal occurred in.
	 * @param changedAt The location of this block.
	 * @param replacedTile The TileEntity that previously occupied the space. The new tile can be acquired via {@link Level#getBlockEntity(BlockPos)}, and may not necessarily be an instance of {@link IWorldUpdateListener}.
	 * @param isMoving Whether or not this was caused by a piston push.
	 */
	default void tellAllNeighborsAddedOrRemoved(BlockState state, Level world, BlockPos changedAt, BlockEntity replacedTile, boolean isMoving) {
		for (Vec3i dir : Cardinals.ADJACENTS_IN_ORDER) {
			BlockPos neighborPos = changedAt.offset(dir);
			
			BlockEntity neighbor = world.getBlockEntity(neighborPos);
			IWorldUpdateListener asListener = from(neighbor);
			if (asListener != null) {
				asListener.neighborAddedOrRemoved(world.getBlockState(neighborPos), world, neighborPos, changedAt, replacedTile, isMoving);
			}
		}
	}
	
}
