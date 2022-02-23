package etithespirit.etimod.registry;

import java.util.ArrayList;
import java.util.List;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.block.decay.flora.DecayLogBlock;
import etithespirit.etimod.common.block.decay.flora.DecayStrippedLogBlock;
import etithespirit.etimod.common.block.decay.world.DecayMyceliumBlock;
import etithespirit.etimod.common.block.decay.world.DecayPoisonBlock;
import etithespirit.etimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.etimod.common.block.light.LightCapacitorBlock;
import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.fluid.DecayFluid;
import etithespirit.etimod.fluid.tags.DecayFluidTags;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registers all custom blocks to the game.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class BlockRegistry {
	
	/** Used in the item registry to generate BlockItems. */
	public static final List<RegistryObject<Block>> BLOCKS_TO_REGISTER = new ArrayList<>();
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DEFERRED REGISTERS PROVIDED BY FORGE
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EtiMod.MODID);
	private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, EtiMod.MODID);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// BLOCKS
	public static final RegistryObject<Block> DECAY_MYCELIUM = BLOCKS.register("decay_mycelium", DecayMyceliumBlock::new);
	public static final RegistryObject<Block> DECAY_LOG = BLOCKS.register("decay_log", DecayLogBlock::new);
	public static final RegistryObject<Block> DECAY_STRIPPED_LOG = BLOCKS.register("stripped_decay_log", DecayStrippedLogBlock::new);
	public static final RegistryObject<Block> DECAY_SURFACE_MYCELIUM = BLOCKS.register("decay_surface_mycelium", DecaySurfaceMyceliumBlock::new);
	
	public static final RegistryObject<Block> LIGHT_CONDUIT = BLOCKS.register("light_conduit", LightConduitBlock::new);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// TILE ENTITY BLOCKS
	public static final RegistryObject<Block> LIGHT_CAPACITOR = BLOCKS.register("light_capacitor", LightCapacitorBlock::new);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// BLOCKS WRAPPING FLUIDS
	public static final RegistryObject<Block> DECAY_POISON = BLOCKS.register("decay_poison", DecayPoisonBlock::new);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// FLUIDS
	public static final RegistryObject<Fluid> DECAY_FLUID_STATIC = FLUIDS.register("decay", () -> DecayFluid.DECAY);
	public static final RegistryObject<Fluid> DECAY_FLUID_FLOWING = FLUIDS.register("flowing_decay", () -> DecayFluid.DECAY_FLOWING);
	
	static {
		BLOCKS_TO_REGISTER.add(DECAY_MYCELIUM);
		BLOCKS_TO_REGISTER.add(DECAY_LOG);
		BLOCKS_TO_REGISTER.add(DECAY_STRIPPED_LOG);
		BLOCKS_TO_REGISTER.add(DECAY_SURFACE_MYCELIUM);
		
		BLOCKS_TO_REGISTER.add(LIGHT_CONDUIT);
		
		BLOCKS_TO_REGISTER.add(LIGHT_CAPACITOR);
	}
	
	public static void registerAll() {
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
		DecayFluidTags.registerAll();
	}

}