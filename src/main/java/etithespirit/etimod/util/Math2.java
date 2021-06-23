package etithespirit.etimod.util;

/**
 * Well boys, we did it. We invented math 2.
 *
 * @author Eti
 */
public final class Math2 {
	
	private Math2() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * Linearly interpolate from {@code start} to {@code finish} by {@code alpha}%.
	 * @param start The starting point.
	 * @param finish The ending point.
	 * @param alpha How far to go from start to end by this number as a percentage.
	 * @return A value {@code alpha}% from start to end.
	 */
	public static double lerp(double start, double finish, double alpha) {
		return (finish - start) * alpha + start;
	}
	
	/**
	 * Clamps the given value between min and max.
	 * @param value The value to limit.
	 * @param min The minimum value that {@code value} will be returned as.
	 * @param max The maximum value that {@code value} will be returned as.
	 * @return {@code value}, but limited between the range of {@code min} and {@code max}
	 * @throws IllegalArgumentException If {@code min} is greater than {@code max}
	 */
	public static double clamp(double value, double min, double max) {
		if (min > max) throw new IllegalArgumentException("Cannot have a minimum value greater than the maximum value.");
		return Math.max(min, Math.min(max, value));
	}
	
}
