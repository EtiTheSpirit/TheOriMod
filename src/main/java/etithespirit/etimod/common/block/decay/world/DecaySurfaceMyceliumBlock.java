package etithespirit.etimod.common.block.decay.world;

import static etithespirit.etimod.common.block.decay.DecayCommon.ADJACENTS_IN_ORDER;
import static etithespirit.etimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.etimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;

import static net.minecraft.state.properties.BlockStateProperties.WEST;
import static net.minecraft.state.properties.BlockStateProperties.EAST;
import static net.minecraft.state.properties.BlockStateProperties.UP;
import static net.minecraft.state.properties.BlockStateProperties.DOWN;
import static net.minecraft.state.properties.BlockStateProperties.NORTH;
import static net.minecraft.state.properties.BlockStateProperties.SOUTH;

import static etithespirit.etimod.common.block.StaticData.FALSE_POSITION_PREDICATE;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import etithespirit.etimod.common.block.decay.DecayCommon;
import etithespirit.etimod.common.block.decay.IDecayBlock;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
	protected static final VoxelShape[] COLLISION_SHAPES = new VoxelShape[64];
	static {
		// Generate collision shapes
		// The bit order is:
		// right, left, up, down, front, back
		// Reverse bit order (that is, the above line is listed backwards. b000001 is right, and b100000 is back)
		for (int idx = 0; idx < 64; idx++) {
			COLLISION_SHAPES[idx] = getShapeFor(idx);
		}
	}
	
	
	/**
	 * Given a numeric value with any of the right-most six bits set (0b00XXXXXX), this will return a collision box with a panel over the corresponding face.<br/>
	 * In order from left to right, the bits are: back, front, bottom, top, left, right<br/>
	 * For example, inputting {@code 0b100001} will return a collision box with a panel on the front and right surfaces of <em>this</em> block (so, that is, on the neighboring blocks it will be on the left face of the block to the right of this one, and on the back face of the block in front of this one.)
	 * @param flags
	 * @return
	 */
	private static VoxelShape getShapeFor(int flags) {
		if (flags == 0) return VoxelShapes.fullCube(); 
		// ^ This is intentional in order to allow the block to be broken if it is placed somewhere where it shouldn't be.
		// By setting its collision box to a full cube, if for whatever reason the automatic removal (see the bottom)
		// fails, we can remove it manually rather than creating a block with no hitbox.
		
		boolean right = hasFlag(flags, 0b000001);
		boolean left = hasFlag(flags, 0b000010);
		boolean top = hasFlag(flags, 0b000100);
		boolean bottom = hasFlag(flags, 0b001000);
		boolean front = hasFlag(flags, 0b010000);
		boolean back = hasFlag(flags, 0b100000);
		
		VoxelShape def = VoxelShapes.empty();
		if (right) def = VoxelShapes.or(def, DecayCommon.PANELS[0]);
		if (left) def = VoxelShapes.or(def, DecayCommon.PANELS[1]);
		if (top) def = VoxelShapes.or(def, DecayCommon.PANELS[2]);
		if (bottom) def = VoxelShapes.or(def, DecayCommon.PANELS[3]);
		if (front) def = VoxelShapes.or(def, DecayCommon.PANELS[4]);
		if (back) def = VoxelShapes.or(def, DecayCommon.PANELS[5]);
		return def;
	}
	
	/////////////////////////
	/// CTOR ///
	
	public DecaySurfaceMyceliumBlock() {
		super(Properties.create(Material.TALL_PLANTS).variableOpacity().hardnessAndResistance(0.4f).doesNotBlockMovement().sound(SoundType.SLIME).tickRandomly().noDrops().setOpaque(FALSE_POSITION_PREDICATE));
		this.setDefaultState(
			this.stateContainer.getBaseState()
			.with(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE)
			.with(EDGE_DETECTION_RARITY, 1)
			.with(WEST, false)
			.with(EAST, false)
			.with(UP, false)
			.with(DOWN, false)
			.with(NORTH, false)
			.with(SOUTH, false)
		);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public void fillStateContainer(StateContainer.Builder builder) {
		builder.add(ALL_ADJACENT_ARE_DECAY);
		builder.add(EDGE_DETECTION_RARITY);
		builder.add(WEST);
		builder.add(EAST);
		builder.add(UP);
		builder.add(DOWN);
		builder.add(NORTH);
		builder.add(SOUTH);
	}
	
	
	///////////////////////////////////////////////////////////
	/// POSITION FLAGS ///
	
	/**
	 * Returns whether or not the given number has the given flag(s), determined by {@code flag} 
	 * @param num A numeric value presumably with one or more bits set.
	 * @param flag A value containing all bits that must be 1.
	 * @return {@code num & flag == flag} 
	 */
	private static boolean hasFlag(int num, int flag) {
		return (num & flag) == flag;
	}
	
	/**
	 * Given six boolean values, each representing a face's occupied state, this returns a numeric value with the appropriate bit flags set.
	 * @param west Whether or not the west (X+) face is occupied. (Note that west is right because this is relative to Z-, where south is forward.)
	 * @param east Whether or not the east (X-) face is occupied. (Note that east is left because this is relative to Z-, where south is forward.)
	 * @param up Whether or not the top face (Y+) is occupied.
	 * @param down Whether or not the bottom (Y-) face is occupied.
	 * @param north Whether or not the back face (Z+) is occupied.
	 * @param south Whether or not the front face (Z-) is occupied.
	 * @return
	 */
	private static int getNumberFromSurfaces(boolean west, boolean east, boolean up, boolean down, boolean north, boolean south) {
		int n = 0;
		n |= west ?  0b000001 : 0;
		n |= east ?  0b000010 : 0;
		
		n |= up ?    0b000100 : 0;
		n |= down ?  0b001000 : 0;
		
		n |= north ? 0b010000 : 0;
		n |= south ? 0b100000 : 0;
		
		return n;
	}
	
	/**
	 * Calls {@code getNumberFromSurfaces(boolean, boolean, boolean, boolean, boolean, boolean)} from the given {@link BlockState}'s data, which should contain six booleans representing each face's occupied state.
	 * @param state
	 * @return
	 */
	private static int getNumberFromSurfaces(BlockState state) {
		return getNumberFromSurfaces(
			state.get(WEST), 
			state.get(EAST),
			state.get(UP),
			state.get(DOWN),
			state.get(NORTH),
			state.get(SOUTH)
		);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	/// STATE MODIFICATION ///
	private static BlockState modifyStateForNeighbors(IWorldReader worldIn, BlockPos pos, BlockState state) {
		return modifyStateForNeighbors(worldIn, pos, state, null);
	}
	
	private static BlockState modifyStateForNeighbors(IWorldReader worldIn, BlockPos pos, BlockState state, @Nullable BlockPos forcedActive) {
		int f = getNeighborFlags(worldIn, pos, forcedActive);
		BlockState retn = state.getBlock().getDefaultState();
		if (hasFlag(f, 0b000001)) {
			retn = retn.with(WEST, true);
		}
		if (hasFlag(f, 0b000010)) {
			retn = retn.with(EAST, true);
		}
		if (hasFlag(f, 0b000100)) {
			retn = retn.with(UP, true);
		}
		if (hasFlag(f, 0b001000)) {
			retn = retn.with(DOWN, true);
		}
		if (hasFlag(f, 0b010000)) {
			retn = retn.with(NORTH, true);
		}
		if (hasFlag(f, 0b100000)) {
			retn = retn.with(SOUTH, true);
		}
		return retn;
	}
	
	/**
	 * Returns the shape of the collision box.
	 */
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return COLLISION_SHAPES[getNumberFromSurfaces(state)];
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// DECAY IMPLEMENTATION ///
	@Override
	public void registerReplacements(List<BlockState> blocksToReplaceWithSelf) {
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.AIR); // to prevent error
	}
	
	@Override
	public boolean hasDecayReplacementFor(World worldIn, BlockPos at, BlockState existingBlock) {
		return existingBlock.getBlock() instanceof AirBlock && hasNeighbor(worldIn, at);
	}
	
	//////////////////////////////////////////////////////////////////
	/// NEIGHBOR ACQUISITION ///
	private static int getNeighborFlags(IWorldReader worldIn, BlockPos at) {
		return getNeighborFlags(worldIn, at, new BlockPos[0]);
	}
	
	/**
	 * Returns a value with up to 6 bits set representing the adjacents in order. 0b000001 represents the first adjacent in the array ADJACENTS_IN_ORDER, and 0b100000 represents the last.<br>
	 * The adjacent block must have a solid, full surface facing this block to set the surface flag to 1.<br>
	 * This is for localized testing, not spreading. This is so that it knows what surface to render.
	 * @param worldIn
	 * @param at
	 * @return
	 */
	private static int getNeighborFlags(IWorldReader worldIn, BlockPos at, BlockPos... forcedNeighbors) {
		if (forcedNeighbors == null) forcedNeighbors = new BlockPos[0];
		int flags = 0;
		
		// ADJACENTS_IN_ORDER is an ordered array of six Vector3is representing a direction to an adjacent block.
		for (int o = 0; o < ADJACENTS_IN_ORDER.length; o++) {
			Vector3i offset = ADJACENTS_IN_ORDER[o];
			BlockPos targetPos = at.add(offset);
			BlockState neighbor = worldIn.getBlockState(targetPos);
			if (!(neighbor.getBlock() instanceof AirBlock) && (neighbor.getFluidState().getFluid() == Fluids.EMPTY)) {
				// if the neighbor isn't air, and if it's not occupied by a fluid...
				
				// isFaceSturdy
				if (neighbor.isSolidSide(worldIn, at, Direction.getFacingFromVector(-offset.getX(), -offset.getY(), -offset.getZ())) || isLeafBlock(neighbor.getBlock()))
					// and if the surface that I'm connecting to is solid or the block is a leaf block...
					flags |= (1 << o); // add the flag
					
			}
			
			// If the position is in the array of forced active neighbors, set the flag.
			for (BlockPos pos : forcedNeighbors) {
				if (pos == targetPos) {
					flags |= (1 << o);
					break;
				}
			}
		}
		return flags;
	}
	
	private static boolean isLeafBlock(Block b) {
		return b == Blocks.OAK_LEAVES || b == Blocks.BIRCH_LEAVES || b == Blocks.SPRUCE_LEAVES || b == Blocks.JUNGLE_LEAVES || b == Blocks.ACACIA_LEAVES || b == Blocks.DARK_OAK_LEAVES;
	}
	
	/**
	 * This returns true if at least one adjacent block can have its surface coated by mycelium - the surface facing the given block, specifically.<br>
	 * It is only useful for checking if spreading is possible by looking for at least one surface to latch onto in a neighboring block.
	 * @param worldIn
	 * @param at
	 * @return
	 */
	private static boolean hasNeighbor(IWorldReader worldIn, BlockPos at) {
		return getNeighborFlags(worldIn, at) != 0;
	}
	
	@Override
	public void doAdjacentSpread(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		BlockPos randomUnoccupied = randomUnoccupiedDirection(worldIn, pos, random);
		if (randomUnoccupied != null) {
			BlockState replacement = getDecayReplacementFor(worldIn.getBlockState(randomUnoccupied));
			if (replacement != null) {
				worldIn.setBlockState(randomUnoccupied, mutateReplacementState(replacement));
			}
		} else {
			// No unoccupied blocks!
			worldIn.setBlockState(pos, state.with(ALL_ADJACENT_ARE_DECAY, Boolean.TRUE).with(EDGE_DETECTION_RARITY, 0));
			// Also reset edge detection rarity to promote more.
		}
	}
	
	///////////////////////////////////////////////////////
	/// NEIGHBOR STATE CHANGES ///
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		defaultRandomTick(state, worldIn, pos, random);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!hasNeighbor(worldIn, pos)) {
			// No neighbors around this block now? Delete it.
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			return;
		}
		
		BlockState newState = modifyStateForNeighbors(worldIn, pos, state); // Modify this state to adapt to its neighbors.
		if (!(blockIn instanceof IDecayBlock) && getDecayReplacementFor(worldIn.getBlockState(fromPos)) != null) {
			// If the new neighbor wasn't a decay block and there is no replacement for it, mark this as false.
			newState = newState.with(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE);
		}
		worldIn.setBlockState(pos, newState); // Update the state
	}
	
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		// onPlace
		BlockState newState = modifyStateForNeighbors(worldIn, pos, state);
		if (newState == state.getBlock().getDefaultState()) {
			// Update our state to fit the new neighbors, but if there are no neighbors, then set the block state to air instead to delete it.
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			return;
		}
		worldIn.setBlockState(pos, newState);
	}
	
	/**
	 * Determines if a surface mycelium block (this block) can spread to the given position. 
	 * @param worldIn
	 * @param at
	 * @param existingBlock
	 * @return
	 */
	public static boolean canSpreadTo(World worldIn, BlockPos at, BlockState existingBlock) {
		boolean willBeReplacedByThis = existingBlock.getBlock() instanceof AirBlock; //BLOCK_REPLACEMENT_TARGETS.containsKey(existingBlock) && BLOCK_REPLACEMENT_TARGETS.get(existingBlock).getBlock() instanceof DecaySurfaceMyceliumBlock;
		boolean neighbor = hasNeighbor(worldIn, at);
		return neighbor && willBeReplacedByThis;
	}
}
