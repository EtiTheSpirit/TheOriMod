package etithespirit.orimod.common.block.decay;

/**
 * Config that Decay blocks must follow when doing their decayish things.
 */
public enum DecayWorldConfigBehavior {
	
	/** This decay block is allowed to spread completely unlimited. Extremely dangerous in this state. */
	ALLOW_SPREADING(true, false, 2),
	
	/** This decay block can exist in the world, but cannot spread or infect nearby blocks. It can still afflict entities with the Decay effect. */
	NO_SPREADING(false, false, 1),
	
	/** This decay block is not allowed to exist in the world. It will destroy itself on the next available random tick, and cannot afflict entities with any effects. */
	NO_EXISTING(false, true, 0);
	
	public final boolean canSpread;
	public final boolean selfDestructs;
	public final int permissiveness;
	
	DecayWorldConfigBehavior(boolean spread, boolean selfDestruct, int permissiveness) {
		this.canSpread = spread;
		this.selfDestructs = selfDestruct;
		this.permissiveness = permissiveness;
	}
}
