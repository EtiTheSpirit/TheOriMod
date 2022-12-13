package etithespirit.orimod.combat;

import etithespirit.orimod.combat.projectile.SpiritArrow;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

/**
 * Additional damage sources defined by this mod.
 */
public final class ExtendedDamageSource {
	
	// Prevent instances.
	private ExtendedDamageSource() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/** A damage source caused by forces of Decay. */
	public static final DamageSource DECAY = new DamageSource("decay").bypassArmor();
	
	/** A damage source caused by forces of Light. */
	public static final DamageSource LIGHT = new DamageSource("light").setMagic();
	
	/** A damage source caused by using your own internal energy to power something. */
	public static final DamageSource USE_SELF_FOR_ENERGY = new DamageSourceWithExhaustion("self_energy").setFoodExhaustion(0.5f).bypassArmor().bypassEnchantments().bypassMagic();
	
	/** Creates a damage source for a Spirit Arc projectile. */
	public static DamageSource spiritArc(SpiritArrow arrow, Entity src) {
		return (new IndirectEntityDamageSource("spirit_arc", arrow, src)).setMagic().setProjectile();
	}
	
	
	
	private static class DamageSourceWithExhaustion extends DamageSource {
		
		public DamageSourceWithExhaustion(String pMessageId) {
			super(pMessageId);
		}
		
		protected float exhaustion = 0.1f;
		
		public DamageSourceWithExhaustion setFoodExhaustion(float to) {
			exhaustion = to;
			return this;
		}
		
		public float getFoodExhaustion() {
			return exhaustion;
		}
	}
}
