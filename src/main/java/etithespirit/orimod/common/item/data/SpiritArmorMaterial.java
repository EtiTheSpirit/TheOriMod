package etithespirit.orimod.common.item.data;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;
import java.util.function.Supplier;

@Deprecated(forRemoval = true)
public enum SpiritArmorMaterial implements ArmorMaterial {
	
	// TODO: Armor equip sounds that are suitable for these types!
	
	/** Better than Diamond, but marginally. Main benefit is the free repairs with energy. Decently strong but not amazing. */
	LIGHT(
		"light",
		34,
		new int[] {4, 7, 8, 3},
		24,
		SoundEvents.ARMOR_EQUIP_DIAMOND,
		2f,
		0.0f,
		Optional.of(1f),
		() -> Ingredient.EMPTY,
		() -> (new Item.Properties()).fireResistant()
	),
	
	/** Roughly the same as Netherite. A little worse. No knockback resistance. */
	GORLEK_STEEL(
		"gorlek_steel",
		28,
		new int[] {4, 6, 8, 4},
		12,
		SoundEvents.ARMOR_EQUIP_IRON,
		2.2f,
		0.0f,
		Optional.empty(),
		() -> Ingredient.of(ItemRegistry.GORLEK_STEEL_INGOT.get()),
		Item.Properties::new
	),
	
	/** The combined strength of Gorlek Steel + Netherite, which is considerably stronger, but a bit lacking in its enchantment capabilities. */
	GORLEK_NETHERITE_ALLOY(
		"gorlek_netherite_alloy",
		40,
		new int[] {4, 7, 9, 4},
		6,
		SoundEvents.ARMOR_EQUIP_NETHERITE,
		3.5f,
		0.2f,
		Optional.empty(),
		() -> Ingredient.of(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get()),
		() -> (new Item.Properties()).fireResistant().rarity(Rarity.EPIC)
	),
	
	/** Gorlek-Netherite Alloy, but infused with Light. Requires a special device to make. */
	LUXEN_GORLEK_NETHERITE_ALLOY(
		"luxen_gorlek_netherite_alloy",
		52,
		new int[] {5, 8, 10, 5},
		16,
		SoundEvents.ARMOR_EQUIP_NETHERITE,
		5.0f,
		0.35f,
		Optional.of(8f),
		() -> Ingredient.EMPTY,
		() -> (new Item.Properties()).fireResistant().rarity(Rarity.EPIC)
	),
	
	/** what the fuck */
	DEBUG(
		"debug",
		0x7FFFFFFF,
		new int[] {0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF, 0x7FFFFFFF},
		0x7FFFFFFF,
		SoundEvents.ARMOR_EQUIP_NETHERITE,
		Float.POSITIVE_INFINITY,
		1f,
		Optional.of(0f),
		() -> Ingredient.EMPTY,
		() -> (new Item.Properties()).fireResistant().rarity(Rarity.EPIC)
	);
	
	// TODO: Play in a lot of mainstream endgame+ modpacks, get a feel for the power and desired playstyle of the modder, try to really tune this before making a formal decision on the stats.
	
	private static final int[] HEALTH_PER_SLOT = new int[] {13, 15, 16, 11};
	private final String name;
	private final String path;
	private final int durabilityMultiplier;
	private final int[] slotProtections;
	private final int enchantmentValue;
	private final SoundEvent sound;
	private final float toughness;
	private final float knockbackResistance;
	private final Supplier<Ingredient> repairIngredient;
	private final Supplier<Item.Properties> baseProperties;
	private final Optional<Float> lightRepairCost;
	
	SpiritArmorMaterial(String path, int pDurabilityMultiplier, int[] pSlotProtections, int pEnchantmentValue, SoundEvent pSound, float pToughness, float pKnockbackResistance, Optional<Float> lightRepairCost, Supplier<Ingredient> pRepairIngredient, Supplier<Item.Properties> baseProperties) {
		if (path.contains(":")) throw new IllegalArgumentException("Cannot create a path including a colon -- this path should not have a namespace (it is automatically assigned to orimod).");
		
		this.name = "orimod:" + path;
		this.path = path;
		this.durabilityMultiplier = pDurabilityMultiplier;
		this.slotProtections = pSlotProtections;
		this.enchantmentValue = pEnchantmentValue;
		this.sound = pSound;
		this.toughness = pToughness;
		this.knockbackResistance = pKnockbackResistance;
		this.lightRepairCost = lightRepairCost;
		this.repairIngredient = pRepairIngredient;
		this.baseProperties = baseProperties;
	}
	
	
	public Optional<Float> getLightRepairCost() {
		return lightRepairCost;
	}
	
	public Item.Properties getBaseProperties() {
		Item.Properties props = baseProperties.get();
		props = props.stacksTo(1).tab(OriModCreativeModeTabs.SPIRIT_COMBAT);
		if (repairIngredient.get().isEmpty()) props = props.setNoRepair();
		return props;
	}
	
	public int getDurabilityForSlot(EquipmentSlot pSlot) {
		
		return HEALTH_PER_SLOT[pSlot.getIndex()] * this.durabilityMultiplier;
	}
	
	public int getDefenseForSlot(EquipmentSlot pSlot) {
		// TODO: Light armor degrades over time.
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
	
	public String getPath() {
		return this.path;
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
