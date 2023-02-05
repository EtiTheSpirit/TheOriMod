package etithespirit.orimod.info.coordinate;


import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Function3;
import etithespirit.orimod.util.Bit32;
import etithespirit.orimod.util.level.BlockIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static etithespirit.orimod.info.coordinate.Cardinals.ADJACENTS_IN_ORDER;
import static etithespirit.orimod.info.coordinate.Cardinals.DIRECTIONS_IN_ORDER;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

/**
 * A static master-class used for all operations pertaining to bitwise flags for blocks that do things with their neighbors.
 * This contains a wide array of features designed to handle cardinal {@link BooleanProperty BooleanProperties} (UP/DOWN/NORTH/SOUTH/EAST/WEST),
 * {@link Direction Directions}, and more.<br/>
 * <br/>
 * Its most important attribute is that it can condense the enabled/disabled status of a given cardinal {@link BooleanProperty} into
 * an integer value that uses its first six bits. The index of these bits (from LSB to MSB) corresponds directly to the order of
 * directions in {@link Cardinals}, so for instance, bit0 represents {@link Cardinals#ADJACENTS_IN_ORDER}[0].<br/>
 * <br/>
 * Finally, due to its extensive use, this provides the ability to convert between {@link Direction} and cardinal {@link BooleanProperty} instances.
 * It also provides a means of getting the opposite cardinal {@link BooleanProperty} relative to another cardinal {@link BooleanProperty}
 * (for example, {@link BlockStateProperties#EAST} will return {@link BlockStateProperties#WEST})
 *
 * @author Eti
 */
public final class SixSidedUtils {
	
	private SixSidedUtils() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * A mapping from single set bits (six values) that map flags to cardinal {@link BooleanProperty} instances.
	 */
	private static final ImmutableMap<Integer, BooleanProperty> BITWISE_ASSOCIATIONS;
	
	static {
		HashMap<Integer, BooleanProperty> map = new HashMap<>();
		map.put(0b000001, BlockStateProperties.EAST);
		map.put(0b000010, BlockStateProperties.WEST);
		map.put(0b000100, BlockStateProperties.UP);
		map.put(0b001000, BlockStateProperties.DOWN);
		map.put(0b010000, BlockStateProperties.NORTH);
		map.put(0b100000, BlockStateProperties.SOUTH);
		
		BITWISE_ASSOCIATIONS = ImmutableMap.copyOf(map);
	}
	
	public static BooleanProperty getBlockStateForSingleFlagValue(int flag) {
		return BITWISE_ASSOCIATIONS.get(flag);
	}
	
	/**
	 * Returns whether or not the given {@link Vec3i} values are directly adjacent to one another.
	 * @param from The origin position.
	 * @param to The target position.
	 * @return True if the two positions are next to eachother in the 3D grid.
	 */
	public static boolean isAdjacent(Vec3i from, Vec3i to) {
		return isAdjacent(to.subtract(from));
	}
	
	/**
	 * Returns whether or not the given {@link Vec3i} value represents an offset that would put it adjacent to any given {@link Vec3i} if the two were added.
	 * @param difference The difference between two {@link Vec3i}s.
	 * @return True if the two positions are next to eachother in the 3D grid.
	 */
	public static boolean isAdjacent(Vec3i difference) {
		int x = Mth.abs(difference.getX());
		int y = Mth.abs(difference.getY());
		int z = Mth.abs(difference.getZ());
		return x + y + z == 1;
	}
	
	/**
	 * Given a method that takes in an integer value ranging from 0b000000 to 0b111111 and returns a corresponding voxel shape,
	 * this will return a 64-VoxelShape array for every possible combination of bits. The specific shape returned should be
	 * determined with the given {@link Function}
	 * @param getShapeFor A {@link Function} that will construct a shape for a given set of flags.
	 * @return An array of 64 {@link VoxelShape} instances representing every possible combination for all six directions.
	 *      These are ordered so that a given flag value as an index will return the appropriate {@link VoxelShape} for that value.
	 */
	public static VoxelShape[] getBitwiseColliderArrayFor(Function<Integer, VoxelShape> getShapeFor) {
		VoxelShape[] shapes = new VoxelShape[64];
		for (int idx = 0; idx < 64; idx++) {
			shapes[idx] = getShapeFor.apply(idx);
		}
		return shapes;
	}
	
