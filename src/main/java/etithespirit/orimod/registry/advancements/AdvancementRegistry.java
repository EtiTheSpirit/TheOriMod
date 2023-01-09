package etithespirit.orimod.registry.advancements;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import etithespirit.orimod.registry.advancements.BecomeSpiritCriterion;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.world.BlockRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public final class AdvancementRegistry {
	
	public static final BecomeSpiritCriterion BECOME_SPIRIT = new BecomeSpiritCriterion("become_spirit");
	public static final InventoryChangeTrigger.TriggerInstance PICKUP_DIAMONDS = has(Items.DIAMOND);
	public static final InventoryChangeTrigger.TriggerInstance PICKUP_NETHERITE_INGOTS = has(Items.NETHERITE_INGOT);
	public static final Supplier<InventoryChangeTrigger.TriggerInstance> GET_PICKUP_ANY_GORLEK_ORE = Suppliers.memoize(() -> has(ItemRegistry.GORLEK_STEEL_INGOT.get()));
	
	protected static ItemPredicate[] any(ItemLike... items) {
		ItemPredicate[] result = new ItemPredicate[items.length];
		for (int index = 0; index < items.length; index++) {
			result[index] = ItemPredicate.Builder.item().of(items[index]).build();
		}
		return result;
	}
	
	protected static InventoryChangeTrigger.TriggerInstance has(ItemLike pItemLike) {
		return inventoryTrigger(any(pItemLike));
	}
	
	protected static InventoryChangeTrigger.TriggerInstance hasAny(ItemLike... items) {
		return inventoryTrigger(any(items));
	}
	
	protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... pPredicates) {
		return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, pPredicates);
	}
	
	public static void registerAll() {
		CriteriaTriggers.register(BECOME_SPIRIT);
	}
}
