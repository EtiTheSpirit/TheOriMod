package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class ForlornStoneBlock extends Block implements IBlockTagProvider, IBlockItemPropertiesProvider {

	public ForlornStoneBlock() {
		super(Properties.of(Material.STONE).strength(0.8f, 80f).requiresCorrectToolForDrops());
	}
	
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
	
	@Override
	public Item.Properties getPropertiesOfItem() {
		return new Item.Properties().tab(OriModCreativeModeTabs.SPIRIT_DECORATION);
	}
	
	@Override
	public MutableComponent getName() {
		return SpiritItemCustomizations.getNameAsLight(super.getName());
	}
	
}
