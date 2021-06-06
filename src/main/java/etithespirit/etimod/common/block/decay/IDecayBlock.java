package etithespirit.etimod.common.block.decay;

import static etithespirit.etimod.common.block.decay.DecayCommon.ADJACENTS_IN_ORDER;
import static etithespirit.etimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.etimod.common.block.decay.DecayCommon.BLOCK_REPLACEMENT_TARGETS;
import static etithespirit.etimod.common.block.decay.DecayCommon.DIAGONALS_IN_ORDER;
import static etithespirit.etimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.etimod.common.block.decay.DecayCommon.EDGE_TEST_MINIMUM_CHANCE;
import static etithespirit.etimod.common.block.decay.DecayCommon.MAX_DIAGONAL_TESTS;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import etithespirit.etimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.etimod.common.potion.DecayEffect;
import etithespirit.etimod.registry.PotionRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

/**
 * An extension to {@link etithespirit.etimod.common.block.decay.IDecayBlockIdentifier} that goes beyond the mere definition of being a Decay block.<br/>
 * <br/>
 * Blocks implementing IDecayBlock are implicitly classified as decay blocks (for obvious reasons), but also contain the more aggressive behaviors of The Decay in that
 * they will spread and infect nearby blocks. The blocks that it can spread to and infect depend on the implementation of the block itself.
 * @author Eti
 *
 */
public interface IDecayBlock extends IDecayBlockIdentifier {
	
	/**
	 * When given a list of BlockStates and a Block instance, this will iterate through all of its states and put them into the list. 
	 * @param intoThisList
	 * @param from
	 */
	public static void registerAllStatesForBlock(List<BlockState> blocksToReplaceWithSelf, Block from) {
		StateContainer<Block, BlockState> container = from.getStateContainer();
		blocksToReplaceWithSelf.addAll(container.getValidStates());
	}
	
	/**
	 * When Decay blocks are registered, they define which block states they replace, and what state to replace those with.<br>
	 * This method can be used to look up what a block's replacement is, e.g. pass in {@code Blocks.STONE.getDefaultState()} to receive the Decay-equivalent of stone.<br>
	 * Returns null if there is no substitute.
	 * @param existingBlock
	 * @return
	 */
	default @Nullable BlockState getDecayReplacementFor(BlockState existingBlock) {
		if (BLOCK_REPLACEMENT_TARGETS.containsKey(existingBlock)) return BLOCK_REPLACEMENT_TARGETS.get(existingBlock);
		return null;
	}
	
	/**
	 * Returns whether or not a replacement for the given BlockState exists.
	 * @param existingBlock
	 * @return
	 */
	default boolean hasDecayReplacementFor(World worldIn, BlockPos at, BlockState existingBlock) {
		return BLOCK_REPLACEMENT_TARGETS.containsKey(existingBlock);
	}
	
	/**
	 * Returns whether or not this decay block needs to spread because one or more of its adjacent blocks is not a decay block.
	 */
	default boolean needsToSpread(BlockState decayBlock) {
		return !decayBlock.get(ALL_ADJACENT_ARE_DECAY);
	}
	
	/**
	 * Checks if this tick operation should be changed into an edge check.
	 * @param rng A randomizer
	 * @param edgeDetectionRarity The denominator of the current detection rarity.
	 * @param denominatorOfMinChance The denominator of the lowest possible chance that can ever occur.
	 * @return
	 */
	default boolean needsToDoEdgeCheck(Random rng, int edgeDetectionRarity, int denominatorOfMinChance) {
		float v = rng.nextFloat() * (denominatorOfMinChance + 1);
		if (v > edgeDetectionRarity) {
			// ^ + n -- add some bias.
			return true;
		}
		return false;
	}
	
	/**
	 * Provides a means of altering the given state. The input state is the state of this block when it replaces a non-decay block.
	 * @param originalState
	 * @return
	 */
	default BlockState mutateReplacementState(BlockState originalState) {
		return originalState;
	}
	
