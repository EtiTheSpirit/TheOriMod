package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.StaticData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Denotes a block as a Forlorn-themed block, which has Luxen highlights that can be either blue or orange. Used in some tools for quick ID.
 */
public interface IForlornBlueOrangeBlock {
	
	/**
	 * A preset method to swap between Orange and Blue skins for a Forlorn block without notifying neighbors.
	 * @param world The world to change in.
	 * @param at The location to make the change at.
	 */
	default void switchLuxenColor(Level world, BlockPos at) {
		if (!world.isClientSide) {
			BlockState current = world.getBlockState(at);
			world.setBlock(at, current.cycle(ForlornAppearanceMarshaller.IS_BLUE), StaticData.SILENT_BLOCK_SWAP_NOLIGHT);
		}
	}
	
	/**
	 * A preset method to determine if the block at the given location has {@link ForlornAppearanceMarshaller#IS_BLUE} set.
	 * @param world The world to check in.
	 * @param at The location to check.
	 * @return True if the given block is blue, false if not.
	 */
	default boolean isBlue(BlockGetter world, BlockPos at) {
		return world.getBlockState(at).getValue(ForlornAppearanceMarshaller.IS_BLUE);
	}
	
}
