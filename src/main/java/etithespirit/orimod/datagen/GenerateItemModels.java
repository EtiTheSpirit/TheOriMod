package etithespirit.orimod.datagen;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.ItemRegistry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Level;

/**
 * Generates models associated with items.
 * @author Eti
 */
public final class GenerateItemModels extends ItemModelProvider {
	/***/
	public GenerateItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, OriMod.MODID, existingFileHelper);
	}
	
	@Override
	protected void registerModels() {
		OriMod.LOG.printf(Level.INFO, "Starting item model generation.");
		//generateToolItem(ItemRegistry.LIGHT_TOOL);
		generateToolItem(ItemRegistry.LUMO_WAND);
		generateShieldItem(ItemRegistry.LIGHT_SHIELD);
		OriMod.LOG.printf(Level.INFO, "Item models registered!");
	}
	
	private void generateToolItem(RegistryObject<Item> item) {
		ResourceLocation id = item.getId();
		singleTexture(
			id.toString(),
			new ResourceLocation("item/handheld"),
			"layer0",
			new ResourceLocation(id.getNamespace(), "item/" + id.getPath())
		);
		OriMod.LOG.printf(Level.INFO, "Created simple handheld (tool) item for %s", id.toString());
	}
	
	/**
	 * Given a RegistryObject&lt;Item&gt; for a shield, this will create its model.
	 * @param item The item to create.
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
		
		OriMod.LOG.printf(Level.INFO, "Created shield model for %s", id.toString());
	}
	
}
