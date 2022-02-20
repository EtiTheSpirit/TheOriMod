package etithespirit.orimod.common.block;

import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

/**
 * An interface that requires blocks to return a list of their appropriate tags, intended for use in datagen.
 */
public interface IToolRequirementProvider {
	
	/**
	 * Returns all tags that this block should be added to. The tags must come from {@link net.minecraft.tags.BlockTags}.
	 * @return A list of tags that this block must use.
	 */
	Iterable<Tag.Named<Block>> getTagsForBlock();
	
}
