package etithespirit.orimod.common.tags;

import etithespirit.orimod.OriMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class OriModBlockTags {
	
	private OriModBlockTags() { }
	
	public static final TagKey<Block> LIGHT_ASSOC = BlockTags.create(OriMod.rsrc("alignment/light_related"));
	public static final TagKey<Block> DECAY_ASSOC = BlockTags.create(OriMod.rsrc("alignment/decay_related"));

}
