package etithespirit.datagen;

import net.minecraftforge.client.model.generators.*;
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
import net.minecraftforge.client.model.generators.ModelBuilder.ElementBuilder;
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
		
		registerConduitBlock(BlockRegistry.LIGHT_CONDUIT);
		registerBlockAndItem(BlockRegistry.LIGHT_CAPACITOR);
		
		EtiMod.LOG.printf(Level.INFO, "Block models registered!");
	}
	
	/**
	 * Registers a block and an item for that block.
	 * @param blockReg
	 */
	protected void registerBlockAndItem(RegistryObject<Block> blockReg) {
		Block block = blockReg.get();
		ModelFile model = this.cubeAll(block);
		VariantBlockStateBuilder builder = this.getVariantBuilder(block);
		builder.forAllStates(state -> {
			return new ConfiguredModel[] { new ConfiguredModel(model) };
		});
		
		this.simpleBlockItem(block, model);
		EtiMod.LOG.printf(Level.INFO, "Generated simple block at %s", model.getLocation().toString());
	}
	
	/**
	 * Creates a block that's "inside out", or, the space it occupies renders on adjacent faces rather than as a full cube.<br/>
	 * This is mostly used for decay mycelium, which coats the surfaces of blocks. The best vanilla block to compare this to is vines.
	 * @param block
	 */
	private void registerInsideOutBlockAndItem(RegistryObject<Block> block) {
		MultiPartBlockStateBuilder multiPart = this.getMultipartBuilder(block.get());
		ModelFile model = getInsideOutBlockModel(block, 0f, 0.1f);
		model.assertExistence();
		multiPart
		.part().modelFile(model).rotationX( 90).rotationY(180).addModel().condition(BlockStateProperties.NORTH, true).end()
		.part().modelFile(model).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.SOUTH, true).end()
		.part().modelFile(model).rotationX( 90).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).end()
		.part().modelFile(model).rotationX( 90).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).end()
		.part().modelFile(model).rotationX(180).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).end()
		.part().modelFile(model).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).end();
		
		ModelFile fullCube = this.cubeAll(block.get());
		this.simpleBlockItem(block.get(), fullCube);
		EtiMod.LOG.printf(Level.INFO, "Generated inside out block at %s", model.getLocation().toString());
	}
	
	private void registerConduitBlock(RegistryObject<Block> block) {
		MultiPartBlockStateBuilder multiPart = this.getMultipartBuilder(block.get());
		ModelFile core = get8Core(block);
		ModelFile connector = get8CoreConnector(block);
		multiPart
		.part().modelFile(core).addModel().end()
		.part().modelFile(connector).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.NORTH, true).end()
		.part().modelFile(connector).rotationX(  0).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).end()
		.part().modelFile(connector).rotationX(  0).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).end()
		.part().modelFile(connector).rotationX(  0).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).end()
		.part().modelFile(connector).rotationX(270).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).end()
		.part().modelFile(connector).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).end();
		
		this.simpleBlockItem(block.get(), core);
		EtiMod.LOG.printf(Level.INFO, "Generated conduit out block at %s and %s", core.getLocation().toString(), connector.getLocation().toString());
	}
	
	protected void registerLogBlockAndItem(RegistryObject<Block> block) {
		this.axisBlockWithStates((RotatedPillarBlock)block.get(), this.blockTexture(block.get()), extend(this.blockTexture(block.get()), "_top"));
	}
	
	public void axisBlockWithStates(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
        axisBlockWithStates(block, models().cubeColumn(name(block), side, end), models().cubeColumnHorizontal(name(block) + "_horizontal", side, end));
    }

	public void axisBlockWithStates(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
	    
		for (BlockState fullState : block.getStateDefinition().getPossibleStates()) {
			
			Axis axis = fullState.getValue(RotatedPillarBlock.AXIS);
			boolean allAdjacentDecayed = fullState.getValue(DecayCommon.ALL_ADJACENT_ARE_DECAY);
			int edgeSpreadRarity = fullState.getValue(DecayCommon.EDGE_DETECTION_RARITY);
			
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
				axis.getName(),
				vertical.getLocation(), horizontal.getLocation()
			);
		}
		
		EtiMod.LOG.printf(Level.INFO, "Creating item for log block.");
		this.simpleBlockItem(block, vertical);
	    
	}
	
	/**
	 * Returns a model suited for the inside out block type.
	 * @param block
	 * @param surfaceOffset How far away from the surface that the plane covering a specific surface is. Useful mostly for 2D models (thickness=0)
	 * @param thickness The depth of each surface plane. Looks a bit better, but also has more to render. This can be used to mitigate z-fighting without introducing gaps on outward-facing corners.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private ModelFile getInsideOutBlockModel(RegistryObject<Block> block, float surfaceOffset, float thickness) {
		String path = ModelProvider.BLOCK_FOLDER + "/" + block.getId().getPath();
		BlockModelBuilder blockBuilder = models().cubeAll(path, blockTexture(block.get()));
		blockBuilder.ao(false).texture("all", modLoc(path));
		ElementBuilder element = blockBuilder.element()
			.from(-thickness, surfaceOffset, -thickness)
			.to(16f + thickness, surfaceOffset + thickness, 16f + thickness)
			.shade(false)
				.face(Direction.UP)
					.uvs(0, 0, 16, 16)
					.texture("#all")
				.end()
				.face(Direction.DOWN)
					.uvs(0, 0, 16, 16)
					.texture("#all")
				.end();
			if (thickness > 0) {
				// Condition: If there's depth to this, also render the horizontal sides.
				element
				.face(Direction.NORTH)
					.uvs(0, 0, thickness, thickness)
					.texture("#all")
				.end()
				.face(Direction.WEST)
					.uvs(0, 0, thickness, thickness)
					.texture("#all")
				.end()
				.face(Direction.SOUTH)
					.uvs(0, 0, thickness, thickness)
					.texture("#all")
				.end()
				.face(Direction.EAST)
					.uvs(0, 0, thickness, thickness)
					.texture("#all")
				.end();
			}
		element.end();
		return blockBuilder;
	}
	
	private ModelFile get8Core(RegistryObject<Block> block) {
		String pathBase = ModelProvider.BLOCK_FOLDER + "/" + block.getId().getPath();
		ResourceLocation texture = localTexture(block.getId(), pathBase, "core");
		BlockModelBuilder blockBuilder = models().cubeAll(texture.getPath(), texture);
		blockBuilder.element()
			.texture("#all")
			.from(4f, 4f, 4f)
			.to(12f, 12f, 12f)
			.face(Direction.NORTH)
				.uvs(0, 0, 16, 16)
				.texture("#all")
			.end()
			.face(Direction.SOUTH)
				.uvs(0, 0, 16, 16)
				.texture("#all")
			.end()
			.face(Direction.EAST)
				.uvs(0, 0, 16, 16)
				.texture("#all")
			.end()
			.face(Direction.WEST)
				.uvs(0, 0, 16, 16)
				.texture("#all")
			.end()
			.face(Direction.UP)
				.uvs(0, 0, 16, 16)
				.texture("#all")
			.end()
			.face(Direction.DOWN)
				.uvs(0, 0, 16, 16)
				.texture("#all")
			.end()
		.end();
		return blockBuilder;
	}
	
	private ModelFile get8CoreConnector(RegistryObject<Block> block) {
		String pathBase = ModelProvider.BLOCK_FOLDER + "/" + block.getId().getPath();
		String pathConnector = pathBase + "-connector";
		ResourceLocation id = block.getId();
		ResourceLocation sides = localTexture(id, pathConnector, "side");
		ResourceLocation back = localTexture(id, pathBase, "core"); // Share with the core
		ResourceLocation front = localTexture(id, pathConnector, "connection");
		BlockModelBuilder blockBuilder = models().cube(pathConnector, sides, sides, front, back, sides, sides);
		blockBuilder.element()
			.from(4f, 4f, 0f)
			.to(12f, 12f, 4f)
			.face(Direction.NORTH)
				.uvs(0, 0, 16, 16)
				.texture("#north")
			.end()
			.face(Direction.SOUTH)
				.uvs(0, 0, 16, 16)
				.texture("#south")
			.end()
			.face(Direction.EAST)
				.uvs(0, 0, 16, 16)
				.texture("#east")
			.end()
			.face(Direction.WEST)
				.uvs(0, 0, 16, 16)
				.texture("#west")
			.end()
			.face(Direction.UP)
				.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90)
				.uvs(0, 0, 16, 16)
				.texture("#up")
			.end()
			.face(Direction.DOWN)
			.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90)
				.uvs(0, 0, 16, 16)
				.texture("#down")
			.end()
		.end();
		return blockBuilder;
	}
	
	/**
	 * Creates a texture named "namespace:path-suffix".
	 * @param block
	 * @param path
	 * @param suffix
	 * @return
	 */
	private ResourceLocation localTexture(ResourceLocation block, String path, String suffix) {
		return new ResourceLocation(block.getNamespace(), path + '-' + suffix);
	}
}
