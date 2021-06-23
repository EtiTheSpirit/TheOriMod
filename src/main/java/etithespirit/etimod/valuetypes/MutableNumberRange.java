package etithespirit.etimod.valuetypes;

import java.util.Random;

/**
 * An implementation of {@link NumberRange} that can be modified.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class MutableNumberRange {
	/**
	 * A randomizer used for {@link #random()}
	 */
	private final Random rng;

	/**
	 * The minimum possible value this will return.
	 */
	private double min;
	
	/**
	 * The maximum possible value this will return.
	 */
	private double max;
	
	public MutableNumberRange(double min, double max) {
		this(min, max, new Random());
	}
	
	public MutableNumberRange(double min, double max, Random rng) {
		this.rng = rng;
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
		if (min == max) return min;
		return ((max - min) * alpha) + min;
	}
	
	/**
	 * @return A random value in the range [min, max), unless min and max are equal, from which that value will be returned verbatim.
	 */
	public double random() {
		if (min == max) return min;
		return lerp(rng.nextDouble());
	}
	
	/**
	 * Returns a NumberRange with a range identical to this, but in an immutable state.
	 * @return An immutable representation of this instance.
	 */
	public NumberRange immutable() {
		return new NumberRange(min, max, rng);
	}
	
	public double min() {
		return min;
	}
	
	public double max() {
		return max;
	}
	
	public void setRange(double min, double max) {
		if (min > max) {
			// Just to handle it.
			this.max = min;
			this.min = max;
		} else {
			this.min = min;
			this.max = max;
		}
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
