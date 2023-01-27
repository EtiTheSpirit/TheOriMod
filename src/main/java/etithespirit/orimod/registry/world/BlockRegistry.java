package etithespirit.orimod.registry.world;

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
import etithespirit.orimod.common.block.light.generation.ThermalGeneratorBlock;
import etithespirit.orimod.common.block.light.interaction.LightRepairBoxBlock;
import etithespirit.orimod.common.block.other.GorlekSteelBlock;
import etithespirit.orimod.common.block.other.GorlekNetheriteAlloyBlock;
import etithespirit.orimod.common.block.other.GorlekOreBlock;
import etithespirit.orimod.common.block.other.RawGorlekOreBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * This class manages the registration of blocks for the mod.
 */
public final class BlockRegistry {
	
	/** Used in the item registry to generate BlockItems. */
	// public static final List<RegistryObject<? extends Block>> BLOCKS_TO_REGISTER = new ArrayList<>(64);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DEFERRED REGISTERS PROVIDED BY FORGE
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OriMod.MODID);
	
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
	
	/** */ public static final RegistryObject<Block> FORLORN_STONE_BRICK_SLAB = BLOCKS.register("forlorn_stone_brick_slab", ForlornStoneBlock.Slab::new);
	/** */ public static final RegistryObject<Block> FORLORN_STONE_BRICK_STAIRS = BLOCKS.register("forlorn_stone_brick_stairs", ForlornStoneBlock.Stairs::new);
	/** */ public static final RegistryObject<Block> FORLORN_STONE_BRICK_WALL = BLOCKS.register("forlorn_stone_brick_wall", ForlornStoneBlock.Wall::new);
	
	/** */ public static final RegistryObject<Block> HARDLIGHT_GLASS = BLOCKS.register("hardlight_block", HardLightBlock::new);
	/** */ public static final RegistryObject<Block> LIGHT_REPAIR_BOX = BLOCKS.register("light_repair_box", LightRepairBoxBlock::new);
	
	/** */ public static final RegistryObject<Block> GORLEK_ORE = BLOCKS.register("gorlek_ore", GorlekOreBlock::new);
	/** */ public static final RegistryObject<Block> RAW_GORLEK_ORE_BLOCK = BLOCKS.register("raw_gorlek_ore_block", RawGorlekOreBlock::new);
	/** */ public static final RegistryObject<Block> GORLEK_STEEL_BLOCK = BLOCKS.register("gorlek_steel_block", GorlekSteelBlock::new);
	/** */ public static final RegistryObject<Block> GORLEK_NETHERITE_ALLOY_BLOCK = BLOCKS.register("gorlek_netherite_alloy_block", GorlekNetheriteAlloyBlock::new);
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// TILE ENTITY BLOCKS
	/** */ public static final RegistryObject<Block> LIGHT_CAPACITOR = BLOCKS.register("light_capacitor", LightCapacitorBlock::new);
	/** */ public static final RegistryObject<Block> LIGHT_CONDUIT = BLOCKS.register("light_conduit", LightConduitBlock::new);
	/** */ public static final RegistryObject<Block> SOLID_LIGHT_CONDUIT = BLOCKS.register("solid_light_conduit", SolidLightConduitBlock::new);
	/** */ public static final RegistryObject<Block> LIGHT_TO_RF = BLOCKS.register("light_to_rf", LightToRFGeneratorBlock::new);
	/** */ public static final RegistryObject<Block> INFINITE_LIGHT_SOURCE = BLOCKS.register("infinite_light_source", InfiniteSourceLightBlock::new);
	/** */ public static final RegistryObject<Block> LIGHT_TO_REDSTONE_SIGNAL = BLOCKS.register("light_to_redstone_signal", LightToRedstoneSignalBlock::new);
	/** */ public static final RegistryObject<Block> SOLAR_ENERGY_BLOCK = BLOCKS.register("solar_generator", SolarGeneratorBlock::new);
	/** */ public static final RegistryObject<Block> THERMAL_ENERGY_BLOCK = BLOCKS.register("thermal_generator", ThermalGeneratorBlock::new);
	/** */ //public static final RegistryObject<Block> LIGHT_DEBUGGER = BLOCKS.register("light_debugger", LightDebuggerBlock::new);
	
	static {
		/*
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
		
		BLOCKS_TO_REGISTER.add(GORLEK_ORE);
		BLOCKS_TO_REGISTER.add(RAW_GORLEK_ORE_BLOCK);
		BLOCKS_TO_REGISTER.add(GORLEK_METAL_BLOCK);
		BLOCKS_TO_REGISTER.add(GORLEK_NETHERITE_ALLOY_BLOCK);
		
		BLOCKS_TO_REGISTER.add(LIGHT_CAPACITOR);
		BLOCKS_TO_REGISTER.add(LIGHT_CONDUIT);
		BLOCKS_TO_REGISTER.add(SOLID_LIGHT_CONDUIT);
		BLOCKS_TO_REGISTER.add(LIGHT_TO_RF);
		BLOCKS_TO_REGISTER.add(INFINITE_LIGHT_SOURCE);
		BLOCKS_TO_REGISTER.add(LIGHT_TO_REDSTONE_SIGNAL);
		BLOCKS_TO_REGISTER.add(SOLAR_ENERGY_BLOCK);
		BLOCKS_TO_REGISTER.add(THERMAL_ENERGY_BLOCK);
		*/
	}
	
	/***/
	public static void registerAll() {
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
