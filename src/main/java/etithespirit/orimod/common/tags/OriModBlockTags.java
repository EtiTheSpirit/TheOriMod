package etithespirit.orimod.common.tags;

import etithespirit.orimod.OriMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class OriModBlockTags {
	
	private OriModBlockTags() { }
	
	public static final TagKey<Block> ALIGNED = BlockTags.create(OriMod.rsrc("aligned"));
	public static final TagKey<Block> ALIGNED_LIGHT = BlockTags.create(OriMod.rsrc("aligned/aligned_light"));
	public static final TagKey<Block> ALIGNED_DECAY = BlockTags.create(OriMod.rsrc("aligned/aligned_decay"));

}
