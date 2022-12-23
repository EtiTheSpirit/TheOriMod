package etithespirit.orimod.datagen.recipe;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.advancements.AdvancementRegistry;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class GenerateRecipes extends RecipeProvider {
	public GenerateRecipes(DataGenerator pGenerator) {
		super(pGenerator);
	}
	
	@Override
	protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
		
		// TODO: Make advancements for all of these!
		
		final AbstractCriterionTriggerInstance becomeSpirit = AdvancementRegistry.BECOME_SPIRIT.createInstance();
		
		/// CRAFTING ITEMS ///
		ShapedRecipeBuilder.shaped(ItemRegistry.HARDLIGHT_SHARD.get(), 16)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('D', Items.DIAMOND)
			.define('A', Items.AMETHYST_SHARD)
			.pattern(" A ")
			.pattern("ADA")
			.pattern(" A ")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.BINDING_ESSENCE.get(), 8)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('G', Items.GLOWSTONE_DUST)
			.define('A', Items.AMETHYST_SHARD)
			.pattern(" A ")
			.pattern("AGA")
			.pattern(" A ")
		.save(recipeConsumer);
		
		/// WEAPONS AND TOOLS ///
		ShapedRecipeBuilder.shaped(ItemRegistry.SPIRIT_ARC.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('D', Items.DIAMOND)
			.define('|', Items.STRING)
			.pattern(" SB")
			.pattern("D |")
			.pattern(" SB")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_SHIELD.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('D', Items.DIAMOND)
			.pattern("SDS")
			.pattern("BDB")
			.pattern("SDS")
			.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LUMO_WAND.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('F', BlockRegistry.FORLORN_STONE.get())
			.pattern("S")
			.pattern("B")
			.pattern("F")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_PICKAXE.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_PICKAXE)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_SHOVEL.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_SHOVEL)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_AXE.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_AXE)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_SWORD.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_SWORD)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_HOE.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_HOE)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		/// BLOCKS ///
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE), 8)
			.unlockedBy("main_action", becomeSpirit)
			.define('D', BlockItem.BY_BLOCK.get(Blocks.POLISHED_DEEPSLATE))
			.define('S', BlockItem.BY_BLOCK.get(Blocks.STONE))
			.pattern("SDS")
			.pattern("DSD")
			.pattern("SDS")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICKS), 4)
			.unlockedBy("main_action", becomeSpirit)
			.define('B', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.pattern("BB")
			.pattern("BB")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_LINE), 8)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('G', BlockItem.BY_BLOCK.get(Blocks.GLASS))
			.pattern("FGF")
			.pattern("FSF")
			.pattern("FGF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_OMNI), 8)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('G', BlockItem.BY_BLOCK.get(Blocks.GLASS))
			.pattern("FGF")
			.pattern("GSG")
			.pattern("FGF")
		.save(recipeConsumer);
		
		/*
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.HARDLIGHT_GLASS), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.pattern("SSS")
			.pattern("SSS")
			.pattern("SSS")
		.save(recipeConsumer);
		 */
		
		ShapelessRecipeBuilder.shapeless(BlockRegistry.HARDLIGHT_GLASS.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.requires(ItemRegistry.HARDLIGHT_SHARD.get(), 9)
		.save(recipeConsumer);
		
		ShapelessRecipeBuilder.shapeless(ItemRegistry.HARDLIGHT_SHARD.get(), 9)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.requires(ItemRegistry.getBlockItemOf(BlockRegistry.HARDLIGHT_GLASS))
		.save(recipeConsumer, new ResourceLocation(OriMod.MODID, "hardlight_block_to_shard"));
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_CONDUIT), 8)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('G', BlockItem.BY_BLOCK.get(Blocks.GLASS))
			.pattern(" F ")
			.pattern("GSG")
			.pattern(" F ")
		.save(recipeConsumer);
		
		ShapelessRecipeBuilder.shapeless(ItemRegistry.getBlockItemOf(BlockRegistry.SOLID_LIGHT_CONDUIT), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.requires(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_CONDUIT))
			.requires(BlockItem.BY_BLOCK.get(Blocks.GLASS))
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_CAPACITOR), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.pattern("FFF")
			.pattern("FSF")
			.pattern("FFF")
		.save(recipeConsumer);
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_TO_REDSTONE_SIGNAL), 4)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('R', Items.REDSTONE)
			.pattern("FRF")
			.pattern("RSR")
			.pattern("FRF")
		.save(recipeConsumer);
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_TO_RF), 2)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('C', Items.COPPER_INGOT)
			.pattern("FCF")
			.pattern("CSC")
			.pattern("FCF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.SOLAR_ENERGY_BLOCK), 2)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('G', BlockItem.BY_BLOCK.get(Blocks.GLOWSTONE))
			.pattern("FGF")
			.pattern("GSG")
			.pattern("FGF")
		.save(recipeConsumer);
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.THERMAL_ENERGY_BLOCK), 2)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('N', Items.NETHERITE_SCRAP)
			.pattern("FNF")
			.pattern("NSN")
			.pattern("FNF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_REPAIR_BOX), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('G', ItemRegistry.getBlockItemOf(BlockRegistry.HARDLIGHT_GLASS))
			.pattern("FFF")
			.pattern("FGF")
			.pattern("FFF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_HELMET.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("BSB")
			.pattern("S S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_CHESTPLATE.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("S S")
			.pattern("BSB")
			.pattern("SSS")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_LEGS.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("BSB")
			.pattern("S S")
			.pattern("S S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_BOOTS.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('b', Items.DIAMOND_BOOTS)
			.pattern("BbB")
			.pattern("S S")
		.save(recipeConsumer);
		
		// Smith Gorlek Ingot + Netherite Ingot
		UpgradeRecipeBuilder.smithing(Ingredient.of(ItemRegistry.GORLEK_INGOT.get()), Ingredient.of(Items.NETHERITE_INGOT), ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get())
			.unlocks("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.unlocks("get_netherite_ingot", Objects.requireNonNull(AdvancementRegistry.PICKUP_NETHERITE_INGOTS))
		.save(recipeConsumer, OriMod.rsrc("merge_gorlek_and_netherite"));
		
		// Smelt Raw Gorlek Ore
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemRegistry.RAW_GORLEK_ORE.get()), ItemRegistry.GORLEK_INGOT.get(), 1, 200)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
		.save(recipeConsumer, OriMod.rsrc("gorlek_ingot_from_raw_gorlek_ore"));
		
		// Smelt Gorlek Ore Block
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BlockRegistry.GORLEK_ORE.get()), ItemRegistry.GORLEK_INGOT.get(), 1,200)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
		.save(recipeConsumer, OriMod.rsrc("gorlek_ingot_from_gorlek_ore_block"));
		
		// Smelt Raw Gorlek Ore (Blast Furnace)
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(ItemRegistry.RAW_GORLEK_ORE.get()), ItemRegistry.GORLEK_INGOT.get(), 1, 100)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
		.save(recipeConsumer, OriMod.rsrc("gorlek_ingot_from_raw_gorlek_ore_blast_furnace"));
		
		// Smelt Gorlek Ore Block (Blast Furnace)
		SimpleCookingRecipeBuilder.blasting(Ingredient.of(BlockRegistry.GORLEK_ORE.get()), ItemRegistry.GORLEK_INGOT.get(), 1,100)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
		.save(recipeConsumer, OriMod.rsrc("gorlek_ingot_from_gorlek_ore_block_blast_furnace"));
		
		// TWO-WAY: INGOT <-> NUGGET
		ShapelessRecipeBuilder.shapeless(ItemRegistry.GORLEK_INGOT.get(), 1)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.requires(ItemRegistry.GORLEK_NUGGET.get(), 9)
		.save(recipeConsumer, OriMod.rsrc("gorlek_ingot_from_nuggets"));
		
		ShapelessRecipeBuilder.shapeless(ItemRegistry.GORLEK_NUGGET.get(), 9)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.requires(ItemRegistry.GORLEK_INGOT.get())
		.save(recipeConsumer, OriMod.rsrc("gorlek_nuggets_from_ingot"));
		
		// TWO-WAY: RAW ORE <-> RAW ORE BLOCK
		ShapelessRecipeBuilder.shapeless(ItemRegistry.RAW_GORLEK_ORE.get(), 9)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.requires(BlockRegistry.RAW_GORLEK_ORE_BLOCK.get(), 1)
		.save(recipeConsumer, OriMod.rsrc("raw_gorlek_ore_from_block"));
		
		ShapelessRecipeBuilder.shapeless(BlockRegistry.RAW_GORLEK_ORE_BLOCK.get(), 1)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.requires(ItemRegistry.RAW_GORLEK_ORE.get(), 9)
		.save(recipeConsumer, OriMod.rsrc("raw_gorlek_block_from_ores"));
		
		// TWO-WAY: GORLEK METAL BLOCK <-> GORLEK INGOT
		ShapelessRecipeBuilder.shapeless(BlockRegistry.GORLEK_METAL_BLOCK.get(), 1)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.requires(ItemRegistry.GORLEK_INGOT.get(), 9)
		.save(recipeConsumer, OriMod.rsrc("gorlek_block_from_ingots"));
		
		ShapelessRecipeBuilder.shapeless(ItemRegistry.GORLEK_INGOT.get(), 9)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.requires(BlockRegistry.GORLEK_METAL_BLOCK.get())
		.save(recipeConsumer, OriMod.rsrc("gorlek_ingots_from_block"));
		
		// TWO-WAY: GORLEK-NETHERITE ALLOY BLOCK <-> INGOT
		ShapelessRecipeBuilder.shapeless(BlockRegistry.GORLEK_NETHERITE_ALLOY_BLOCK.get(), 1)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.unlockedBy("get_netherite_ingot", Objects.requireNonNull(AdvancementRegistry.PICKUP_NETHERITE_INGOTS))
			.requires(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get(), 9)
		.save(recipeConsumer, OriMod.rsrc("gorlek_netherite_alloy_block_from_ingots"));
	
		ShapelessRecipeBuilder.shapeless(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get(), 9)
			.unlockedBy("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.PICKUP_GORLEK_ORE.get()))
			.unlockedBy("get_netherite_ingot", Objects.requireNonNull(AdvancementRegistry.PICKUP_NETHERITE_INGOTS))
			.requires(BlockRegistry.GORLEK_NETHERITE_ALLOY_BLOCK.get())
		.save(recipeConsumer, OriMod.rsrc("gorlek_netherite_alloy_ingots_from_block"));
		
		
		
		//ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_STRONG_HELMET)
		//UpgradeRecipeBuilder.smithing(ItemRegistry.LIGHT_HELMET.get(), )
	}
}
