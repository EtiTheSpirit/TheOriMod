package etithespirit.orimod.common.item.data;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum SpiritArmorMaterial implements ArmorMaterial {
	
	LIGHT("orimod:light", 33, new int[]{3, 6, 8, 3}, 22, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.6f, 0.0f, () -> null);
	
	private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] slotProtections;
	private final int enchantmentValue;
	private final SoundEvent sound;
	private final float toughness;
	private final float knockbackResistance;
	private final Supplier<Ingredient> repairIngredient;
	
	SpiritArmorMaterial(String pName, int pDurabilityMultiplier, int[] pSlotProtections, int pEnchantmentValue, SoundEvent pSound, float pToughness, float pKnockbackResistance, Supplier<Ingredient> pRepairIngredient) {
		this.name = pName;
		this.durabilityMultiplier = pDurabilityMultiplier;
		this.slotProtections = pSlotProtections;
		this.enchantmentValue = pEnchantmentValue;
		this.sound = pSound;
		this.toughness = pToughness;
		this.knockbackResistance = pKnockbackResistance;
		this.repairIngredient = pRepairIngredient;
	}
	
	public int getDurabilityForSlot(EquipmentSlot pSlot) {
		return HEALTH_PER_SLOT[pSlot.getIndex()] * this.durabilityMultiplier;
	}
	
	public int getDefenseForSlot(EquipmentSlot pSlot) {
		return this.slotProtections[pSlot.getIndex()];
	}
	
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}
	
	public SoundEvent getEquipSound() {
		return this.sound;
	}
	
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}
	
	public String getName() {
		return this.name;
	}
	
	public float getToughness() {
		return this.toughness;
	}
	
	/**
	 * Gets the percentage of knockback resistance provided by armor of the material.
	 */
	public float getKnockbackResistance() {
		return this.knockbackResistance;
	}
}
