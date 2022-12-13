package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;

import etithespirit.orimod.common.block.decay.flora.DecayLogBlock;
import etithespirit.orimod.common.block.decay.flora.DecayStrippedLogBlock;
import etithespirit.orimod.common.block.decay.world.DecayDirtMyceliumBlock;
import etithespirit.orimod.common.block.decay.world.DecayPlantMatterBlock;
import etithespirit.orimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.orimod.common.block.light.connection.SolidLightConduitBlock;
import etithespirit.orimod.common.block.light.decoration.HardLightBlock;
import etithespirit.orimod.common.block.light.LightCapacitorBlock;
import etithespirit.orimod.common.block.light.LightToRFGeneratorBlock;
import etithespirit.orimod.common.block.light.LightToRedstoneSignalBlock;
import etithespirit.orimod.common.block.light.connection.LightConduitBlock;
import etithespirit.orimod.common.block.light.creative.InfiniteSourceLightBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornStoneBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornStoneLineBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornStoneOmniBlock;
import etithespirit.orimod.common.block.light.generation.SolarGeneratorBlock;
import etithespirit.orimod.common.block.light.interaction.LightRepairBoxBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the registration of blocks for the mod.
 */
public final class BlockRegistry {
	
	/** Used in the item registry to generate BlockItems. */
	public static final List<RegistryObject<? extends Block>> BLOCKS_TO_REGISTER = new ArrayList<>(64);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DEFERRED REGISTERS PROVIDED BY FORGE
	static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OriMod.MODID);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// BLOCKS
	/** */ public static final RegistryObject<Block> DECAY_DIRT_MYCELIUM = BLOCKS.register("decay_dirt_mycelium", DecayDirtMyceliumBlock::new);
	/** */ public static final RegistryObject<Block> DECAY_PLANTMATTER_MYCELIUM = BLOCKS.register("decay_plantmatter_mycelium", DecayPlantMatterBlock::new);
	/** */ public static final RegistryObject<Block> DECAY_LOG = BLOCKS.register("decay_log", DecayLogBlock::new);
	/** */ public static final RegistryObject<Block> DECAY_STRIPPED_LOG = BLOCKS.register("stripped_decay_log", DecayStrippedLogBlock::new);
	/** */ public static final RegistryObject<Block> DECAY_SURFACE_MYCELIUM = BLOCKS.register("decay_surface_mycelium", DecaySurfaceMyceliumBlock::new);
	
	/** */ public static final RegistryObject<Block> FORLORN_STONE = BLOCKS.register("forlorn_stone", ForlornStoneBlock::new);
	/** */ public static final RegistryObject<Block> FORLORN_STONE_BRICKS = BLOCKS.register("forlorn_stone_bricks", ForlornStoneBlock::new); // Yes, share.
	/** */ public static final RegistryObject<Block> FORLORN_STONE_LINE = BLOCKS.register("forlorn_stone_line", ForlornStoneLineBlock::new);
	/** */ public static final RegistryObject<Block> FORLORN_STONE_OMNI = BLOCKS.register("forlorn_stone_omni", ForlornStoneOmniBlock::new);
	
	/** */ public static final RegistryObject<Block> HARDLIGHT_GLASS = BLOCKS.register("hardlight_block", HardLightBlock::new);
	/** */ public static final RegistryObject<Block> LIGHT_REPAIR_BOX = BLOCKS.register("light_repair_box", LightRepairBoxBlock::new);
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// TILE ENTITY BLOCKS
	/** */ public static final RegistryObject<Block> LIGHT_CAPACITOR = BLOCKS.register("light_capacitor", LightCapacitorBlock::new);
	/** */ public static final RegistryObject<Block> LIGHT_CONDUIT = BLOCKS.register("light_conduit", LightConduitBlock::new);
	/** */ public static final RegistryObject<Block> SOLID_LIGHT_CONDUIT = BLOCKS.register("solid_light_conduit", SolidLightConduitBlock::new);
	/** */ public static final RegistryObject<Block> LIGHT_TO_RF = BLOCKS.register("light_to_rf", LightToRFGeneratorBlock::new);
	/** */ public static final RegistryObject<Block> INFINITE_LIGHT_SOURCE = BLOCKS.register("infinite_light_source", InfiniteSourceLightBlock::new);
	/** */ public static final RegistryObject<Block> LIGHT_TO_REDSTONE_SIGNAL = BLOCKS.register("light_to_redstone_signal", LightToRedstoneSignalBlock::new);
	/** */ public static final RegistryObject<Block> SOLAR_ENERGY_BLOCK = BLOCKS.register("solar_generator", SolarGeneratorBlock::new);
	/** */ //public static final RegistryObject<Block> LIGHT_DEBUGGER = BLOCKS.register("light_debugger", LightDebuggerBlock::new);
	
	static {
		BLOCKS_TO_REGISTER.add(DECAY_DIRT_MYCELIUM);
		BLOCKS_TO_REGISTER.add(DECAY_PLANTMATTER_MYCELIUM);
		BLOCKS_TO_REGISTER.add(DECAY_LOG);
		BLOCKS_TO_REGISTER.add(DECAY_STRIPPED_LOG);
		BLOCKS_TO_REGISTER.add(DECAY_SURFACE_MYCELIUM);
		
		BLOCKS_TO_REGISTER.add(FORLORN_STONE);
		BLOCKS_TO_REGISTER.add(FORLORN_STONE_BRICKS);
		BLOCKS_TO_REGISTER.add(FORLORN_STONE_LINE);
		BLOCKS_TO_REGISTER.add(FORLORN_STONE_OMNI);
		
		BLOCKS_TO_REGISTER.add(HARDLIGHT_GLASS);
		BLOCKS_TO_REGISTER.add(LIGHT_REPAIR_BOX);
		
		BLOCKS_TO_REGISTER.add(LIGHT_CAPACITOR);
		BLOCKS_TO_REGISTER.add(LIGHT_CONDUIT);
		BLOCKS_TO_REGISTER.add(SOLID_LIGHT_CONDUIT);
		BLOCKS_TO_REGISTER.add(LIGHT_TO_RF);
		BLOCKS_TO_REGISTER.add(INFINITE_LIGHT_SOURCE);
		BLOCKS_TO_REGISTER.add(LIGHT_TO_REDSTONE_SIGNAL);
		BLOCKS_TO_REGISTER.add(SOLAR_ENERGY_BLOCK);
	}
	
	/***/
	public static void registerAll() {
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
