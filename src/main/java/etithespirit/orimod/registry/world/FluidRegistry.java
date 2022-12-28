package etithespirit.orimod.registry.world;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.decay.DecayWorldConfigHelper;
import etithespirit.orimod.common.block.fluid.DecayLiquidBlock;
import etithespirit.orimod.common.fluid.DecayPoisonFluid;
import etithespirit.orimod.common.material.ExtendedMaterials;
import etithespirit.orimod.config.OriModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FluidRegistry {
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DEFERRED REGISTERS PROVIDED BY FORGE
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, OriMod.MODID);
	private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, OriMod.MODID);
	private static final DeferredRegister<Block> BLOCKS = BlockRegistry.BLOCKS;
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// FLUIDS
	public static final RegistryObject<? extends FlowingFluid> DECAY_FLUID_STATIC = FLUIDS.register("decay_poison", DecayPoisonFluid.Source::new);
	public static final RegistryObject<? extends FlowingFluid> DECAY_FLUID_FLOWING = FLUIDS.register("flowing_decay_poison", DecayPoisonFluid.Flowing::new);
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// BLOCKS WRAPPING FLUIDS
	public static final RegistryObject<LiquidBlock> DECAY_POISON = BLOCKS.register("decay_poison", () -> new DecayLiquidBlock(DECAY_FLUID_STATIC, BlockBehaviour.Properties.of(ExtendedMaterials.DECAY_LIQUID).noCollission().strength(100).noLootTable()));
	public static final RegistryObject<FluidType> DECAY_POISON_TYPE = FLUID_TYPES.register("decay_poison", DecayPoisonFluid.DECAY_POISON_FLUID_TYPE);
	
	static {
		// BlockRegistry.BLOCKS_TO_REGISTER.add(DECAY_POISON);
	}
	
	public static void registerAll() {
		FLUID_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
		//DecayFluidTags.registerAll();
		
		
		
		
	}
	
	
	private static class FluidInteractions {
		
		private static boolean decayPoisonHasInteraction(Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState) {
			if (true)
			return false;
			
			
			if (!DecayWorldConfigHelper.getSpreadLimits(DecayWorldConfigHelper.SpreadType.FLUID).canSpread) {
				return false;
			}
			
			BlockPos location = currentPos.offset(relativePos);
			if (level.getFluidState(location).is(Fluids.WATER)) {
				return true;
			}
			
			return false;
		}
		
		private static void decayPoisonDoInteraction(Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState) {
			BlockPos location = currentPos.offset(relativePos);
		}
		
		public static void registerAll() {
			FluidInteractionRegistry.addInteraction(
				DECAY_POISON_TYPE.get(),
				new FluidInteractionRegistry.InteractionInformation(
					FluidInteractions::decayPoisonHasInteraction,
					FluidInteractions::decayPoisonDoInteraction
				)
			);
		}
		
	}
}
