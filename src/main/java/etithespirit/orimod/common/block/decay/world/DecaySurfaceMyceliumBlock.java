package etithespirit.orimod.common.block.decay.world;


import etithespirit.orimod.common.block.decay.DecayCommon;
import etithespirit.orimod.common.block.decay.IDecayBlock;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import etithespirit.orimod.util.level.StateHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_TEST_MINIMUM_CHANCE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WEST;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.EAST;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.UP;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOWN;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.NORTH;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SOUTH;

import static etithespirit.orimod.common.block.StaticData.FALSE_POSITION_PREDICATE;
import static etithespirit.orimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.orimod.util.Bit32.hasAnyFlag;

/**
 * A unique block that represents a thin film of Decay mycelium. It grows over the surface of blocks.
 * @author Eti
 *
 */
public class DecaySurfaceMyceliumBlock extends Block implements IDecayBlock {
	
	/* NOTE TO INDIVIDUALS HERE TRYING TO FIGURE OUT HOW TO MAKE INSIDE OUT BLOCKS:
	 *
	 * I was doing the same thing, but I was looking at Thaumcraft's code for fibrous taint. It was kinda complex and took a while.
	 * (If you're a TC dev, no, I didn't copy/paste anything. I just didn't realize how to set collision shapes.)
	 *
	 * I've made extra efforts to annotate this code with ample comments so that you can figure out what I'm doing and why.
	 * I encourage you to use this code (or, rewrite it yourself for your mod) if you want to make an inside out block.
	 * It's super complicated and if I can save you the trouble then I will.
	 */
	
	/// COLLISION CONSTRUCTION ///
	/** Every possible combination of occupied surfaces. **/
	protected static final VoxelShape[] COLLISION_SHAPES = SixSidedUtils.getBitwiseColliderArrayFor(DecaySurfaceMyceliumBlock::getShapeFor);
	
	/** Every block that is specifically replaced by Decay Surface Mycelium, which is tracked because of its special directional handling. */
	protected final List<BlockState> blocksToReplaceWithMycelium = new ArrayList<>();
	
	/**
	 * Given a numeric value with any of the right-most six bits set (0b00XXXXXX), this will return a collision box with a panel over the corresponding face.<br/>
	 * In order from left to right, the bits are: back, front, bottom, top, left, right<br/>
	 * For example, inputting {@code 0b100001} will return a collision box with a panel on the front and right surfaces of <em>this</em> block (so, that is, on the neighboring blocks it will be on the left face of the block to the right of this one, and on the back face of the block in front of this one.)
	 * @param flags The shape flags.
	 * @return A shape corresponding to the given flags.
	 */
	private static VoxelShape getShapeFor(int flags) {
		if (flags == 0) return Shapes.block();
		// ^ This is intentional in order to allow the block to be broken if it is placed somewhere where it shouldn't be.
		// By setting its collision box to a full cube, if for whatever reason the automatic removal (see the bottom)
		// fails, we can remove it manually rather than creating a block with no hitbox.
		
		boolean right = hasAnyFlag(flags, 0b000001);
		boolean left = hasAnyFlag(flags, 0b000010);
		boolean top = hasAnyFlag(flags, 0b000100);
		boolean bottom = hasAnyFlag(flags, 0b001000);
		boolean front = hasAnyFlag(flags, 0b010000);
		boolean back = hasAnyFlag(flags, 0b100000);
		
		VoxelShape def = Shapes.empty();
		if (left) def = Shapes.or(def, DecayCommon.PANELS[0]);
		if (right) def = Shapes.or(def, DecayCommon.PANELS[1]);
		if (top) def = Shapes.or(def, DecayCommon.PANELS[2]);
		if (bottom) def = Shapes.or(def, DecayCommon.PANELS[3]);
		if (front) def = Shapes.or(def, DecayCommon.PANELS[4]);
		if (back) def = Shapes.or(def, DecayCommon.PANELS[5]);
		return def;
	}
	
