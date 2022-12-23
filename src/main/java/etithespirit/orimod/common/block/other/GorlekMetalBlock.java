package etithespirit.orimod.common.block.other;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.tags.PresetBlockTags;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.List;

public class GorlekMetalBlock extends Block implements IToolRequirementProvider, IBlockItemPropertiesProvider {
	public GorlekMetalBlock() {
		super(Properties.of(Material.HEAVY_METAL, MaterialColor.COLOR_GRAY).strength(2f, 100f).requiresCorrectToolForDrops().isRedstoneConductor(StaticData.ALWAYS_TRUE));
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return List.of(
			BlockTags.MINEABLE_WITH_PICKAXE,
			BlockTags.NEEDS_IRON_TOOL
		);
	}
	
	@Override
	public Item.Properties getPropertiesOfItem() {
		return (new Item.Properties()).tab(OriModCreativeModeTabs.BLOCKS);
	}
}
