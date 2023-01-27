package etithespirit.orimod.datagen.block;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.decay.DecayCommon;
import etithespirit.orimod.common.block.decay.flora.DecayLogBase;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

public final class BlockGenerationTools {
	
	public static class Assets {
		
		/**
		 * Appends the given suffix string onto the end of the ResourceLocation.
		 *
		 * @param rsrc   The resource to edit.
		 * @param suffix What to append onto the path (the part after the : symbol) of the resource.
		 * @return A new resource location formatted as namespace:pathsuffix
		 */
		public static ResourceLocation extend(ResourceLocation rsrc, String suffix) {
			return new ResourceLocation(rsrc.getNamespace(), rsrc.getPath() + suffix);
		}
		
		/**
		 * Returns the ID of the given block.
		 *
		 * @param block The block to get.
		 * @return The block's ID.
		 */
		public static ResourceLocation key(Block block) {
			return ForgeRegistries.BLOCKS.getKey(block);
		}
		
		/**
		 * Returns the name of the given block without its namespace (the mod ID).
		 *
		 * @param block The block to get the name of.
		 * @return The name of the block without its namespace, such as "stone" instead of "minecraft:stone".
		 */
		@Deprecated(forRemoval = true)
		public static String name(Block block) {
			return key(block).getPath();
		}
		
		/**
		 * Returns the name of the given block without its namespace (the mod ID).
		 *
		 * @param block The block to get the name of.
		 * @return The name of the block without its namespace, such as "stone" instead of "minecraft:stone".
		 */
		public static String name(Block block, @Nullable String textureSubPath) {
			if (textureSubPath == null) {
				return key(block).getPath();
			} else {
				return ModelProvider.BLOCK_FOLDER + "/" + textureSubPath + "/" + key(block).getPath();
				// TODO: Why do I have to specify BLOCK_FOLDER?
			}
		}
		
		/**
		 * Returns the resource path to a block's texture.
		 * @param block The block to get the texture of.
		 * @return The path to this block's texture.
		 */
		public static ResourceLocation blockTexture(Block block) {
			ResourceLocation name = key(block);
			return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
		}
		
		/**
		 * Returns the resource path to a block's texture within a subfolder of textures/block.
		 * @param block The block to get the texture of.
		 * @param textureSubPath The subfolder(s), separated with / - this should not start nor end with /
		 * @return The path to this block's texture.
		 */
		public static ResourceLocation blockTexture(Block block, @Nullable String textureSubPath) {
			if (textureSubPath == null) return blockTexture(block);
			
			ResourceLocation name = key(block);
			return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + textureSubPath + "/" + name.getPath());
		}
		
		/**
		 * Allows getting a texture like {@link #blockTexture(Block, String)}, but allows appending a suffix onto the end. The suffix is always prepended with a dash.
		 * @param block The block to get the ID of.
		 * @param textureSubPath The folder the texture is in, or null if it is in the root blocks folder.
		 * @param suffix The suffix to append to the end of the block's name, which always has a dash before it added by the code.
		 * @return A constructed string of all the pieces: blocks/subfolder/blockId-suffix
		 */
		public static ResourceLocation blockTexture(Block block, @Nullable String textureSubPath, @Nullable String suffix) {
			ResourceLocation name = key(block);
			
			String path = ModelProvider.BLOCK_FOLDER;
			if (textureSubPath != null) path += "/" + textureSubPath;
			path += "/" + name.getPath();
			if (suffix != null) path += "-" + suffix;
			
			return new ResourceLocation(name.getNamespace(), path);
		}
		
		/**
		 * DANGER: Hardcoded to use the ori mod's ID!
		 * @param name The name of the asset.
		 * @return The name within a ResourceLocation denoting it as part of The Ori Mod.
		 */
		public static ResourceLocation modLoc(String name) {
			return new ResourceLocation(OriMod.MODID, name);
		}
		
