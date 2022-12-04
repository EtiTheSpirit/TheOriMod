package etithespirit.orimod.player;

import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

/**
 * The DamageMarshaller class is responsible for controlling how Damage is applied to Spirits. They should be immune to all impact damage (falling, hitting walls).
 */
public class DamageMarshaller {
	
	/**
	 * Connected to Forge manually. This occurs when the spirit is attacked, and exists mostly for coverage.
	 * @param evt The event forge sends.
	 */
	public static void onEntityAttacked(LivingAttackEvent evt) {
		LivingEntity target = evt.getEntity();
		
		if (target instanceof Player) {
			DamageSource src = evt.getSource();
			if (src == DamageSource.FLY_INTO_WALL || src == DamageSource.FALL || src == DamageSource.IN_WALL) {
				if (SpiritIdentifier.isSpirit(target)) {
					evt.setCanceled(true);
				}
			}
		}
	}
	
	/**
	 * Connected to Forge manually. This occurs when the spirit is damaged.
	 * @param evt The event forge sends.
	 */
	public static void onEntityDamaged(LivingDamageEvent evt) {
		LivingEntity target = evt.getEntity();
		
		if (target instanceof Player) {
			DamageSource src = evt.getSource();
			if (src == DamageSource.FLY_INTO_WALL || src == DamageSource.FALL || src == DamageSource.IN_WALL) {
				if (SpiritIdentifier.isSpirit(target)) {
					evt.setCanceled(true);
				}
			}
		}
	}
}
