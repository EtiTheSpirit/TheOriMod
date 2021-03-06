package etithespirit.orimod.datagen;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.registry.BlockRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Level;

/**
 * This data generator is responsible for searching for mod objects implementing {@link IToolRequirementProvider} and using the data of that method to figure out
 * what tags to give to the block.
 */
public final class BlockToolRelations extends BlockTagsProvider {
	
	/**
	 * Create a new data generator for block to tool relations.
	 * @param p_126511_ The real generator.
	 * @param existingFileHelper A tool to create or edit existing files.
	 */
	public BlockToolRelations(DataGenerator p_126511_, ExistingFileHelper existingFileHelper) {
		super(p_126511_, OriMod.MODID, existingFileHelper);
	}
	
	@Override
	protected void addTags() {
		OriMod.LOG.printf(Level.INFO, "Starting block to tool tag generation.");
		addTagsToBlocks();
		OriMod.LOG.printf(Level.INFO, "Done associating blocks with optimal tools!");
	}
	
	private void addTagsToBlocks() {
		for (RegistryObject<Block> blockReg : BlockRegistry.BLOCKS_TO_REGISTER) {
			Block block = blockReg.get();
			if (block instanceof IToolRequirementProvider) {
				IToolRequirementProvider provider = (IToolRequirementProvider)block;
				for (Tag.Named<Block> tag : provider.getTagsForBlock()) {
					this.tag(tag).add(block);
					OriMod.LOG.printf(Level.INFO, "Added tag %s to %s", tag.getName().toString(), blockReg.getId().toString());
				}
				OriMod.LOG.printf(Level.INFO, "\n");
			}
		}
	}
}
