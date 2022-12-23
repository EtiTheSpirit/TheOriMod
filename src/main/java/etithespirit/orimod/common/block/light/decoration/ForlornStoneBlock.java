package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

public class ForlornStoneBlock extends Block implements IToolRequirementProvider, IBlockItemPropertiesProvider {

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
		return StaticData.getNameAsLight(super.getName());
	}
	
}
