package etithespirit.etimod.registry;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

/**
 * Registers attributes to my entities.
 *
 * @author Eti
 */
public final class EntityAttributeMarshaller {
	
	private static void onAttributesRegistered(final EntityAttributeCreationEvent attrCreation) {
		Map<Attribute, ModifiableAttributeInstance> attrMap = new HashMap<>();
		attrMap.put(Attributes.MAX_HEALTH, new ModifiableAttributeInstance(Attributes.MAX_HEALTH, inst -> inst.setBaseValue(10.0)));
		attrMap.put(Attributes.ATTACK_DAMAGE, new ModifiableAttributeInstance(Attributes.ATTACK_DAMAGE, inst -> inst.setBaseValue(2)));
		attrMap.put(Attributes.MOVEMENT_SPEED, new ModifiableAttributeInstance(Attributes.MOVEMENT_SPEED, inst -> inst.setBaseValue(0.185)));
		attrMap.put(Attributes.FOLLOW_RANGE, new ModifiableAttributeInstance(Attributes.FOLLOW_RANGE, inst -> inst.setBaseValue(0)));
		attrMap.put(Attributes.ARMOR, new ModifiableAttributeInstance(Attributes.ARMOR, inst -> inst.setBaseValue(0)));
		attrMap.put(Attributes.ARMOR_TOUGHNESS, new ModifiableAttributeInstance(Attributes.ARMOR_TOUGHNESS, inst -> inst.setBaseValue(0)));
		attrMap.put(Attributes.KNOCKBACK_RESISTANCE, new ModifiableAttributeInstance(Attributes.KNOCKBACK_RESISTANCE, inst -> inst.setBaseValue(0.8)));
		
		attrCreation.put(EntityRegistry.SPIRIT.get(), new AttributeModifierMap(attrMap));
	}
	
	public static void registerAll() {
		MinecraftForge.EVENT_BUS.addListener(EntityAttributeMarshaller::onAttributesRegistered);
	}
	
}
