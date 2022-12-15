package etithespirit.orimod.common.item.data;

import etithespirit.orimod.registry.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Function;
import java.util.function.Supplier;


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
	SIMPLE_LIGHT(3, 500, 7f, 2.2f, 18, () -> Ingredient.EMPTY),
	
	/** Strong light tools. */
	COMPLEX_LIGHT(5, 1000, 9.5f, 3.5f, 24, () -> Ingredient.EMPTY);
	
	
	private final int level;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;
	
	SpiritTiers(int pLevel, int pUses, float pSpeed, float pDamage, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
		this.level = pLevel;
		this.uses = pUses;
		this.speed = pSpeed;
		this.damage = pDamage;
		this.enchantmentValue = pEnchantmentValue;
		this.repairIngredient = pRepairIngredient;
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