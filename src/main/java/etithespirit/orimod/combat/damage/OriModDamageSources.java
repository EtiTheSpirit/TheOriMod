package etithespirit.orimod.combat.damage;

import etithespirit.orimod.combat.damage.implementation.ExtendedDamageSource;
import etithespirit.orimod.combat.projectile.SpiritArrow;
import etithespirit.orimod.common.entity.DecayExploder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

/**
 * Additional damage sources defined by this mod.
 */
public final class OriModDamageSources {
	
	// Prevent instances.
	private OriModDamageSources() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/** A damage source caused by forces of Decay. */
	public static final ExtendedDamageSource DECAY = IExtendedDamageSource.setDecay(new ExtendedDamageSource("decay")).lock();
	
	/** A damage source caused by forces of Light. */
	public static final ExtendedDamageSource LIGHT = IExtendedDamageSource.setLight(new ExtendedDamageSource("light")).lock();
	
	/** A damage source caused by using your own internal energy to power something. */
	public static final DamageSource USE_SELF_FOR_ENERGY = new DamageSourceWithExhaustion("self_energy").setFoodExhaustion(0.5f).bypassArmor().bypassEnchantments().bypassMagic();
	
	/** Creates a damage source for a Spirit Arc projectile. */
	public static DamageSource spiritArc(SpiritArrow arrow, Entity src) {
		return (new IndirectEntityDamageSource("spirit_arc", arrow, src)).setMagic().setProjectile();
	}
	
	/**
	 * Automatically reads the input damage source and checks if it has the data classifying it as a decay type source.
	 * @param srcIn The source of damage.
	 * @return True if the source is decay-based damage.
	 */
	public static boolean isDecayDamage(DamageSource srcIn) {
		if (srcIn instanceof IExtendedDamageSource<?> alignedSrc) {
			return IExtendedDamageSource.isDecay(alignedSrc);
		}
		return false;
	}
	
	/**
	 * Automatically reads the input damage source and checks if it has the data classifying it as a light type source.
	 * @param srcIn The source of damage.
	 * @return True if the source is light-based damage.
	 */
	public static boolean isLightDamage(DamageSource srcIn) {
		if (srcIn instanceof IExtendedDamageSource<?> alignedSrc) {
			return IExtendedDamageSource.isLight(alignedSrc);
		}
		return false;
	}
	
	private static class DamageSourceWithExhaustion extends DamageSource {
		
		public DamageSourceWithExhaustion(String pMessageId) {
			super(pMessageId);
		}
		
		protected float exhaustion = 0f;
		
		public DamageSourceWithExhaustion setFoodExhaustion(float to) {
			exhaustion = to;
			return this;
		}
		
		public float getFoodExhaustion() {
			return exhaustion;
		}
	}
}
