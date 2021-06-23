package etithespirit.etimod.info;

import net.minecraft.util.DamageSource;

/**
 * Custom damage sources relevant to the mod.
 *
 * @author Eti
 */
public final class EtiModDamageSource {
	
	// Prevent instances.
	private EtiModDamageSource() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/** A damage source caused by forces of Decay. */
	public static final DamageSource DECAY = new DamageSource("decay").bypassArmor();

}
