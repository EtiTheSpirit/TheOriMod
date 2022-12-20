package etithespirit.orimod.common.item.armor;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.ISpiritLightItem;
import etithespirit.orimod.common.item.data.SpiritArmorMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightArmorItem extends ArmorItem implements ISpiritLightItem {
	protected LightArmorItem(EquipmentSlot pSlot, boolean isHeavy) {
		super(isHeavy ? SpiritArmorMaterial.HEAVY_LIGHT : SpiritArmorMaterial.SIMPLE_LIGHT, pSlot, (new Item.Properties()).fireResistant().setNoRepair().tab(OriModCreativeModeTabs.SPIRIT_COMBAT).rarity(Rarity.EPIC));
	}
	
	public static LightArmorItem newSimpleHelmet() { return newHelmet(false); }
	public static LightArmorItem newSimpleChestplate() { return newChestplate(false); }
	public static LightArmorItem newSimpleLegs() { return newLegs(false); }
	public static LightArmorItem newSimpleBoots() { return newBoots(false); }
	
	public static LightArmorItem newHeavyHelmet() { return newHelmet(true); }
	public static LightArmorItem newHeavyChestplate() { return newChestplate(true); }
	public static LightArmorItem newHeavyLegs() { return newLegs(true); }
	public static LightArmorItem newHeavyBoots() { return newBoots(true); }
	
	private static LightArmorItem newHelmet(boolean heavy) {
		return new LightArmorItem(EquipmentSlot.HEAD, heavy);
	}
	private static LightArmorItem newChestplate(boolean heavy) {
		return new LightArmorItem(EquipmentSlot.CHEST, heavy);
	}
	private static LightArmorItem newLegs(boolean heavy) {
		return new LightArmorItem(EquipmentSlot.LEGS, heavy);
	}
	private static LightArmorItem newBoots(boolean heavy) {
		return new LightArmorItem(EquipmentSlot.FEET, heavy);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		InteractionResultHolder<ItemStack> result = ISpiritLightItem.super.useForSelfRepair(pLevel, pPlayer, pUsedHand);
		return (result != null) ? result : super.use(pLevel, pPlayer, pUsedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		ISpiritLightItem.super.appendDefaultRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}
	
	@Override
	public int getBarColor(ItemStack pStack) {
		return ISpiritLightItem.super.getBarColor(pStack);
	}
}
