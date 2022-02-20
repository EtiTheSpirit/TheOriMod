package etithespirit.orimod.common.block.decay;


import etithespirit.orimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.orimod.common.potion.DecayEffect;
import etithespirit.orimod.networking.potion.EffectModificationReplication;
import etithespirit.orimod.registry.PotionRegistry;
import etithespirit.orimod.util.EffectConstructors;
import etithespirit.orimod.util.extension.MobEffectDataStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static etithespirit.orimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.orimod.common.block.decay.DecayCommon.BLOCK_REPLACEMENT_TARGETS;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_TEST_MINIMUM_CHANCE;
import static etithespirit.orimod.common.block.decay.DecayCommon.MAX_DIAGONAL_TESTS;
import static etithespirit.orimod.info.coordinate.Cardinals.ADJACENTS_IN_ORDER;
import static etithespirit.orimod.info.coordinate.Cardinals.DIAGONALS_IN_ORDER;

/**
 * An extension to {@link etithespirit.orimod.common.block.decay.IDecayBlockIdentifier} that goes beyond the mere definition of being a Decay block.<br/>
 * <br/>
 * Blocks implementing IDecayBlock are implicitly classified as decay blocks (for obvious reasons), but also contain the more aggressive behaviors of The Decay in that
 * they will spread and infect nearby blocks. The blocks that it can spread to and infect depend on the implementation of the block itself.
 * @author Eti
 *
 */
public interface IDecayBlock extends IDecayBlockIdentifier {
	
	/**
	 * When given a list of BlockStates and a Block instance, this will iterate through all of its states and put them into the list.
	 * @param blocksToReplaceWithSelf This is a list storing all block states that can be replaced by this Decay block.
	 * @param from All states of this block will be registered as replacable by this Decay block.
	 */
	static void registerAllStatesForBlock(List<BlockState> blocksToReplaceWithSelf, Block from) {
		StateDefinition<Block, BlockState> container = from.getStateDefinition();
		blocksToReplaceWithSelf.addAll(container.getPossibleStates());
	}
	
	/**
	 * When Decay blocks are registered, they define which block states they replace, and what state to replace those with.<br>
	 * This method can be used to look up what a block's replacement is, e.g. pass in {@code Blocks.STONE.getDefaultState()} to receive the Decay-equivalent of stone.<br>
	 * Returns null if there is no substitute.
	 * @param existingBlock The block that will be replaced.
	 * @return The replacement blockstate, or null if there is no replacement.
	 */
	default @Nullable
	BlockState getDecayReplacementFor(BlockState existingBlock) {
		if (BLOCK_REPLACEMENT_TARGETS.containsKey(existingBlock)) return BLOCK_REPLACEMENT_TARGETS.get(existingBlock);
		return null;
	}
	
	/**
	 * Returns whether or not a replacement for the given BlockState exists.
	 * @param worldIn The {@link Level} that this block exists in.
	 * @param at The location of this block in the world.
	 * @param existingBlock The block that may be replaced.
	 * @return Whether or not this block actually has a replacement.
	 */
	default boolean hasDecayReplacementFor(Level worldIn, BlockPos at, BlockState existingBlock) {
		return BLOCK_REPLACEMENT_TARGETS.containsKey(existingBlock);
	}
	
	/**
	 * Returns whether or not this decay block needs to spread because one or more of its adjacent blocks is not a decay block.
	 * @param decayBlock The block that should be checked for spreadability.
	 * @return Whether or not this block should spread.
	 */
	default boolean needsToSpread(BlockState decayBlock) {
		return !decayBlock.getValue(ALL_ADJACENT_ARE_DECAY);
	}
	
	/**
	 * Checks if this tick operation should be changed into an edge check.
	 * @param rng A randomizer
	 * @param edgeDetectionRarity The denominator of the current detection rarity.
	 * @param denominatorOfMinChance The denominator of the lowest possible chance that can ever occur.
	 * @return Whether or not this block needs to do an edge check on this tick.
	 */
	default boolean needsToDoEdgeCheck(Random rng, int edgeDetectionRarity, int denominatorOfMinChance) {
		float v = rng.nextFloat() * (denominatorOfMinChance + 1);
		// ^ + n -- add some bias.
		return v > edgeDetectionRarity;
	}
	