	/**
	 * Given six boolean values, each representing a face's occupied state, this returns a numeric value with the appropriate bit flags set.
	 * @param west Whether or not the west (X+) face is occupied. (Note that west is right because this is relative to Z-, where south is forward.)
	 * @param east Whether or not the east (X-) face is occupied. (Note that east is left because this is relative to Z-, where south is forward.)
	 * @param up Whether or not the top face (Y+) is occupied.
	 * @param down Whether or not the bottom (Y-) face is occupied.
	 * @param north Whether or not the back face (Z+) is occupied.
	 * @param south Whether or not the front face (Z-) is occupied.
	 * @return A numeric value with the first six
	 */
	public static int getNumberFromSurfaces(boolean east, boolean west, boolean up, boolean down, boolean north, boolean south) {
		int n = 0;
		n |= east ?  0b000001 : 0;
		n |= west ?  0b000010 : 0;
		
		n |= up ?    0b000100 : 0;
		n |= down ?  0b001000 : 0;
		
		n |= north ? 0b010000 : 0;
		n |= south ? 0b100000 : 0;
		
		return n;
	}
	
	/**
	 * Calls {@link #getNumberFromSurfaces(boolean, boolean, boolean, boolean, boolean, boolean)} from the given {@link BlockState}'s data, which should contain six booleans representing each face's occupied state.
	 * @param state The state to use.
	 * @return A numeric value representing which surfaces are enabled.
	 */
	public static int getNumberFromSurfaces(BlockState state) {
		return getNumberFromSurfaces(
			state.getValue(EAST),
			state.getValue(WEST),
			state.getValue(UP),
			state.getValue(DOWN),
			state.getValue(NORTH),
			state.getValue(SOUTH)
		);
	}
	
	/**
	 * Given two given block positions, this will return a value with a single flag set
	 * that represents the direction from {@code at} -&gt; {@code otherAt}. Note that these blocks MUST be adjacent.
	 * @param at The origin block; the "from" component of this direction.
	 * @param otherAt The destination block; the "to" component of this direction.
	 * @return A numeric value representing the given direction.
	 */
	public static int neighborFlagForBlockDirection(BlockPos at, BlockPos otherAt) {
		Vec3i diff = otherAt.subtract(at);
		if (!isAdjacent(diff)) return 0;
		for (int i = 0; i < ADJACENTS_IN_ORDER.length; i++) {
			Vec3i adj = ADJACENTS_IN_ORDER[i];
			if (adj.equals(diff)) {
				return 1 << i;
			}
		}
		return 0;
	}
	
	/**
	 * Returns directional flags representing which blocks around the given location are not air, not a fluid or waterlogged, and have a full face facing the block at the given {@link BlockPos}.
	 * @param worldIn The {@link LevelReader} for the world this block exists in.
	 * @param at The location of this block in the world.
	 * @return Flags representing which surrounding neighbors have a full face facing this block, and that are not waterlogged or a fluid.
	 */
	public static int getNonAirNonFluidFullNeighbors(BlockGetter worldIn, BlockPos at) {
		return getNonAirNonFluidFullNeighbors(worldIn, at, new BlockPos[0]);
	}
	
