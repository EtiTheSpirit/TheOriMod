package etithespirit.etimod.datagen;

import org.apache.logging.log4j.Level;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.block.decay.DecayCommon;
import etithespirit.etimod.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

/**
 * Generates all decay block models.
 * @author Eti
 *
 */
public class GenerateBlockModels extends BlockStateProvider {
	public GenerateBlockModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, EtiMod.MODID, exFileHelper);
	}
	
	private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }
	
	private String name(Block block) {
		return block.getRegistryName().getPath();
	}

	@Override
	protected void registerStatesAndModels() {
		EtiMod.LOG.printf(Level.INFO, "Starting block model generation.");
		registerBlockAndItem(BlockRegistry.DECAY_MYCELIUM);
		registerLogBlockAndItem(BlockRegistry.DECAY_LOG);
		registerLogBlockAndItem(BlockRegistry.DECAY_STRIPPED_LOG);
		registerInsideOutBlockAndItem(BlockRegistry.DECAY_SURFACE_MYCELIUM);
		
		registerBlockAndItem(BlockRegistry.DECAY_POISON);
		
		EtiMod.LOG.printf(Level.INFO, "Block models registered!");
	}
	
	/**
	 * Registers a block and an item for that block.
	 * @param block
	 */
	protected void registerBlockAndItem(RegistryObject<Block> blockReg) {
		Block block = blockReg.get();
		ModelFile model = this.cubeAll(block);
		VariantBlockStateBuilder builder = this.getVariantBuilder(block);
		builder.forAllStates((BlockState state) -> {
			return new ConfiguredModel[] { new ConfiguredModel(model) };
		});
		
		this.simpleBlockItem(block, model);
		EtiMod.LOG.printf(Level.INFO, "Generated simple block at %s", model.getLocation().toString());
	}
	
	/**
	 * Creates a block that's "inside out", or, the space it occupies renders on adjacent faces rather than as a full cube.<br/>
	 * This is mostly used for decay mycelium, which coats the surfaces of blocks. The best vanilla block to compare this to is vines.
	 * @param block
	 * @param rsrc
	 */
	private void registerInsideOutBlockAndItem(RegistryObject<Block> block) {
		MultiPartBlockStateBuilder multiPart = this.getMultipartBuilder(block.get());
		ModelFile model = getInsideOutBlockModel(block, 0f);
		model.assertExistence();
		multiPart
		.part().modelFile(model).rotationX( 90).rotationY(180).addModel().condition(BlockStateProperties.NORTH, true).end()
		.part().modelFile(model).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.SOUTH, true).end()
		.part().modelFile(model).rotationX( 90).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).end()
		.part().modelFile(model).rotationX( 90).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).end()
		.part().modelFile(model).rotationX(180).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).end()
		.part().modelFile(model).rotationX(  0).rotationX(  0).addModel().condition(BlockStateProperties.DOWN, true).end();
		
		this.simpleBlockItem(block.get(), model);
		EtiMod.LOG.printf(Level.INFO, "Generated inside out block at %s", model.getLocation().toString());
	}
	
	protected void registerLogBlockAndItem(RegistryObject<Block> block) {
		this.axisBlockWithStates((RotatedPillarBlock)block.get(), this.blockTexture(block.get()), extend(this.blockTexture(block.get()), "_top"));
	}
	
	public void axisBlockWithStates(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        axisBlockWithStates(block, models().cubeColumn(name(block), side, end), models().cubeColumnHorizontal(name(block) + "_horizontal", side, end));
    }

	public void axisBlockWithStates(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
	    
		for (BlockState fullState : block.getStateContainer().getValidStates()) {
			
			Axis axis = fullState.get(RotatedPillarBlock.AXIS);
			boolean allAdjacentDecayed = fullState.get(DecayCommon.ALL_ADJACENT_ARE_DECAY);
			int edgeSpreadRarity = fullState.get(DecayCommon.EDGE_DETECTION_RARITY);
			
			// Populate the required state.
			PartialBlockstate basic = getVariantBuilder(block).partialState().with(DecayCommon.ALL_ADJACENT_ARE_DECAY, allAdjacentDecayed).with(DecayCommon.EDGE_DETECTION_RARITY, edgeSpreadRarity).with(RotatedPillarBlock.AXIS, axis);
			if (axis == Axis.Y) {
    			basic.modelForState().modelFile(vertical).addModel();
    		} else if (axis == Axis.Z) {
    			basic.modelForState().modelFile(horizontal).rotationX(90).addModel();
    		} else if (axis == Axis.X) {
    			basic.modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
    		}
			
			EtiMod.LOG.printf(Level.INFO, "Generating special log block state where %s = %s, %s = %s | axis = %s at %s / %s", 
				DecayCommon.ALL_ADJACENT_ARE_DECAY.getName(), allAdjacentDecayed, 
				DecayCommon.EDGE_DETECTION_RARITY.getName(), edgeSpreadRarity,
				axis.getName2(),
				vertical.getLocation(), horizontal.getLocation()
			);
		}
	    
	}
	
	/**
	 * Returns a model suited for the inside out block type.
	 * @param block
	 * @return
	 */
	@SuppressWarnings("unused")
	private ModelFile getInsideOutBlockModel(RegistryObject<Block> block) {
		return getInsideOutBlockModel(block, 0.03f);
	}
	
	/**
	 * Returns a model suited for the inside out block type.
	 * @param block
	 * @return
	 */
	private ModelFile getInsideOutBlockModel(RegistryObject<Block> block, float surfaceOffset) {
		String path = "block/" + block.getId().getPath();
		BlockModelBuilder blockBuilder = models().cubeAll(path, blockTexture(block.get()));
		blockBuilder.ao(false).texture("all", modLoc(path));
		blockBuilder.element()
			.from(0f, surfaceOffset, 0f)
			.to(16f, surfaceOffset, 16f)
			.shade(false)
				.face(Direction.UP)
					.uvs(0, 0, 16, 16)
					.texture("#all")
				.end()
			.end();
		return blockBuilder;
	}
}
