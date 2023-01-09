package etithespirit.orimod.datagen;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.registry.world.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Level;

/**
 * This data generator is responsible for searching for mod objects implementing {@link IBlockTagProvider} and using the data of that method to figure out
 * what tags to give to the block.
 */
public final class BlockToolRelations extends BlockTagsProvider {
	
	/**
	 * Create a new data generator for block to tool relations.
	 * @param pGenerator The real generator.
	 * @param existingFileHelper A tool to create or edit existing files.
	 */
	public BlockToolRelations(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, OriMod.MODID, existingFileHelper);
	}
	
	@Override
	protected void addTags() {
		OriMod.LOG.printf(Level.INFO, "Starting custom datagen system: Block -> Tag Binding Generation.");
		addTagsToBlocks();
		OriMod.LOG.printf(Level.INFO, "Done associating blocks with their declared tags!");
	}
	
	private void addTagsToBlocks() {
		for (RegistryObject<? extends Block> blockReg : BlockRegistry.BLOCKS.getEntries()) {
			Block block = blockReg.get();
			if (block instanceof IBlockTagProvider) {
				IBlockTagProvider provider = (IBlockTagProvider)block;
				for (TagKey<Block> tag : provider.getTagsForBlock()) {
					this.tag(tag).add(block);
					OriMod.LOG.printf(Level.INFO, "Added tag %s to %s", tag.toString(), blockReg.getId().toString());
				}
			}
		}
	}
}
