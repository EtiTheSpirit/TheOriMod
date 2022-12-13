package etithespirit.orimod.datagen.block;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.decay.DecayCommon;
import etithespirit.orimod.common.block.decay.flora.DecayLogBase;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.registry.BlockRegistry;
import etithespirit.orimod.registry.FluidRegistry;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

/***/
public final class GenerateBlockModels extends BlockStateProvider {
	
	/***
	 * Create a new instance of the Block Model Generator
	 * @param gen The generator itself.
	 * @param exFileHelper A tool to modify files.
	 */
	public GenerateBlockModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, OriMod.MODID, exFileHelper);
	}
	
	private ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}
	
	private ResourceLocation key(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}
	
	private String name(Block block) {
		return key(block).getPath();
	}
	
	@Override
	protected void registerStatesAndModels() {
		OriMod.LOG.printf(Level.INFO, "Starting block model generation.");
		
		BlockGenerationTools.Common.registerBlockAndItem(this, BlockRegistry.DECAY_DIRT_MYCELIUM, "decay/generic");
		BlockGenerationTools.Common.registerBlockAndItem(this, BlockRegistry.DECAY_PLANTMATTER_MYCELIUM, "decay/generic");
		BlockGenerationTools.DecayBlockCode.registerPillarBlockAndItem(this, BlockRegistry.DECAY_LOG, "decay/logs");
		BlockGenerationTools.DecayBlockCode.registerPillarBlockAndItem(this, BlockRegistry.DECAY_STRIPPED_LOG, "decay/logs");
		BlockGenerationTools.SpecializedCommon.registerInsideOutBlockAndItem(this, BlockRegistry.DECAY_SURFACE_MYCELIUM, "decay/generic");
		BlockGenerationTools.Common.registerBlockAndItem(this, FluidRegistry.DECAY_POISON, null);
		
		BlockGenerationTools.Common.registerBlockAndItem(this, BlockRegistry.FORLORN_STONE, "forlorn_stone/base");
		BlockGenerationTools.Common.registerBlockAndItem(this, BlockRegistry.FORLORN_STONE_BRICKS, "forlorn_stone/base");
		BlockGenerationTools.ForlornDecorativeBlockCode.fullBlockAndItem(this, BlockRegistry.FORLORN_STONE_OMNI, "forlorn_stone/omni");
		BlockGenerationTools.ForlornDecorativeBlockCode.pillarBlockAndItem(this, BlockRegistry.FORLORN_STONE_LINE, "forlorn_stone/line");
		
		BlockGenerationTools.SpecializedCommon.registerBlockAndItemWithRenderType(this, BlockRegistry.HARDLIGHT_GLASS, "light", "translucent");
		
		BlockGenerationTools.LightTechBlockCode.registerConduitBlock(this, BlockRegistry.LIGHT_CONDUIT, "techblocks/conduits/connector", "techblocks/conduits/core");
		BlockGenerationTools.LightTechBlockCode.registerAirtightConduitBlock(this, BlockRegistry.SOLID_LIGHT_CONDUIT, "techblocks/conduits/solidconnector", "techblocks/conduits/solidcore");
		BlockGenerationTools.ForlornDecorativeBlockCode.fullBlockAndItem(this, BlockRegistry.LIGHT_CAPACITOR, "techblocks/storage");
		BlockGenerationTools.ForlornDecorativeBlockCode.fullBlockAndItem(this, BlockRegistry.INFINITE_LIGHT_SOURCE, "techblocks/storage");
		BlockGenerationTools.ForlornDecorativeBlockCode.fullBlockAndItem(this, BlockRegistry.LIGHT_TO_RF, "techblocks/conversion");
		BlockGenerationTools.ForlornDecorativeBlockCode.fullBlockAndItem(this, BlockRegistry.LIGHT_TO_REDSTONE_SIGNAL, "techblocks/conversion");
		BlockGenerationTools.ForlornDecorativeBlockCode.fullBlockAndItem(this, BlockRegistry.SOLAR_ENERGY_BLOCK, "techblocks/conversion");
		BlockGenerationTools.ForlornDecorativeBlockCode.fullBlockAndItem(this, BlockRegistry.LIGHT_REPAIR_BOX, "techblocks/conversion");
		
		OriMod.LOG.printf(Level.INFO, "Block models registered!");
	}
	
}