	/**
	 * Returns directional flags representing which blocks around the given location are not air, not a fluid or waterlogged, and have a full face facing the block at the given {@link BlockPos}.
	 * @param worldIn The {@link LevelReader} for the world this block exists in.
	 * @param at The location of this block in the world.
	 * @param forcedNeighbors Any neighbors defined here will forcefully have their flag set to 1. This only works for normals. (magnitude of 1, only 1 axis set to 1).
	 * @return Flags representing which surrounding neighbors have a full face facing this block, and that are not waterlogged or a fluid.
	 */
	public static int getNonAirNonFluidFullNeighbors(BlockGetter worldIn, BlockPos at, BlockPos... forcedNeighbors) {
		if (forcedNeighbors == null) forcedNeighbors = new BlockPos[0];
		int flags = 0;
		
		// ADJACENTS_IN_ORDER is an ordered array of six Vector3is representing a direction to an adjacent block.
		for (int o = 0; o < ADJACENTS_IN_ORDER.length; o++) {
			Vec3i offset = ADJACENTS_IN_ORDER[o];
			BlockPos targetPos = at.offset(offset);
			BlockState neighbor = worldIn.getBlockState(targetPos);
			if (!(neighbor.isAir()) && (neighbor.getFluidState().getType() == Fluids.EMPTY)) {
				// if the neighbor isn't air, and if it's not occupied by a fluid...
				
				// isFaceSturdy
				if (neighbor.isFaceSturdy(worldIn, targetPos, Direction.getNearest(-offset.getX(), -offset.getY(), -offset.getZ())) || BlockIdentifier.isLeafBlock(neighbor.getBlock())) {                    // and if the surface that I'm connecting to is solid or the block is a leaf block...
					flags |= (1 << o); // add the flag
				}
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
	
	/**
	 * Returns all neighbors to the block at the given position that satisfy the given predicate.
	 * @param worldIn The {@link LevelReader} for the world that this block exists in.
	 * @param at The location of this block in the world.
	 * @param filter A {@link Predicate} that determines whether a given neighbor's {@link BlockState} is valid.
	 * @return A numeric value with all bits set for directions that satisfy the predicate.
	 */
	public static int getFlagsForNeighborsWhere(BlockGetter worldIn, BlockPos at, Function3<BlockState, BlockPos, BlockPos, Boolean> filter) {
		int flags = 0;
		for (int o = 0; o < ADJACENTS_IN_ORDER.length; o++) {
			Vec3i offset = ADJACENTS_IN_ORDER[o];
			BlockPos targetPos = at.offset(offset);
			BlockState neighbor = worldIn.getBlockState(targetPos);
			if (filter.apply(neighbor, at, targetPos)) {
				flags |= (1 << o);
			}
		}
		return flags;
	}
	
	/**
	 * This returns true if at least one adjacent block is not air, not occupied by a fluid or waterlogged, and has a funn face facing the block at the given {@link BlockPos}.
	 * @param worldIn The {@link LevelReader} for the world the given block exists in.
	 * @param at The position of the block to check in the world.
	 * @return Whether or not at least one neighbor is sturdy and is not a fluid.
	 */
	public static boolean hasNonAirNonFluidFullNeighbor(BlockGetter worldIn, BlockPos at) {
		return getNonAirNonFluidFullNeighbors(worldIn, at) != 0;
	}
	
	/**
	 * Given a {@link Direction}, this will return the associated cardinal blockstate
	 * (for instance, {@link Direction#DOWN} returns {@link BlockStateProperties#DOWN}).
	 * @param direction The direction to convert.
	 * @return A {@link BooleanProperty} representing a direction equivalent to the given {@link Direction}.
	 * @throws IllegalArgumentException If the input value is not valid.
	 */
	public static BooleanProperty getBlockStateFromDirection(Direction direction) throws IllegalArgumentException {
		if (direction == Direction.DOWN) {
			return BlockStateProperties.DOWN;
			
		} else if (direction == Direction.UP) {
			return BlockStateProperties.UP;
			
		} else if (direction == Direction.EAST) {
			return BlockStateProperties.EAST;
			
		} else if (direction == Direction.WEST) {
			return BlockStateProperties.WEST;
			
		} else if (direction == Direction.NORTH) {
			return BlockStateProperties.NORTH;
			
		} else if (direction == Direction.SOUTH) {
			return BlockStateProperties.SOUTH;
		}
		
		throw new IllegalArgumentException();
	}
	
	/**
	 * Given a "cardinal blockstate" (UP/DOWN/NORTH/SOUTH/EAST/WEST), this will return the BooleanProperty in the opposite direction
	 * (for instance, {@link BlockStateProperties#DOWN} returns {@link BlockStateProperties#UP}).
	 * @param cardinalBlockstateProp A cardinal state.
	 * @return A state in the opposite direction.
	 * @throws IllegalArgumentException If the input value is not one of the six cardinal blockstates.
	 */
	public static BooleanProperty oppositeState(BooleanProperty cardinalBlockstateProp) throws IllegalArgumentException {
		if (cardinalBlockstateProp == BlockStateProperties.DOWN) {
			return BlockStateProperties.UP;
			
		} else if (cardinalBlockstateProp == BlockStateProperties.UP) {
			return BlockStateProperties.DOWN;
			
		} else if (cardinalBlockstateProp == BlockStateProperties.EAST) {
			return BlockStateProperties.WEST;
			
		} else if (cardinalBlockstateProp == BlockStateProperties.WEST) {
			return BlockStateProperties.EAST;
			
		} else if (cardinalBlockstateProp == BlockStateProperties.NORTH) {
			return BlockStateProperties.SOUTH;
			
		} else if (cardinalBlockstateProp == BlockStateProperties.SOUTH) {
			return BlockStateProperties.NORTH;
		}
		
		throw new IllegalArgumentException();
	}
	
	/**
	 * Returns the {@link Direction} of from -> to. The blocks must be adjacent, otherwise the method will return null.
	 * @param from The start position.
	 * @param to The end position.
	 * @return A direction from start to end, or null if the blocks are not adjacent.
	 */
	public static @Nullable	Direction getDirectionBetweenBlocks(BlockPos from, BlockPos to) {
		Vec3i diff = to.subtract(from);
		
		// Below: MUST be adjacent blocks.
		if (!isAdjacent(diff)) return null;
		
		for (Direction adj : DIRECTIONS_IN_ORDER) {
			if (adj.getNormal().equals(diff)) {
				return adj;
			}
		}
		return null;
	}
	
	/**
	 * Given a block raytrace, this returns the closest face direction when comparing the ray's precise 3D location to the center of the block.<br/>
	 * This is strictly useful if the block is NOT a cube. Otherwise just use {@link net.minecraft.world.phys.BlockHitResult#getDirection()} and pass it into {@link #getBlockStateFromDirection(Direction)}.
	 * @param blockPos The position of the block to test.
	 * @param preciseClickPos A precise point somewhere around this block, usually acquired from a raycast result.
	 * @return A {@link BooleanProperty} representing one of the six cardinal directions.
	 */
	public static BooleanProperty getBlockStateFromEvidentFace(BlockPos blockPos, Vec3 preciseClickPos) {
		Vec3 dir = preciseClickPos.subtract(Vec3.atCenterOf(blockPos)).normalize();
		return getBlockStateFromDirection(Direction.getNearest(dir.x, dir.y, dir.z));
	}
	
	/**
	 * Returns a {@link Direction} representative of where the given {@link Vec3} sits relative to the given {@link BlockPos}
	 * @param blockPos The position of the block to test.
	 * @param preciseClickPos A precise point somewhere around this block, usually acquired from a raycast result.
	 * @return A {@link Direction} best representing the direction that the point is at relative to the block.
	 */
	public static Direction getDirectionBetweenBlocks(BlockPos blockPos, Vec3 preciseClickPos) {
		Vec3 dir = preciseClickPos.subtract(Vec3.atCenterOf(blockPos)).normalize();
		return Direction.getNearest(dir.x, dir.y, dir.z);
	}
	
	/**
	 * Assuming the input BlockState has the six cardinal flags (UP/DOWN/NORTH/SOUTH/EAST/WEST),
	 * and assuming that the input flags value is a six-bit value acquired from something like {@link #getNumberFromSurfaces(BlockState)}
	 * this will return the BlockState with a given property set to true or false depending on if a given bit is 1 or 0.
	 * @param original The original {@link BlockState} to base this off of. It will be used to acquire the default state for its {@link Block}.
	 * @param flags The flags representing which directions should be true and false.
	 * @return A new {@link BlockState} associated with the given {@link Block} whose cardinal properties are representative of the given flags.
	 */
	public static BlockState whereSurfaceFlagsAre(BlockState original, int flags) {
		BlockState state = original.getBlock().defaultBlockState();
		for (int idx = 0; idx < 6; idx++) {
			int value = 1 << idx;
			BooleanProperty prop = getBlockStateForSingleFlagValue(value);
			state = state.setValue(prop, (value &  flags) == value);
		}
		return state;
	}
	
	/**
	 * For each flag in {@code flags} that is set, the neighbor to this block will be changed to the given blockstate.
	 * @param level The world to change.
	 * @param state The state to place
	 * @param origin The origin of the placement, which the neighbors are situated around.
	 * @param flags Surface flags acquired by this class.
	 */
	public static void setAllBlocksForFlags(ServerLevel level, BlockState state, BlockPos origin, int flags) {
		for (int i = 0; i < 6; i++) {
			if (Bit32.hasAnyFlag(flags, 1 << i)) {
				level.setBlockAndUpdate(origin.offset(ADJACENTS_IN_ORDER[i]), state);
			}
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static BlockState copySingleProperty(BlockState toModify, BlockState from, Property prop) {
		return toModify.setValue(prop, from.getValue(prop));
	}
	
	public static BlockState copyDirs(BlockState defaultState, BlockState toCopyFrom) {
		defaultState = copySingleProperty(defaultState, toCopyFrom, BlockStateProperties.EAST);
		defaultState = copySingleProperty(defaultState, toCopyFrom, BlockStateProperties.WEST);
		defaultState = copySingleProperty(defaultState, toCopyFrom, BlockStateProperties.NORTH);
		defaultState = copySingleProperty(defaultState, toCopyFrom, BlockStateProperties.SOUTH);
		defaultState = copySingleProperty(defaultState, toCopyFrom, BlockStateProperties.UP);
		defaultState = copySingleProperty(defaultState, toCopyFrom, BlockStateProperties.DOWN);
		return defaultState;
	}
	
}
