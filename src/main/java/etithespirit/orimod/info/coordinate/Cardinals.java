package etithespirit.orimod.info.coordinate;

import net.minecraft.core.Vec3i;

/**
 * Cardinals stores utilities related to the six adjacent directions around a cube, or, the six "cardinal" directions.
 */
public final class Cardinals {
	
	private Cardinals() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * All adjacent directions sorted by test order. The test order is: EAST, WEST, UP, DOWN, NORTH, SOUTH
	 */
	public static final Vec3i[] ADJACENTS_IN_ORDER = new Vec3i[] {
		new Vec3i(1, 0, 0),
		new Vec3i(-1, 0, 0),
		new Vec3i(0, 1, 0),
		new Vec3i(0, -1, 0),
		new Vec3i(0, 0, -1),
		new Vec3i(0, 0, 1)
	};
	
	/**
	 * All diagonal directions sorted by test order. This fills a 3x3 cube, with the exception of the local position, and all adjacent positions (which are tested separately)
	 */
	public static final Vec3i[] DIAGONALS_IN_ORDER = new Vec3i[] {
		// [X] top back
		new Vec3i(1, 1, 1),
		new Vec3i(0, 1, 1),
		new Vec3i(-1, 1, 1),
		
		// [X] middle back
		new Vec3i(1, 0, 1),
		// adj, back
		new Vec3i(-1, 0, 1),
		
		// [X] bottom back
		new Vec3i(1, -1, 1),
		new Vec3i(0, -1, 1),
		new Vec3i(-1, -1, 1),
		
		// [X] top front
		new Vec3i(1, 1, -1),
		new Vec3i(0, 1, -1),
		new Vec3i(-1, 1, -1),
		
		// [X] middle front
		new Vec3i(1, 0, -1),
		// adj, front
		new Vec3i(-1, 0, -1),
		
		// [X] bottom front
		new Vec3i(1, -1, -1),
		new Vec3i(0, -1, -1),
		new Vec3i(-1, -1, -1),
		
		
		// right [Y] middle
		new Vec3i(1, 1, 0),
		// adj. right
		new Vec3i(1, -1, 0),
		
		// left [Y] middle
		new Vec3i(-1, 1, 0),
		// adj. left
		new Vec3i(-1, -1, 0)
	};
	
	/**
	 * All adjacent directions sorted by test order, excluding up and down.
	 */
	public static final Vec3i[] LATERAL_ADJACENTS_IN_ORDER = new Vec3i[] {
		new Vec3i(1, 0, 0),
		new Vec3i(-1, 0, 0),
		new Vec3i(0, 0, -1),
		new Vec3i(0, 0, 1)
	};
	
	/**
	 * All diagonal directions sorted by test order. This fills a 3x3 cube, with the exception of the local position, and all adjacent positions (which are tested separately), and all vertical movements.
	 */
	public static final Vec3i[] LATERAL_DIAGONALS_IN_ORDER = new Vec3i[] {
		new Vec3i(1, 0, 1),
		new Vec3i(-1, 0, 1),
		new Vec3i(1, 0, -1),
		new Vec3i(-1, 0, -1),
	};

}
