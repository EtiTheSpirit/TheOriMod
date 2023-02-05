package etithespirit.orimod.player;

import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;

/**
 * This class manages the speed, health, and knockback resistance of Spirits.
 */
public final class EffectEnforcement {
	
	private EffectEnforcement() { }
	
	private static final String SPIRIT_EFFECT_SPEED_UUID = "3B1CA2C0-2E94-4DAD-803D-8CFAE551DBD4";
	private static final String SPIRIT_EFFECT_KNOCKBACK_UUID = "3FB8A8D0-3C6A-436A-BFF8-2ADA5887D3BB";
	private static final String SPIRIT_EFFECT_MAX_HEALTH_UUID = "39BA687E-176C-11EB-ADC1-0242AC120002";
	
	// Looking for jump? SpiritJump.java
	
	/** This attribute makes Spirits faster. It is equal to Speed II. */
	public static final AttributeModifier SPEED_MOD = new AttributeModifier(SPIRIT_EFFECT_SPEED_UUID, 0.0425D, AttributeModifier.Operation.ADDITION);
	
	/** This attribute makes Spirits immune to knockback. */
	public static final AttributeModifier KNOCKBACK_MOD = new AttributeModifier(SPIRIT_EFFECT_KNOCKBACK_UUID, 1.0D, AttributeModifier.Operation.ADDITION);
	
	/** This attribute makes Spirits have only half the health of a player. */
	public static final AttributeModifier HEALTH_MOD = new AttributeModifier(SPIRIT_EFFECT_MAX_HEALTH_UUID, -10D, AttributeModifier.Operation.ADDITION);
	
	/**
	 * Removes the spirit speed, knockback, and health modifications.
	 * @param player The player to modify.
	 */
	@SuppressWarnings("ConstantConditions")
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
	@SuppressWarnings("ConstantConditions")
	public static void updatePlayerAttrs(Player player) {
		resetPlayerAttrs(player);
		
		if (SpiritIdentifier.isSpirit(player)) {
			AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
			AttributeInstance knockbackResist = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
			AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
			
			maxHealth.addTransientModifier(HEALTH_MOD);
			knockbackResist.addTransientModifier(KNOCKBACK_MOD);
			speed.addTransientModifier(SPEED_MOD);
		}
	}
	
	/**
	 * Removes darkness if the player is wearing a full luxen gorlek-netherite alloy set.
	 * @param pTick
	 */
	public static void onPlayerTick(TickEvent.PlayerTickEvent pTick) {
		Player plr = pTick.player;
		if (plr != null && plr.hasEffect(MobEffects.DARKNESS)) {
			// This code should be okay because it only executes when the Darkness effect is present.
			ItemStack helm = plr.getItemBySlot(EquipmentSlot.HEAD);
			ItemStack chest = plr.getItemBySlot(EquipmentSlot.CHEST);
			ItemStack legs = plr.getItemBySlot(EquipmentSlot.LEGS);
			ItemStack boots = plr.getItemBySlot(EquipmentSlot.FEET);
			boolean megaHelm = helm.is(ItemRegistry.LUXEN_GORLEK_NETHERITE_ALLOY_HELMET.get());
			boolean megaChest = chest.is(ItemRegistry.LUXEN_GORLEK_NETHERITE_ALLOY_CHESTPLATE.get());
			boolean megaLegs = legs.is(ItemRegistry.LUXEN_GORLEK_NETHERITE_ALLOY_LEGGINGS.get());
			boolean megaBoots = boots.is(ItemRegistry.LUXEN_GORLEK_NETHERITE_ALLOY_BOOTS.get());
			if (megaHelm && megaChest && megaLegs && megaBoots) {
				plr.removeEffect(MobEffects.DARKNESS);
			}
		}
	}
}
