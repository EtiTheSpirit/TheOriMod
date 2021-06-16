package etithespirit.etimod.common.tile;

import etithespirit.etimod.info.coordinate.Cardinals;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * Allows TileEntities to see block updates in the world.
 */
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
	 * Occurs when a {@link IWorldUpdateListener} adjacent to this {@link TileEntity} is added, replaced, or removed.
	 * @param state The state of this block.
	 * @param world The world this change occurred in.
	 * @param at Where in the world this change occurred.
	 * @param replacedTile The previous TileEntity at the block. It will be missing core data, so reference the {@code world} and {@code changedAt} parameters to get the world and location.
	 * @param changedAt Where the change occurred.
	 * @param isMoving Whether or not this change occurred due to a piston push.
	 */
	void neighborAddedOrRemoved(BlockState state, World world, BlockPos at, BlockPos changedAt, TileEntity replacedTile, boolean isMoving);
	
	/**
	 * Occurs when this block changes.
	 * @param world The world the change occurred in.
	 * @param at The location the change occurred in.
	 */
	void changed(IWorld world, BlockPos at);
	
	/**
	 * Tells all neighbors of {@code changedAt} that this TE was added, replaced, or removed. Fires only for neighbors with TEs that are instances of {@link IWorldUpdateListener}.
	 * @param state The state of the block receiving the change signal.
	 * @param world The world the signal occurred in.
	 * @param changedAt The location of this block.
	 * @param replacedTile The TileEntity that previously occupied the space. The new tile can be acquired via {@link World#getBlockEntity(BlockPos)}, and may not necessarily be an instance of {@link IWorldUpdateListener}.
	 * @param isMoving Whether or not this was caused by a piston push.
	 */
	default void tellAllNeighborsAddedOrRemoved(BlockState state, World world, BlockPos changedAt, TileEntity replacedTile, boolean isMoving) {
		for (Vector3i dir : Cardinals.ADJACENTS_IN_ORDER) {
			BlockPos neighborPos = changedAt.offset(dir);
			
			TileEntity neighbor = world.getBlockEntity(neighborPos);
			IWorldUpdateListener asListener = from(neighbor);
			if (asListener != null) {
				asListener.neighborAddedOrRemoved(world.getBlockState(neighborPos), world, neighborPos, changedAt, replacedTile, isMoving);
			}
		}
	}
	
}
