package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.ILightBlockIdentifier;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.util.PresetBlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ForlornStoneBlock extends Block implements ILightBlockIdentifier, IToolRequirementProvider, IBlockItemPropertiesProvider {

	public ForlornStoneBlock() {
		super(Properties.copy(Blocks.STONE).destroyTime(0.8f).requiresCorrectToolForDrops());
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY;
	}
	
	@Override
	public Item.Properties getPropertiesOfItem() {
		return new Item.Properties().tab(OriModCreativeModeTabs.SPIRIT_DECORATION);
	}
}