	/////////////////////////
	/// CTOR ///
	/** */
	public DecaySurfaceMyceliumBlock() {
		super(Properties.of(Material.REPLACEABLE_PLANT).dynamicShape().strength(0.4f).noCollission().sound(SoundType.SLIME_BLOCK).randomTicks().noLootTable().isRedstoneConductor(FALSE_POSITION_PREDICATE));
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE)
				.setValue(EDGE_DETECTION_RARITY, 1)
				.setValue(WEST, false)
				.setValue(EAST, false)
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(NORTH, false)
				.setValue(SOUTH, false)
		);
	}
	
	@Override
	@SuppressWarnings({ "unchecked" })
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		builder.add(ALL_ADJACENT_ARE_DECAY);
		builder.add(EDGE_DETECTION_RARITY);
		builder.add(WEST);
		builder.add(EAST);
		builder.add(UP);
		builder.add(DOWN);
		builder.add(NORTH);
		builder.add(SOUTH);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	/// STATE MODIFICATION ///
	private static StateHolder<?, ?> modifyStateForNeighbors(LevelReader worldIn, BlockPos pos, BlockState state) {
		return modifyStateForNeighbors(worldIn, pos, state, null);
	}
	
	private static StateHolder<?, ?> modifyStateForNeighbors(LevelReader worldIn, BlockPos pos, BlockState state, @Nullable BlockPos forcedActive) {
		int f = SixSidedUtils.getNonAirNonFluidFullNeighbors(worldIn, pos, forcedActive);
		BlockState retn = state.getBlock().defaultBlockState();
		if (hasAnyFlag(f, 0b000001)) {
			retn = retn.setValue(WEST, true);
		}
		if (hasAnyFlag(f, 0b000010)) {
			retn = retn.setValue(EAST, true);
		}
		if (hasAnyFlag(f, 0b000100)) {
			retn = retn.setValue(UP, true);
		}
		if (hasAnyFlag(f, 0b001000)) {
			retn = retn.setValue(DOWN, true);
		}
		if (hasAnyFlag(f, 0b010000)) {
			retn = retn.setValue(NORTH, true);
		}
		if (hasAnyFlag(f, 0b100000)) {
			retn = retn.setValue(SOUTH, true);
		}
		return retn;
	}
	
	/**
	 * Returns the shape of the collision box.
	 */
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return COLLISION_SHAPES[SixSidedUtils.getNumberFromSurfaces(state)];
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DECAY IMPLEMENTATION ///
	@Override
	public void registerReplacements(List<StateHolder<?, ?>> blocksToReplaceWithSelf) {
		registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.AIR); // to prevent error. Something has to be registered.
		registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.VINE);
		registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.CAVE_VINES);
		registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.MOSS_CARPET);
		registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.SNOW);
		
	}
	
	private void registerAllStatesForBlock(List<StateHolder<?, ?>> blocksToReplaceWithSelf, Block block) {
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, block);
		blocksToReplaceWithMycelium.addAll(block.getStateDefinition().getPossibleStates());
	}
	
	// This override affects the randomUnoccupied* methods.
	@Override
	public boolean hasDecayReplacementFor(Level worldIn, BlockPos at, StateHolder<?, ?> existingBlock) {
		StateHolder<?, ?> replacement = getDecayReplacementFor(existingBlock);
		if (replacement != null) {
			if (replacement instanceof BlockState replacementBlock && replacementBlock.getBlock() instanceof DecaySurfaceMyceliumBlock) {
				return SixSidedUtils.hasNonAirNonFluidFullNeighbor(worldIn, at);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public BlockState healsInto(BlockState thisState) {
		return Blocks.AIR.defaultBlockState();
	}
	
	@Override
	public StateHolder<?, ?> getDecayReplacementFor(StateHolder<?, ?> existingBlock) {
		if (existingBlock instanceof BlockState blockState && blockState.isAir()) return defaultBlockState();
		return IDecayBlock.getDefaultDecayReplacementFor(existingBlock);
	}
	
	@Override
	public void doDiagonalSpread(StateHolder<?, ?> state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		BlockPos randomUnoccupied = randomUnoccupiedDiagonal(worldIn, pos, random);
		if (randomUnoccupied != null) {
			// Could find one. Replace and increase the chance.
			StateHolder<?, ?> replacement = this.getDecayReplacementFor(worldIn.getBlockState(randomUnoccupied));
			if (replacement != null) {
				if (replacement instanceof BlockState replacementBlock && replacementBlock.getBlock() instanceof DecaySurfaceMyceliumBlock) replacement = modifyStateForNeighbors(worldIn, randomUnoccupied, replacementBlock);
				//worldIn.setBlockAndUpdate(randomUnoccupied, mutateReplacementState(replacement));
				StateHelper.setBlockAndUpdateIn(worldIn, randomUnoccupied, mutateReplacementState(replacement, worldIn, randomUnoccupied));
				int currentRarity = state.getValue(EDGE_DETECTION_RARITY);
				if (currentRarity > 0) {
					//worldIn.setBlockAndUpdate(pos, state.setValue(EDGE_DETECTION_RARITY, currentRarity - 1));
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
	public void doAdjacentSpread(StateHolder<?, ?> state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		BlockPos randomUnoccupied = randomUnoccupiedDirection(worldIn, pos, random);
		if (randomUnoccupied != null) {
			BlockState original = worldIn.getBlockState(randomUnoccupied);
			StateHolder<?, ?> replacement = this.getDecayReplacementFor(original);
			if (replacement != null) {
				if (replacement instanceof BlockState replacementBlock && replacementBlock.getBlock() instanceof DecaySurfaceMyceliumBlock) replacement = modifyStateForNeighbors(worldIn, randomUnoccupied, replacementBlock);
				StateHelper.setBlockAndUpdateIn(worldIn, randomUnoccupied, mutateReplacementState(replacement, worldIn, randomUnoccupied));
			}
		} else {
			// No unoccupied blocks!
			StateHelper.setBlockAndUpdateIn(worldIn, pos, StateHelper.setManyValues(state, ALL_ADJACENT_ARE_DECAY, true, EDGE_DETECTION_RARITY, 0));
			// Also reset edge detection rarity to promote more.
		}
	}
	
	///////////////////////////////////////////////////////
	/// NEIGHBOR STATE CHANGES ///
	@Override
	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		defaultRandomTick(state, worldIn, pos, random);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!SixSidedUtils.hasNonAirNonFluidFullNeighbor(worldIn, pos)) {
			// No neighbors around this block now? Delete it.
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		
		BlockState newState = (BlockState)modifyStateForNeighbors(worldIn, pos, state); // Modify this state to adapt to its neighbors.
		if (!(blockIn instanceof IDecayBlock) && getDecayReplacementFor(worldIn.getBlockState(fromPos)) != null) {
			// If the new neighbor wasn't a decay block and there is no replacement for it, mark this as false.
			newState = newState.setValue(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE);
		}
		worldIn.setBlockAndUpdate(pos, newState); // Update the state
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		// onPlace
		BlockState newState = (BlockState)modifyStateForNeighbors(worldIn, pos, state);
		if (newState == state.getBlock().defaultBlockState()) {
			// Update our state to fit the new neighbors, but if there are no neighbors, then set the block state to air instead to delete it.
			worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		worldIn.setBlockAndUpdate(pos, newState);
	}
	
	/**
	 * Determines if a surface mycelium block (this block) can spread to the given position. This should <strong>NOT</strong>
	 * return true for any other Decay replacements, only surface mycelium replacements should return true.
	 * @param worldIn The world this is spreading in.
	 * @param at The location this wants to spread to.
	 * @param existingBlock The block that is already there.
	 * @return Whether or not this block can spread to the given block position.
	 */
	public static boolean canSpreadSurfaceMyceliumTo(Level worldIn, BlockPos at, BlockState existingBlock) {
		boolean willBeReplacedByThis = existingBlock.isAir();
		boolean neighbor = SixSidedUtils.hasNonAirNonFluidFullNeighbor(worldIn, at);
		return neighbor && willBeReplacedByThis;
	}
}
