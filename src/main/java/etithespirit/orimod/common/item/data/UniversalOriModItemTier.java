package etithespirit.orimod.common.item.data;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import etithespirit.exception.ArgumentNullException;
import etithespirit.exception.ArgumentOutOfRangeException;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public enum UniversalOriModItemTier implements ArmorMaterial, Tier {
	
	LIGHT(
		OriMod.rsrc("light"),
		3,
		500,
		20,
		new int[] {4, 7, 8, 3},
		7.5f,
		2.5f,
		1.8f,
		0f,
		() -> SoundRegistry.get("item.armor.orimod.equip_light"),
		Optional.of(1f),
		Optional.of(Float.POSITIVE_INFINITY),
		() -> Ingredient.EMPTY,
		() -> (new Item.Properties()).fireResistant()
	),
	
	GORLEK_STEEL(
		OriMod.rsrc("gorlek_steel"),
		4,
		1200,
		12,
		new int[] {4, 6, 8, 4},
		8.5f,
		3.5f,
		2.5f,
		0f,
		() -> SoundEvents.ARMOR_EQUIP_IRON,
		Optional.empty(),
		Optional.empty(),
		() -> Ingredient.of(ItemRegistry.GORLEK_STEEL_INGOT.get()),
		Item.Properties::new
	),
	
	GORLEK_NETHERITE_ALLOY(
		OriMod.rsrc("gorlek_netherite_alloy"),
		5,
		2750,
		8,
		new int[] {4, 7, 9, 4},
		10f,
		5f,
		3.5f,
		0.5f / 4f,
		() -> SoundEvents.ARMOR_EQUIP_NETHERITE,
		Optional.empty(),
		Optional.empty(),
		() -> Ingredient.of(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get()),
		() -> (new Item.Properties()).fireResistant()
	),
	
	LUXEN_GORLEK_NETHERITE_ALLOY(
		OriMod.rsrc("luxen_gorlek_netherite_alloy"),
		6,
		3500,
		16,
		new int[] {5, 8, 10, 5},
		11f,
		6f,
		5f,
		0.575f / 4,
		() -> SoundEvents.ARMOR_EQUIP_NETHERITE,
		Optional.of(8f),
		Optional.of(1.25f),
		() -> Ingredient.of(ItemRegistry.GORLEK_NETHERITE_ALLOY_INGOT.get()),
		() -> (new Item.Properties()).fireResistant()
	);
	
	/** The key for the value associated with the last minimum health a tool had. This is used to limit how much a Luxen Reconstructor can repair it. */
	public static final String LAST_KNOWN_MAX_DAMAGE_KEY = "LastDamageBeforeIngotRepair";
	
	/**
	 * Create a new universal item tier that can be used for both ArmorItem (material) and TieredItem (tier)
	 * @param id The ID of this tier or material.
	 * @param toolTierLevel For tools, this is the mining level of the tool.
	 * @param baseDurability The base durability of this material. Armor is calculated from this.
	 * @param enchantability The enchantability of this material. Higher values increase the chance to get better enchantments.
	 * @param protection How much Armor stat each slot has (must be four ints long).
	 * @param miningSpeed The mining speed of this item, for tools.
	 * @param additionalAttackDamage The additional attach damage added to the player's 1 damage.
	 * @param toughness The toughness of the armor, which reduces damage proportionally to how much was dealt (it does a better job at reducing high damage than it does low damage).
	 * @param knockbackResist The percentage of knockback resistance that this armor provides per piece. This must not be greater than 0.25f.
	 * @param equipSound The sound that plays when equipping this armor. The supplier itself can be null to play no sound. The result of this supplier is memoized.
	 * @param getLuxenRepairCostMult The cost of repairing this item in a Luxen Reconstructor, multiplied with the Reconstructor's base repair cost stat (1 means to use the normal cost of 256 Lum/tick, 2 means 512 Lum/tick, etc).
	 * @param getLuxenRepairLimit If present, this represents the maximum amount of repairing that can be done by the Reconstructor, as a factor of the lowest remaining durability. For more information on how this works, see {@link }
	 * @param getRepairIngredient The repair ingredient used to repair this item in an anvil. This must never return null.
	 * @param getBaseProperties The base properties of the item. When {@link #getItemProperties()} is called, <code>stacksTo</code> and <code>setNoRepair</code> get set appropriately. This must never return null.
	 */
	UniversalOriModItemTier(@NotNull ResourceLocation id, int toolTierLevel, int baseDurability, int enchantability, @NotNull int[] protection, float miningSpeed, float additionalAttackDamage, float toughness, float knockbackResist, @Nullable Supplier<@NotNull SoundEvent> equipSound, @NotNull Optional<Float> getLuxenRepairCostMult, @NotNull Optional<Float> getLuxenRepairLimit, @NotNull Supplier<@NotNull Ingredient> getRepairIngredient, @NotNull Supplier<Item.Properties> getBaseProperties) {
		// Assertions:
		ArgumentNullException.throwIfNull(id, "id");
		if (id.getNamespace().equals("minecraft")) throw new IllegalArgumentException("Cannot declare new materials for Minecraft! Did you forget your namespace?");
		if (toolTierLevel < 0) throw new ArgumentOutOfRangeException("toolTierLevel", toolTierLevel, "Parameter must be greater than zero.");
		if (baseDurability < 0) throw new ArgumentOutOfRangeException("baseDurability", baseDurability, "Parameter must be greater than zero.");
		if (enchantability < 0) throw new ArgumentOutOfRangeException("enchantability", enchantability, "Parameter must be greater than zero.");
		ArgumentNullException.throwIfNull(protection, "protection");
		if (protection.length != 4) throw new IllegalArgumentException("Parameter 'protection' must be exactly four integers in length.");
		if (miningSpeed < 0 || Float.isNaN(miningSpeed) || Float.isInfinite(miningSpeed)) throw new ArgumentOutOfRangeException("miningSpeed", miningSpeed, "Parameter must be finite and greater than zero.");
		if (Float.isNaN(additionalAttackDamage)) throw new ArgumentOutOfRangeException("additionalAttackDamage", "Attack damage must be a real number.");
		if (toughness < 0 || Float.isNaN(toughness) || Float.isInfinite(toughness)) throw new ArgumentOutOfRangeException("toughness", toughness, "Toughness must be a real number that is greater than zero.");
		if (knockbackResist < 0 || knockbackResist > 0.25 || Float.isNaN(knockbackResist) || Float.isInfinite(knockbackResist)) throw new ArgumentOutOfRangeException("knockbackResist", knockbackResist, "Knockback Resistance must be greater than zero and less than 0.25 (because this applies per-piece)");
		// equipSound is ok
		ArgumentNullException.throwIfNull(getLuxenRepairCostMult, "getLuxenRepairCostMult");
		ArgumentNullException.throwIfNull(getLuxenRepairLimit, "getLuxenRepairLimit");
		if (getLuxenRepairCostMult.isEmpty() && getLuxenRepairLimit.isPresent()) throw new IllegalArgumentException("The Luxen Repair Cost Multiplier value is not present, but the Luxen Repair Limit is! The limit can never apply, because an empty cost multiplier signifies an object is not compatible with the reconstructor.");
		if (getLuxenRepairCostMult.isPresent() && (getLuxenRepairCostMult.get() < 0 || Float.isInfinite(getLuxenRepairCostMult.get()) || Float.isNaN(getLuxenRepairCostMult.get()))) throw new ArgumentOutOfRangeException("getLuxenRepairCostMult", getLuxenRepairCostMult.get(), "Value must be finite and greater than 0.");
		if (getLuxenRepairLimit.isPresent() && (getLuxenRepairLimit.get() <= 1 || Float.isNaN(getLuxenRepairLimit.get()))) throw new ArgumentOutOfRangeException("getLuxenRepairLimit", getLuxenRepairLimit.get(), "Value must be finite and greater than (but not equal to) 1.");
		ArgumentNullException.throwIfNull(getRepairIngredient, "getRepairIngredient");
		ArgumentNullException.throwIfNull(getBaseProperties, "getBaseProperties");
		for (int idx = 0; idx < 4; idx++) {
			int prot = protection[idx];
			if (prot < 0) throw new ArgumentOutOfRangeException("protection[" + idx + "]", prot, "Protection values must be non-negative.");
		}
		
		this.id = id;
		this.name = id.toString();
		this.path = id.getPath();
		
		this.baseDurability = baseDurability;
		this.precalculatedArmorDurability = new int[] {
			// To future Xan, asking "where the hell did these values come from?"
			// You went to the Minecraft wiki and found the durability values for each Netherite armor piece.
			// You then figured out what the ratio from the individual armor piece's durability to netherite's base durability for tools (2031) was.
			Math.round((407f/2031f) * baseDurability),
			Math.round((592f/2031f) * baseDurability),
			Math.round((555f/2031f) * baseDurability),
			Math.round((481f/2031f)* baseDurability)
		};
		this.enchantability = enchantability;
		this.luxenRepairCostMult = getLuxenRepairCostMult;
		this.luxenRepairLimit = getLuxenRepairLimit;
		this.repairIngredient = getRepairIngredient;
		this.baseProperties = getBaseProperties;
		
		this.armorProtectionsPerSlot = protection;
		this.toughness = toughness;
		this.knockbackResistPercent = knockbackResist;
		this.equipSound = Suppliers.memoize(equipSound != null ? equipSound : () -> SoundRegistry.get("nullsound"));
		
		this.toolTierLevel = toolTierLevel;
		this.miningSpeed = miningSpeed;
		this.additionalAttackDamage = additionalAttackDamage;
	}
	
	private final ResourceLocation id;
	private final String name;
	private final String path;
	
	private final int baseDurability;
	private final int[] precalculatedArmorDurability;
	private final int enchantability;
	private final Optional<Float> luxenRepairCostMult;
	private final Optional<Float> luxenRepairLimit;
	private final Supplier<Ingredient> repairIngredient;
	private final Supplier<Item.Properties> baseProperties;
	
	private final int[] armorProtectionsPerSlot;
	private final float toughness;
	private final float knockbackResistPercent;
	private final Supplier<SoundEvent> equipSound;
	
	private final int toolTierLevel;
	private final float miningSpeed;
	private final float additionalAttackDamage;
	
	
	// COMMON:
	
	public ResourceLocation getId() {
		return id;
	}
	
	public String getPath() {
		return path;
	}
	
	public Optional<Float> getLuxenRepairCost() {
		return luxenRepairCostMult;
	}
	
	/**
	 * The repair limiter keeps track of the lowest durability the tool has ever reached. This value is only ever increased
	 * iff the tool is repaired using its repair ingredient in an anvil.<br/>
	 * <br/>
	 * When evaluating what durability the Luxen Reconstructor can get an item to, the cached lowest durability is multiplied by this value
	 * to compute how high the tool's durability can go via a Luxen Reconstructor. This makes it so that the tool user can still
	 * repair it, but the effectiveness of repairs gets worse as the tool gets closer to breaking.<br/>
	 * <br/>
	 * This is intended for use on tools that are a hybrid material of both Light and something physical, to reflect upon the idea
	 * that both the Light and the physical material get destroyed and damaged with use (thus both must be repaired). It's not
	 * perfect, but its primary intention is to make the reconstructor not be an easy bypass for infinite durability.
	 */
	public Optional<Float> getLuxenRepairLimit() {
		return luxenRepairLimit;
	}
	
	public boolean hasRepairLimits() {
		return getLuxenRepairCost().isPresent() && getLuxenRepairLimit().isPresent();
	}
	
	@Override
	public int getEnchantmentValue() {
		return enchantability;
	}
	
	//FIXME: How does reobf work? There is a crash caused by a missing method because reobf only turns getEnchantmentValue into one of the two searge methods.
	public int m_6601_() { return getEnchantmentValue(); }
	
	@Override
	public Ingredient getRepairIngredient() {
		return repairIngredient.get();
	}
	
	//FIXME: How does reobf work? There is a crash caused by a missing method because reobf only turns getEnchantmentValue into one of the two searge methods.
	public Ingredient m_6282_() { return getRepairIngredient(); }
	
	public Item.Properties getItemProperties() {
		Item.Properties props = baseProperties.get();
		props = props.stacksTo(1);
		if (getRepairIngredient().isEmpty()) props = props.setNoRepair();
		return props;
	}
	
	// ARMOR:
	
	/** Only valid for armor. */
	@Override
	public int getDurabilityForSlot(EquipmentSlot pSlot) {
		return precalculatedArmorDurability[pSlot.getIndex()];
	}
	
	/** Only valid for armor. */
	@Override
	public int getDefenseForSlot(EquipmentSlot pSlot) {
		return armorProtectionsPerSlot[pSlot.getIndex()];
	}
	
	/** Only valid for armor. */
	@Override
	public String getName() {
		return name;
	}
	
	/** Only valid for armor. */
	@Override
	public float getToughness() {
		return toughness;
	}
	
	/** Only valid for armor. */
	@Override
	public float getKnockbackResistance() {
		return knockbackResistPercent;
	}
	
	/** Only valid for armor. */
	@Override
	public SoundEvent getEquipSound() {
		return equipSound.get();
	}
	
	// ITEMS:
	/** Only valid for items. */
	@Override
	public int getUses() {
		return baseDurability;
	}
	
	/** Only valid for items. */
	@Override
	public float getSpeed() {
		return miningSpeed;
	}
	
	/** Only valid for items. */
	@Override
	public float getAttackDamageBonus() {
		return additionalAttackDamage;
	}
	
	/** Only valid for items. */
	@Override
	public int getLevel() {
		return toolTierLevel;
	}
}
