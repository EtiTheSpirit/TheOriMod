package etithespirit.orimod.spirit;

import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.config.OriModConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.Event;

public final class SpiritRestrictions {
	
	public static void onEat(LivingEntityUseItemEvent.Start evt) {
		if (!evt.getEntity().isAddedToWorld()) return;
		if (evt.getEntity().getLevel().isClientSide) return;
		if (evt.getEntity() instanceof ServerPlayer player) {
			if (OriModConfigs.ONLY_EAT_PLANTS.get()) {
				FoodProperties food = evt.getItem().getItem().getFoodProperties();
				if (food != null && food.isMeat()) {
					GeneralUtils.message(player, "orimod.spirit_restrictions.no_meat");
					evt.setCanceled(true);
				}
			}
		}
	}
	
}
