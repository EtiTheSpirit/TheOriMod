package etithespirit.orimod.common.block;

import etithespirit.orimod.common.chat.ExtendedChatColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

@SuppressWarnings("PointlessBitwiseExpression")
public final class StaticData {
	
	public static final BlockBehaviour.StatePredicate ALWAYS_FALSE = (x, y, z) -> false;
	
	public static final BlockBehaviour.StatePredicate ALWAYS_TRUE = (x, y, z) -> true;
	
	/**
	 * This alias represents silently swapping a block out. It changes the block and sends it over the network, but does not notify the neighbors of the block, does not update lighting, and suppresses any drops.
	 */
	public static final int SILENT_BLOCK_SWAP_NOLIGHT = Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_LIGHT | Block.UPDATE_SUPPRESS_DROPS;
	
	/**
	 * This alias represents silently swapping a block out. It changes the block and sends it over the network, but does not notify the neighbors of the block and suppresses any drops.
	 */
	public static final int SILENT_BLOCK_SWAP = Block.UPDATE_CLIENTS | Block.UPDATE_SUPPRESS_DROPS;
	
	
}
