package etithespirit.orimod.datagen.recipe;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.AdvancementRegistry;
import etithespirit.orimod.registry.BlockRegistry;
import etithespirit.orimod.registry.ItemRegistry;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class GenerateRecipes extends RecipeProvider {
	public GenerateRecipes(DataGenerator pGenerator) {
		super(pGenerator);
	}
	
	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
		
		// TODO: Make advancements for all of these!
		
		final AbstractCriterionTriggerInstance becomeSpirit = AdvancementRegistry.BECOME_SPIRIT.createInstance();
		
		/// CRAFTING ITEMS ///
		ShapedRecipeBuilder.shaped(ItemRegistry.HARDLIGHT_SHARD.get(), 16)
			.unlockedBy("main_action", becomeSpirit)
			.define('D', Items.DIAMOND)
			.define('A', Items.AMETHYST_SHARD)
			.pattern(" A ")
			.pattern("ADA")
			.pattern(" A ")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.BINDING_ESSENCE.get(), 8)
			.unlockedBy("main_action", becomeSpirit)
			.define('G', Items.GLOWSTONE_DUST)
			.define('A', Items.AMETHYST_SHARD)
			.pattern(" A ")
			.pattern("AGA")
			.pattern(" A ")
		.save(recipeConsumer);
		
		/// WEAPONS AND TOOLS ///
		ShapedRecipeBuilder.shaped(ItemRegistry.SPIRIT_ARC.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
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
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('D', Items.DIAMOND)
			.pattern("SDS")
			.pattern("BDB")
			.pattern("SDS")
			.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LUMO_WAND.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('F', BlockRegistry.FORLORN_STONE.get())
			.pattern("S")
			.pattern("B")
			.pattern("F")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_PICKAXE.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_PICKAXE)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_SHOVEL.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_SHOVEL)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_AXE.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_AXE)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_SWORD.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('P', Items.DIAMOND_SWORD)
			.pattern("BSB")
			.pattern("SPS")
			// No third row
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_HOE.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
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
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('G', BlockItem.BY_BLOCK.get(Blocks.GLASS))
			.pattern("FGF")
			.pattern("FSF")
			.pattern("FGF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_OMNI), 8)
			.unlockedBy("main_action", becomeSpirit)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('G', BlockItem.BY_BLOCK.get(Blocks.GLASS))
			.pattern("FGF")
			.pattern("GSG")
			.pattern("FGF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.HARDLIGHT_GLASS), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.pattern("SSS")
			.pattern("SSS")
			.pattern("SSS")
		.save(recipeConsumer);
		
		ShapelessRecipeBuilder.shapeless(ItemRegistry.HARDLIGHT_SHARD.get(), 9)
			.unlockedBy("main_action", becomeSpirit)
			.requires(ItemRegistry.getBlockItemOf(BlockRegistry.HARDLIGHT_GLASS))
		.save(recipeConsumer, new ResourceLocation(OriMod.MODID, "hardlight_block_to_shard"));
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_CONDUIT), 8)
			.unlockedBy("main_action", becomeSpirit)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('G', BlockItem.BY_BLOCK.get(Blocks.GLASS))
			.pattern(" F ")
			.pattern("GSG")
			.pattern(" F ")
		.save(recipeConsumer);
		
		ShapelessRecipeBuilder.shapeless(ItemRegistry.getBlockItemOf(BlockRegistry.SOLID_LIGHT_CONDUIT), 1)
			.unlockedBy("main_action", becomeSpirit)
			.requires(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_CONDUIT))
			.requires(BlockItem.BY_BLOCK.get(Blocks.GLASS))
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_CAPACITOR), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.pattern("FFF")
			.pattern("FSF")
			.pattern("FFF")
		.save(recipeConsumer);
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_TO_REDSTONE_SIGNAL), 4)
			.unlockedBy("main_action", becomeSpirit)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('R', Items.REDSTONE)
			.pattern("FRF")
			.pattern("RSR")
			.pattern("FRF")
		.save(recipeConsumer);
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_TO_RF), 2)
			.unlockedBy("main_action", becomeSpirit)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('C', Items.COPPER_INGOT)
			.pattern("FCF")
			.pattern("CSC")
			.pattern("FCF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.SOLAR_ENERGY_BLOCK), 2)
			.unlockedBy("main_action", becomeSpirit)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('G', BlockItem.BY_BLOCK.get(Blocks.GLOWSTONE))
			.pattern("FGF")
			.pattern("GSG")
			.pattern("FGF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.LIGHT_REPAIR_BOX), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('F', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE))
			.define('G', ItemRegistry.getBlockItemOf(BlockRegistry.HARDLIGHT_GLASS))
			.pattern("FFF")
			.pattern("FGF")
			.pattern("FFF")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_HELMET.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("BSB")
			.pattern("S S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_CHESTPLATE.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("S S")
			.pattern("BSB")
			.pattern("SSS")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_LEGS.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("BSB")
			.pattern("S S")
			.pattern("S S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_BOOTS.get(), 1)
			.unlockedBy("main_action", becomeSpirit)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("B B")
			.pattern("S S")
		.save(recipeConsumer);
	}
}
