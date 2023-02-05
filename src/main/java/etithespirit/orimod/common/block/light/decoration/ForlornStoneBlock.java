package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.common.tags.PresetBlockTags;
import etithespirit.orimod.registry.world.BlockRegistry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

public class ForlornStoneBlock extends Block implements IBlockTagProvider, IBlockItemPropertiesProvider {

	public ForlornStoneBlock() {
		super(Properties.of(Material.STONE).strength(0.8f, 80f).requiresCorrectToolForDrops());
	}
	
	
	@Override
	public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
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
	
	
	public static class Slab extends SlabBlock implements IBlockTagProvider, IBlockItemPropertiesProvider {
		
		public Slab() {
			super(Properties.of(Material.STONE).strength(0.8f, 80f).requiresCorrectToolForDrops());
		}
		
		
		@Override
		public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
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
	
	public static class Stairs extends StairBlock implements IBlockTagProvider, IBlockItemPropertiesProvider {
		
		public Stairs() {
			super(
				() -> BlockRegistry.FORLORN_STONE.get().defaultBlockState(),
				Properties.of(Material.STONE).strength(0.8f, 80f).requiresCorrectToolForDrops()
			);
		}
		
		
		@Override
		public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
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
	
	public static class Wall extends WallBlock implements IBlockTagProvider, IBlockItemPropertiesProvider {
		
		
		public Wall() {
			super(Properties.of(Material.STONE).strength(0.8f, 80f).requiresCorrectToolForDrops());
		}
		
		@Override
		public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
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
}
