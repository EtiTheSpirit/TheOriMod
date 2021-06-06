package etithespirit.etimod.datagen;

import org.apache.logging.log4j.Level;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.registry.ItemRegistry;
import net.minecraft.client.renderer.model.BlockModel.GuiLight;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public final class GenerateItemModels extends ItemModelProvider {

	public GenerateItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, EtiMod.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		EtiMod.LOG.printf(Level.INFO, "Starting item model generation.");
		generateToolItem(ItemRegistry.LIGHT_TOOL);
		generateShieldItem(ItemRegistry.LIGHT_SHIELD);
		EtiMod.LOG.printf(Level.INFO, "Item models registered!");
	}
	
	private void generateToolItem(RegistryObject<Item> item) {
		ResourceLocation id = item.getId();
		singleTexture(
			id.toString(),
			new ResourceLocation("item/handheld"),
			"layer0",
			new ResourceLocation(id.getNamespace(), "item/" + id.getPath())
		);
		EtiMod.LOG.printf(Level.INFO, "Created simple handheld (tool) item for %s", id.toString());
	}
	
	/**
	 * Given a RegistryObject&lt;Item&gt; for a shield, this will create its model.
	 * @param item
	 */
	private void generateShieldItem(RegistryObject<Item> item) {
		ResourceLocation id = item.getId();
		ModelFile blocking = withExistingParent(
			"item/" + id.getPath() + "_blocking",
			mcLoc("item/shield_blocking")
		).texture(
			"particle",
			mcLoc("block/glass")
		);
		
		withExistingParent(
			"item/" + id.getPath(),
			mcLoc("item/shield")
		).texture(
			"particle",
			mcLoc("block/glass")
		).override()
			.predicate(mcLoc("blocking"), 1)
			.model(blocking)
		.end();
		
		EtiMod.LOG.printf(Level.INFO, "Created shield model for %s", id.toString());
	}
	
	@Deprecated
	protected void generateShieldItem_(RegistryObject<Item> item) {
		ResourceLocation id = item.getId();
		
		///////////////////////////////////////////////////////////
		// BLOCKING SHIELD COMPONENT:
		ModelFile blockingShield = singleTexture(
			id.toString() + "_blocking",
			new ResourceLocation("item/shield"),
			"particle",
			new ResourceLocation("block/glass")
		)
		.guiLight(GuiLight.FRONT)
		.transforms()
			.transform(Perspective.THIRDPERSON_RIGHT)
				.rotation(45, 135, 0)
				.translation(3.51f, 11, -2)
				.scale(1)
			.end()
			.transform(Perspective.THIRDPERSON_LEFT)
				.rotation(45, 135, 0)
				.translation(13.51f, 3, 5)
				.scale(1)
			.end()
			.transform(Perspective.FIRSTPERSON_RIGHT)
				.rotation(0, 180, -5)
				.translation(-15, 5, -11)
				.scale(1.25f)
			.end()
			.transform(Perspective.FIRSTPERSON_LEFT)
				.rotation(0, 180, -5)
				.translation(5, 5, -11)
				.scale(1.25f)
			.end()
			.transform(Perspective.GUI)
				.rotation(15, -25, -5)
				.translation(2, 3, 0)
				.scale(0.65f)
			.end()
		.end();
		
		///////////////////////////////////////////////////////////
		// STANDARD SHIELD COMPONENT:
		singleTexture(
			id.toString(),
			new ResourceLocation("item/shield"),
			"particle",
			new ResourceLocation("block/glass")
		)
		.guiLight(GuiLight.FRONT)
		.transforms()
			.transform(Perspective.THIRDPERSON_RIGHT)
				.rotation(0, 90, 0)
				.translation(10, 6, -6)
				.scale(1)
			.end()
			.transform(Perspective.THIRDPERSON_LEFT)
				.rotation(0, 90, 0)
				.translation(10, 6, 12)
				.scale(1)
			.end()
			.transform(Perspective.FIRSTPERSON_RIGHT)
				.rotation(0, 180, 5)
				.translation(-10, 2, -10)
				.scale(1.25f)
			.end()
			.transform(Perspective.FIRSTPERSON_LEFT)
				.rotation(0, 180, 5)
				.translation(10, 0, -10)
				.scale(1.25f)
			.end()
			.transform(Perspective.GUI)
				.rotation(15, -25, -5)
				.translation(2, 3, 0)
				.scale(0.65f)
			.end()
			.transform(Perspective.FIXED)
				.rotation(0, 180, 0)
				.translation(-2, 4, -5)
				.scale(0.5f)
			.end()
			.transform(Perspective.GROUND)
				.rotation(0, 0, 0)
				.translation(4, 4, 2)
				.scale(0.25f)
			.end()
		.end()
		.override()
			.model(blockingShield)
			.predicate(new ResourceLocation("blocking"), 1)
		.end();
		
	}

}
