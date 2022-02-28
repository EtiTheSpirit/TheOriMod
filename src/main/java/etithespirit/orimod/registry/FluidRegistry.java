package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.fluid.DecayPoisonFluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FluidRegistry {
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DEFERRED REGISTERS PROVIDED BY FORGE
	private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, OriMod.MODID);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// FLUIDS
	public static final RegistryObject<Fluid> DECAY_FLUID_STATIC = FLUIDS.register("decay_poison", DecayPoisonFluid.Source::new);
	public static final RegistryObject<Fluid> DECAY_FLUID_FLOWING = FLUIDS.register("flowing_decay_poison", DecayPoisonFluid.Flowing::new);
	
	
	public static void registerAll() {
		FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
		//DecayFluidTags.registerAll();
	}
	
}
