package etithespirit.orimod.common.tile.light.helpers;

/**
 * This class provides utilities to energy consumers that help them to manage energy when it is not abundant enough.
 */
public final class EnergyReservoir {

	private float stashedEnergy;
	private final float maxStashed;
	
	public EnergyReservoir(float maxStash) {
		this.maxStashed = maxStash;
	}
	
	/**
	 * @return The amount of energy currently stored in the stash.
	 */
	public float getStashedEnergy() {
		return stashedEnergy;
	}
	
	/**
	 * @return The maximum amount of energy that can be stashed.
	 */
	public float getMaxStashedEnergy() {
		return maxStashed;
	}
	
	/**
	 * Adds the given amount of energy to the stash.
	 * @param amount The amount to add.
	 * @return The actual amount of energy that was stashed.
	 */
	public float stash(float amount, boolean simulate) {
		float result = stashedEnergy + amount;
		if (result > maxStashed) {
			if (!simulate) {
				stashedEnergy = maxStashed;
			}
			float overflow = result - maxStashed;
			return amount - overflow;
		}
		if (!simulate) {
			stashedEnergy = result;
		}
		return amount;
	}
	
	/**
	 * Returns true if the desired amount given can be consumed.
	 * @param desiredAmount The amount that should be consumed.
	 * @return True if the amount of stashed energy is greater than or equal to the desired amount.
	 */
	public boolean canConsume(float desiredAmount) {
		return stashedEnergy >= desiredAmount;
	}
	
	/**
	 * Tries to consume the given amount of energy.
	 * @param desiredAmount The desired amount that should be consumed.
	 * @return True if the amount was consumed, false if nothing changed due to there being too little energy.
	 */
	public boolean tryConsume(float desiredAmount, boolean simulate) {
		if (simulate) return stashedEnergy >= desiredAmount;
		
		if (stashedEnergy < desiredAmount) return false;
		stashedEnergy -= desiredAmount;
		return true;
	}
	
	/**
	 * Attempts to consume the given desired amount, returning the actual amount of power that was consumed.
	 * @param desiredAmount The amount that the caller wants.
	 * @return The actual amount of energy that was able to be removed from reserve.
	 */
	public float consumeUpTo(float desiredAmount, boolean simulate) {
		if (desiredAmount <= 0) return 0;
		float spent = Math.min(stashedEnergy, desiredAmount);
		if (!simulate) {
			stashedEnergy -= spent;
		}
		return spent;
	}

}
