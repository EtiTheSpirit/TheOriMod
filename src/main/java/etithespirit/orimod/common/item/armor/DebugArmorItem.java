package etithespirit.orimod.common.item.armor;

import etithespirit.orimod.common.item.data.SpiritArmorMaterial;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;

public class DebugArmorItem extends ArmorItem {
	
	public DebugArmorItem(EquipmentSlot pSlot) {
		super(SpiritArmorMaterial.DEBUG, pSlot, new Item.Properties());
	}
	
	public static DebugArmorItem helmet() {
		return new DebugArmorItem(EquipmentSlot.HEAD);
	}
	public static DebugArmorItem chestplate() {
		return new DebugArmorItem(EquipmentSlot.CHEST);
	}
	public static DebugArmorItem leggings() {
		return new DebugArmorItem(EquipmentSlot.LEGS);
	}
	public static DebugArmorItem boots() {
		return new DebugArmorItem(EquipmentSlot.FEET);
	}
	
}
