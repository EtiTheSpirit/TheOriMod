package etithespirit.etimod.common.player;

import java.util.ArrayList;
import etithespirit.etimod.common.potion.SpiritEffect;
import etithespirit.etimod.info.spirit.SpiritData;
import etithespirit.etimod.registry.PotionRegistry;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class EffectEnforcement {
	
	private static final String SPIRIT_EFFECT_SPEED_UUID = "3B1CA2C0-2E94-4DAD-803D-8CFAE551DBD4";
	private static final String SPIRIT_EFFECT_KNOCKBACK_UUID = "3FB8A8D0-3C6A-436A-BFF8-2ADA5887D3BB";
	private static final String SPIRIT_EFFECT_MAX_HEALTH_UUID = "39BA687E-176C-11EB-ADC1-0242AC120002";
	
	public static final AttributeModifier KNOCKBACK_MOD = new AttributeModifier(SPIRIT_EFFECT_KNOCKBACK_UUID, 1.0D, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier SPEED_MOD = new AttributeModifier(SPIRIT_EFFECT_SPEED_UUID, 0.0425D, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier HEALTH_MOD = new AttributeModifier(SPIRIT_EFFECT_MAX_HEALTH_UUID, -10D, AttributeModifier.Operation.ADDITION);
	
	/*
	public static void enforceEffects(PlayerTickEvent event) {
		PlayerEntity player = event.player;
		updatePlayerAttrs(player);
	}
	*/
	
	public static void updatePlayerAttrs(PlayerEntity player) {
		ModifiableAttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		ModifiableAttributeInstance knockbackResist = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
		ModifiableAttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		
		if (SpiritData.isSpirit(player)) {
			if (!maxHealth.hasModifier(HEALTH_MOD)) {
				maxHealth.addTransientModifier(HEALTH_MOD);
			}
			if (!knockbackResist.hasModifier(KNOCKBACK_MOD)) {
				knockbackResist.addTransientModifier(KNOCKBACK_MOD);
			}
			if (!speed.hasModifier(SPEED_MOD)) {
				speed.addTransientModifier(SPEED_MOD);
			}
		} else {
			if (maxHealth.hasModifier(HEALTH_MOD)) {
				maxHealth.removeModifier(HEALTH_MOD);
			}
			if (knockbackResist.hasModifier(KNOCKBACK_MOD)) {
				knockbackResist.removeModifier(KNOCKBACK_MOD);
			}
			if (speed.hasModifier(SPEED_MOD)) {
				speed.removeModifier(SPEED_MOD);
			}
		}
	}
	
}
