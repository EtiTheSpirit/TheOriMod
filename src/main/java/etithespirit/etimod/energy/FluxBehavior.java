package etithespirit.etimod.energy;

import java.util.Random;

import etithespirit.etimod.valuetypes.MutableNumberRange;

/**
 * Represents "flux behavior" in Light equipment.
 * @author Eti
 *
 */
public final class FluxBehavior {

	/**
	 * A FluxBehavior that is always disabled. All getters will return null or zero, and all setters will fail.
	 */
	public static final FluxBehavior DISABLED = new FluxBehavior();
	
	/**
	 * Whether or not this FluxBehavior has no behavior. This is only true on {@link #DISABLED}.
	 */
	public final boolean hasNoBehavior = this == DISABLED;
	
	/**
	 * The range in which flux can occur. Negative values take energy away, and positive values give energy.
	 */
	private final MutableNumberRange fluxMagnitude;
	
	/** Whether or not this is a locked FluxBehavior which means immutable. */
	private final boolean locked;
	
	/** The bias of this flux behavior, which is a value always added to a given random magnitude. */
	private double bias;
	
	/** Whether or not flux is enabled. If false, {@link #getNextEnvFlux(boolean, boolean, boolean)} will always return 0. */
	private boolean enabled;
	
	private long iteration;
	
	private Random rng;
	
	public FluxBehavior(MutableNumberRange magnitude) {
		this(magnitude, 0);
	}
	
	public FluxBehavior(MutableNumberRange magnitude, double bias) {
		this.fluxMagnitude = magnitude;
		this.bias = bias;
		this.locked = false;
		
		rng = new Random();
		iteration = rng.nextLong();
		rng.setSeed(iteration);
	}
	
	private FluxBehavior() {
		fluxMagnitude = null;
		rng = null;
		bias = 0;
		locked = true;
		enabled = false;
	}
	
	/**
	 * Returns the next amount of flux. canExtract and canReceive should reflect the ILightEnergyStorage this exists for, and will set the min (or max) of the randomized energy modifier to 0 accordingly.
	 * @param canExtract
	 * @param canReceive
	 * @param simulate
	 * @return
	 */
	public double getNextEnvFlux(boolean canExtract, boolean canReceive, boolean simulate) {
		if (!enabled) return 0;
		if (locked) return 0;
		
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
	 * Returns the {@link MutableNumberRange} that is used to generate values. Modify this object as needed.
	 * @return
	 */
	public MutableNumberRange getRange() {
		if (locked) return null;
		return fluxMagnitude;
	}
	
	/**
	 * Returns the bias of this behavior.
	 * @return
	 */
	public double getBias() {
		if (locked) return 0;
		return bias;
	}
	
	/**
	 * Sets the bias of this behavior.
	 * @param newBias
	 */
	public void setBias(double newBias) {
		if (locked) return;
		bias = newBias;
	}
	
	public boolean isEnabled() {
		if (locked) return false;
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		if (locked) return;
		this.enabled = enabled;
	}
	
}
