package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;

import etithespirit.orimod.common.block.decay.DecayLiquidBlock;
import etithespirit.orimod.common.block.decay.flora.DecayLogBlock;
import etithespirit.orimod.common.block.decay.flora.DecayStrippedLogBlock;
import etithespirit.orimod.common.block.decay.world.DecayDirtMyceliumBlock;
import etithespirit.orimod.common.block.decay.world.DecayPlantMatterBlock;
import etithespirit.orimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.orimod.common.block.light.LightCapacitorBlock;
import etithespirit.orimod.common.block.light.LightConduitBlock;
import etithespirit.orimod.common.material.ExtendedMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
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
	public static final List<RegistryObject<Block>> BLOCKS_TO_REGISTER = new ArrayList<>();
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DEFERRED REGISTERS PROVIDED BY FORGE
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OriMod.MODID);
	private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, OriMod.MODID);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// BLOCKS
	/** */ public static final RegistryObject<Block> DECAY_DIRT_MYCELIUM = BLOCKS.register("decay_dirt_mycelium", DecayDirtMyceliumBlock::new);
	/** */ public static final RegistryObject<Block> DECAY_PLANTMATTER_MYCELIUM = BLOCKS.register("decay_plantmatter_mycelium", DecayPlantMatterBlock::new);
	/** */ public static final RegistryObject<Block> DECAY_LOG = BLOCKS.register("decay_log", DecayLogBlock::new);
	/** */ public static final RegistryObject<Block> DECAY_STRIPPED_LOG = BLOCKS.register("stripped_decay_log", DecayStrippedLogBlock::new);
	/** */ public static final RegistryObject<Block> DECAY_SURFACE_MYCELIUM = BLOCKS.register("decay_surface_mycelium", DecaySurfaceMyceliumBlock::new);
	
	/** */ public static final RegistryObject<Block> LIGHT_CONDUIT = BLOCKS.register("light_conduit", LightConduitBlock::new);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// TILE ENTITY BLOCKS
	/** */ public static final RegistryObject<Block> LIGHT_CAPACITOR = BLOCKS.register("light_capacitor", LightCapacitorBlock::new);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// BLOCKS WRAPPING FLUIDS
	public static final RegistryObject<Block> DECAY_POISON = BLOCKS.register("decay_poison", () -> new DecayLiquidBlock(() -> (FlowingFluid)FluidRegistry.DECAY_FLUID_STATIC.get(), BlockBehaviour.Properties.of(ExtendedMaterials.DECAY).noCollission().strength(100).noDrops(), true));
	
	
	
	static {
		BLOCKS_TO_REGISTER.add(DECAY_DIRT_MYCELIUM);
		BLOCKS_TO_REGISTER.add(DECAY_PLANTMATTER_MYCELIUM);
		BLOCKS_TO_REGISTER.add(DECAY_LOG);
		BLOCKS_TO_REGISTER.add(DECAY_STRIPPED_LOG);
		BLOCKS_TO_REGISTER.add(DECAY_SURFACE_MYCELIUM);
		
		BLOCKS_TO_REGISTER.add(LIGHT_CONDUIT);
		BLOCKS_TO_REGISTER.add(LIGHT_CAPACITOR);
		
		BLOCKS_TO_REGISTER.add(DECAY_POISON);
	}
	
	/***/
	public static void registerAll() {
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
