package etithespirit.orimod.api.util.valuetypes;


import java.util.Random;

/**
 * An implementation of {@link NumberRange} that can have its minimum and maximum set after construction.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public class MutableNumberRange extends NumberRange {
	
	/**
	 * Creates a new range between min and max.
	 * If min is greater than max, an {@link IllegalArgumentException} is thrown.
	 * @param min The minimum possible value to contain.
	 * @param max The maximum possible value to contain.
	 * @throws IllegalArgumentException If min is greater than max.
	 */
	public MutableNumberRange(double min, double max) throws IllegalArgumentException {
		super(min, max);
	}
	
	
	/**
	 * Creates a new range between min and max, using the given randomizer for the {@link #random()} method.
	 * If min is greater than max, an {@link IllegalArgumentException} is thrown.
	 * @param min The minimum possible value to contain.
	 * @param max The maximum possible value to contain.
	 * @param rng The randomizer to use in the {@link #random()} method.
	 * @throws IllegalArgumentException If min is greater than max.
	 */
	public MutableNumberRange(double min, double max, Random rng) throws IllegalArgumentException {
		super(min, max, rng);
	}
	
	/**
	 * Sets the value of min.
	 * @param min The new value of min.
	 * @throws IllegalArgumentException If the new value is greater than the current maximum.
	 */
	public void setMin(double min) throws IllegalArgumentException {
		if (min > this.max) {
			throw new IllegalArgumentException("Parameter 'min' is greater than the current maximum!");
		}
		this.min = min;
	}
	
	/**
	 * Sets the value of max.
	 * @param max The new value of max.
	 * @throws IllegalArgumentException If the new value is less than the current minimum.
	 */
	public void setMax(double max) throws IllegalArgumentException {
		if (this.min > max) {
			throw new IllegalArgumentException("Parameter 'max' is less than the current minimum!");
		}
		this.max = max;
	}
	
	/**
	 * A method to set both the value of min and the value of max simultaneously.
	 * @param min The new value of min.
	 * @param max The new value of max.
	 * @throws IllegalArgumentException If min is greater than max.
	 */
	public void setMinAndMax(double min, double max) throws IllegalArgumentException {
		if (min > max) {
			throw new IllegalArgumentException("Parameter 'min' is greater than parameter 'max'!");
		}
		this.min = min;
		this.max = max;
	}
	
	/**
	 * @return Whether or not this {@link NumberRange} is able to have its minimum and maximum edited.
	 */
	@Override
	public boolean isMutable() {
		return true;
	}
	
	/**
	 * @return A copy of this {@link NumberRange} as an immutable instance.
	 */
	@Override
	public NumberRange immutableCopy() {
		return new NumberRange(min, max, rng);
	}
	
	/**
	 * @return A copy of this {@link NumberRange} as a mutable instance.
	 */
	@Override
	public MutableNumberRange mutableCopy() {
		return new MutableNumberRange(min, max, rng);
	}
}
