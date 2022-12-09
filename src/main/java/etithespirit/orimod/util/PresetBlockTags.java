package etithespirit.orimod.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;

public final class PresetBlockTags {
	
	private PresetBlockTags() {}
	
	public static final Iterable<TagKey<Block>> PICKAXE_ONLY = ImmutableList.of(BlockTags.MINEABLE_WITH_PICKAXE);
	
	public static final Iterable<TagKey<Block>> SHOVEL_ONLY = ImmutableList.of(BlockTags.MINEABLE_WITH_SHOVEL);
	
	public static final Iterable<TagKey<Block>> AXE_ONLY = ImmutableList.of(BlockTags.MINEABLE_WITH_AXE);
	
}
