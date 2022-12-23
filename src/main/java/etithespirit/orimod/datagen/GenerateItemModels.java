package etithespirit.orimod.datagen;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

/**
 * Generates models associated with items.
 * @author Eti
 */
public final class GenerateItemModels extends ItemModelProvider {
	
	/***
	 * Create the Item Model Generator
	 * @param generator The generator itself
	 * @param existingFileHelper A tool for accessing files.
	 */
	public GenerateItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, OriMod.MODID, existingFileHelper);
	}
	
	@Override
	protected void registerModels() {
		OriMod.LOG.printf(Level.INFO, "Starting item model generation.");
		//generateToolItem(ItemRegistry.LIGHT_TOOL);
		generateToolItem(ItemRegistry.LUMO_WAND, "tools");
		generateShieldItem(ItemRegistry.LIGHT_SHIELD, "combat");
		generateItem(ItemRegistry.POISON_BUCKET, "fluid");
		generateBowItem(ItemRegistry.SPIRIT_ARC, "combat");
		generateToolItem(ItemRegistry.LIGHT_SWORD, "combat");
		generateToolItem(ItemRegistry.LIGHT_PICKAXE, "tools");
		generateToolItem(ItemRegistry.LIGHT_SHOVEL, "tools");
		generateToolItem(ItemRegistry.LIGHT_AXE, "tools");
		generateToolItem(ItemRegistry.LIGHT_HOE, "tools");
		generateItem(ItemRegistry.HARDLIGHT_SHARD, "crafting");
		generateItem(ItemRegistry.BINDING_ESSENCE, "crafting");
		generateItem(ItemRegistry.LARGE_LIGHT_LENS, "crafting");
		
		generateItem(ItemRegistry.LIGHT_HELMET, "armor");
		generateItem(ItemRegistry.LIGHT_CHESTPLATE, "armor");
		generateItem(ItemRegistry.LIGHT_LEGS, "armor");
		generateItem(ItemRegistry.LIGHT_BOOTS, "armor");
		
		generateItem(ItemRegistry.RAW_GORLEK_ORE, "ore");
		generateItem(ItemRegistry.GORLEK_INGOT, "ingot");
		generateItem(ItemRegistry.GORLEK_NUGGET, "ingot");
		generateItem(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT, "ingot");
		
		OriMod.LOG.printf(Level.INFO, "Item models registered!");
	}
	
	private void generateToolItem(RegistryObject<Item> item, @Nullable String subPath) {
		ResourceLocation id = item.getId();
		if (subPath != null) {
			subPath += '/';
		} else {
			subPath = "";
		}
		singleTexture(
			//id.getNamespace() + ':' + ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath(),
			id.toString(),
			new ResourceLocation(ModelProvider.ITEM_FOLDER + "/handheld"),
			"layer0",
			new ResourceLocation(id.getNamespace(), ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath())
		);
		OriMod.LOG.printf(Level.INFO, "Created simple handheld (tool) item for %s", id.toString());
	}
	
	private void generateItem(RegistryObject<Item> item, @Nullable String subPath) {
		ResourceLocation id = item.getId();
		if (subPath != null) {
			subPath += '/';
		} else {
			subPath = "";
		}
		singleTexture(
			//id.getNamespace() + ':' + ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath(),
			id.toString(),
			new ResourceLocation(ModelProvider.ITEM_FOLDER + "/generated"),
			"layer0",
			new ResourceLocation(id.getNamespace(), ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath())
		);
		OriMod.LOG.printf(Level.INFO, "Created generic generated item for %s", id.toString());
	}
	
	/**
	 * Given a RegistryObject&lt;Item&gt; for a shield, this will create its model.
	 * @param item The item to create.
	 */
	private void generateShieldItem(RegistryObject<Item> item, @Nullable String subPath) {
		ResourceLocation id = item.getId();
		if (subPath != null) {
			subPath += '/';
		} else {
			subPath = "";
		}
		
		
		/*
		ModelFile blocking = withExistingParent(
			//ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath() + "_blocking",
			id.getPath() + "_blocking",
			mcLoc(ModelProvider.ITEM_FOLDER + "/shield_blocking")
		).texture(
			"particle",
			mcLoc("block/glass")
		);
		
		withExistingParent(
			//ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath(),
			id.getPath(),
			mcLoc(ModelProvider.ITEM_FOLDER + "/shield")
		).texture(
			"particle",
			mcLoc("block/glass")
		).override()
			.predicate(mcLoc("blocking"), 1)
			.model(blocking)
			.end();
		*/
		
		ModelFile blocking = withExistingParent(id.getPath() + "_blocking", mcLoc(ModelProvider.ITEM_FOLDER + "/shield"))
			.texture("particle", mcLoc("block/glass"))
			.guiLight(BlockModel.GuiLight.FRONT)
			.transforms()
				.transform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
					.rotation(45, 135, 0)
					.translation(0, 12, 0)
					.scale(1)
				.end()
				.transform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
					.rotation(45, 135, 0)
					.translation(12, 5, 8)
					.scale(1)
				.end()
				.transform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
					.rotation(0, 180, -5)
					.translation(-15, 5, -11)
					.scale(1.25f)
				.end()
				.transform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND)
					.rotation(15, 185, -5)
					.translation(-2, 8, 0)
					.scale(0.65f)
				.end()
			.end();
		
		withExistingParent(id.getPath(), mcLoc(ModelProvider.ITEM_FOLDER + "/shield"))
			.texture("particle", mcLoc("block/glass"))
			.guiLight(BlockModel.GuiLight.FRONT)
			.transforms()
				.transform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
					.rotation(0, 90, 0)
					.translation(10, 6, -4)
					.scale(1)
				.end()
				.transform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
					.rotation(0, 90, 0)
					.translation(10, 6, 12)
					.scale(1)
				.end()
				.transform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
					.rotation(0, 180, 5)
					.translation(-10, 2, -10)
					.scale(1.25f)
				.end()
				.transform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND)
					.rotation(0, 180, 5)
					.translation(10, 0, -10)
					.scale(1.25f)
				.end()
				.transform(ItemTransforms.TransformType.GUI)
					.rotation(15, -25, -5)
					.translation(2, 3, 0)
					.scale(0.65f)
				.end()
				.transform(ItemTransforms.TransformType.FIXED)
					.rotation(0, 180, 0)
					.translation(-4.5f, 4.5f, -5)
					.scale(0.55f)
				.end()
				.transform(ItemTransforms.TransformType.GROUND)
					.rotation(0, 0, 0)
					.translation(2, 4, 2)
					.scale(0.25f)
				.end()
			.end()
			.override()
				.predicate(modLoc("blocking"), 1)// TO FUTURE XAN: ItemRegistry.registerPredicates
				.model(blocking)
			.end();
		
		OriMod.LOG.printf(Level.INFO, "Created shield model for %s", id.toString());
	}
	
	private ModelFile generateBowItem$pulling(ResourceLocation id, int pullIndex, String subPath) {
		return singleTexture(
			//id.getNamespace() + ':' + ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath() + "_pulling_" + pullIndex,
			id.toString() + "_pulling_" + pullIndex,
			//modLoc(ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath()),
			modLoc(id.getPath()),
			"layer0",
			new ResourceLocation(id.getNamespace(), ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath() + "_pulling_" + pullIndex)
		);
	}
	
	private void generateBowItem(RegistryObject<Item> item, @Nullable String subPath) {
		ResourceLocation id = item.getId();
		if (subPath != null) {
			subPath += '/';
		} else {
			subPath = "";
		}
		
		singleTexture(
			//ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath(),
			id.getPath(),
	              new ResourceLocation(ModelProvider.ITEM_FOLDER + "/generated"),
	              "layer0",
	              new ResourceLocation(id.getNamespace(), ModelProvider.ITEM_FOLDER + "/" + subPath + id.getPath())
			)
			.transforms()
				.transform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
					.rotation(-80f, 260f, -40f)
					.translation(-1f, -2f, 2.5f)
					.scale(0.9f)
				.end()
				.transform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
					.rotation(-80f, -280f, 40f)
					.translation(-1f, -2f, 2.5f)
					.scale(0.9f)
				.end()
				.transform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
					.rotation(0f, -90f, 25f)
					.translation(1.13f, 3.2f, 1.13f)
					.scale(0.68f)
				.end()
				.transform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND)
					.rotation(0f, 90f, -25f)
					.translation(1.13f, 3.2f, 1.13f)
					.scale(0.68f)
				.end()
			.end()
			.override()
				.predicate(modLoc("arcphase"), 1) // TO FUTURE XAN: ItemRegistry.registerPredicates
				.model(generateBowItem$pulling(id, 0, subPath))
			.end()
			.override()
				.predicate(modLoc("arcphase"), 2)
				.model(generateBowItem$pulling(id, 1, subPath))
			.end()
			.override()
				.predicate(modLoc("arcphase"), 3)
				.model(generateBowItem$pulling(id, 2, subPath))
			.end();
		
	}
	
}
