package etithespirit.orimod.registry;

import etithespirit.orimod.registry.criterion.BecomeSpiritCriterion;
import net.minecraft.advancements.CriteriaTriggers;

public final class AdvancementRegistry {
	
	public static final BecomeSpiritCriterion BECOME_SPIRIT = new BecomeSpiritCriterion("become_spirit");
	
	public static void registerAll() {
		CriteriaTriggers.register(BECOME_SPIRIT);
	}
}
