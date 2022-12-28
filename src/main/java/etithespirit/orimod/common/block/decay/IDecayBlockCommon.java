package etithespirit.orimod.common.block.decay;


import etithespirit.orimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.potion.DecayEffect;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.networking.potion.EffectModificationReplication;
import etithespirit.orimod.registry.gameplay.EffectRegistry;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.util.EffectConstructors;
import etithespirit.orimod.util.extension.MobEffectDataStorage;
import etithespirit.orimod.util.level.StateHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static etithespirit.orimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.orimod.common.block.decay.DecayCommon.DECAY_REPLACEMENT_TARGETS;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_TEST_MINIMUM_CHANCE;
import static etithespirit.orimod.common.block.decay.DecayCommon.MAX_DIAGONAL_TESTS;
import static etithespirit.orimod.info.coordinate.Cardinals.ADJACENTS_IN_ORDER;
import static etithespirit.orimod.info.coordinate.Cardinals.DIAGONALS_IN_ORDER;

/**
 * Blocks implementing IDecayBlock are implicitly classified as decay blocks (for obvious reasons), but also contain the more aggressive behaviors of The Decay in that
 * they will spread and infect nearby blocks. The blocks that it can spread to and infect depend on the implementation of the block itself. To check if a block
 * is a decay block, use the block tag system.
 * @author Eti
 *
 */
public interface IDecayBlockCommon extends IBlockItemPropertiesProvider {
	
	@Override
	default Item.Properties getPropertiesOfItem() {
		return (new Item.Properties()).tab(OriModCreativeModeTabs.DECAY);
	}
	
	/**
	 * When given a list of BlockStates and a Block instance, this will iterate through all of its states and put them into the list.
	 * @param blocksToReplaceWithSelf This is a list storing all block states that can be replaced by this Decay block.
	 * @param from All states of this block will be registered as replacable by this Decay block.
	 */
	static void registerAllStatesFor(List<StateHolder<?, ?>> blocksToReplaceWithSelf, Block from) {
		blocksToReplaceWithSelf.addAll(from.getStateDefinition().getPossibleStates());
	}
	
	/**
	 * When Decay blocks are registered, they define which block states or fluid states they replace, and what state to replace those with.<br/>
	 * This method can be used to look up what a block's block replacement is, e.g. pass in {@code Blocks.STONE.getDefaultState()} to receive the Decay-equivalent of stone.<br/>
	 * Returns null if there is no substitute.<br/>
	 * <br/>
	 * This is a per-instance sensitive variant of the function and should always be called if an instance of the spreading Decay block is available.
	 * @param existingBlock The block that will be replaced.
	 * @return The replacement blockstate, or null if there is no replacement.
	 */
	default @Nullable StateHolder<?, ?> getDecayReplacementFor(StateHolder<?, ?> existingBlock) {
		return getDefaultDecayReplacementFor(existingBlock);
	}
	
