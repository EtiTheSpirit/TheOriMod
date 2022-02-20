package etithespirit.orimod.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;

public final class BlockIdentifier {
	
	private BlockIdentifier() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * Returns whether or not the given block is considered a leaf block.
	 * @param b The block to test.
	 * @return True if the block is considered a leaf block.
	 */
	public static boolean isLeafBlock(Block b) {
		return b instanceof LeavesBlock;
	}
	
	
}
