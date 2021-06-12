package etithespirit.etimod.info.coordinate;

import net.minecraft.util.math.vector.Vector3i;

/**
 * A registry of various bits and pieces related to cardinal directions surrounding a block. Naturally, all coords are in local space.
 */
public final class Cardinals {
	
	private Cardinals() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
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
	
}
