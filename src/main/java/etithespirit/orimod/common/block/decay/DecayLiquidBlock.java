package etithespirit.orimod.common.block.decay;

import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.registry.world.FluidRegistry;
import etithespirit.orimod.util.level.StateHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static etithespirit.orimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.orimod.common.block.decay.DecayCommon.DECAY_REPLACEMENT_TARGETS;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_TEST_MINIMUM_CHANCE;

/**
 * A decay liquid block, which inherits the attributes of both liquid and decay materials.
 */
public class DecayLiquidBlock extends LiquidBlock implements IDecayBlockCommon {
	/**
	 * @param fluid      A fluid supplier such as {@link RegistryObject < FlowingFluid >}
	 * @param properties The properties of this fluid.
	 */
	public DecayLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
		this(fluid, properties, false);
	}
	
	public DecayLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties, boolean spreads) {
		super(fluid, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE).setValue(EDGE_DETECTION_RARITY, 1)); // Set this to 1 so that it's a half chance.
		if (spreads) {
			List<StateHolder<?, ?>> thisBlockReplacements = new ArrayList<>();
			registerReplacements(thisBlockReplacements);
			if (thisBlockReplacements.size() == 0) {
				throw new IllegalStateException("New Decay block had SPREADS=TRUE but did not register any blocks to spread to! Offender: " + this.getClass().getName());
			} else {
				for (StateHolder<?, ?> repl : thisBlockReplacements) {
					DECAY_REPLACEMENT_TARGETS.put(repl, this.defaultBlockState());
				}
				properties.randomTicks();
			}
		}
	}
	
	// Registering water will do nothing. There is no need to register it anyway as this does not inherit DecayBlockBase, and thus no warning exists.
	@Override
	public void registerReplacements(List<StateHolder<?, ?>> blocksToReplaceWithSelf) {
		blocksToReplaceWithSelf.add(Fluids.WATER.defaultFluidState());
	}
	
	@Override
	public StateHolder<?, ?> mutateReplacementState(StateHolder<?, ?> replacementState, Level world, BlockPos replacingBlockAt) {
		return replacementState;
	}
	
	@Override
	@SuppressWarnings({ "unchecked" })
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ALL_ADJACENT_ARE_DECAY);
		builder.add(EDGE_DETECTION_RARITY);
	}
	
	@Override
	public BlockState healsInto(BlockState thisState) {
		return Blocks.WATER.defaultBlockState();
	}
	
	/**
	 * The default routine for spreading to / infecting adjacent blocks.
	 * @param state The block that is responsible for the spreading.
	 * @param worldIn The {@link Level} that this spread is occurring in.
	 * @param pos The location at which this block exists.
	 * @param random The pseudorandomizer of this world.
	 */
	@Override
	public void doAdjacentSpread(StateHolder<?, ?> state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		if (!OriModConfigs.getDecaySpreadBehavior(state).canSpread) return;
		if (!shouldSpreadByRNG(worldIn, pos)) return;
		
		BlockPos randomUnoccupied = randomUnoccupiedDirection(worldIn, pos, random);
		if (randomUnoccupied != null) {
			StateHolder<?, ?> replacement = this.getDecayReplacementFor(StateHelper.getFluidOrBlock(worldIn, randomUnoccupied));
			if (replacement != null) {
				if (replacement instanceof FluidState fluid) {
					Fluid thisFluid = FluidRegistry.DECAY_FLUID_STATIC.get();
					if (fluid.getType() == thisFluid) {
						BlockState originalState = worldIn.getBlockState(randomUnoccupied);
						if (!originalState.isAir() && originalState.getBlock() instanceof LiquidBlockContainer liquidCtr && liquidCtr.canPlaceLiquid(worldIn, randomUnoccupied, originalState, thisFluid)) {
							liquidCtr.placeLiquid(worldIn, randomUnoccupied, originalState, thisFluid.defaultFluidState());
							return;
						}
					}
				}
				StateHelper.setBlockAndUpdateIn(worldIn, randomUnoccupied, mutateReplacementState(replacement, worldIn, randomUnoccupied));
			} else {
				throw new IllegalStateException("An unoccupied space was present with no replacement! This should NEVER happen! Did you accidentally explicitly allow a type of block in randomUnoccupiedDirection?");
			}
		} else {
			// No unoccupied blocks!
			StateHelper.setBlockAndUpdateIn(worldIn, pos, StateHelper.setManyValues(state, ALL_ADJACENT_ARE_DECAY, true, EDGE_DETECTION_RARITY, 0));
			// Also reset edge detection rarity to promote more.
		}
	}
	
	/**
	 * The default routine for spreading to / infecting diagonally oriented blocks (2D or 3D).
	 * This can be imagined with the current block being the center of a 3x3x3 cube. It applies to all blocks that are not directly adjacent to one of the six faces of this block.
	 * @param state The block that is responsible for the spreading.
	 * @param worldIn The {@link Level} that this spread is occurring in.
	 * @param pos The location at which this block exists.
	 * @param random The pseudorandomizer of this world.
	 */
	@Override
	public void doDiagonalSpread(StateHolder<?, ?> state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		BlockPos randomUnoccupied = randomUnoccupiedDiagonal(worldIn, pos, random);
		if (randomUnoccupied != null) {
			// Could find one. Replace and increase the chance.
			StateHolder<?, ?> replacement = this.getDecayReplacementFor(StateHelper.getFluidOrBlock(worldIn, randomUnoccupied));
			if (replacement != null) {
				StateHelper.setBlockAndUpdateIn(worldIn, randomUnoccupied, mutateReplacementState(replacement, worldIn, randomUnoccupied));
				int currentRarity = state.getValue(EDGE_DETECTION_RARITY);
				if (currentRarity > 0) {
					if (replacement instanceof FluidState fluid) {
						Fluid thisFluid = FluidRegistry.DECAY_FLUID_STATIC.get();
						if (fluid.getType() == thisFluid) {
							BlockState originalState = worldIn.getBlockState(randomUnoccupied);
							if (!originalState.isAir() && originalState.getBlock() instanceof LiquidBlockContainer liquidCtr && liquidCtr.canPlaceLiquid(worldIn, randomUnoccupied, originalState, thisFluid)) {
								liquidCtr.placeLiquid(worldIn, randomUnoccupied, originalState, thisFluid.defaultFluidState());
								return;
							}
						}
					}
					StateHelper.setBlockAndUpdateIn(worldIn, pos, StateHelper.setManyValues(state, EDGE_DETECTION_RARITY, currentRarity - 1));
				}
				return;
			}
		}
		// Couldn't find one. Reduce the chance.
		int currentRarity = state.getValue(EDGE_DETECTION_RARITY);
		if (currentRarity < EDGE_TEST_MINIMUM_CHANCE) {
			StateHelper.setBlockAndUpdateIn(worldIn, pos, StateHelper.setManyValues(state, EDGE_DETECTION_RARITY, currentRarity + 1));
		}
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		super.randomTick(state, worldIn, pos, random);
		/*
		n.b. this does not adequately cause spread behaviors, need to do it manually in the fluid too!
		 */
		defaultRandomTick(state, worldIn, pos, random);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		defaultNeighborChanged(state, worldIn, pos, blockIn, worldIn.getBlockState(fromPos), fromPos, isMoving);
	}
	
	@Override
	public void stepOn(Level world, BlockPos at, BlockState state, Entity ent) {
		super.stepOn(world, at, state, ent);
		if (world.isClientSide) return;
		IDecayBlockCommon.super.defaultOnEntityWalked(world, at, ent);
	}
}
