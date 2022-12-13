package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.decay.DecayLiquidBlock;
import etithespirit.orimod.common.fluid.DecayPoisonFluid;
import etithespirit.orimod.common.material.ExtendedMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FluidRegistry {
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DEFERRED REGISTERS PROVIDED BY FORGE
	private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, OriMod.MODID);
	private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, OriMod.MODID);
	private static final DeferredRegister<Block> BLOCKS = BlockRegistry.BLOCKS;
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// FLUIDS
	public static final RegistryObject<Fluid> DECAY_FLUID_STATIC = FLUIDS.register("decay_poison", () -> new ForgeFlowingFluid.Source(DecayPoisonFluid.createProperties()));
	public static final RegistryObject<Fluid> DECAY_FLUID_FLOWING = FLUIDS.register("flowing_decay_poison", () -> new ForgeFlowingFluid.Flowing(DecayPoisonFluid.createProperties()));
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// BLOCKS WRAPPING FLUIDS
	public static final RegistryObject<LiquidBlock> DECAY_POISON = BLOCKS.register("decay_poison", () -> new DecayLiquidBlock(() -> (FlowingFluid)FluidRegistry.DECAY_FLUID_STATIC.get(), BlockBehaviour.Properties.of(ExtendedMaterials.DECAY_LIQUID).noCollission().strength(100).noLootTable(), true));
	public static final RegistryObject<FluidType> DECAY_POISON_TYPE = FLUID_TYPES.register("decay_poison", DecayPoisonFluid.DECAY_POISON_FLUID_TYPE);
	
	static {
		BlockRegistry.BLOCKS_TO_REGISTER.add(DECAY_POISON);
	}
	
	public static void registerAll() {
		FLUID_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
		//DecayFluidTags.registerAll();
	}
	
}
