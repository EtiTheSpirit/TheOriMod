package etithespirit.etimod.info.spirit.capabilities;

public interface ISpiritCapabilities {
	
	/**
	 * Set whether or not the associated entity is a spirit.
	 * @param isSpirit Whether or not the entity is a spirit.
	 */
	void setIsSpirit(boolean isSpirit);
	
	/**
	 * @return Whether or not the entity is classified as a spirit.
	 */
	boolean getIsSpirit();
	
	/**
	 * @return The spirit ability container, which can be mutated.
	 */
	SpiritGameAbilities getSpiritAbilities();
	
	/**
	 * Copies the data of this instance into the other instance.
	 * @param other
	 */
	void copyTo(ISpiritCapabilities other);
	
}
