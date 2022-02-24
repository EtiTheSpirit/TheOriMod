package etithespirit.orimod.api.util.valuetypes;


import java.util.Random;

/**
 * A value representing a range between two numbers [min, max]
 * @author Eti
 */
@SuppressWarnings("unused")
public class NumberRange {
	
	/** A NumberRange whose min and max are both zero. This can be used as a lazy default. */
	public static final NumberRange ZERO = new NumberRange(0, 0);
	
	/** A randomizer used for {@link #random()} */
	protected final Random rng;
	
	/** The minimum possible value this will return. */
	protected double min;
	
	/** The maximum possible value this will return. */
	protected double max;
	
	/** @return The minimum possible value that this can return. */
	public double getMin() {
		return min;
	}
	
	/** @return The maximum possible value that this can return. */
	public double getMax() {
		return max;
	}
	
	/** @return Whether or not this has a range of zero. */
	public boolean isSingular() {
		return min == max;
	}
	
	/**
	 * Creates a new range between min and max.
	 * If min is greater than max, an {@link IllegalArgumentException} is thrown.
	 * @param min The minimum possible value to contain.
	 * @param max The maximum possible value to contain.
	 * @throws IllegalArgumentException If min is greater than max.
	 */
	public NumberRange(double min, double max) throws IllegalArgumentException {
		this(min, max, new Random());
	}
	
	
	/**
	 * Creates a new range between min and max, using the given randomizer for the {@link #random()} method.
	 * If min is greater than max, an {@link IllegalArgumentException} is thrown.
	 * @param min The minimum possible value to contain.
	 * @param max The maximum possible value to contain.
	 * @param rng The randomizer to use in the {@link #random()} method.
	 * @throws IllegalArgumentException If min is greater than max.
	 */
	public NumberRange(double min, double max, Random rng) throws IllegalArgumentException {
		this.rng = rng;
		if (min > max) {
			throw new IllegalArgumentException("Parameter 'min' is greater than parameter 'max'!");
		}
		this.min = min;
		this.max = max;
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
	 * @return Whether or not this {@link NumberRange} is able to have its minimum and maximum edited.
	 */
	public boolean isMutable() {
		return false;
	}
	
	/**
	 * @return A copy of this {@link NumberRange} as an immutable instance.
	 */
	public NumberRange immutableCopy() {
		return new NumberRange(min, max, rng);
	}
	
	/**
	 * @return A copy of this {@link NumberRange} as a mutable instance.
	 */
	public MutableNumberRange mutableCopy() {
		return new MutableNumberRange(min, max, rng);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof NumberRange o) {
			return min == o.min && max == o.max && rng.equals(o.rng);
		}
		return false;
	}
	
}
