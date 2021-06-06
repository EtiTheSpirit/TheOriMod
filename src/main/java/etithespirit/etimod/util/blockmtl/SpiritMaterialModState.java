package etithespirit.etimod.util.blockmtl;

/** Describes a modifier for a material. The player can have one of these states associated with them to alter the sound played. 
 * Generally speaking, the block that modifies the state will contain the player (no collision) and the block that reflects upon the modified state will be
 * below the player.
 */
public enum SpiritMaterialModState {
	
	/** Use the first material in the code-defined material list. */
	DEFAULT,
	
	/** The block the player is walking on is dry. */
	DRY,
	
	/** The block the player is walking on is always wet. */
	WET,
	
	/** The event in which the block the player is walking on is considered wet is manually defined. That's a mouthful. */
	@Deprecated WET_CUSTOM,
	
	/** The block the player is walking on is considered wet while it's raining and the block can see the sky. */
	@Deprecated WET_RAINING,
	
	/** The block the player is walking on is snowy. */
	SNOWY,
	
	/** The event in which the block the player is walking on is considered snowy is manually defined. That's a mouthful. */
	@Deprecated SNOWY_CUSTOM,
	
	/** The block the player is walking on is snowy, but only when its snowing and the block can see the sky. */
	@Deprecated SNOWY_SNOWING,
	
	/** The block the player is walking on is mossy or has a layer of plantlife above it, by default, only vines trip this. */
	MOSSY,
	
	/** The event in which the block the player is walking on is considered mossy is manually defined. That's a mouthful. */
	@Deprecated MOSSY_CUSTOM,
	
	/** The player is walking in shallow water. */
	SHALLOW,
	
	/** The player is walking in deep water. */
	DEEP;
	
}
