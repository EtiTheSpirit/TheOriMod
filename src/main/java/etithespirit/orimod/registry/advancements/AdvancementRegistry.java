package etithespirit.orimod.registry.advancements;

import etithespirit.orimod.registry.advancements.BecomeSpiritCriterion;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.Items;

public final class AdvancementRegistry {
	
	public static final BecomeSpiritCriterion BECOME_SPIRIT = new BecomeSpiritCriterion("become_spirit");
	public static final InventoryChangeTrigger.TriggerInstance PICKUP_DIAMONDS = InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Items.DIAMOND).build());
	public static void registerAll() {
		CriteriaTriggers.register(BECOME_SPIRIT);
	}
}
