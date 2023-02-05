package etithespirit.orimod.common.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * An interface that requires blocks to return a list of their appropriate tags, intended for use in datagen.
 */
public interface IBlockTagProvider {
	
	/**
	 * Returns all tags that this block should be added to.
	 * Reminder to self: Because it keeps eluding you, this is not a MC method. This is your method.
	 * @return A list of tags that this block must use.
	 */
	Iterable<TagKey<Block>> getAdditionalTagsForBlock();
	
}
