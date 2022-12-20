package etithespirit.orimod.common.block.other;

import com.google.common.collect.ImmutableList;
import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.StaticData;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class GorlekNetheriteAlloyBlock extends Block implements IToolRequirementProvider {
	
	public GorlekNetheriteAlloyBlock() {
		super(
			Properties.of(Material.HEAVY_METAL)
				.isRedstoneConductor(StaticData.ALWAYS_TRUE)
				.strength(18, 30000)
				.requiresCorrectToolForDrops()
				.sound(SoundType.NETHERITE_BLOCK)
		);
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return ImmutableList.of(
			BlockTags.MINEABLE_WITH_PICKAXE,
			BlockTags.NEEDS_DIAMOND_TOOL,
			BlockTags.IMPERMEABLE,
			BlockTags.DRAGON_IMMUNE,
			BlockTags.WITHER_IMMUNE,
			BlockTags.DAMPENS_VIBRATIONS,
			BlockTags.OCCLUDES_VIBRATION_SIGNALS
		);
	}
}
