package etithespirit.orimod.registry.advancements;

import com.google.gson.JsonObject;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

public class BecomeSpiritCriterion extends SimpleCriterionTrigger<BecomeSpiritCriterion.BecomeSpiritCriterionTrigger> {
	
	private final ResourceLocation ID;
	
	public BecomeSpiritCriterion(String id) {
		ID = new ResourceLocation(OriMod.MODID, id);
	}
	
	@Override
	protected BecomeSpiritCriterionTrigger createInstance(JsonObject pJson, EntityPredicate.Composite pPlayer, DeserializationContext pContext) {
		return createInstance();
	}
	
	public BecomeSpiritCriterionTrigger createInstance() {
		return new BecomeSpiritCriterionTrigger(ID, EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().of(EntityType.PLAYER).build()));
	}
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	public void trigger(ServerPlayer player) {
		this.trigger(player, trigger -> trigger.qualifies(player));
	}
	
	public static class BecomeSpiritCriterionTrigger extends AbstractCriterionTriggerInstance {
		
		public BecomeSpiritCriterionTrigger(ResourceLocation pCriterion, EntityPredicate.Composite pPlayer) {
			super(pCriterion, pPlayer);
		}
		
		public boolean qualifies(ServerPlayer player) {
			return SpiritIdentifier.isSpirit(player);
		}
	}
	
}