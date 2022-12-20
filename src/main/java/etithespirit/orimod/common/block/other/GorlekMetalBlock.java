package etithespirit.orimod.common.block.other;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class GorlekMetalBlock extends Block implements IToolRequirementProvider {
	public GorlekMetalBlock() {
		super(Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(2f, 100f).requiresCorrectToolForDrops().isRedstoneConductor(StaticData.ALWAYS_TRUE));
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY;
	}
}
