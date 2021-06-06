package etithespirit.etimod.data;

import net.minecraft.util.DamageSource;

public final class EtiModDamageSource {
	
	// Prevent instances.
	private EtiModDamageSource() { }
	
	/** A damage source caused by forces of Decay. */
	public static final DamageSource DECAY = new DamageSource("decay").setDamageBypassesArmor();

}
