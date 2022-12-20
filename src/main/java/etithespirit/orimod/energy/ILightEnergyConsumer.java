package etithespirit.orimod.energy;

/**
 * Closely related to {@link ILightEnergyStorage}, this denotes a Block Entity that is designed to only consume power rather than transfer it and store it.
 */
public interface ILightEnergyConsumer {
	
	/**
	 * Attempts to consume the given amount of energy, returning the amount of power that was actually consumed.
	 * @param desiredAmount The amount of energy that is free for consumption. This can be {@link Float#POSITIVE_INFINITY} to query the amount of power the device wants.
	 * @param simulate If true, the consumption will only be simulated for the sake of querying the desired value, and will not affect the device.
	 * @return The amount of energy that was actually used by this device.
	 */
	float consumeEnergy(float desiredAmount, boolean simulate);
	
	/**
	 * For informational purposes, this returns the absolute maximum amount of power that this consumer could theoretically ever consume on a single tick.
	 * @return The absolute maximum amount of power that this consumer could theoretically ever consume on a single tick.
	 */
	float getMaximumDrawnAmountForDisplay();
	
	/**
	 * Returns true if the last call to {@link #consumeEnergy(float, boolean)} (simulated or not) resulted in less power than desired being consumed.
	 * This is intended for use by external tools (or, in the case of this mod specifically, by Jade) to determine when the device is underpowered without
	 * actually having access to the energy provider(s).
	 * @return True if the last call to {@link #consumeEnergy(float, boolean)} did not have enough power to satisfy the needs of the device.
	 */
	boolean hadTooLittlePowerLastForDisplay();
	
}
