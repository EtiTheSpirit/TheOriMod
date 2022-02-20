package etithespirit.orimod.player;

import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class DamageMarshaller {
	
	public static void onEntityAttacked(LivingAttackEvent evt) {
		LivingEntity target = evt.getEntityLiving();
		
		if (target instanceof Player) {
			DamageSource src = evt.getSource();
			if (src == DamageSource.FLY_INTO_WALL || src == DamageSource.FALL || src == DamageSource.IN_WALL) {
				if (SpiritIdentifier.isSpirit(target)) {
					evt.setCanceled(true);
				}
			}
		}
	}
	
	public static void onEntityDamaged(LivingDamageEvent evt) {
		LivingEntity target = evt.getEntityLiving();
		
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
