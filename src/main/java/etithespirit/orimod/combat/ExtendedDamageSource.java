package etithespirit.orimod.combat;

import net.minecraft.world.damagesource.DamageSource;

/**
 * Additional damage sources defined by this mod.
 */
public final class ExtendedDamageSource {
	
	// Prevent instances.
	private ExtendedDamageSource() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/** A damage source caused by forces of Decay. */
	public static final DamageSource DECAY = new DamageSource("decay").bypassArmor();
}
