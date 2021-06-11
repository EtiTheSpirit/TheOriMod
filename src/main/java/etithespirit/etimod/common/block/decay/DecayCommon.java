package etithespirit.etimod.common.block.decay;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3i;

/**
 * Values common across most spreading Decay blocks.
 * @author Eti
 *
 */
public final class DecayCommon {
	
	/**
	 * A limit of 1 in (this many) blocks will perform an edge test.
	 */
	public static final int EDGE_TEST_MINIMUM_CHANCE = 5;
	
	/**
	 * The maximum amount of times that a random diagonal placement will bounce between positions if it scans a position that is already occupied by a decay block before just giving up.
	 */
	public static final int MAX_DIAGONAL_TESTS = 4;
	
	/**
	 * A property storing if all adjacent blocks are decay blocks. This is used for performance optimizations to avoid checking if there's no need to.<br/>
	 * While it was a bit of an extra step to implement, this was instrumental 
	 */
	public static final BooleanProperty ALL_ADJACENT_ARE_DECAY = BooleanProperty.create("adjacent_filled");
	
	/**
	 * Since some blocks may not be connected in an adjacent fashion, this is the rarity of a chance that a random tick will be used to check diagonally connected blocks (to get a full 3x3 cube).<br>
	 * Lower values denote more frequent tests. Think of it as "1 in (this many + 1) tests will look for edge blocks."  
	 */
	public static final IntegerProperty EDGE_DETECTION_RARITY = IntegerProperty.create("edge_test_rarity", 0, EDGE_TEST_MINIMUM_CHANCE);
	
	/**
	 * All adjacent directions sorted by test order. The test order is: EAST, WEST, UP, DOWN, NORTH, SOUTH
	 */
	public static final Vector3i[] ADJACENTS_IN_ORDER = new Vector3i[] {
		new Vector3i(1, 0, 0),
		new Vector3i(-1, 0, 0),
		new Vector3i(0, 1, 0),
		new Vector3i(0, -1, 0),
		new Vector3i(0, 0, -1),
		new Vector3i(0, 0, 1)
	};
	
	/**
	 * An array of panel shaped collision boxes. For those unaware, the units are pixels, so 16 is a full block. The order of this array matters for use in {@link etithespirit.etimod.common.block.decay.world.DecaySurfaceMyceliumBlock}
	 */
	public static final VoxelShape[] PANELS = new VoxelShape[] {
		Block.box(15D, 0D, 0D, 16D, 16D, 16D),
		Block.box(0D, 0D, 0D, 1D, 16D, 16D),
		
		Block.box(0D, 15D, 0D, 16D, 16D, 16D),
		Block.box(0D, 0D, 0D, 16D, 1D, 16D),
		
		Block.box(0D, 0D, 0D, 16D, 16D, 1D),
		Block.box(0D, 0D, 15D, 16D, 16D, 16D),
	};
	
	/**
	 * All diagonal directions sorted by test order. This fills a 3x3 cube, with the exception of the local position, and all adjacent positions (which are tested separately)
	 */
	public static final Vector3i[] DIAGONALS_IN_ORDER = new Vector3i[] {
		// [X] top back
		new Vector3i(1, 1, 1),
		new Vector3i(0, 1, 1),
		new Vector3i(-1, 1, 1),
		
		// [X] middle back
		new Vector3i(1, 0, 1),
		// adj, back
		new Vector3i(-1, 0, 1),
		
		// [X] bottom back
		new Vector3i(1, -1, 1),
		new Vector3i(0, -1, 1),
		new Vector3i(-1, -1, 1),
		
		// [X] top front
		new Vector3i(1, 1, -1),
		new Vector3i(0, 1, -1),
		new Vector3i(-1, 1, -1),
		
		// [X] middle front
		new Vector3i(1, 0, -1),
		// adj, front
		new Vector3i(-1, 0, -1),
		
		// [X] bottom front
		new Vector3i(1, -1, -1),
		new Vector3i(0, -1, -1),
		new Vector3i(-1, -1, -1),
		
		
		// right [Y] middle
		new Vector3i(1, 1, 0),
		// adj. right
		new Vector3i(1, -1, 0),
		
		// left [Y] middle
		new Vector3i(-1, 1, 0),
		// adj. left
		new Vector3i(-1, -1, 0)
	};
	
	/**
	 * All adjacent directions sorted by test order, excluding up and down.
	 */
	public static final Vector3i[] LATERAL_ADJACENTS_IN_ORDER = new Vector3i[] {
		new Vector3i(1, 0, 0),
		new Vector3i(-1, 0, 0),
		new Vector3i(0, 0, -1),
		new Vector3i(0, 0, 1)
	};
	
	/**
	 * All diagonal directions sorted by test order. This fills a 3x3 cube, with the exception of the local position, and all adjacent positions (which are tested separately), and all vertical movements.
	 */
	public static final Vector3i[] LATERAL_DIAGONALS_IN_ORDER = new Vector3i[] {
		new Vector3i(1, 0, 1),
		new Vector3i(-1, 0, 1),
		new Vector3i(1, 0, -1),
		new Vector3i(-1, 0, -1),
	};
	
	/**
	 * A mapping where keys are vanilla BlockStates and values are Decay equivalents.
	 */
	public static final Map<BlockState, BlockState> BLOCK_REPLACEMENT_TARGETS = new HashMap<BlockState, BlockState>();
	
	public static boolean isDecayBlock(Block block) {
		return block instanceof IDecayBlockIdentifier;
	}
	
	static {
		int realMaxTests = ADJACENTS_IN_ORDER.length + DIAGONALS_IN_ORDER.length;
		if (MAX_DIAGONAL_TESTS > realMaxTests) {
			System.err.println("[WARNING] DecayBlockBase -- MAX_DIAGONAL_TESTS is greater than " + realMaxTests + " - being higher than this value will do absolutely nothing.");
		}
	}

}
