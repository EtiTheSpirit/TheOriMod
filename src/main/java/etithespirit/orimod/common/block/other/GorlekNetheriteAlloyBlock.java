package etithespirit.orimod.common.block.other;

import com.google.common.collect.ImmutableList;
import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

public class GorlekNetheriteAlloyBlock extends Block implements IBlockTagProvider, IBlockItemPropertiesProvider {
	
	public GorlekNetheriteAlloyBlock() {
		super(
			Properties.copy(Blocks.NETHERITE_BLOCK)
				.isRedstoneConductor(StaticData.ALWAYS_TRUE)
				.strength(60, 4000)
				.requiresCorrectToolForDrops()
				.sound(SoundType.NETHERITE_BLOCK)
				// To future Xan: The call to .sound on the properties is accompanied by an override in BlockToMaterialBinding (you tell this block specifically to be SpiritMaterial.INHERITED)
				// Don't go removing the sound type and then start wondering why it's not playing the proper sound. That's why.
		);
	}
	
	@Override
	public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
		return ImmutableList.of(
			BlockTags.MINEABLE_WITH_PICKAXE,
			BlockTags.NEEDS_DIAMOND_TOOL,
			BlockTags.IMPERMEABLE,
			BlockTags.DRAGON_IMMUNE,
			BlockTags.WITHER_IMMUNE,
			BlockTags.DAMPENS_VIBRATIONS,
			BlockTags.OCCLUDES_VIBRATION_SIGNALS,
			BlockTags.BEACON_BASE_BLOCKS
		);
	}
	
	@Override
	public Item.Properties getPropertiesOfItem() {
		return (new Item.Properties()).fireResistant().rarity(Rarity.EPIC).tab(OriModCreativeModeTabs.BLOCKS);
	}
}
