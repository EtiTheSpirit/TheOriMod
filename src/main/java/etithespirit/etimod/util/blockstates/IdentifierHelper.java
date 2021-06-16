package etithespirit.etimod.util.blockstates;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.datafix.fixes.LeavesFix;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.common.Tags;

/**
 * Assists in finding out qualities of various things. Very unorganized, generally handy.
 */
public final class IdentifierHelper {
	
	private IdentifierHelper() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * Returns whether or not the given block is considered a leaf block.
	 * @param b The block to test.
	 * @return True if the block is considered a leaf block.
	 */
	public static boolean isLeafBlock(Block b) {
		return b instanceof LeavesBlock;
	}
	
}
