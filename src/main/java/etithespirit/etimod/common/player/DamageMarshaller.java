package etithespirit.etimod.common.player;

import etithespirit.etimod.info.spirit.SpiritData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class DamageMarshaller {
	
	public static void onEntityAttacked(LivingAttackEvent evt) {
		LivingEntity target = evt.getEntityLiving();
		
		if (target instanceof PlayerEntity) {
			DamageSource src = evt.getSource();
			if (src == DamageSource.FLY_INTO_WALL || src == DamageSource.FALL || src == DamageSource.IN_WALL) {
				if (SpiritData.isSpirit((PlayerEntity)target)) {
					evt.setCanceled(true);
				}
			}
		}
	}
	
	public static void onEntityDamaged(LivingDamageEvent evt) {
		LivingEntity target = evt.getEntityLiving();
		
		if (target instanceof PlayerEntity) {
			DamageSource src = evt.getSource();
			if (src == DamageSource.FLY_INTO_WALL || src == DamageSource.FALL || src == DamageSource.IN_WALL) {
				if (SpiritData.isSpirit((PlayerEntity)target)) {
					evt.setCanceled(true);
				}
			}
		}
	}
}
