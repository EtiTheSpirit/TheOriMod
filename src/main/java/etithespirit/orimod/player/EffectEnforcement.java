package etithespirit.orimod.player;

import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class EffectEnforcement {
	
	private EffectEnforcement() { }
	
	private static final String SPIRIT_EFFECT_SPEED_UUID = "3B1CA2C0-2E94-4DAD-803D-8CFAE551DBD4";
	private static final String SPIRIT_EFFECT_KNOCKBACK_UUID = "3FB8A8D0-3C6A-436A-BFF8-2ADA5887D3BB";
	private static final String SPIRIT_EFFECT_MAX_HEALTH_UUID = "39BA687E-176C-11EB-ADC1-0242AC120002";
	
	public static final AttributeModifier SPEED_MOD = new AttributeModifier(SPIRIT_EFFECT_SPEED_UUID, 0.0425D, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier KNOCKBACK_MOD = new AttributeModifier(SPIRIT_EFFECT_KNOCKBACK_UUID, 1.0D, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier HEALTH_MOD = new AttributeModifier(SPIRIT_EFFECT_MAX_HEALTH_UUID, -10D, AttributeModifier.Operation.ADDITION);
	
	/**
	 * Removes the spirit speed, knockback, and health modifications.
	 * @param player The player to modify.
	 */
	public static void resetPlayerAttrs(Player player) {
		AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		AttributeInstance knockbackResist = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
		AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		
		maxHealth.removeModifier(HEALTH_MOD);
		knockbackResist.removeModifier(KNOCKBACK_MOD);
		speed.removeModifier(SPEED_MOD);
	}
	
	/**
	 * Removes pre-existing spirit speed, knockback, and health modifications, then adds a set of new ones.
	 * @param player The player to modify.
	 */
	public static void updatePlayerAttrs(Player player) {
		AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		AttributeInstance knockbackResist = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
		AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
		
		resetPlayerAttrs(player);
		if (SpiritIdentifier.isSpirit(player)) {
			maxHealth.addTransientModifier(HEALTH_MOD);
			knockbackResist.addTransientModifier(KNOCKBACK_MOD);
			speed.addTransientModifier(SPEED_MOD);
		}
	}
	
}