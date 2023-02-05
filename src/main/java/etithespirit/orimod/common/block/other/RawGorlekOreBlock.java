package etithespirit.orimod.common.block.other;

import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.List;

public class RawGorlekOreBlock extends Block implements IBlockTagProvider, IBlockItemPropertiesProvider {
	public RawGorlekOreBlock() {
		super(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(1.8f, 40f));
	}
	
	@Override
	public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
		return List.of(
			BlockTags.MINEABLE_WITH_PICKAXE,
			BlockTags.NEEDS_IRON_TOOL
		);
	}
	
	@Override
	public Item.Properties getPropertiesOfItem() {
		return (new Item.Properties()).tab(OriModCreativeModeTabs.SPIRIT_DECORATION);
	}
}
