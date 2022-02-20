package etithespirit.orimod.common.block.decay;


import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

import etithespirit.orimod.info.coordinate.Cardinals;

import java.util.HashMap;
import java.util.Map;

/**
 * Values common across most spreading Decay blocks.
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public final class DecayCommon {
	
	private DecayCommon() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
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
	 * A mapping where keys are vanilla BlockStates and values are Decay equivalents.
	 */
	public static final Map<BlockState, BlockState> BLOCK_REPLACEMENT_TARGETS = new HashMap<>();
	
	public static boolean isDecayBlock(Block block) {
		return block instanceof IDecayBlockIdentifier;
	}
	
	static {
		int realMaxTests = Cardinals.ADJACENTS_IN_ORDER.length + Cardinals.DIAGONALS_IN_ORDER.length;
		if (MAX_DIAGONAL_TESTS > realMaxTests) {
			System.err.println("[WARNING] DecayBlockBase -- MAX_DIAGONAL_TESTS is greater than " + realMaxTests + " - being higher than this value will do absolutely nothing.");
		}
	}
	
}
