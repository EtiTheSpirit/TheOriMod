package etithespirit.orimod.datagen.recipe;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.advancements.AdvancementRegistry;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import net.minecraft.advancements.CriterionTriggerInstance;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class GenerateRecipes extends RecipeProvider {
	public GenerateRecipes(DataGenerator pGenerator) {
		super(pGenerator);
	}
	private AbstractCriterionTriggerInstance becomeSpirit;
	
	
	private void makeCraftingItems(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
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
	}
	
	private void makeHardlightToolsAndCombat(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
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
		
		/// LIGHT TOOLS ///
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
	}
	
	private void makeHardlightArmor(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
		// TODO: Future Xan, playtest this. See if this is the right way to do it.
		// I am debating on turning this into a smithing recipe (diamond + hardlight shard).
		// This makes binding essence obsolete. Could I make my own crafting table of sorts?
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_HELMET.get())
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("BSB")
			.pattern("S S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_CHESTPLATE.get())
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("S S")
			.pattern("BSB")
			.pattern("SSS")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_LEGGINGS.get())
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.pattern("BSB")
			.pattern("S S")
			.pattern("S S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_BOOTS.get())
			.unlockedBy("main_action", becomeSpirit)
			.unlockedBy("get_diamond", AdvancementRegistry.PICKUP_DIAMONDS)
			.define('S', ItemRegistry.HARDLIGHT_SHARD.get())
			.define('B', ItemRegistry.BINDING_ESSENCE.get())
			.define('b', Items.DIAMOND_BOOTS)
			.pattern("BbB")
			.pattern("S S")
		.save(recipeConsumer);
	}
	
	private void makeToolsRequiringGorlekSteel(@NotNull Consumer<FinishedRecipe> recipeConsumer, ItemLike ingot, ItemLike pickaxe, ItemLike shovel, ItemLike axe, ItemLike sword, ItemLike hoe) {
		AbstractCriterionTriggerInstance getGorlekOre = Objects.requireNonNull(AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get());
		
		ShapedRecipeBuilder.shaped(pickaxe)
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('I', ingot)
			.define('S', Items.STICK)
			.pattern("III")
			.pattern(" S ")
			.pattern(" S ")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(shovel)
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('I', ingot)
			.define('S', Items.STICK)
			.pattern("I")
			.pattern("S")
			.pattern("S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(axe)
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('I', ingot)
			.define('S', Items.STICK)
			.pattern("II")
			.pattern("IS")
			.pattern(" S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(sword)
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('I', ingot)
			.define('S', Items.STICK)
			.pattern("I")
			.pattern("I")
			.pattern("S")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(hoe)
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('I', ingot)
			.define('S', Items.STICK)
			.pattern("II")
			.pattern(" S")
			.pattern(" S")
		.save(recipeConsumer);
	}
	
	private void makeGorlekSteelToolsAndCombat(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
		makeToolsRequiringGorlekSteel(
			recipeConsumer,
			ItemRegistry.GORLEK_STEEL_INGOT.get(),
			ItemRegistry.GORLEK_STEEL_PICKAXE.get(),
			ItemRegistry.GORLEK_STEEL_SHOVEL.get(),
			ItemRegistry.GORLEK_STEEL_AXE.get(),
			ItemRegistry.GORLEK_STEEL_SWORD.get(),
			ItemRegistry.GORLEK_STEEL_HOE.get()
		);
	}
	
	private void makeGorlekSteelArmor(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
		AbstractCriterionTriggerInstance getGorlekOre = Objects.requireNonNull(AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get());
		
		ShapedRecipeBuilder.shaped(ItemRegistry.GORLEK_STEEL_HELMET.get())
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('G', ItemRegistry.GORLEK_STEEL_INGOT.get())
			.pattern("GGG")
			.pattern("G G")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.GORLEK_STEEL_CHESTPLATE.get())
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('G', ItemRegistry.GORLEK_STEEL_INGOT.get())
			.pattern("G G")
			.pattern("GGG")
			.pattern("GGG")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.GORLEK_STEEL_LEGGINGS.get())
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('G', ItemRegistry.GORLEK_STEEL_INGOT.get())
			.pattern("GGG")
			.pattern("G G")
			.pattern("G G")
		.save(recipeConsumer);
		
		ShapedRecipeBuilder.shaped(ItemRegistry.GORLEK_STEEL_BOOTS.get())
			.unlockedBy("get_gorlek_ore", getGorlekOre)
			.define('G', ItemRegistry.GORLEK_STEEL_INGOT.get())
			.pattern("G G")
			.pattern("G G")
		.save(recipeConsumer);
	}
	
	/**
	 * Allows upgrading <strong>Diamond Armor + Gorlek-Netherite Alloy -&gt; Gorlek-Netherite Alloy Armor</strong> OR <strong>Gorlek Armor + Netherite Ingot -&gt; Gorlek-Netherite Alloy Armor</strong>
	 * @param recipeConsumer
	 * @param diamondGear
	 * @param netheriteGear
	 * @param result
	 */
	private void getGorlekNetheriteUpgrade(@NotNull Consumer<FinishedRecipe> recipeConsumer, Item diamondGear, Item netheriteGear, RegistryObject<Item> result) {
		UpgradeRecipeBuilder.smithing(Ingredient.of(diamondGear), Ingredient.of(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get()), result.get())
			.unlocks("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get()))
			.unlocks("get_netherite_ingot", Objects.requireNonNull(AdvancementRegistry.PICKUP_NETHERITE_INGOTS))
		.save(recipeConsumer, OriMod.rsrc("smith_diamond_into_" + result.getId().getPath()));
		
		UpgradeRecipeBuilder.smithing(Ingredient.of(netheriteGear), Ingredient.of(ItemRegistry.GORLEK_STEEL_INGOT.get()), result.get())
			.unlocks("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get()))
			.unlocks("get_netherite_ingot", Objects.requireNonNull(AdvancementRegistry.PICKUP_NETHERITE_INGOTS))
		.save(recipeConsumer, OriMod.rsrc("smith_netherite_into_" + result.getId().getPath()));
	}
	
	private void makeGorlekNetheriteAlloyToolsAndCombat(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
		// TODO: Require full ingots? Use smithing?
		// FOR NOW: Smithing it is!
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE, ItemRegistry.GORLEK_NETHERITE_ALLOY_PICKAXE);
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL, ItemRegistry.GORLEK_NETHERITE_ALLOY_SHOVEL);
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_AXE, Items.NETHERITE_AXE, ItemRegistry.GORLEK_NETHERITE_ALLOY_AXE);
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD, ItemRegistry.GORLEK_NETHERITE_ALLOY_SWORD);
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_HOE, Items.NETHERITE_HOE, ItemRegistry.GORLEK_NETHERITE_ALLOY_HOE);
	}
	
	private void makeGorlekNetheriteAlloyArmor(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
		// TODO: Require full ingots? Use smithing?
		// FOR NOW: Smithing it is!
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, ItemRegistry.GORLEK_NETHERITE_ALLOY_HELMET);
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE, ItemRegistry.GORLEK_NETHERITE_ALLOY_CHESTPLATE);
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS, ItemRegistry.GORLEK_NETHERITE_ALLOY_LEGGINGS);
		getGorlekNetheriteUpgrade(recipeConsumer, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS, ItemRegistry.GORLEK_NETHERITE_ALLOY_BOOTS);
	}
	
	private void makeForlornStoneBlocks(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
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
	}
	
	private void makeLuxenTechBlocks(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
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
			.define('N', BlockItem.BY_BLOCK.get(Blocks.IRON_BLOCK))
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
	}
	
	private void makeNuggetIngotStorage(@NotNull Consumer<FinishedRecipe> recipeConsumer, RegistryObject<Item> nugget, RegistryObject<Item> ingot, List<Pair<String, CriterionTriggerInstance>> requirements) {
		ShapelessRecipeBuilder builder;
		
		builder = ShapelessRecipeBuilder.shapeless(ingot.get(), 1).requires(nugget.get(), 9);
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlockedBy(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc(ingot.getId().getPath() + "_from_" + nugget.getId().getPath()));
		
		builder = ShapelessRecipeBuilder.shapeless(nugget.get(), 9).requires(ingot.get(), 1);
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlockedBy(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc(nugget.getId().getPath() + "_from_" + ingot.getId().getPath()));
	}
	
	private void makeIngotBlockStorage(@NotNull Consumer<FinishedRecipe> recipeConsumer, RegistryObject<Item> ingot, RegistryObject<Block> block, List<Pair<String, CriterionTriggerInstance>> requirements) {
		ShapelessRecipeBuilder builder;
		
		builder = ShapelessRecipeBuilder.shapeless(block.get(), 1).requires(ingot.get(), 9);
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlockedBy(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc(block.getId().getPath() + "_from_" + ingot.getId().getPath()));
		
		builder = ShapelessRecipeBuilder.shapeless(ingot.get(), 9).requires(block.get(), 1);
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlockedBy(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc(ingot.getId().getPath() + "_from_" + block.getId().getPath()));
	}
	
	private void makeNuggetIngotBlockStorage(@NotNull Consumer<FinishedRecipe> recipeConsumer, RegistryObject<Item> nugget, RegistryObject<Item> ingot, RegistryObject<Block> block, List<Pair<String, CriterionTriggerInstance>> requirements) {
		makeNuggetIngotStorage(recipeConsumer, nugget, ingot, requirements);
		makeIngotBlockStorage(recipeConsumer, ingot, block, requirements);
	}
	
	private void makeBlastAndSmeltOf(@NotNull Consumer<FinishedRecipe> recipeConsumer, RegistryObject<Item> rawOre, RegistryObject<Block> oreBlock, RegistryObject<Item> resultIngot, List<Pair<String, CriterionTriggerInstance>> requirements) {
		SimpleCookingRecipeBuilder builder;
		builder = SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawOre.get()), resultIngot.get(), 1, 200);
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlockedBy(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc(resultIngot.getId().getPath() + "_from_smelting_" + rawOre.getId().getPath()));
		
		builder = SimpleCookingRecipeBuilder.smelting(Ingredient.of(oreBlock.get()), resultIngot.get(), 1, 200);
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlockedBy(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc(resultIngot.getId().getPath() + "_from_smelting_" + oreBlock.getId().getPath()));
		
		builder = SimpleCookingRecipeBuilder.blasting(Ingredient.of(rawOre.get()), resultIngot.get(), 1, 100);
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlockedBy(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc(resultIngot.getId().getPath() + "_from_blasting_" + rawOre.getId().getPath()));
		
		builder = SimpleCookingRecipeBuilder.blasting(Ingredient.of(oreBlock.get()), resultIngot.get(), 1, 100);
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlockedBy(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc(resultIngot.getId().getPath() + "_from_blasting_" + oreBlock.getId().getPath()));
		
	}
	
	private void makeTwoWaySmithing(@NotNull Consumer<FinishedRecipe> recipeConsumer, RegistryObject<Item> a, RegistryObject<Item> b, RegistryObject<Item> out, List<Pair<String, CriterionTriggerInstance>> requirements) {
		// Smith Gorlek Ingot + Netherite Ingot
		UpgradeRecipeBuilder builder;
		
		builder = UpgradeRecipeBuilder.smithing(Ingredient.of(a.get()), Ingredient.of(Items.NETHERITE_INGOT), ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get());
		if (requirements != null) {
			for (Pair<String, CriterionTriggerInstance> data : requirements) {
				builder = builder.unlocks(data.getA(), data.getB());
			}
		}
		builder.save(recipeConsumer, OriMod.rsrc("merge_" + a.getId().getPath() + "_with_" + b.getId().getPath()));
	}
	
	@Override
	protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> recipeConsumer) {
		
		// TODO: Make advancements for all of these!
		becomeSpirit = AdvancementRegistry.BECOME_SPIRIT.createInstance();
		
		makeCraftingItems(recipeConsumer);
		
		makeHardlightToolsAndCombat(recipeConsumer);
		makeHardlightArmor(recipeConsumer);
		
		makeGorlekSteelToolsAndCombat(recipeConsumer);
		makeGorlekSteelArmor(recipeConsumer);
		
		makeGorlekNetheriteAlloyToolsAndCombat(recipeConsumer);
		makeGorlekNetheriteAlloyArmor(recipeConsumer);
		
		makeForlornStoneBlocks(recipeConsumer);
		makeLuxenTechBlocks(recipeConsumer);
		
		makeNuggetIngotBlockStorage(recipeConsumer, ItemRegistry.GORLEK_STEEL_NUGGET, ItemRegistry.GORLEK_STEEL_INGOT, BlockRegistry.GORLEK_STEEL_BLOCK, List.of(new Pair("get_gorlek_ore", AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get())));
		makeIngotBlockStorage(recipeConsumer, ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT, BlockRegistry.GORLEK_NETHERITE_ALLOY_BLOCK, List.of(new Pair("get_gorlek_ore", AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get())));
		makeIngotBlockStorage(recipeConsumer, ItemRegistry.RAW_GORLEK_ORE, BlockRegistry.RAW_GORLEK_ORE_BLOCK, List.of(new Pair("get_gorlek_ore", AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get())));
		
		makeBlastAndSmeltOf(recipeConsumer, ItemRegistry.RAW_GORLEK_ORE, BlockRegistry.GORLEK_ORE, ItemRegistry.GORLEK_STEEL_INGOT, List.of(new Pair("get_gorlek_ore", AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get())));
		
		// Smith Gorlek Ingot + Netherite Ingot
		UpgradeRecipeBuilder.smithing(Ingredient.of(ItemRegistry.GORLEK_STEEL_INGOT.get()), Ingredient.of(Items.NETHERITE_INGOT), ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get())
			.unlocks("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get()))
			.unlocks("get_netherite_ingot", Objects.requireNonNull(AdvancementRegistry.PICKUP_NETHERITE_INGOTS))
			.save(recipeConsumer, OriMod.rsrc("merge_gorlek_and_netherite"));
		
		// Smith Gorlek Ingot + Netherite Ingot (but backwards)
		UpgradeRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_INGOT), Ingredient.of(ItemRegistry.GORLEK_STEEL_INGOT.get()), ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get())
			.unlocks("get_gorlek_ore", Objects.requireNonNull(AdvancementRegistry.GET_PICKUP_ANY_GORLEK_ORE.get()))
			.unlocks("get_netherite_ingot", Objects.requireNonNull(AdvancementRegistry.PICKUP_NETHERITE_INGOTS))
			.save(recipeConsumer, OriMod.rsrc("merge_gorlek_and_netherite_reversed"));
		
		ShapedRecipeBuilder.shaped(ItemRegistry.SPIRIT_APPLE.get())
			.unlockedBy("become_spirit", becomeSpirit)
			.define('L', ItemRegistry.getBlockItemOf(BlockRegistry.HARDLIGHT_GLASS))
			.define('A', Items.GOLDEN_APPLE)
			.pattern("LLL")
			.pattern("LAL")
			.pattern("LLL")
		.save(recipeConsumer);
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICK_SLAB), 6)
			.unlockedBy("become_spirit", becomeSpirit)
			.define('B', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICKS))
			.pattern("BBB")
		.save(recipeConsumer);
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICK_STAIRS), 4)
			.unlockedBy("become_spirit", becomeSpirit)
			.define('B', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICKS))
			.pattern("B  ")
			.pattern("BB ")
			.pattern("BBB")
		.save(recipeConsumer);
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICK_STAIRS), 4)
			.unlockedBy("become_spirit", becomeSpirit)
			.define('B', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICKS))
			.pattern("  B")
			.pattern(" BB")
			.pattern("BBB")
		.save(recipeConsumer, OriMod.rsrc("forlorn_stone_brick_stairs_for_monsters"));
		
		
		ShapedRecipeBuilder.shaped(ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICK_WALL), 6)
			.unlockedBy("become_spirit", becomeSpirit)
			.define('B', ItemRegistry.getBlockItemOf(BlockRegistry.FORLORN_STONE_BRICKS))
			.pattern("BBB")
			.pattern("BBB")
		.save(recipeConsumer);
		
		//ShapedRecipeBuilder.shaped(ItemRegistry.LIGHT_STRONG_HELMET)
		//UpgradeRecipeBuilder.smithing(ItemRegistry.LIGHT_HELMET.get(), )
	}
}