		/**
		 * @param name The name of the asset.
		 * @return The name within a ResourceLocation denoting it as part of Minecraft.
		 */
		public static ResourceLocation mcLoc(String name) {
			return new ResourceLocation(name);
		}
	}
	
	public static class Common {
		
		/**
		 * Registers a default full block model as well as an item for that block in a single method.
		 *
		 * @param blockReg The block registry object to create.
		 */
		public static void registerBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg) {
			registerBlockAndItem(provider, blockReg, null);
		}
		
		/**
		 * Registers a default full block model as well as an item for that block in a single method.
		 *
		 * @param provider       The BlockStateProvider used in datagen.
		 * @param blockReg       The block registry object to create.
		 * @param textureSubPath If the texture for this block is in a subfolder, this is the subfolder(s). Individual folders are separated with /, but the string should not begin nor end with /
		 */
		public static void registerBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg, @Nullable String textureSubPath) {
			Block block = blockReg.get();
			ModelFile model = Models.cubeAll(provider, block, textureSubPath);
			VariantBlockStateBuilder builder = provider.getVariantBuilder(block);
			builder.forAllStates(state -> new ConfiguredModel[] {new ConfiguredModel(model)});
			
			simpleBlockItem(provider, block, model);
			OriMod.LOG.printf(Level.INFO, "Generated simple block and block-item at %s", model.getLocation().toString());
		}
		
		public static void simpleBlockItem(BlockStateProvider provider, Block block, ModelFile model) {
			//provider.itemModels().getBuilder(ModelProvider.ITEM_FOLDER + "/" + ModelProvider.BLOCK_FOLDER + "/" + Assets.key(block).getPath()).parent(model);
			provider.simpleBlockItem(block, model);
		}
		
		public static void registerSlabAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg) {
			registerSlabAndItem(provider, blockReg, null);
		}
		
		public static void registerSlabAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg, @Nullable String textureSubPath) {
			SlabBlock block = (SlabBlock)blockReg.get();
			ModelFile[] models = Models.slabAll(provider, block, textureSubPath);
			ModelFile lower = models[0];
			ModelFile upper = models[1];
			ModelFile doubleSlab = models[2];
			/*
			for (SlabType slabType : SlabType.values()) {
				VariantBlockStateBuilder.PartialBlockstate builder = provider.getVariantBuilder(block).partialState().with(SlabBlock.TYPE, slabType);
				switch (slabType) {
					case TOP -> builder.modelForState().modelFile(upper).addModel();
					case BOTTOM -> builder.modelForState().modelFile(lower).addModel();
					case DOUBLE -> builder.modelForState().modelFile(doubleSlab).addModel();
				}
				OriMod.LOG.printf(Level.INFO, "Made slab block for %s of type SlabType=%s", blockReg.getId(), slabType.name());
			}*/
			provider.slabBlock(block, lower,upper,doubleSlab);
			OriMod.LOG.printf(Level.INFO, "Generated slab for %s", blockReg.getId());
			
			provider.simpleBlockItem(block, lower);
			OriMod.LOG.printf(Level.INFO, "Generated inventory model for slab %s", blockReg.getId());
			
		}
		
		public static void registerWallAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg) {
			registerWallAndItem(provider, blockReg, null);
		}
		
		public static void registerWallAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg, @Nullable String textureSubPath) {
			WallBlock block = (WallBlock)blockReg.get();
			/*
			ModelFile[] modelsAndItem = Models.wall(provider, block, textureSubPath);
			ModelFile post = modelsAndItem[0];
			ModelFile side = modelsAndItem[1];
			ModelFile sideTall = modelsAndItem[2];
			provider.wallBlock(block, post, side, sideTall);
			*/
			
			ResourceLocation texture = Assets.blockTexture(block, textureSubPath);
			provider.wallBlock(block, texture);
			OriMod.LOG.printf(Level.INFO, "Generated wall for %s", blockReg.getId().toString());
			
			ModelFile invMdl = provider.itemModels().wallInventory(Assets.name(block, textureSubPath), texture);
			provider.simpleBlockItem(block, invMdl);
			//provider.simpleBlockItem(block, post);
			OriMod.LOG.printf(Level.INFO, "Generated inventory model for wall %s", blockReg.getId().toString());
		}
		
		public static void registerStairsAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg) {
			registerStairsAndItem(provider, blockReg, null);
		}
		
		public static void registerStairsAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg, @Nullable String textureSubPath) {
			StairBlock block = (StairBlock)blockReg.get();
			/*
			ModelFile[] stairParts = Models.stairsAll(provider, block, textureSubPath);
			ModelFile base = stairParts[0];
			ModelFile inner = stairParts[1];
			ModelFile outer = stairParts[2];
			provider.stairsBlock(block, base, inner, outer);
			*/
			ResourceLocation texture = Assets.blockTexture(block, textureSubPath);
			provider.stairsBlock(block, texture);
			OriMod.LOG.printf(Level.INFO, "Generated stairs for %s", blockReg.getId().toString());
			
			ModelFile invMdl = provider.itemModels().stairs(Assets.name(block, textureSubPath), texture, texture, texture);
			provider.simpleBlockItem(block, invMdl);
			OriMod.LOG.printf(Level.INFO, "Generated inventory model for stairs %s", blockReg.getId().toString());
		}
		
	}
	
	public static class SpecializedCommon {
		
		
		/**
		 * Creates a block that's "inside out", or, the space it occupies renders on adjacent faces rather than as a full cube.<br/>
		 * This is mostly used for decay mycelium, which coats the surfaces of blocks. The best vanilla block to compare this to is vines.
		 * @param provider The BlockStateProvider used in datagen.
		 * @param block The block registry object to create.
		 */
		public static void registerInsideOutBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> block) {
			registerInsideOutBlockAndItem(provider, block, null);
		}
		
		/**
		 * Creates a block that's "inside out", or, the space it occupies renders on adjacent faces rather than as a full cube.<br/>
		 * This is mostly used for decay mycelium, which coats the surfaces of blocks. The best vanilla block to compare this to is vines.
		 * @param provider The BlockStateProvider used in datagen.
		 * @param block The block registry object to create.
		 * @param textureSubPath The subfolder(s) of the block's texture.
		 */
		public static void registerInsideOutBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> block, @Nullable String textureSubPath) {
			MultiPartBlockStateBuilder multiPart = provider.getMultipartBuilder(block.get());
			ModelFile model = Models.getInsideOutBlockModel(provider, block, 0f, 0.1f, textureSubPath);
			model.assertExistence();
			multiPart
				.part().modelFile(model).rotationX( 90).rotationY(180).addModel().condition(BlockStateProperties.NORTH, true).end()
				.part().modelFile(model).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.SOUTH, true).end()
				.part().modelFile(model).rotationX( 90).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).end()
				.part().modelFile(model).rotationX( 90).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).end()
				.part().modelFile(model).rotationX(180).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).end()
				.part().modelFile(model).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).end();
			
			ModelFile fullCube = Models.cubeAll(provider, block.get(), textureSubPath);
			Common.simpleBlockItem(provider, block.get(), fullCube);
			OriMod.LOG.printf(Level.INFO, "Generated inside out block at %s", model.getLocation().toString());
		}
		
		/**
		 * Registers a default full block model as well as an item for that block in a single method.
		 *
		 * @param provider       The BlockStateProvider used in datagen.
		 * @param blockReg       The block registry object to create.
		 * @param textureSubPath If the texture for this block is in a subfolder, this is the subfolder(s). Individual folders are separated with /, but the string should not begin nor end with /
		 */
		public static void registerBlockAndItemWithRenderType(BlockStateProvider provider, RegistryObject<? extends Block> blockReg, @Nullable String textureSubPath, String type) {
			Block block = blockReg.get();
			ModelFile model = Models.cubeAllWithRenderType(provider, block, type, textureSubPath);
			VariantBlockStateBuilder builder = provider.getVariantBuilder(block);
			builder.forAllStates(state -> new ConfiguredModel[] {new ConfiguredModel(model)});
			
			Common.simpleBlockItem(provider, block, model);
			OriMod.LOG.printf(Level.INFO, "Generated simple block and block-item at %s", model.getLocation().toString());
		}
		
	}
	
	public static class DecayBlockCode {
		
		public static void registerPillarBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg) {
			registerPillarBlockAndItem(provider, blockReg, null);
		}
		
		public static void registerPillarBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg, @Nullable String textureSubPath) {
			Block block = blockReg.get();
			ModelFile baseVertical = Models.cubeColumn(provider, block, textureSubPath);
			ModelFile baseHorizontal = Models.cubeColumnHorizontal(provider, block, textureSubPath);
			
			for (BlockState fullState : block.getStateDefinition().getPossibleStates()) {
				Direction.Axis axis = fullState.getValue(RotatedPillarBlock.AXIS);
				boolean allAdjacentDecayed = fullState.getValue(DecayCommon.ALL_ADJACENT_ARE_DECAY);
				int edgeSpreadRarity = fullState.getValue(DecayCommon.EDGE_DETECTION_RARITY);
				boolean isSafe = fullState.getValue(DecayLogBase.IS_SAFE);
				
				// Populate the required state.
				VariantBlockStateBuilder.PartialBlockstate basic = provider.getVariantBuilder(block).partialState()
					.with(DecayCommon.ALL_ADJACENT_ARE_DECAY, allAdjacentDecayed)
					.with(DecayCommon.EDGE_DETECTION_RARITY, edgeSpreadRarity)
					.with(RotatedPillarBlock.AXIS, axis)
					.with(DecayLogBase.IS_SAFE, isSafe);
				
				if (axis == Direction.Axis.Y) {
					basic.modelForState().modelFile(baseVertical).addModel();
				} else if (axis == Direction.Axis.Z) {
					basic.modelForState().modelFile(baseHorizontal).rotationX(90).addModel();
				} else if (axis == Direction.Axis.X) {
					basic.modelForState().modelFile(baseHorizontal).rotationX(90).rotationY(90).addModel();
				}
				
				OriMod.LOG.printf(Level.INFO, "Generating special Decay log block state where %s = %s, %s = %s, %s = %s | axis = %s at %s / %s",
				                  DecayCommon.ALL_ADJACENT_ARE_DECAY.getName(), allAdjacentDecayed,
				                  DecayCommon.EDGE_DETECTION_RARITY.getName(), edgeSpreadRarity,
				                  DecayLogBase.IS_SAFE.getName(), isSafe,
				                  axis.getName(),
				                  baseVertical.getLocation(), baseHorizontal.getLocation()
				);
			}
			
			OriMod.LOG.printf(Level.INFO, "Creating item for Decay log block.");
			Common.simpleBlockItem(provider, block, baseVertical);
			
		}
	}
	
	public static class LightTechBlockCode {
		
		public static void registerConduitBlock(BlockStateProvider provider, RegistryObject<Block> block) {
			registerConduitBlock(provider, block, null, null);
		}
		
		public static void registerConduitBlock(BlockStateProvider provider, RegistryObject<Block> block, @Nullable String commonPath) {
			registerConduitBlock(provider, block, commonPath, commonPath);
		}
		
		public static void registerConduitBlock(BlockStateProvider provider, RegistryObject<Block> block, @Nullable String connectorSubPath, @Nullable String coreSubPath) {
			MultiPartBlockStateBuilder multiPart = provider.getMultipartBuilder(block.get());
			ModelFile coreBlue = Models.get8CoreModel(provider, block, true, coreSubPath);
			ModelFile connectorBlue = Models.get8CoreConnectorModel(provider, block, true, connectorSubPath);
			ModelFile coreOrange = Models.get8CoreModel(provider, block, false, coreSubPath);
			ModelFile connectorOrange = Models.get8CoreConnectorModel(provider, block, false, connectorSubPath);
			multiPart
				.part().modelFile(coreOrange).addModel().condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.NORTH, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(  0).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(  0).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(  0).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(270).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end();
			multiPart
				.part().modelFile(coreBlue).addModel().condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.NORTH, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(  0).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(  0).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(  0).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(270).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end();
			
			Common.simpleBlockItem(provider, block.get(), coreBlue);
			OriMod.LOG.printf(Level.INFO, "Generated conduit block at [Blue=%s|%s, Orange=%s|%s]", coreBlue.getLocation().toString(), connectorBlue.getLocation().toString(), coreOrange.getLocation().toString(), connectorOrange.getLocation().toString());
		}
		
		public static void registerAirtightConduitBlock(BlockStateProvider provider, RegistryObject<Block> block, @Nullable String connectorSubPath, @Nullable String coreSubPath) {
			MultiPartBlockStateBuilder multiPart = provider.getMultipartBuilder(block.get());
			ModelFile coreBlue = Models.get8CoreModel(provider, block, true, coreSubPath);
			ModelFile connectorBlue = Models.get8CoreConnectorModel(provider, block, true, connectorSubPath);
			ModelFile coreOrange = Models.get8CoreModel(provider, block, false, coreSubPath);
			ModelFile connectorOrange = Models.get8CoreConnectorModel(provider, block, false, connectorSubPath);
			ModelFile glaalg = provider.models().cubeAll("glass", Assets.mcLoc(ModelProvider.BLOCK_FOLDER + "/glass")).renderType("translucent");//Models.cubeAll(provider, Blocks.GLASS);
			// glass? who gives a shit about glaalg? no im gonna talk about taaaalkin aabout coks, and eh, frankly the word cock sounds amazing coming out of scouts voice. cock. lemme just say it again. cock. lemme say it a little closer to the microphone. C O C K.
			
			multiPart
				.part().modelFile(glaalg).addModel().end();
			multiPart
				.part().modelFile(coreOrange).addModel().condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.NORTH, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(  0).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(  0).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(  0).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX(270).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end()
				.part().modelFile(connectorOrange).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).condition(ConnectableLightTechBlock.IS_BLUE, false).end();
			multiPart
				.part().modelFile(coreBlue).addModel().condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(  0).rotationY(  0).addModel().condition(BlockStateProperties.NORTH, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(  0).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(  0).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(  0).rotationY( 90).addModel().condition(BlockStateProperties.EAST, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX(270).rotationY(  0).addModel().condition(BlockStateProperties.UP, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end()
				.part().modelFile(connectorBlue).rotationX( 90).rotationY(  0).addModel().condition(BlockStateProperties.DOWN, true).condition(ConnectableLightTechBlock.IS_BLUE, true).end();
			
			Common.simpleBlockItem(provider, block.get(), coreBlue);
			OriMod.LOG.printf(Level.INFO, "Generated conduit block at [Blue=%s|%s, Orange=%s|%s]", coreBlue.getLocation().toString(), connectorBlue.getLocation().toString(), coreOrange.getLocation().toString(), connectorOrange.getLocation().toString());
		}
		
	}
	
	public static class ForlornDecorativeBlockCode {
		
		public static void fullBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg) {
			fullBlockAndItem(provider, blockReg, null);
		}
		
		/**
		 * A preset utility for Forlorn style blocks constructed with the help of {@link etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller}.
		 * @param blockReg
		 */
		@SuppressWarnings({"unchecked", "rawtypes"})
		public static void fullBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg, @Nullable String textureSubPath) {
			
			Block block = blockReg.get();
			BlockModelBuilder base = provider.models().cubeAll(Assets.name(block, textureSubPath) + "_base", Assets.blockTexture(block, textureSubPath, "base"));
			
			BlockModelBuilder blueActive = provider.models().cubeAll(Assets.name(block, textureSubPath) + "_blue", Assets.blockTexture(block, textureSubPath, "base"))
				.texture("emissive", Assets.blockTexture(block, textureSubPath, "blue"))
				.renderType("cutout");
			Texturing.allFacesAs(blueActive, "all");
			Texturing.allFacesAs(blueActive, "emissive");
			
			BlockModelBuilder orangeActive = provider.models().cubeAll(Assets.name(block, textureSubPath) + "_orange", Assets.blockTexture(block, textureSubPath, "base"))
				.texture("emissive", Assets.blockTexture(block, textureSubPath, "orange"))
				.renderType("cutout");
			Texturing.allFacesAs(orangeActive, "all");
			Texturing.allFacesAs(orangeActive, "emissive");
			
			
			for (BlockState fullState : block.getStateDefinition().getPossibleStates()) {
				boolean powered = fullState.getValue(ForlornAppearanceMarshaller.POWERED);
				boolean isBlue = fullState.getValue(ForlornAppearanceMarshaller.IS_BLUE);
				VariantBlockStateBuilder.PartialBlockstate variantBuilder = provider.getVariantBuilder(block).partialState()
					.with(ForlornAppearanceMarshaller.POWERED, powered)
					.with(ForlornAppearanceMarshaller.IS_BLUE, isBlue);
				
				for (Property prop : fullState.getProperties()) {
					if (prop.equals(ForlornAppearanceMarshaller.IS_BLUE) || prop.equals(ForlornAppearanceMarshaller.POWERED)) continue;
					variantBuilder = variantBuilder.with(prop, fullState.getValue(prop));
				}
				
				if (powered) {
					if (isBlue) {
						variantBuilder.modelForState().modelFile(blueActive).addModel();
					} else {
						variantBuilder.modelForState().modelFile(orangeActive).addModel();
					}
				} else {
					variantBuilder.modelForState().modelFile(base).addModel();
				}
			}
			Common.simpleBlockItem(provider, blockReg.get(), blueActive);
			OriMod.LOG.printf(Level.INFO, "Generated Forlorn Blocks at %s | %s | %s", base.getLocation().toString(), blueActive.getLocation().toString(), orangeActive.getLocation().toString());
			
		}
		
		private static void fullBlockAndItem$BlueAndPower(BlockStateProvider provider, Block block, ModelFile base, ModelFile blueActive, ModelFile orangeActive, boolean powered, boolean isBlue) {
			VariantBlockStateBuilder.PartialBlockstate variantBuilder = provider.getVariantBuilder(block).partialState()
				.with(ForlornAppearanceMarshaller.POWERED, powered)
				.with(ForlornAppearanceMarshaller.IS_BLUE, isBlue);
			
			if (powered) {
				if (isBlue) {
					variantBuilder.modelForState().modelFile(blueActive).addModel();
				} else {
					variantBuilder.modelForState().modelFile(orangeActive).addModel();
				}
			} else {
				variantBuilder.modelForState().modelFile(base).addModel();
			}
		}
		
		public static void pillarBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg) {
			pillarBlockAndItem(provider, blockReg, null);
		}
		
		public static void pillarBlockAndItem(BlockStateProvider provider, RegistryObject<? extends Block> blockReg, @Nullable String textureSubPath) {
			Block block = blockReg.get();
			ResourceLocation baseSide = Assets.blockTexture(block, textureSubPath, "base_side");
			ResourceLocation baseTop = Assets.blockTexture(block, textureSubPath, "base_end");
			ResourceLocation blueSide = Assets.blockTexture(block, textureSubPath, "blue_side");
			ResourceLocation blueTop = Assets.blockTexture(block, textureSubPath, "blue_end");
			ResourceLocation orangeSide = Assets.blockTexture(block, textureSubPath, "orange_side");
			ResourceLocation orangeTop = Assets.blockTexture(block, textureSubPath, "orange_end");
			
			BlockModelBuilder baseVertical = provider.models().cubeColumn(Assets.name(block, textureSubPath) + "_base", baseSide, baseTop);
			
			BlockModelBuilder blueVertical = provider.models().cubeColumn(Assets.name(block, textureSubPath) + "_blue", baseSide, baseTop)
				.texture("side_emissive", blueSide)
				.texture("end_emissive", blueTop)
				.renderType("cutout");
			Texturing.sideFacesAs(blueVertical, "side");
			Texturing.endFacesAs(blueVertical, "end");
			Texturing.sideFacesAs(blueVertical, "side_emissive");
			Texturing.endFacesAs(blueVertical, "end_emissive");
			
			BlockModelBuilder orangeVertical = provider.models().cubeColumn(Assets.name(block, textureSubPath) + "_orange", baseSide, baseTop)
				.texture("side_emissive", orangeSide)
				.texture("end_emissive", orangeTop)
				.renderType("cutout");
			Texturing.sideFacesAs(orangeVertical, "side");
			Texturing.endFacesAs(orangeVertical, "end");
			Texturing.sideFacesAs(orangeVertical, "side_emissive");
			Texturing.endFacesAs(orangeVertical, "end_emissive");
			
			BlockModelBuilder baseHorizontal = provider.models().cubeColumnHorizontal(Assets.name(block, textureSubPath) + "_horizontal_base", baseSide, baseTop);
			
			BlockModelBuilder blueHorizontal = provider.models().cubeColumn(Assets.name(block, textureSubPath) + "_horizontal_blue", baseSide, baseTop)
				.texture("side_emissive", blueSide)
				.texture("end_emissive", blueTop)
				.renderType("cutout");
			Texturing.sideFacesAs(blueHorizontal, "side");
			Texturing.endFacesAs(blueHorizontal, "end");
			Texturing.sideFacesAs(blueHorizontal, "side_emissive");
			Texturing.endFacesAs(blueHorizontal, "end_emissive");
			
			BlockModelBuilder orangeHorizontal = provider.models().cubeColumn(Assets.name(block, textureSubPath) + "_horizontal_orange", baseSide, baseTop)
				.texture("side_emissive", orangeSide)
				.texture("end_emissive", orangeTop)
				.renderType("cutout");
			Texturing.sideFacesAs(orangeHorizontal, "side");
			Texturing.endFacesAs(orangeHorizontal, "end");
			Texturing.sideFacesAs(orangeHorizontal, "side_emissive");
			Texturing.endFacesAs(orangeHorizontal, "end_emissive");
			
			for (BlockState fullState : block.getStateDefinition().getPossibleStates()) {
				
				Direction.Axis axis = fullState.getValue(RotatedPillarBlock.AXIS);
				boolean powered = fullState.getValue(ForlornAppearanceMarshaller.POWERED);
				boolean isBlue = fullState.getValue(ForlornAppearanceMarshaller.IS_BLUE);
				
				// Populate the required state.
				VariantBlockStateBuilder.PartialBlockstate variantBuilder = provider.getVariantBuilder(block).partialState().with(RotatedPillarBlock.AXIS, axis).with(ForlornAppearanceMarshaller.POWERED, powered).with(ForlornAppearanceMarshaller.IS_BLUE, isBlue);
				
				ModelFile verticalTarget = !powered ? baseVertical : (isBlue ? blueVertical : orangeVertical);
				ModelFile horizontalTarget = !powered ? baseHorizontal : (isBlue ? blueHorizontal : orangeHorizontal);
				
				if (axis == Direction.Axis.Y) {
					variantBuilder.modelForState().modelFile(verticalTarget).addModel();
				} else if (axis == Direction.Axis.Z) {
					variantBuilder.modelForState().modelFile(horizontalTarget).rotationX(90).addModel();
				} else if (axis == Direction.Axis.X) {
					variantBuilder.modelForState().modelFile(horizontalTarget).rotationX(90).rotationY(90).addModel();
				}
				
				OriMod.LOG.printf(Level.INFO, "Generating special Forlorn pillar block state where %s = %s, %s = %s | axis = %s at %s / %s",
				                  ForlornAppearanceMarshaller.POWERED.getName(), powered,
				                  ForlornAppearanceMarshaller.IS_BLUE.getName(), isBlue,
				                  axis.getName(),
				                  verticalTarget.getLocation(), horizontalTarget.getLocation()
				);
			}
			OriMod.LOG.printf(Level.INFO, "Creating item for Forlorn pillar block.");
			Common.simpleBlockItem(provider, block, blueVertical);
		}
	}
	
	private static class Models {
		
		/**
		 * Returns a model suited for the inside out block type.
		 * @param provider The BlockStateProvider used in datagen.
		 * @param block The block registry to use.
		 * @param surfaceOffset How far away from the surface that the plane covering a specific surface is. Useful mostly for 2D models (thickness=0)
		 * @param thickness The depth of each surface plane. Looks a bit better, but also has more to render. This can be used to mitigate z-fighting without introducing gaps on outward-facing corners.
		 * @return A model for an inside-out block.
		 */
		public static ModelFile getInsideOutBlockModel(BlockStateProvider provider, RegistryObject<? extends Block> block, float surfaceOffset, float thickness) {
			return getInsideOutBlockModel(provider, block, surfaceOffset, thickness, null);
		}
		
		/**
		 * Returns a model suited for the inside out block type.
		 * @param provider The BlockStateProvider used in datagen.
		 * @param block The block registry to use.
		 * @param surfaceOffset How far away from the surface that the plane covering a specific surface is. Useful mostly for 2D models (thickness=0)
		 * @param thickness The depth of each surface plane. Looks a bit better, but also has more to render. This can be used to mitigate z-fighting without introducing gaps on outward-facing corners.
		 * @param textureSubPath The subdirectory holding texture images.
		 * @return A model for an inside-out block.
		 */
		@SuppressWarnings("rawtypes")
		public static ModelFile getInsideOutBlockModel(BlockStateProvider provider, RegistryObject<? extends Block> block, float surfaceOffset, float thickness, @Nullable String textureSubPath) {
			ResourceLocation texturePath = Assets.blockTexture(block.get(), textureSubPath);
			BlockModelBuilder blockBuilder = provider.models().cubeAll(texturePath.getPath(), texturePath);
			blockBuilder.ao(false).texture("all", texturePath);
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
		
		public static ModelFile get8CoreModel(BlockStateProvider provider, RegistryObject<Block> block, boolean isBlue) {
			return get8CoreModel(provider, block, isBlue, null);
		}
		
		public static ModelFile get8CoreModel(BlockStateProvider provider, RegistryObject<Block> block, boolean isBlue, @Nullable String textureSubPath) {
			String colorSuffix = isBlue ? "-blue" : "-orange";
			ResourceLocation texture = Assets.blockTexture(block.get(), textureSubPath, "core");
			ResourceLocation glowTexture = Assets.blockTexture(block.get(), textureSubPath, "core" + colorSuffix);
			
			BlockModelBuilder blockBuilder = provider.models().cubeAll(texture.getPath() + colorSuffix, texture)
				.texture("glow", glowTexture)
				.renderType("cutout");
			blockBuilder.element()
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
			.end()
			.element()
				.from(4f, 4f, 4f)
				.to(12f, 12f, 12f)
				.face(Direction.NORTH)
				.uvs(0, 0, 16, 16)
				.texture("#glow")
				.emissive()
				.end()
				.face(Direction.SOUTH)
				.uvs(0, 0, 16, 16)
				.texture("#glow")
				.emissive()
				.end()
				.face(Direction.EAST)
				.uvs(0, 0, 16, 16)
				.texture("#glow")
				.emissive()
				.end()
				.face(Direction.WEST)
				.uvs(0, 0, 16, 16)
				.texture("#glow")
				.emissive()
				.end()
				.face(Direction.UP)
				.uvs(0, 0, 16, 16)
				.texture("#glow")
				.emissive()
				.end()
				.face(Direction.DOWN)
				.uvs(0, 0, 16, 16)
				.texture("#glow")
				.emissive()
				.end()
			.end();
				
			return blockBuilder;
		}
		
		public static ModelFile get8CoreConnectorModel(BlockStateProvider provider, RegistryObject<Block> block, boolean isBlue) {
			return get8CoreConnectorModel(provider, block, isBlue, null);
		}
		
		public static ModelFile get8CoreConnectorModel(BlockStateProvider provider, RegistryObject<Block> block, boolean isBlue, @Nullable String textureSubPath) {
			String colorSuffix = isBlue ? "-blue" : "-orange";
			Block blockObj = block.get();
			
			ResourceLocation connectionSurface = Assets.blockTexture(blockObj, textureSubPath, "connector-core");
			ResourceLocation sides = Assets.blockTexture(blockObj, textureSubPath, "connector-side");
			
			ResourceLocation connectionSurfaceGlow = Assets.blockTexture(blockObj, textureSubPath, "connector-core" + colorSuffix);
			ResourceLocation sidesGlow = Assets.blockTexture(blockObj, textureSubPath, "connector-side" + colorSuffix);
			
			BlockModelBuilder blockBuilder = provider.models().withExistingParent(Assets.name(blockObj, textureSubPath) + colorSuffix, Assets.mcLoc("cube"))
				.texture("up", sides)
				.texture("down", sides)
				.texture("east", sides)
				.texture("west", sides)
				.texture("north", connectionSurface)
				.texture("south", connectionSurface)
				.texture("upglow", sidesGlow)
				.texture("downglow", sidesGlow)
				.texture("eastglow", sidesGlow)
				.texture("westglow", sidesGlow)
				.texture("northglow", connectionSurfaceGlow)
				.texture("southglow", connectionSurfaceGlow)
				.renderType("cutout");
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
			.end()
			.element()
				.from(4f, 4f, 0f)
				.to(12f, 12f, 4f)
				.face(Direction.NORTH)
				.emissive()
				.uvs(0, 0, 16, 16)
				.texture("#northglow")
				.end()
				.face(Direction.SOUTH)
				.emissive()
				.uvs(0, 0, 16, 16)
				.texture("#southglow")
				.end()
				.face(Direction.EAST)
				.emissive()
				.uvs(0, 0, 16, 16)
				.texture("#eastglow")
				.end()
				.face(Direction.WEST)
				.emissive()
				.uvs(0, 0, 16, 16)
				.texture("#westglow")
				.end()
				.face(Direction.UP)
				.emissive()
				.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90)
				.uvs(0, 0, 16, 16)
				.texture("#upglow")
				.end()
				.face(Direction.DOWN)
				.emissive()
				.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90)
				.uvs(0, 0, 16, 16)
				.texture("#downglow")
			.end();
			return blockBuilder;
		}
		
		public static ModelFile cubeAllEmissive(BlockStateProvider provider, Block block, @Nullable String textureSubPath) {
			return provider.models().cubeAll(Assets.name(block, textureSubPath), Assets.blockTexture(block, textureSubPath)).texture("glow", Assets.blockTexture(block, textureSubPath, "glow"));
		}
		
		public static ModelFile cubeAllEmissive(BlockStateProvider provider, Block block) {
			return cubeAllEmissive(provider, block, null);
		}
		
		public static ModelFile cubeAll(BlockStateProvider provider, Block block, @Nullable String textureSubPath) {
			if (textureSubPath == null) return provider.cubeAll(block);
			return provider.models().cubeAll(Assets.name(block, textureSubPath), Assets.blockTexture(block, textureSubPath));
		}
		
		public static ModelFile cubeAll(BlockStateProvider provider, Block block) {
			return provider.cubeAll(block);
		}
		
		public static ModelFile cubeAllWithRenderType(BlockStateProvider provider, Block block, String type, @Nullable String textureSubPath) {
			return provider.models().cubeAll(Assets.name(block, textureSubPath), Assets.blockTexture(block, textureSubPath)).renderType(type);
		}
		
		public static ModelFile cubeAllWithRenderType(BlockStateProvider provider, Block block, String type) {
			return cubeAllWithRenderType(provider, block, type, null);
		}
		
		public static ModelFile cubeColumn(BlockStateProvider provider, Block block, @Nullable String textureSubPath) {
			return provider.models().cubeColumn(Assets.name(block, textureSubPath) + "_vertical", Assets.blockTexture(block, textureSubPath), Assets.blockTexture(block, textureSubPath, "top"));
		}
		
		public static ModelFile cubeColumn(BlockStateProvider provider, Block block) {
			return cubeColumn(provider, block, null);
		}
		
		public static ModelFile cubeColumnHorizontal(BlockStateProvider provider, Block block, @Nullable String textureSubPath) {
			return provider.models().cubeColumnHorizontal(Assets.name(block, textureSubPath) + "_horizontal", Assets.blockTexture(block, textureSubPath), Assets.blockTexture(block, textureSubPath, "top"));
		}
		
		public static ModelFile cubeColumnHorizontal(BlockStateProvider provider, Block block) {
			return cubeColumnHorizontal(provider, block, null);
		}
		
		public static ModelFile[] slab(BlockStateProvider provider, SlabBlock slabBlock) {
			return slab(provider, slabBlock, null);
		}
		
		public static ModelFile[] slab(BlockStateProvider provider, SlabBlock slabBlock, @Nullable String textureSubPath) {
			return new ModelFile[] {
				provider.models().slab(Assets.name(slabBlock, textureSubPath) + "_bottom", Assets.blockTexture(slabBlock, textureSubPath, "side"), Assets.blockTexture(slabBlock, textureSubPath, "bottom"), Assets.blockTexture(slabBlock, textureSubPath, "top")),
				provider.models().slabTop(Assets.name(slabBlock, textureSubPath) + "_top", Assets.blockTexture(slabBlock, textureSubPath, "side"), Assets.blockTexture(slabBlock, textureSubPath, "bottom"), Assets.blockTexture(slabBlock, textureSubPath, "top")),
				provider.models().cubeBottomTop(Assets.name(slabBlock, textureSubPath) + "_double", Assets.blockTexture(slabBlock, textureSubPath, "side"), Assets.blockTexture(slabBlock, textureSubPath, "bottom"), Assets.blockTexture(slabBlock, textureSubPath, "top"))
			};
		}
		
		
		public static ModelFile[] slabAll(BlockStateProvider provider, SlabBlock slabBlock) {
			return slabAll(provider, slabBlock, null);
		}
		
		public static ModelFile[] slabAll(BlockStateProvider provider, SlabBlock slabBlock, @Nullable String textureSubPath) {
			ResourceLocation tex = Assets.blockTexture(slabBlock, textureSubPath);
			return new ModelFile[] {
				provider.models().slab(Assets.name(slabBlock, textureSubPath) + "_bottom", tex, tex, tex),
				provider.models().slabTop(Assets.name(slabBlock, textureSubPath) + "_top", tex, tex, tex),
				provider.models().cubeAll(Assets.name(slabBlock, textureSubPath) + "_double", tex)
			};
		}
		
		public static ModelFile[] wall(BlockStateProvider provider, WallBlock wallBlock) {
			return wall(provider, wallBlock, null);
		}
		
		public static ModelFile[] wall(BlockStateProvider provider, WallBlock wallBlock, @Nullable String textureSubPath) {
			return new ModelFile[] {
				provider.models().wallPost(Assets.name(wallBlock, textureSubPath), Assets.blockTexture(wallBlock, textureSubPath)),
				provider.models().wallSide(Assets.name(wallBlock, textureSubPath), Assets.blockTexture(wallBlock, textureSubPath)),
				provider.models().wallSideTall(Assets.name(wallBlock, textureSubPath), Assets.blockTexture(wallBlock, textureSubPath))
			};
		}
		
		public static ModelFile[] stairs(BlockStateProvider provider, StairBlock stairBlock) {
			return stairs(provider, stairBlock, null);
		}
		
		public static ModelFile[] stairs(BlockStateProvider provider, StairBlock stairBlock, @Nullable String textureSubPath) {
			return new ModelFile[] {
				provider.models().stairs(Assets.name(stairBlock, textureSubPath), Assets.blockTexture(stairBlock, textureSubPath, "side"), Assets.blockTexture(stairBlock, textureSubPath, "bottom"), Assets.blockTexture(stairBlock, textureSubPath, "top")),
				provider.models().stairsInner(Assets.name(stairBlock, textureSubPath), Assets.blockTexture(stairBlock, textureSubPath, "side"), Assets.blockTexture(stairBlock, textureSubPath, "bottom"), Assets.blockTexture(stairBlock, textureSubPath, "top")),
				provider.models().stairsOuter(Assets.name(stairBlock, textureSubPath), Assets.blockTexture(stairBlock, textureSubPath, "side"), Assets.blockTexture(stairBlock, textureSubPath, "bottom"), Assets.blockTexture(stairBlock, textureSubPath, "top"))
			};
		}
		
		
		public static ModelFile[] stairsAll(BlockStateProvider provider, StairBlock stairBlock) {
			return stairs(provider, stairBlock, null);
		}
		
		public static ModelFile[] stairsAll(BlockStateProvider provider, StairBlock stairBlock, @Nullable String textureSubPath) {
			ResourceLocation tex = Assets.blockTexture(stairBlock, textureSubPath);
			return new ModelFile[] {
				provider.models().stairs(Assets.name(stairBlock, textureSubPath), tex, tex, tex),
				provider.models().stairsInner(Assets.name(stairBlock, textureSubPath), tex, tex, tex),
				provider.models().stairsOuter(Assets.name(stairBlock, textureSubPath), tex, tex, tex)
			};
		}
		
	}
	
	private static class Texturing {
		
		public static void allFacesAs(ModelBuilder<BlockModelBuilder> builder, String texture) {
			ModelBuilder<BlockModelBuilder>.ElementBuilder element = builder.element().from(0, 0, 0).to(16, 16, 16);;
			for (Direction dir : Direction.values()) {
				singleFaceAs(element, dir, texture);
			}
			element.end();
		}
		
		public static void sideFacesAs(ModelBuilder<BlockModelBuilder> builder, String texture) {
			ModelBuilder<BlockModelBuilder>.ElementBuilder element = builder.element().from(0, 0, 0).to(16, 16, 16);;
			for (Direction dir : Direction.values()) {
				if (dir == Direction.UP || dir == Direction.DOWN) continue;
				singleFaceAs(element, dir, texture);
			}
			element.end();
		}
		
		public static void endFacesAs(ModelBuilder<BlockModelBuilder> builder, String texture) {
			ModelBuilder<BlockModelBuilder>.ElementBuilder element = builder.element().from(0, 0, 0).to(16, 16, 16);;
			for (Direction dir : Direction.values()) {
				if (dir == Direction.UP || dir == Direction.DOWN) {
					singleFaceAs(element, dir, texture);
				}
			}
			element.end();
		}
		
		public static void singleFaceAs(ModelBuilder<BlockModelBuilder>.ElementBuilder element, Direction dir, String texture) {
			element.face(dir)
				.cullface(dir)
				.texture("#" + texture)
				.emissive()
				.end();
		}
		
	}
}
