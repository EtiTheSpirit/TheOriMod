package etithespirit.orimod.spirit;

import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.config.OriModConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.Event;

public final class SpiritRestrictions {
	
	public static void onEat(LivingEntityUseItemEvent.Start evt) {
		LivingEntity entity = evt.getEntity();
		if (!entity.isAddedToWorld()) return;
		if (entity instanceof Player player) {
			if (OriModConfigs.ONLY_EAT_PLANTS.get()) {
				FoodProperties food = evt.getItem().getFoodProperties(evt.getEntity());
				if (food != null && food.isMeat()) {
					if (!entity.getLevel().isClientSide) {
						GeneralUtils.Server.message((ServerPlayer)player, "orimod.spirit_restrictions.no_meat");
					}
					evt.setCanceled(true);
				}
			}
		}
	}
	
}
