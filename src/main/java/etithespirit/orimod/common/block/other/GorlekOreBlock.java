package etithespirit.orimod.common.block.other;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class GorlekOreBlock extends Block implements IToolRequirementProvider {
	public GorlekOreBlock() {
		super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(1.2f, 30f));
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY;
	}
}
