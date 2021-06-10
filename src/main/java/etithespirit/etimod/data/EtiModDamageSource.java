package etithespirit.etimod.data;

import net.minecraft.util.DamageSource;

public final class EtiModDamageSource {
	
	// Prevent instances.
	private EtiModDamageSource() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/** A damage source caused by forces of Decay. */
	public static final DamageSource DECAY = new DamageSource("decay").bypassArmor();

}
