package etithespirit.orimod.common.tags;

import com.google.common.collect.ImmutableList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class PresetBlockTags {
	
	private PresetBlockTags() {}
	
	public static final Iterable<TagKey<Block>> PICKAXE_ONLY = ImmutableList.of(BlockTags.MINEABLE_WITH_PICKAXE);
	
	public static final Iterable<TagKey<Block>> SHOVEL_ONLY = ImmutableList.of(BlockTags.MINEABLE_WITH_SHOVEL);
	
	public static final Iterable<TagKey<Block>> AXE_ONLY = ImmutableList.of(BlockTags.MINEABLE_WITH_AXE);
	
	public static final Iterable<TagKey<Block>> PICKAXE_ONLY_AND_LIGHT = ImmutableList.of(BlockTags.MINEABLE_WITH_PICKAXE, OriModBlockTags.ALIGNED_LIGHT);
	
	public static final Iterable<TagKey<Block>> SHOVEL_ONLY_AND_LIGHT = ImmutableList.of(BlockTags.MINEABLE_WITH_SHOVEL, OriModBlockTags.ALIGNED_LIGHT);
	
	public static final Iterable<TagKey<Block>> AXE_ONLY_AND_LIGHT = ImmutableList.of(BlockTags.MINEABLE_WITH_AXE, OriModBlockTags.ALIGNED_LIGHT);
	
}