	/**
	 * Returns an array of 6 boolean values mapping in the order of: EAST, WEST, UP, DOWN, NORTH, SOUTH<br>
	 * The boolean is true if the block is occupied by an instance of a decay block, and false if it is not.<br>
	 * @param airIsOccupied If true, air is treated as occupied by decay.
	 * @return
	 */
	default boolean[] getOccupiedAdjacents(World world, BlockPos pos) {
		boolean[] result = new boolean[6];
		for (int idx = 0; idx < ADJACENTS_IN_ORDER.length; idx++) {
			BlockPos newPos = pos.add(ADJACENTS_IN_ORDER[idx]);
			BlockState neighbor = world.getBlockState(newPos);
			result[idx] = !hasDecayReplacementFor(world, newPos, neighbor); // It is considered occupied if there is no replacement for it.
		}
		return result;
	}
	
	default BlockPos randomUnoccupiedDirection(World world, BlockPos originalPos, Random rng) {
		boolean[] adjacents = getOccupiedAdjacents(world, originalPos);
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
		return originalPos.add(ADJACENTS_IN_ORDER[index]);
	}
	
	default BlockPos randomUnoccupiedDiagonal(World world, BlockPos originalPos, Random rng) {
		for (int idx = 0; idx < MAX_DIAGONAL_TESTS; idx++) {
			int rngIdx = rng.nextInt(DIAGONALS_IN_ORDER.length);
			BlockPos newPos = originalPos.add(DIAGONALS_IN_ORDER[rngIdx]);
			BlockState neighbor = world.getBlockState(newPos);
			if (hasDecayReplacementFor(world, newPos, neighbor)) {
				return newPos;
			}
		}
		return null;
	}
	
	default void defaultRandomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		boolean doEdgeCheckInstead = needsToDoEdgeCheck(random, state.get(EDGE_DETECTION_RARITY), EDGE_TEST_MINIMUM_CHANCE);
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
	
	default void doAdjacentSpread(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		BlockPos randomUnoccupied = randomUnoccupiedDirection(worldIn, pos, random);
		if (randomUnoccupied != null) {
			BlockState replacement = getDecayReplacementFor(worldIn.getBlockState(randomUnoccupied));
			if (replacement != null) {
				worldIn.setBlockState(randomUnoccupied, mutateReplacementState(replacement));
			} else {
				throw new IllegalStateException("An unoccupied space was present with no replacement! This should NEVER happen! Did you accidentally explicitly allow a type of block in randomUnoccupiedDirection?");
			}
		} else {
			// No unoccupied blocks!
			worldIn.setBlockState(pos, state.with(ALL_ADJACENT_ARE_DECAY, Boolean.TRUE).with(EDGE_DETECTION_RARITY, 0));
			// Also reset edge detection rarity to promote more.
		}
	}
	
	default void doDiagonalSpread(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		BlockPos randomUnoccupied = randomUnoccupiedDiagonal(worldIn, pos, random);
		if (randomUnoccupied != null) {
			// Could find one. Replace and increase the chance.
			BlockState replacement = getDecayReplacementFor(worldIn.getBlockState(randomUnoccupied));
			if (replacement != null) {
				worldIn.setBlockState(randomUnoccupied, mutateReplacementState(replacement));
				int currentRarity = state.get(EDGE_DETECTION_RARITY);
				if (currentRarity > 0) {
					worldIn.setBlockState(pos, state.with(EDGE_DETECTION_RARITY, currentRarity - 1));
				}
				return;
			}
		}
		// Couldn't find one. Reduce the chance.
		int currentRarity = state.get(EDGE_DETECTION_RARITY);
		if (currentRarity < EDGE_TEST_MINIMUM_CHANCE) {
			worldIn.setBlockState(pos, state.with(EDGE_DETECTION_RARITY, currentRarity + 1));
		}
	}
	
	public abstract void registerReplacements(List<BlockState> blocksToReplaceWithSelf);
	
	default void defaultNeighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState replacement = getDecayReplacementFor(worldIn.getBlockState(fromPos));
		if (!(blockIn instanceof IDecayBlock) && replacement != null && !(replacement.getBlock() instanceof DecaySurfaceMyceliumBlock)) {
			worldIn.setBlockState(pos, state.with(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE));
		}
	}
	
	default void defaultOnEntityWalked(World worldIn, BlockPos pos, Entity entityIn) {
		if (!(entityIn instanceof LivingEntity)) return;
		LivingEntity entity = (LivingEntity)entityIn;
		if (worldIn.getRandom().nextDouble() > 0.3D) {
			DecayEffect decay = (DecayEffect)PotionRegistry.get(DecayEffect.class);
			entity.addPotionEffect(decay.constructEffect(20, 0));
		}
	}
	

}
