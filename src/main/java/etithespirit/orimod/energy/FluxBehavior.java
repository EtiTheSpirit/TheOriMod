package etithespirit.orimod.energy;


import etithespirit.orimod.util.valuetypes.MutableNumberRange;
import etithespirit.orimod.util.valuetypes.NumberRange;

import java.util.Random;

/**
 * Represents "flux behavior" in Light equipment. This behavior causes random energy storage deviations (such as randomly adding
 * or removing an arbitrary amount of energy). The exact manner in which this occurs and in what amount is determined by an instance
 * of this class.<br/>
 * <br/>
 * A crude analogy is to call this "a means of applying entropy".
 *
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public final class FluxBehavior {
	
	/** A {@link FluxBehavior} that representing no entropy whatsoever, that is, the storage container this applies to is 100% stable. */
	public static final FluxBehavior DISABLED = new FluxBehavior();
	
	/** Whether or not this {@link FluxBehavior} has no behavior. This is only true on {@link #DISABLED}. */
	public final boolean isDisabledFlux = this == DISABLED;
	
	/** The range in which flux can occur. Negative values take energy away, and positive values give energy. */
	private final NumberRange fluxMagnitude;
	
	/** The bias of this flux behavior, which is a value always added to a given random magnitude. */
	private double bias;
	
	/** Whether or not flux is enabled. If false, {@link #getNextEnvFlux(boolean, boolean, boolean)} will always return 0. */
	private boolean enabled;
	
	private long iteration;
	
	private final Random rng;
	
	/**
	 * Create a new flux behavior using the given {@link NumberRange} as its room for error, and a bias of 0.
	 * @param magnitude A {@link NumberRange} representing the lowest and highest bounds of randomization to add to a value.
	 */
	public FluxBehavior(NumberRange magnitude) {
		this(magnitude, 0);
	}
	
	/**
	 * Create a new flux behavior using the given {@link NumberRange} as is room for error, with the given bias, which is always
	 * added to the return value of this flux (see {@link #getNextEnvFlux(boolean, boolean, boolean)}.
	 * @param magnitude A {@link NumberRange} representing the lowest and highest bounds of randomization to add to a value.
	 * @param bias A constant value that is added to the return values generated by this flux.
	 */
	public FluxBehavior(NumberRange magnitude, double bias) {
		this.fluxMagnitude = magnitude;
		this.bias = bias;
		
		rng = new Random();
		iteration = rng.nextLong();
		rng.setSeed(iteration);
	}
	
	private FluxBehavior() {
		fluxMagnitude = NumberRange.ZERO;
		rng = null;
		bias = 0;
		enabled = false;
	}
	
	/**
	 * Returns the next amount of flux. canExtract and canReceive should reflect the {@link ILightEnergyStorage} this exists for,
	 * and will set the min (or max) of the randomized energy modifier to 0 accordingly as to not cause unwanted changes.
	 * This will always return 0 on {@link #DISABLED}.
	 * @param canExtract Whether or not the element can extract energy.
	 * @param canReceive Whether or not the element can receive energy.
	 * @param simulate Whether or not this fluctuation is simulated.
	 * @return The net energy, which may be positive or negative depending on the circumstances.
	 */
	public double getNextEnvFlux(boolean canExtract, boolean canReceive, boolean simulate) {
		if (!enabled) return 0;
		if (isDisabledFlux) return 0;
		
		rng.setSeed(iteration);
		double amount = fluxMagnitude.random() + bias;
		
		if (amount > 0) {
			if (!canReceive) amount = 0;
		} else if (amount < 0) {
			if (!canExtract) amount = 0;
		}
		
		if (!simulate) iteration++;
		return amount;
	}
	
	/**
	 * Returns the {@link NumberRange} that is used to generate values. If the flux was constructed with a {@link MutableNumberRange}, then
	 * it is also possible to use this return value to change the applicable range.
	 * This will always return {@link NumberRange#ZERO} on {@link #DISABLED}.
	 * @return The range used to generate random values.
	 */
	public NumberRange getRange() {
		if (isDisabledFlux) return NumberRange.ZERO;
		return fluxMagnitude;
	}
	
	/**
	 * Returns the bias of this behavior. This will always return 0 on {@link #DISABLED}.
	 * @return A constant bias always added to the value.
	 */
	public double getBias() {
		if (isDisabledFlux) return 0;
		return bias;
	}
	
	/**
	 * Sets the bias of this behavior. This will always do nothing on {@link #DISABLED}.
	 * @param newBias A new bias to constantly add to this value.
	 */
	public void setBias(double newBias) {
		if (isDisabledFlux) return;
		bias = newBias;
	}
	
	/**
	 * @return Whether or not this {@link FluxBehavior} is allowed to alter the storage it is tied to. This will always return false on {@link #DISABLED}.
	 */
	public boolean isEnabled() {
		if (isDisabledFlux) return false;
		return enabled;
	}
	
	/**
	 * Sets whether or not this {@link FluxBehavior} is allowed to alter the storage it is tied to. This will always do nothing on {@link #DISABLED}.
	 * @param enabled Whether or not this can alter the storage it's tied to.
	 */
	public void setEnabled(boolean enabled) {
		if (isDisabledFlux) return;
		this.enabled = enabled;
	}
	
}
