package etithespirit.orimod.common.tile;

import etithespirit.orimod.info.coordinate.Cardinals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * This interface mandates that the block entity implementing it receives notifications of the world changing.
 */
public final class WorldUpdateListener {
	
	private WorldUpdateListener() {}
	
	private static final List<IBlockUpdatePacket> BEFORE_CALLBACKS = Lists.newArrayList();
	private static final List<IBlockUpdatePacket> AFTER_CALLBACKS = Lists.newArrayList();
	
	/**
	 * For internal use only. Runs all callbacks set to run before execution of a block change.
	 * @param at The location of the change
	 * @param oldBlock The block that was there before
	 * @param newBlock The block that was there after
	 */
	public static void notifyOfUpdatingBlockChangeBefore(BlockPos at, BlockState oldBlock, BlockState newBlock) {
		BEFORE_CALLBACKS.forEach(mtd -> mtd.accept(at, oldBlock, newBlock, true));
	}
	
	/**
	 * For internal use only. Runs all callbacks set to run after execution of a block change.
	 * @param at
	 * @param oldBlock
	 * @param newBlock
	 */
	public static void notifyOfUpdatingBlockChangeAfter(BlockPos at, BlockState oldBlock, BlockState newBlock) {
		AFTER_CALLBACKS.forEach(mtd -> mtd.accept(at, oldBlock, newBlock, false));
	}
	
	public static void connect(boolean before, IBlockUpdatePacket callback) {
		if (before)
			BEFORE_CALLBACKS.add(callback);
		else
			AFTER_CALLBACKS.add(callback);
	}
	
	public static void disconnect(boolean before, IBlockUpdatePacket callback) {
		if (before)
			BEFORE_CALLBACKS.remove(callback);
		else
			AFTER_CALLBACKS.remove(callback);
	}
	
	@FunctionalInterface
	public interface IBlockUpdatePacket {
		void accept(BlockPos at, BlockState oldBlock, BlockState newBlock, boolean isBeforeChange);
	}
	
}
