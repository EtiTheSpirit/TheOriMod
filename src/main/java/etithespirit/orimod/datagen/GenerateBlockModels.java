package etithespirit.orimod.datagen;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.decay.DecayCommon;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.registry.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Level;

public class GenerateBlockModels extends BlockStateProvider {
	public GenerateBlockModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, OriMod.MODID, exFileHelper);
	}
	
	private ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}
	
	private String name(Block block) {
		return block.getRegistryName().getPath();
	}
	
	@Override
	protected void registerStatesAndModels() {
		OriMod.LOG.printf(Level.INFO, "Starting block model generation.");
		
		registerBlockAndItem(BlockRegistry.DECAY_MYCELIUM);
		registerLogBlockAndItem(BlockRegistry.DECAY_LOG);
		registerLogBlockAndItem(BlockRegistry.DECAY_STRIPPED_LOG);
		registerInsideOutBlockAndItem(BlockRegistry.DECAY_SURFACE_MYCELIUM);
		
		// registerBlockAndItem(BlockRegistry.DECAY_POISON);
		
		registerConduitBlock(BlockRegistry.LIGHT_CONDUIT);
		registerBlockAndItem(BlockRegistry.LIGHT_CAPACITOR);
		
		OriMod.LOG.printf(Level.INFO, "Block models registered!");
	}
	
	/**
	 * Registers a block and an item for that block.
	 * @param blockReg The block registry object to create.
	 */
	protected void registerBlockAndItem(RegistryObject<Block> blockReg) {
		Block block = blockReg.get();
		ModelFile model = this.cubeAll(block);
		VariantBlockStateBuilder builder = this.getVariantBuilder(block);
		builder.forAllStates(state -> new ConfiguredModel[] { new ConfiguredModel(model) });
		
		this.simpleBlockItem(block, model);
		OriMod.LOG.printf(Level.INFO, "Generated simple block at %s", model.getLocation().toString());
	}
	
	/**
	 * Creates a block that's "inside out", or, the space it occupies renders on adjacent faces rather than as a full cube.<br/>
	 * This is mostly used for decay mycelium, which coats the surfaces of blocks. The best vanilla block to compare this to is vines.
	 * @param block The block registry object to create.
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
		OriMod.LOG.printf(Level.INFO, "Generated inside out block at %s", model.getLocation().toString());
	}
	
	private void registerConduitBlock(RegistryObject<Block> block) {
		MultiPartBlockStateBuilder multiPart = this.getMultipartBuilder(block.get());
		ModelFile core = get8Core(block, false);
		ModelFile connector = get8CoreConnector(block, false);
		ModelFile coreGlowing = get8Core(block, true);
		ModelFile connectorGlowing = get8CoreConnector(block, true);
		multiPart
			.part().modelFile(core).addModel().condition(ConnectableLightTechBlock.ENERGIZED, false).end()
			.part().modelFile(connector).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.NORTH, true).condition(ConnectableLightTechBlock.ENERGIZED, false).end()
			.part().modelFile(connector).rotationX(  0).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).condition(ConnectableLightTechBlock.ENERGIZED, false).end()
			.part().modelFile(connector).rotationX(  0).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).condition(ConnectableLightTechBlock.ENERGIZED, false).end()
			.part().modelFile(connector).rotationX(  0).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).condition(ConnectableLightTechBlock.ENERGIZED, false).end()
			.part().modelFile(connector).rotationX(270).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).condition(ConnectableLightTechBlock.ENERGIZED, false).end()
			.part().modelFile(connector).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).condition(ConnectableLightTechBlock.ENERGIZED, false).end();
		multiPart
			.part().modelFile(coreGlowing).addModel().condition(ConnectableLightTechBlock.ENERGIZED, true).end()
			.part().modelFile(connectorGlowing).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.NORTH, true).condition(ConnectableLightTechBlock.ENERGIZED, true).end()
			.part().modelFile(connectorGlowing).rotationX(  0).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).condition(ConnectableLightTechBlock.ENERGIZED, true).end()
			.part().modelFile(connectorGlowing).rotationX(  0).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).condition(ConnectableLightTechBlock.ENERGIZED, true).end()
			.part().modelFile(connectorGlowing).rotationX(  0).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).condition(ConnectableLightTechBlock.ENERGIZED, true).end()
			.part().modelFile(connectorGlowing).rotationX(270).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).condition(ConnectableLightTechBlock.ENERGIZED, true).end()
			.part().modelFile(connectorGlowing).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).condition(ConnectableLightTechBlock.ENERGIZED, true).end();
		
		this.simpleBlockItem(block.get(), core);
		OriMod.LOG.printf(Level.INFO, "Generated conduit block at %s and %s", core.getLocation().toString(), connector.getLocation().toString());
	}
	
	protected void registerLogBlockAndItem(RegistryObject<Block> block) {
		this.axisBlockWithStates((RotatedPillarBlock)block.get(), this.blockTexture(block.get()), extend(this.blockTexture(block.get()), "_top"));
	}
	
	public void axisBlockWithStates(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
		axisBlockWithStates(block, models().cubeColumn(name(block), side, end), models().cubeColumnHorizontal(name(block) + "_horizontal", side, end));
	}
	
	public void axisBlockWithStates(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
		
		for (BlockState fullState : block.getStateDefinition().getPossibleStates()) {
			
			Direction.Axis axis = fullState.getValue(RotatedPillarBlock.AXIS);
			boolean allAdjacentDecayed = fullState.getValue(DecayCommon.ALL_ADJACENT_ARE_DECAY);
			int edgeSpreadRarity = fullState.getValue(DecayCommon.EDGE_DETECTION_RARITY);
			
			// Populate the required state.
			VariantBlockStateBuilder.PartialBlockstate basic = getVariantBuilder(block).partialState().with(DecayCommon.ALL_ADJACENT_ARE_DECAY, allAdjacentDecayed).with(DecayCommon.EDGE_DETECTION_RARITY, edgeSpreadRarity).with(RotatedPillarBlock.AXIS, axis);
			if (axis == Direction.Axis.Y) {
				basic.modelForState().modelFile(vertical).addModel();
			} else if (axis == Direction.Axis.Z) {
				basic.modelForState().modelFile(horizontal).rotationX(90).addModel();
			} else if (axis == Direction.Axis.X) {
				basic.modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
			}
			
			OriMod.LOG.printf(Level.INFO, "Generating special log block state where %s = %s, %s = %s | axis = %s at %s / %s",
			                  DecayCommon.ALL_ADJACENT_ARE_DECAY.getName(), allAdjacentDecayed,
			                  DecayCommon.EDGE_DETECTION_RARITY.getName(), edgeSpreadRarity,
			                  axis.getName(),
			                  vertical.getLocation(), horizontal.getLocation()
			);
		}
		
		OriMod.LOG.printf(Level.INFO, "Creating item for log block.");
		this.simpleBlockItem(block, vertical);
		
	}
	
	/**
	 * Returns a model suited for the inside out block type.
	 * @param block The block registry to use.
	 * @param surfaceOffset How far away from the surface that the plane covering a specific surface is. Useful mostly for 2D models (thickness=0)
	 * @param thickness The depth of each surface plane. Looks a bit better, but also has more to render. This can be used to mitigate z-fighting without introducing gaps on outward-facing corners.
	 * @return A model for an inside-out block.
	 */
	@SuppressWarnings("rawtypes")
	private ModelFile getInsideOutBlockModel(RegistryObject<Block> block, float surfaceOffset, float thickness) {
		String path = ModelProvider.BLOCK_FOLDER + "/" + block.getId().getPath();
		BlockModelBuilder blockBuilder = models().cubeAll(path, blockTexture(block.get()));
		blockBuilder.ao(false).texture("all", modLoc(path));
		ModelBuilder.ElementBuilder element = blockBuilder.element()
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
	
	private ModelFile get8Core(RegistryObject<Block> block, boolean glow) {
		String glowSuffix = glow ? "-energized" : "";
		String pathBase = ModelProvider.BLOCK_FOLDER + "/" + block.getId().getPath();
		ResourceLocation texture = localTexture(block.getId(), pathBase, "core" + glowSuffix);
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
	
	private ModelFile get8CoreConnector(RegistryObject<Block> block, boolean glow) {
		String glowSuffix = glow ? "-energized" : "";
		String pathBase = ModelProvider.BLOCK_FOLDER + "/" + block.getId().getPath();
		String pathConnector = pathBase + "-connector";
		ResourceLocation id = block.getId();
		ResourceLocation sides = localTexture(id, pathConnector, "side" + glowSuffix);
		ResourceLocation back = localTexture(id, pathBase, "core" + glowSuffix); // Share with the core
		ResourceLocation front = localTexture(id, pathConnector, "connection" + glowSuffix);
		BlockModelBuilder blockBuilder = models().cube(pathConnector + glowSuffix, sides, sides, front, back, sides, sides);
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
	 * @param block The block that this is for. This is used to acquire the namespace.
	 * @param path The path for this element.
	 * @param suffix A special suffix for this element.
	 * @return A {@link ResourceLocation} formatted as "namespace:path-suffix"
	 */
	private ResourceLocation localTexture(ResourceLocation block, String path, String suffix) {
		return new ResourceLocation(block.getNamespace(), path + '-' + suffix);
	}
}