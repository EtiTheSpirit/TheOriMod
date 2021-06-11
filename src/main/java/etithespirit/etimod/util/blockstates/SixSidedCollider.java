package etithespirit.etimod.util.blockstates;

import com.google.common.collect.ImmutableMap;
import etithespirit.etimod.common.block.decay.DecayCommon;
import etithespirit.etimod.util.EtiUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorldReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static etithespirit.etimod.common.block.decay.DecayCommon.ADJACENTS_IN_ORDER;
import static net.minecraft.state.properties.BlockStateProperties.*;
import static net.minecraft.state.properties.BlockStateProperties.SOUTH;

public final class SixSidedCollider {
	
	private SixSidedCollider() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	public static final ImmutableMap<Integer, BooleanProperty> BITWISE_ASSOCIATIONS;
	
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
	
	/**
	 * Given a method that takes in an integer value ranging from 0b000000 to 0b111111 and returns a corresponding voxel shape,
	 * this will return a 64-VoxelShape array for every possible combination of bits.
	 * @param getShapeFor
	 * @return
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
	 * @return
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
	 * Calls {@code getNumberFromSurfaces(boolean, boolean, boolean, boolean, boolean, boolean)} from the given {@link BlockState}'s data, which should contain six booleans representing each face's occupied state.
	 * @param state
	 * @return
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
	
	public static int neighborFlagForBlock(IWorldReader worldIn, BlockPos at, BlockPos otherAt) {
		Vector3i diff = at.subtract(otherAt);
		for (int i = 0; i < ADJACENTS_IN_ORDER.length; i++) {
			Vector3i adj = ADJACENTS_IN_ORDER[i];
			if (adj.equals(diff)) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Returns a value with up to 6 bits set representing the adjacents in order. 0b000001 represents the first adjacent in the array ADJACENTS_IN_ORDER, and 0b100000 represents the last.<br>
	 * The adjacent block must have a solid, full surface facing this block to set the surface flag to 1.<br>
	 * This is for localized testing, not spreading. This is so that it knows what surface to render.
	 * @param worldIn
	 * @param at
	 * @return
	 */
	public static int getNonAirNonFluidFullNeighbors(IWorldReader worldIn, BlockPos at) {
		return getNonAirNonFluidFullNeighbors(worldIn, at, new BlockPos[0]);
	}
	
	/**
	 * Returns a value with up to 6 bits set representing the adjacents in order. 0b000001 represents the first adjacent in the array ADJACENTS_IN_ORDER, and 0b100000 represents the last.<br>
	 * The adjacent block must have a solid, full surface facing this block to set the surface flag to 1.<br>
	 * This is for localized testing, not spreading. This is so that it knows what surface to render.
	 * @param worldIn
	 * @param at
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getNonAirNonFluidFullNeighbors(IWorldReader worldIn, BlockPos at, BlockPos... forcedNeighbors) {
		if (forcedNeighbors == null) forcedNeighbors = new BlockPos[0];
		int flags = 0;
		
		// ADJACENTS_IN_ORDER is an ordered array of six Vector3is representing a direction to an adjacent block.
		for (int o = 0; o < ADJACENTS_IN_ORDER.length; o++) {
			Vector3i offset = ADJACENTS_IN_ORDER[o];
			BlockPos targetPos = at.offset(offset);
			BlockState neighbor = worldIn.getBlockState(targetPos);
			if (!(neighbor.isAir(worldIn, at)) && (neighbor.getFluidState().getType() == Fluids.EMPTY)) {
				// if the neighbor isn't air, and if it's not occupied by a fluid...
				
				// isFaceSturdy
				if (Block.isFaceFull(neighbor.getShape(worldIn, at), Direction.getNearest(-offset.getX(), -offset.getY(), -offset.getZ())) || IdentifierHelper.isLeafBlock(neighbor.getBlock()))
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
	
	/**
	 * Returns all neighbors to the block at the given position that satisfy the given predicate.
	 * @param worldIn
	 * @param at
	 * @param filter
	 * @return
	 */
	public static int getNeighborsWhere(IWorldReader worldIn, BlockPos at, Predicate<BlockState> filter) {
		int flags = 0;
		for (int o = 0; o < ADJACENTS_IN_ORDER.length; o++) {
			Vector3i offset = ADJACENTS_IN_ORDER[o];
			BlockPos targetPos = at.offset(offset);
			BlockState neighbor = worldIn.getBlockState(targetPos);
			if (filter.test(neighbor)) {
				flags |= (1 << o);
			}
		}
		return flags;
	}
	
	/**
	 * This returns true if at least one adjacent block is not air, not fluid, and has a sturdy face facing the block at the given BlockPos.
	 * @param worldIn
	 * @param at
	 * @return
	 */
	public static boolean hasNonAirNonFluidFullNeighbor(IWorldReader worldIn, BlockPos at) {
		return getNonAirNonFluidFullNeighbors(worldIn, at) != 0;
	}
	
	public static BooleanProperty getBlockStateFromDirection(Direction direction) {
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
	 * @param cardinalBlockstateProp
	 * @return
	 */
	public static BooleanProperty opposite(BooleanProperty cardinalBlockstateProp) {
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
	 * Given a block raytrace, this returns the closest face direction when comparing the ray's precise 3D location to the center of the block.<br/>
	 * This is strictly useful if the block is NOT a cube. Otherwise just use {@link BlockRayTraceResult#getDirection()} and pass it into {@link #getBlockStateFromDirection(Direction)}.
	 * @param ray
	 * @return
	 */
	public static BooleanProperty getBlockStateFromEvidentFace(BlockRayTraceResult ray) {
		Vector3d center = Vector3d.atCenterOf(ray.getBlockPos());
		Vector3d hit = ray.getLocation();
		Vector3d dir = hit.subtract(center).normalize();
		return getBlockStateFromDirection(Direction.getNearest(dir.x, dir.y, dir.z));
	}
	
	/**
	 * Assuming the input BlockState has the six cardinal flags (UP/DOWN/NORTH/SOUTH/EAST/WEST), and assuming that the input flags value is a six-bit value acquired from something like {@link #getNumberFromSurfaces(BlockState)}
	 * this will return the BlockState with directions matching the values of each bit.
	 * @param original
	 * @param newFlags
	 * @return
	 */
	public static BlockState withSurfaceFlags(BlockState original, int newFlags) {
		BlockState state = original.getBlock().defaultBlockState();
		for (int idx = 0; idx < 6; idx++) {
			int value = 1 << idx;
			if (EtiUtils.hasFlag(newFlags, value)) {
				BooleanProperty prop = BITWISE_ASSOCIATIONS.get(value);
				state = state.setValue(prop, true);
			}
		}
		return state;
	}
	
}
