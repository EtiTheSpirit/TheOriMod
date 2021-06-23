package etithespirit.etimod.valuetypes;

import java.util.Random;

/**
 * A value representing a range between two numbers [min, max]
 * @author Eti
 */
@SuppressWarnings("unused")
public final class NumberRange {
	
	/**
	 * A NumberRange whose min and max are both zero.
	 */
	public static final NumberRange ZERO = new NumberRange(0, 0);
	
	/**
	 * Whether or not this has a range of zero.
	 */
	public final boolean isZero = this.equals(ZERO);
	
	/**
	 * A randomizer used for {@link #random()}
	 */
	private final Random rng;

	/**
	 * The minimum possible value this will return.
	 */
	public final double min;
	
	/**
	 * The maximum possible value this will return.
	 */
	public final double max;
	
	/**
	 * Whether or not min and max are equal.
	 */
	private final boolean equal;
	
	public NumberRange(double min, double max) {
		this(min, max, new Random());
	}
	
	public NumberRange(double min, double max, Random rng) {
		this.rng = rng;
		this.equal = min == max;
		if (min > max) {
			// Just to handle it.
			this.max = min;
			this.min = max;
		} else {
			this.min = min;
			this.max = max;
		}
	}
	
	/**
	 * Returns the value of this range interpolated to the given percentage.
	 * @param alpha The percentage to go from min to max.
	 * @return A value linearly interpolated from min to max by alpha percent.
	 */
	public double lerp(double alpha) {
		if (equal) return min;
		return ((max - min) * alpha) + min;
	}
	
	/**
	 * @return A random value in the range [min, max), unless min and max are equal, from which that value will be returned verbatim.
	 */
	public double random() {
		if (equal) return min;
		return lerp(rng.nextDouble());
	}
	
	/**
	 * Returns a new {@link NumberRange} with a range identical to this, but using the given randomizer.
	 * @param newRandomizer The new randomizer to use.
	 * @return A new {@link NumberRange} using the given randomizer.
	 */
	public NumberRange withRandomizer(Random newRandomizer) {
		return new NumberRange(min, max, newRandomizer);
	}
	
	/**
	 * @return This {@link NumberRange} in a mutable state. The returned value will not affect this instance, but it will share the same randomizer.
	 */
	public MutableNumberRange mutable() {
		return new MutableNumberRange(min, max, rng);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof NumberRange) {
			NumberRange o = (NumberRange)other;
			return min == o.min && max == o.max;
		}
		return false;
	}
	
}