	/**
	 * When Decay blocks are registered, they define which block or fluid states they replace, and what state to replace those with.<br/>
	 * This method can be used to look up what a block's replacement is, e.g. pass in {@code Blocks.STONE.getDefaultState()} to receive the Decay-equivalent of stone.<br/>
	 * Returns null if there is no substitute.<br/>
	 * <br/>
	 * This should only be called if there is no spreading Decay block to call the {@link #getDecayReplacementFor(StateHolder)} method on, as this does not
	 * respect per-block custom behavior.
	 * @param existingBlock The block that will be replaced.
	 * @return The replacement blockstate, or null if there is no replacement.
	 */
	static @Nullable StateHolder<?, ?> getDefaultDecayReplacementFor(StateHolder<?, ?> existingBlock) {
		if (DECAY_REPLACEMENT_TARGETS.containsKey(existingBlock)) return DECAY_REPLACEMENT_TARGETS.get(existingBlock);
		if (existingBlock instanceof FluidState fluid) {
			for (StateHolder<?, ?> key : DECAY_REPLACEMENT_TARGETS.keySet()) {
				if (key instanceof FluidState keyFluid) {
					if (keyFluid.is(fluid.getType())) return DECAY_REPLACEMENT_TARGETS.get(key);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns whether or not a replacement for the given state exists. This works for block and fluid states.
	 * @param worldIn The {@link Level} that this block exists in.
	 * @param at The location of this block in the world.
	 * @param existingBlock The block that may be replaced.
	 * @return Whether or not this block actually has a replacement.
	 */
	default boolean hasDecayReplacementFor(Level worldIn, BlockPos at, StateHolder<?, ?> existingBlock) {
		return this.getDecayReplacementFor(existingBlock) != null;
	}
	
	/**
	 * Returns whether or not this decay block needs to spread because one or more of its adjacent blocks is not a decay block. This works for blocks and fluids.
	 * @param decayBlock The block that should be checked for spreadability.
	 * @return Whether or not this block should spread.
	 */
	default boolean needsToSpread(StateHolder<?, ?> decayBlock) {
		return !decayBlock.getValue(ALL_ADJACENT_ARE_DECAY);
	}
	
	/**
	 * Returns whether or not this should be allowed to spread in general.
	 * @param worldIn The world to check in.
	 * @param at The location of this block.
	 * @return True if this decay block should spread now, false if it should try again on the next random tick.
	 */
	default boolean shouldSpreadByRNG(Level worldIn, BlockPos at) {
		float partialDifficulty = worldIn.getCurrentDifficultyAt(at).getEffectiveDifficulty();
		// 0 peaceful, 3 hard
		if (partialDifficulty <= 0) return false;
		if (partialDifficulty < 2.5) {
			return (worldIn.getRandom().nextFloat() * partialDifficulty) > 0.5f;
		}
		return true;
	}
	
	/**
	 * Checks if this tick operation should be changed into an edge check.
	 * @param rng A randomizer
	 * @param edgeDetectionRarity The denominator of the current detection rarity.
	 * @param denominatorOfMinChance The denominator of the lowest possible chance that can ever occur.
	 * @return Whether or not this block needs to do an edge check on this tick.
	 */
	default boolean needsToDoEdgeCheck(RandomSource rng, int edgeDetectionRarity, int denominatorOfMinChance) {
		float v = rng.nextFloat() * (denominatorOfMinChance + 1);
		// ^ + n -- add some bias.
		return v > edgeDetectionRarity;
	}
	
	/**
	 * Provides a means of altering the given state. The input state is the state of this block when it replaces a non-decay block.
	 * @param originalState The default state of this decay block
	 * @param world The world that the state is being placed into.
	 * @param replacingBlockAt THe location of the block that is being replaced in the world.
	 * @return The state of the decay block when replacing a given block.
	 */
	default StateHolder<?, ?> mutateReplacementState(StateHolder<?, ?> originalState, Level world, BlockPos replacingBlockAt) {
		return originalState;
	}
	
	/**
	 * Returns an array of 6 boolean values mapping in the order of: EAST, WEST, UP, DOWN, NORTH, SOUTH<br>
	 * A given boolean is true if the corresponding block has no decay replacement. Note that decay blocks are not decayable either. As such, this method better describes when a block is stopping decay spread.<br>
	 * @param world The {@link Level} that this block exists in.
	 * @param pos The location of this block in the level it exits in.
	 * @return Six boolean values in the order of EAST, WEST, UP, DOWN, NORTH, SOUTH that represents whether or not the given adjacent block in that direction can be mutated.
	 */
	default boolean[] getNonDecayableAdjacents(Level world, BlockPos pos) {
		boolean[] result = new boolean[6];
		for (int idx = 0; idx < ADJACENTS_IN_ORDER.length; idx++) {
			BlockPos newPos = pos.offset(ADJACENTS_IN_ORDER[idx]);
			result[idx] = !hasDecayReplacementFor(world, newPos, StateHelper.getFluidOrBlock(world, newPos)); // It is considered occupied if there is no replacement for it.
		}
		return result;
	}
	
	/**
	 * Returns a random unoccupied adjacent block. "Unoccupied" refers to meaning that the adjacent space has a valid decay block replacement.
	 * @param world The world to check in.
	 * @param originalPos The position of this block.
	 * @param rng A randomizer used to select a random direction.
	 * @return An adjacent {@link BlockPos} in a random cardinal direction that is able to decay.
	 */
	default BlockPos randomUnoccupiedDirection(Level world, BlockPos originalPos, RandomSource rng) {
		boolean[] adjacents = getNonDecayableAdjacents(world, originalPos);
		int[] validIndices = new int[6];
		int numIndices = 0;
		for (int idx = 0; idx < adjacents.length; idx++) {
			if (!adjacents[idx]) {
				// Not occupied
				validIndices[numIndices] = idx;
				numIndices++;
			}
		}
		
		if (numIndices == 0) return null;
		int index = validIndices[rng.nextInt(numIndices)];
		return originalPos.offset(ADJACENTS_IN_ORDER[index]);
	}
	
	/**
	 * Returns one of the (up to) 20 possible diagonal adjacent directions that are occupied by a block that can decay.
	 * @param world The world to test in.
	 * @param originalPos The location of this block.
	 * @param rng A randomizer used to select a random diagonal.
	 * @return One of the (up to) 20 possible diagonal adjacent directions that are occupied by a block that can decay.
	 */
	default BlockPos randomUnoccupiedDiagonal(Level world, BlockPos originalPos, RandomSource rng) {
		for (int idx = 0; idx < MAX_DIAGONAL_TESTS; idx++) {
			int rngIdx = rng.nextInt(DIAGONALS_IN_ORDER.length);
			BlockPos newPos = originalPos.offset(DIAGONALS_IN_ORDER[rngIdx]);
			if (hasDecayReplacementFor(world, newPos, StateHelper.getFluidOrBlock(world, newPos))) {
				return newPos;
			}
		}
		return null;
	}
	
	/**
	 * When this Decay block is cured, this is the {@link BlockState} (of some vanilla or mod block) that it should heal into.
	 * @return The vanilla or mod block that this decay block turns into once cured.
	 */
	BlockState healsInto(BlockState thisState);
	
	/**
	 * The default behavior that should generally occur on a random tick for a decay block.
	 * @param state The block that is ticking.
	 * @param worldIn The {@link Level} that this block is ticking in.
	 * @param pos The location at which this block is ticking.
	 * @param random The pseudorandomizer of this world.
	 */
	default void defaultRandomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		DecayWorldConfigBehavior behavior = DecayWorldConfigHelper.getSpreadLimits(DecayWorldConfigHelper.SpreadType.GENERAL);
		if (behavior.canSpread) {
			if (!shouldSpreadByRNG(worldIn, pos)) return;
			
			boolean doEdgeCheckInstead = OriModConfigs.DO_DIAGONAL_SPREAD.get() && needsToDoEdgeCheck(random, state.getValue(EDGE_DETECTION_RARITY), EDGE_TEST_MINIMUM_CHANCE);
			boolean needsToSpreadToAdjacent = needsToSpread(state);
			if (!needsToSpreadToAdjacent) {
				if (doEdgeCheckInstead) {
					doDiagonalSpread(state, worldIn, pos, random);
				}
			} else {
				if (doEdgeCheckInstead) {
					doDiagonalSpread(state, worldIn, pos, random);
				} else {
					doAdjacentSpread(state, worldIn, pos, random);
				}
			}
		}
		if (behavior.selfDestructs) {
			worldIn.setBlockAndUpdate(pos, healsInto(state));
		}
	}
	
	/**
	 * The default routine for spreading to / infecting adjacent blocks.
	 * @param state The block that is responsible for the spreading.
	 * @param worldIn The {@link Level} that this spread is occurring in.
	 * @param pos The location at which this block exists.
	 * @param random The pseudorandomizer of this world.
	 */
	default void doAdjacentSpread(StateHolder<?, ?> state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		BlockPos randomUnoccupied = randomUnoccupiedDirection(worldIn, pos, random);
		if (randomUnoccupied != null) {
			StateHolder<?, ?> replacement = this.getDecayReplacementFor(StateHelper.getFluidOrBlock(worldIn, randomUnoccupied));
			if (replacement != null) {
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
	default void doDiagonalSpread(StateHolder<?, ?> state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		BlockPos randomUnoccupied = randomUnoccupiedDiagonal(worldIn, pos, random);
		if (randomUnoccupied != null) {
			// Could find one. Replace and increase the chance.
			StateHolder<?, ?> replacement = this.getDecayReplacementFor(StateHelper.getFluidOrBlock(worldIn, randomUnoccupied));
			if (replacement != null) {
				StateHelper.setBlockAndUpdateIn(worldIn, randomUnoccupied, mutateReplacementState(replacement, worldIn, randomUnoccupied));
				int currentRarity = state.getValue(EDGE_DETECTION_RARITY);
				if (currentRarity > 0) {
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
	
	/**
	 * Register all of the blocks or fluids that this decay block will replace with itself once those blocks are infected.
	 * @param blocksToReplaceWithSelf A list of every block that is replaced by this upon infection.
	 */
	void registerReplacements(List<StateHolder<?, ?>> blocksToReplaceWithSelf);
	
	/**
	 * The default logic for when a neighbor of this decay block is changed.
	 * @param state This block's state.
	 * @param worldIn The {@link Level} in which this change is occurring.
	 * @param pos The location at which this change is occurring.
	 * @param blockIn The block itself.
	 * @param fromState The state of the block that changed.
	 * @param fromPos The location of the block that changed.
	 * @param isMoving Whether or not this change is the result of a piston.
	 */
	@SuppressWarnings("unused")
	default void defaultNeighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockState fromState, BlockPos fromPos, boolean isMoving) {
		StateHolder<?, ?> replacement = getDefaultDecayReplacementFor(fromState);
		if (!(blockIn instanceof IDecayBlockCommon) && replacement instanceof BlockState blockReplacement && !(blockReplacement.getBlock() instanceof DecaySurfaceMyceliumBlock)) {
			worldIn.setBlockAndUpdate(pos, state.setValue(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE));
		}
	}
	
	/**
	 * The default logic for when an entity walks on this decay block.
	 * @param worldIn The {@link Level} in which this occurred.
	 * @param pos The location of the block that was walked on.
	 * @param entityIn The entity that walked on the block.
	 */
	@SuppressWarnings("unused")
	default void defaultOnEntityWalked(Level worldIn, BlockPos pos, Entity entityIn) {
		if (worldIn.getDifficulty() == Difficulty.PEACEFUL) return;
		
		if (!(entityIn instanceof LivingEntity entity)) return;
		if (worldIn.isClientSide) return;
		if (worldIn.getRandom().nextDouble() > 0.99) {
			DecayEffect decay = (DecayEffect) EffectRegistry.DECAY.get();
			MobEffectInstance existing = entity.getEffect(decay);
			if (existing != null) {
				existing.duration += 60;
				MobEffectDataStorage.addMaxDuration(existing, 60);
				if (entity instanceof ServerPlayer player) {
					EffectModificationReplication.Server.tellClientDurationModified(player, DecayEffect.class, 60);
				}
			} else {
				MobEffectInstance instance = EffectConstructors.constructEffect(decay, 60, 0, true);
				instance.setCurativeItems(new ArrayList<>());
				MobEffectDataStorage.setMaxDuration(instance, instance.getDuration());
				entity.addEffect(instance);
			}
		}
	}
}