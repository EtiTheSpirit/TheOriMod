package etithespirit.orimod.registry.advancements;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import etithespirit.orimod.registry.advancements.BecomeSpiritCriterion;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.world.BlockRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.Items;

public final class AdvancementRegistry {
	
	public static final BecomeSpiritCriterion BECOME_SPIRIT = new BecomeSpiritCriterion("become_spirit");
	public static final InventoryChangeTrigger.TriggerInstance PICKUP_DIAMONDS = InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Items.DIAMOND).build());
	public static final InventoryChangeTrigger.TriggerInstance PICKUP_NETHERITE_INGOTS = InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Items.NETHERITE_INGOT).build());
	public static final Supplier<InventoryChangeTrigger.TriggerInstance> PICKUP_GORLEK_ORE = Suppliers.memoize(() -> InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemRegistry.RAW_GORLEK_ORE.get(), BlockRegistry.GORLEK_ORE.get()).build()));
	
	public static void registerAll() {
		CriteriaTriggers.register(BECOME_SPIRIT);
	}
}
