package etithespirit.etimod.util.blockstates;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.datafix.fixes.LeavesFix;
import net.minecraftforge.common.Tags;

public final class IdentifierHelper {
	
	private IdentifierHelper() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	public static boolean isLeafBlock(Block b) {
		return b instanceof LeavesBlock;
	}
	
}
