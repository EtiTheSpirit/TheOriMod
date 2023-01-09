package etithespirit.orimod.common.item.data;

import etithespirit.orimod.registry.gameplay.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;
import java.util.function.Supplier;

@Deprecated(forRemoval = true)
public enum SpiritTiers implements Tier {
	/*
	WOOD(0, 59, 2.0F, 0.0F, 15, () -> {
		return Ingredient.of(ItemTags.PLANKS);
	}),
	STONE(1, 131, 4.0F, 1.0F, 5, () -> {
		return Ingredient.of(ItemTags.STONE_TOOL_MATERIALS);
	}),
	IRON(2, 250, 6.0F, 2.0F, 14, () -> {
		return Ingredient.of(Items.IRON_INGOT);
	}),
	DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
		return Ingredient.of(Items.DIAMOND);
	}),
	GOLD(0, 32, 12.0F, 0.0F, 22, () -> {
		return Ingredient.of(Items.GOLD_INGOT);
	}),
	NETHERITE(4, 2031, 9.0F, 4.0F, 15, () -> {
		return Ingredient.of(Items.NETHERITE_INGOT);
	})
	*/
	
	/** Weak light tools. */
	LIGHT(
		"light",
		3,
		500,
		7.5f,
		2.5f,
		20,
		Optional.of(1f),
		() -> Ingredient.EMPTY,
		() -> (new Item.Properties()).fireResistant()
	),
	
	/** Gorlek Steel tools. */
	GORLEK_STEEL(
		"gorlek_steel",
		4,
		1200,
		8.5f,
		3.5f,
		12,
		Optional.empty(),
		() -> Ingredient.of(ItemRegistry.GORLEK_STEEL_INGOT.get()),
		Item.Properties::new
	),
	
	/** Gorlek-Netherite Alloy tools. */
	GORLEK_NETHERITE_ALLOY(
		"gorlek_netherite_alloy",
		5,
		2750,
		10f,
		5f,
		8,
		Optional.of(3f),
		() -> Ingredient.of(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get()),
		() -> (new Item.Properties()).fireResistant()
	),
	
	/** Luxen-infused Gorlek-Netherite Alloy tools. */
	LUXEN_GORLEK_NETHERITE_ALLOY(
		"luxen_gorlek_netherite_alloy",
		6,
		3500,
		11f,
		6f,
		16,
		Optional.of(8f),
		() -> Ingredient.of(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get()),
		() -> (new Item.Properties()).fireResistant()
	);
	
	private final String path;
	private final int level;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;
	private final Supplier<Item.Properties> baseProperties;
	private final Optional<Float> lightRepairCost;
	
	SpiritTiers(String path, int pLevel, int pUses, float pSpeed, float pDamage, int pEnchantmentValue, Optional<Float> lightRepairCost, Supplier<Ingredient> pRepairIngredient, Supplier<Item.Properties> basePropertySupplier) {
		if (path.contains(":")) throw new IllegalArgumentException("Cannot create a path including a colon -- this path should not have a namespace (it is automatically assigned to orimod).");
		
		this.path = path;
		this.level = pLevel;
		this.uses = pUses;
		this.speed = pSpeed;
		this.damage = pDamage;
		this.enchantmentValue = pEnchantmentValue;
		this.lightRepairCost = lightRepairCost;
		this.repairIngredient = pRepairIngredient;
		this.baseProperties = basePropertySupplier;
	}
	
	/**
	 * For complex hardlight tools, the physical material is factored into the durability.
	 * This means that the last known, most severe amount of damage is saved to the item.
	 * It can only be repaired to that last known point + (the return value of this function)
	 * with the Reconstructor. Anything beyond that requires repairing in an anvil.
	 */
	public int getMaxAdditionalRepairValue() {
		if (this == LUXEN_GORLEK_NETHERITE_ALLOY) {
			return 500;
		}
		return 0;
	}
	
	public Optional<Float> getLightRepairCost() {
		return lightRepairCost;
	}
	
	public Item.Properties getBaseProperties() {
		Item.Properties props = baseProperties.get();
		props = props.stacksTo(1);
		if (repairIngredient.get().isEmpty()) props = props.setNoRepair();
		return props;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public int getUses() {
		return this.uses;
	}
	
	public float getSpeed() {
		return this.speed;
	}
	
	public float getAttackDamageBonus() {
		return this.damage;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}
	
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}
}