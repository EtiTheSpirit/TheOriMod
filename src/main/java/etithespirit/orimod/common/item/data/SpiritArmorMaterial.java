package etithespirit.orimod.common.item.data;

import etithespirit.orimod.registry.gameplay.ItemRegistry;
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
	
	// TODO: Armor equip sounds that are suitable for these types!
	
	/** Somewhere around Iron and Diamond, but more enchantable than Gold. Must be repaired with energy. Decently strong but not amazing. */
	//LIGHT("orimod:light", 30, new int[]{2, 5, 7, 2}, 24, SoundEvents.ARMOR_EQUIP_DIAMOND, 1.8f, 0.0f, () -> Ingredient.EMPTY),
	
	// TEMPORARY: So I can play, I wil buff this back up to the previous strength
	LIGHT("orimod:light", 30, new int[]{3, 6, 8, 3}, 24, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.2f, 0.0f, () -> Ingredient.EMPTY),
	
	/** Roughly the same as Netherite. A little worse. No knockback resistance. */
	GORLEK("orimod:gorlek", 34, new int[]{3, 6, 8, 3}, 12, SoundEvents.ARMOR_EQUIP_IRON, 2.5f, 0.0f, () -> Ingredient.of(ItemRegistry.GORLEK_INGOT.get())),
	
	/** The combined strength of Gorlek + Netherite, which is considerably stronger, but a bit lacking in its enchantment capabilities. */
	GORLEK_NETHERITE_ALLOY("orimod:gorlek_netherite_alloy", 40, new int[]{4, 7, 9, 4}, 6, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5f, 0.2f, () -> Ingredient.of(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get())),
	
	/** what the fuck */
	DEBUG("orimod:debug", 0x7FFFFFFF, new int[]{0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF}, 0x7FFFFFFF, SoundEvents.ARMOR_EQUIP_NETHERITE, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, () -> Ingredient.EMPTY);
	
	// TODO: Go the extra mile for endgame+ players and allow combining Light + Gorlek (Alloy)? This would be very powerful mostly due to the cheaper repairs, I would have to actually balance this.
	// TODO: Play in a lot of mainstream endgame+ modpacks, get a feel for the power and desired playstyle of the modder, try to really tune this before making a formal decision.
	
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
