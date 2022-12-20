package etithespirit.orimod.energy;

/**
 * Closely related to {@link ILightEnergyStorage}, this denotes a Block Entity that is designed to only generate power rather than transfer it and store it.
 */
public interface ILightEnergyGenerator {
	
	/**
	 * Attempts to generate the desired amount of energy, returning the actual amount that was generated.
	 * @param desiredAmount The amount of energy that the caller wants to consume (that this generator should generate). This can be {@link Float#POSITIVE_INFINITY} to query the amount of power the device wants.
	 * @param simulate If true, the generation will only be simulated for the sake of querying the desired value, and will not affect the device.
	 * @return The amount of energy that was actually used by this device.
	 */
	float takeGeneratedEnergy(float desiredAmount, boolean simulate);
	
	/**
	 * For informational purposes, this returns the absolute maximum amount of power that this generator could theoretically ever put out on a single tick.
	 * @return The absolute maximum amount of power that this generator could theoretically ever put out on a single tick.
	 */
	float getMaximumGeneratedAmountForDisplay();
	
	/**
	 * Returns true if the last call to {@link #takeGeneratedEnergy(float, boolean)} (simulated or not) resulted in less power than desired being generated.
	 * This is intended for use by external tools (or, in the case of this mod specifically, by Jade) to determine when the device is overdrawn without
	 * actually having access to the energy consumer(s).
	 * @return True if the last call to {@link #takeGeneratedEnergy(float, boolean)} did not have enough power to satisfy the needs of the consumer(s).
	 */
	boolean hadTooMuchDrawLastForDisplay();
}
