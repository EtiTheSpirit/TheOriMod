package etithespirit.orimod.spiritmaterial.implementation;

import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import etithespirit.orimod.common.material.ExtendedMaterials;
import etithespirit.orimod.spiritmaterial.data.SpiritMaterialContainer;
import etithespirit.orimod.spiritmaterial.defaults.DefaultAcquisitionImplementations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;

public final class VanillaBindings {
	
	public static void initialize() {
		SpiritMaterialContainer ctr = SpiritMaterialContainer.getForMinecraft();
		
		ctr.registerMaterial(Material.AIR, SpiritMaterial.NULL);
		ctr.registerMaterial(Material.BAMBOO, SpiritMaterial.WOOD_DRY);
		ctr.registerMaterial(Material.BAMBOO_SAPLING, SpiritMaterial.GRASS_HARD);
		ctr.registerMaterial(Material.STRUCTURAL_AIR, SpiritMaterial.INHERITED);
		ctr.registerMaterial(Material.BUBBLE_COLUMN, SpiritMaterial.INHERITED);
		ctr.registerMaterial(Material.BARRIER, SpiritMaterial.HARDLIGHT_GLASS);
		ctr.registerMaterial(Material.CACTUS, SpiritMaterial.SHROOM);
		ctr.registerMaterial(Material.CAKE, SpiritMaterial.SHROOM);
		ctr.registerMaterial(Material.CLAY, SpiritMaterial.SLIMY);
		ctr.registerMaterial(Material.CLOTH_DECORATION, SpiritMaterial.WOOL);
		ctr.registerMaterial(Material.DECORATION, SpiritMaterial.INHERITED);
		ctr.registerMaterial(Material.DIRT, SpiritMaterial.SAND);
		ctr.registerMaterial(Material.EGG, SpiritMaterial.ICE);
		ctr.registerMaterial(Material.EXPLOSIVE, SpiritMaterial.SAND);
		ctr.registerMaterial(Material.FIRE, SpiritMaterial.INHERITED);
		ctr.registerMaterial(Material.GLASS, SpiritMaterial.ROCK);
		ctr.registerMaterial(Material.PLANT, SpiritMaterial.GRASS_SOFT);
		ctr.registerMaterial(Material.HEAVY_METAL, SpiritMaterial.METAL);
		ctr.registerMaterial(Material.ICE, SpiritMaterial.ICE);
		ctr.registerMaterial(Material.ICE_SOLID, SpiritMaterial.ICE);
		ctr.registerMaterial(Material.LAVA, SpiritMaterial.INHERITED);
		ctr.registerMaterial(Material.LEAVES, SpiritMaterial.GRASS_SOFT);
		ctr.registerMaterial(Material.METAL, SpiritMaterial.METAL);
		ctr.registerMaterial(Material.PISTON, SpiritMaterial.ROCK);
		ctr.registerMaterial(Material.REPLACEABLE_PLANT, SpiritMaterial.GRASS_SOFT);
		ctr.registerMaterial(Material.PORTAL, SpiritMaterial.HARDLIGHT_GLASS);
		ctr.registerMaterial(Material.REPLACEABLE_FIREPROOF_PLANT, SpiritMaterial.GRASS_HARD);
		ctr.registerMaterial(Material.WATER_PLANT, SpiritMaterial.SHROOM);
		ctr.registerMaterial(Material.SAND, SpiritMaterial.SAND);
		ctr.registerMaterial(Material.SHULKER_SHELL, SpiritMaterial.ROCK);
		ctr.registerMaterial(Material.SNOW, SpiritMaterial.SNOW);
		ctr.registerMaterial(Material.SPONGE, SpiritMaterial.WOOL);
		ctr.registerMaterial(Material.STONE, SpiritMaterial.ROCK);
		ctr.registerMaterial(Material.TOP_SNOW, SpiritMaterial.SNOW);
		ctr.registerMaterial(Material.VEGETABLE, SpiritMaterial.SHROOM);
		ctr.registerMaterial(Material.REPLACEABLE_WATER_PLANT, SpiritMaterial.SHROOM);
		ctr.registerMaterial(Material.WEB, SpiritMaterial.INHERITED);
		ctr.registerMaterial(Material.WOOL, SpiritMaterial.WOOL);
		
		// CUSTOM MATERIAL PREDICATES
		ctr.registerMaterial(Material.NETHER_WOOD, DefaultAcquisitionImplementations::getWoodMaterial, SpiritMaterial.WOOD_DRY);
		ctr.registerMaterial(Material.WOOD, DefaultAcquisitionImplementations::getWoodMaterial, SpiritMaterial.WOOD_DRY);
		ctr.registerMaterial(Material.WATER, DefaultAcquisitionImplementations::getWaterMaterial);
		
		// STATE BINDINGS
		ctr.registerState(() -> Blocks.GRASS_BLOCK.defaultBlockState().setValue(BlockStateProperties.SNOWY, Boolean.TRUE), SpiritMaterial.SNOW);
		
		// VANILLA BLOCKS
		// Grass variants & General Overworld
		ctr.registerBlock(() -> Blocks.PODZOL, SpiritMaterial.SNOW);
		ctr.registerBlock(() -> Blocks.GRASS_BLOCK, SpiritMaterial.GRASS_SOFT); // wat
		ctr.registerBlock(() -> Blocks.MYCELIUM, SpiritMaterial.SHROOM); // from grass
		ctr.registerBlock(() -> Blocks.GRAVEL, SpiritMaterial.GRAVEL_DRY); // from sand
		
		ctr.registerBlock(() -> Blocks.SAND, SpiritMaterial.SAND);
		ctr.registerBlock(() -> Blocks.SANDSTONE, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.CHISELED_SANDSTONE, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SANDSTONE_SLAB, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SANDSTONE_STAIRS, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SANDSTONE_WALL, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.CUT_SANDSTONE, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.CUT_SANDSTONE_SLAB, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SMOOTH_SANDSTONE, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SMOOTH_SANDSTONE_SLAB, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SMOOTH_SANDSTONE_STAIRS, SpiritMaterial.CERAMIC_BROKEN);
		
		ctr.registerBlock(() -> Blocks.RED_SAND, SpiritMaterial.SAND);
		ctr.registerBlock(() -> Blocks.RED_SANDSTONE, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.CHISELED_RED_SANDSTONE, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.RED_SANDSTONE_SLAB, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.RED_SANDSTONE_STAIRS, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.RED_SANDSTONE_WALL, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.CUT_RED_SANDSTONE, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.CUT_RED_SANDSTONE_SLAB, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SMOOTH_RED_SANDSTONE, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SMOOTH_RED_SANDSTONE_SLAB, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.SMOOTH_RED_SANDSTONE_STAIRS, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.REDSTONE_BLOCK, SpiritMaterial.GRAVEL_DRY);
		
		// 1.19.2 added some wack blocks man
		ctr.registerBlock(() -> Blocks.MANGROVE_LOG, SpiritMaterial.WOOD_MOSSY);
		ctr.registerBlock(() -> Blocks.INFESTED_STONE, SpiritMaterial.ROCK);
		ctr.registerBlock(() -> Blocks.INFESTED_CHISELED_STONE_BRICKS, SpiritMaterial.ROCK);
		ctr.registerBlock(() -> Blocks.INFESTED_COBBLESTONE, SpiritMaterial.ROCK);
		ctr.registerBlock(() -> Blocks.INFESTED_DEEPSLATE, SpiritMaterial.ROCK);
		ctr.registerBlock(() -> Blocks.INFESTED_CRACKED_STONE_BRICKS, SpiritMaterial.ROCK);
		ctr.registerBlock(() -> Blocks.INFESTED_MOSSY_STONE_BRICKS, SpiritMaterial.ROCK);
		ctr.registerBlock(() -> Blocks.INFESTED_STONE_BRICKS, SpiritMaterial.ROCK);
		
		
		// Shrooms (there is a fungus among us)
		ctr.registerBlock(() -> Blocks.RED_MUSHROOM_BLOCK, SpiritMaterial.SHROOM); // from wood
		ctr.registerBlock(() -> Blocks.BROWN_MUSHROOM_BLOCK, SpiritMaterial.SHROOM); // from wood
		ctr.registerBlock(() -> Blocks.MUSHROOM_STEM, SpiritMaterial.SHROOM); // from wood
		ctr.registerBlock(() -> Blocks.CHORUS_FLOWER, SpiritMaterial.SHROOM); // from wood
		ctr.registerBlock(() -> Blocks.CHORUS_PLANT, SpiritMaterial.SHROOM); // from wood
		ctr.registerBlock(() -> Blocks.NETHER_WART_BLOCK, SpiritMaterial.SHROOM);
		ctr.registerBlock(() -> Blocks.CRIMSON_FUNGUS, SpiritMaterial.SHROOM);
		ctr.registerBlock(() -> Blocks.WARPED_FUNGUS, SpiritMaterial.SHROOM);
		
		// Ores n Spawners
		ctr.registerBlock(() -> Blocks.SPAWNER, SpiritMaterial.METAL); // from stone
		ctr.registerBlock(() -> Blocks.EMERALD_BLOCK, SpiritMaterial.ROCK); // from metal
		ctr.registerBlock(() -> Blocks.LAPIS_BLOCK, SpiritMaterial.ROCK); // from metal
		ctr.registerBlock(() -> Blocks.DIAMOND_BLOCK, SpiritMaterial.ROCK); // from metal
		
		// The Netha (i do not have a nether pass)
		ctr.registerBlock(() -> Blocks.NETHER_QUARTZ_ORE, SpiritMaterial.INHERITED); // from stone
		ctr.registerBlock(() -> Blocks.NETHER_GOLD_ORE, SpiritMaterial.INHERITED); // from stone
		ctr.registerBlock(() -> Blocks.NETHERRACK, SpiritMaterial.INHERITED); // duh
		ctr.registerBlock(() -> Blocks.BASALT, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.POLISHED_BASALT, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.SMOOTH_BASALT, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.GILDED_BLACKSTONE, SpiritMaterial.INHERITED);
		ctr.registerBlock(() -> Blocks.NETHERITE_BLOCK, SpiritMaterial.INHERITED); // from metal
		ctr.registerBlock(() -> Blocks.ANCIENT_DEBRIS, SpiritMaterial.INHERITED); // from metal
		
		// Command Blocks
		ctr.registerBlock(() -> Blocks.COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		ctr.registerBlock(() -> Blocks.CHAIN_COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		ctr.registerBlock(() -> Blocks.REPEATING_COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		
		// Redstone
		ctr.registerBlock(() -> Blocks.DAYLIGHT_DETECTOR, SpiritMaterial.ROCK); // from wood
		
		// Da Goop & Da Bees
		ctr.registerBlock(() -> Blocks.CLAY, SpiritMaterial.ROCK);
		ctr.registerBlock(() -> Blocks.SLIME_BLOCK, SpiritMaterial.SLIMY);
		ctr.registerBlock(() -> Blocks.HONEY_BLOCK, SpiritMaterial.SLIMY);
		ctr.registerBlock(() -> Blocks.HONEYCOMB_BLOCK, SpiritMaterial.SHROOM); // NOT slimy
		ctr.registerBlock(() -> Blocks.BEE_NEST, SpiritMaterial.SHROOM); // from wood
		
		
		// B R I C K S
		ctr.registerBlock(() -> Blocks.BRICKS, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.BRICK_SLAB, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.BRICK_STAIRS, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.BRICK_WALL, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.NETHER_BRICKS, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.NETHER_BRICK_SLAB, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.NETHER_BRICK_STAIRS, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.NETHER_BRICK_WALL, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.NETHER_BRICK_FENCE, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.CHISELED_NETHER_BRICKS, SpiritMaterial.CERAMIC_SOLID);
		ctr.registerBlock(() -> Blocks.CRACKED_NETHER_BRICKS, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.RED_NETHER_BRICKS, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.RED_NETHER_BRICK_STAIRS, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.RED_NETHER_BRICK_SLAB, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerBlock(() -> Blocks.RED_NETHER_BRICK_WALL, SpiritMaterial.CERAMIC_BROKEN);
		
		
		// outsourced to ungengengingan vilag (twomad home)
		ctr.registerBlock(() -> Blocks.TARGET, SpiritMaterial.WOOL); // from wood -- literal target block (like you shoot arrows at it, not a "goal")
		ctr.registerBlock(() -> Blocks.DRIED_KELP_BLOCK, SpiritMaterial.GRASS_HARD); // from shroom
		ctr.registerBlock(() -> Blocks.SMITHING_TABLE, SpiritMaterial.METAL); // from wood
		ctr.registerBlock(() -> Blocks.HAY_BLOCK, SpiritMaterial.GRASS_HARD); // from ... wool? forgot tbh
		ctr.registerBlock(() -> Blocks.BLACKSTONE, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.BLACKSTONE_SLAB, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.BLACKSTONE_STAIRS, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.BLACKSTONE_WALL, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.POLISHED_BLACKSTONE, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.POLISHED_BLACKSTONE_BRICKS, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.POLISHED_BLACKSTONE_BRICK_WALL, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.CHISELED_POLISHED_BLACKSTONE, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.GILDED_BLACKSTONE, SpiritMaterial.ASH);
		ctr.registerBlock(() -> Blocks.GRINDSTONE, SpiritMaterial.ROCK); // For some reason this was metal by default.
		
		ctr.registerTag(BlockTags.ICE, SpiritMaterial.ICE);
		ctr.registerTag(BlockTags.SNOW, SpiritMaterial.SNOW);
		ctr.registerTag(BlockTags.SAND, SpiritMaterial.SAND);
		ctr.registerTag(BlockTags.DIRT, SpiritMaterial.SAND);
		ctr.registerTag(BlockTags.LOGS, DefaultAcquisitionImplementations::getWoodMaterial);
		ctr.registerTag(BlockTags.LEAVES, SpiritMaterial.GRASS_SOFT);
		ctr.registerTag(BlockTags.TERRACOTTA, SpiritMaterial.CERAMIC_SOLID);
		
		// FORGE OFFICIAL:
		TagKey<Block> forgeCobblestone = BlockTags.create(new ResourceLocation("forge", "cobblestone"));
		TagKey<Block> forgeGlass = BlockTags.create(new ResourceLocation("forge", "glass"));
		TagKey<Block> forgeGravel = BlockTags.create(new ResourceLocation("forge", "gravel"));
		TagKey<Block> forgeSand = BlockTags.create(new ResourceLocation("forge", "sand"));
		TagKey<Block> forgeSandstone = BlockTags.create(new ResourceLocation("forge", "sandstone"));
		TagKey<Block> forgeStone = BlockTags.create(new ResourceLocation("forge", "stone"));
		
		ctr.registerTag(forgeCobblestone, SpiritMaterial.ROCK);
		ctr.registerTag(forgeGlass, SpiritMaterial.ROCK);
		ctr.registerTag(forgeGravel, SpiritMaterial.GRAVEL_DRY);
		ctr.registerTag(forgeSand, SpiritMaterial.SAND);
		ctr.registerTag(forgeSandstone, SpiritMaterial.CERAMIC_BROKEN);
		ctr.registerTag(forgeStone, SpiritMaterial.ROCK);
		
		// FCW:
		// (nothing here yet)
		
		
	}
	
}
