package etithespirit.orimod.common.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import static etithespirit.orimod.common.tags.TagHelpers.oriMod;

public final class OriModBlockTags {
	
	private OriModBlockTags() { }
	
	public static final TagKey<Block> LIGHT_ASSOC = BlockTags.create(oriMod("power_associated/light"));
	public static final TagKey<Block> DECAY_ASSOC = BlockTags.create(oriMod("power_associated/decay"));

}