	/**
	 * Provides a means of altering the given state. The input state is the state of this block when it replaces a non-decay block.
	 * @param originalState The default state of this decay block
	 * @return The state of the decay block when replacing a given block.
	 */
	default BlockState mutateReplacementState(BlockState originalState) {
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
			BlockState neighbor = world.getBlockState(newPos);
			result[idx] = !hasDecayReplacementFor(world, newPos, neighbor); // It is considered occupied if there is no replacement for it.
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
	default BlockPos randomUnoccupiedDirection(Level world, BlockPos originalPos, Random rng) {
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
	default BlockPos randomUnoccupiedDiagonal(Level world, BlockPos originalPos, Random rng) {
		for (int idx = 0; idx < MAX_DIAGONAL_TESTS; idx++) {
			int rngIdx = rng.nextInt(DIAGONALS_IN_ORDER.length);
			BlockPos newPos = originalPos.offset(DIAGONALS_IN_ORDER[rngIdx]);
			BlockState neighbor = world.getBlockState(newPos);
			if (hasDecayReplacementFor(world, newPos, neighbor)) {
				return newPos;
			}
		}
		return null;
	}
	
	/**
	 * The default behavior that should generally occur on a random tick for a decay block.
	 * @param state The block that is ticking.
	 * @param worldIn The {@link Level} that this block is ticking in.
	 * @param pos The location at which this block is ticking.
	 * @param random The pseudorandomizer of this world.
	 */
	default void defaultRandomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		boolean doEdgeCheckInstead = needsToDoEdgeCheck(random, state.getValue(EDGE_DETECTION_RARITY), EDGE_TEST_MINIMUM_CHANCE);
		boolean needsToSpreadToNewBlock = needsToSpread(state);
		if (!needsToSpreadToNewBlock) {
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
	
	/**
	 * The default routine for spreading to / infecting adjacent blocks.
	 * @param state The block that is responsible for the spreading.
	 * @param worldIn The {@link Level} that this spread is occurring in.
	 * @param pos The location at which this block exists.
	 * @param random The pseudorandomizer of this world.
	 */
	default void doAdjacentSpread(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		BlockPos randomUnoccupied = randomUnoccupiedDirection(worldIn, pos, random);
		if (randomUnoccupied != null) {
			BlockState replacement = getDecayReplacementFor(worldIn.getBlockState(randomUnoccupied));
			if (replacement != null) {
				worldIn.setBlockAndUpdate(randomUnoccupied, mutateReplacementState(replacement));
			} else {
				throw new IllegalStateException("An unoccupied space was present with no replacement! This should NEVER happen! Did you accidentally explicitly allow a type of block in randomUnoccupiedDirection?");
			}
		} else {
			// No unoccupied blocks!
			worldIn.setBlockAndUpdate(pos, state.setValue(ALL_ADJACENT_ARE_DECAY, Boolean.TRUE).setValue(EDGE_DETECTION_RARITY, 0));
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
	default void doDiagonalSpread(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
		BlockPos randomUnoccupied = randomUnoccupiedDiagonal(worldIn, pos, random);
		if (randomUnoccupied != null) {
			// Could find one. Replace and increase the chance.
			BlockState replacement = getDecayReplacementFor(worldIn.getBlockState(randomUnoccupied));
			if (replacement != null) {
				worldIn.setBlockAndUpdate(randomUnoccupied, mutateReplacementState(replacement));
				int currentRarity = state.getValue(EDGE_DETECTION_RARITY);
				if (currentRarity > 0) {
					worldIn.setBlockAndUpdate(pos, state.setValue(EDGE_DETECTION_RARITY, currentRarity - 1));
				}
				return;
			}
		}
		// Couldn't find one. Reduce the chance.
		int currentRarity = state.getValue(EDGE_DETECTION_RARITY);
		if (currentRarity < EDGE_TEST_MINIMUM_CHANCE) {
			worldIn.setBlockAndUpdate(pos, state.setValue(EDGE_DETECTION_RARITY, currentRarity + 1));
		}
	}
	
	/**
	 * Register all of the blocks that this decay block will replace with itself once those blocks are infected.
	 * @param blocksToReplaceWithSelf A list of every block that is replaced by this upon infection.
	 */
	void registerReplacements(List<BlockState> blocksToReplaceWithSelf);
	
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
		BlockState replacement = getDecayReplacementFor(fromState);
		if (!(blockIn instanceof IDecayBlock) && replacement != null && !(replacement.getBlock() instanceof DecaySurfaceMyceliumBlock)) {
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
		if (!(entityIn instanceof LivingEntity)) return;
		if (worldIn.isClientSide) return;
		LivingEntity entity = (LivingEntity)entityIn;
		if (worldIn.getRandom().nextDouble() > 0.95) {
			DecayEffect decay = (DecayEffect) PotionRegistry.get(DecayEffect.class);
			MobEffectInstance existing = entity.getEffect(decay);
			if (existing != null) {
				existing.duration += 60;
				MobEffectDataStorage.addMaxDuration(existing, 60);
				if (entity instanceof ServerPlayer player) {
					EffectModificationReplication.tellClientDurationModified(player, DecayEffect.class, 60);
				}
			} else {
				MobEffectInstance instance = EffectConstructors.constructEffect(decay, 60, 2, true);
				instance.setCurativeItems(new ArrayList<>());
				MobEffectDataStorage.setMaxDuration(instance, instance.getDuration());
				entity.addEffect(instance);
			}
		}
	}
	
	
}