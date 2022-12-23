package etithespirit.orimod.common.item.armor;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.ISpiritLightRepairableItem;
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

public class LightArmorItem extends ArmorItem implements ISpiritLightRepairableItem {
	
	protected LightArmorItem(EquipmentSlot pSlot) {
		super(SpiritArmorMaterial.LIGHT, pSlot, (new Item.Properties()).fireResistant().setNoRepair().tab(OriModCreativeModeTabs.SPIRIT_COMBAT).rarity(Rarity.EPIC));
	}

	public static LightArmorItem newHelmet() {
		return new LightArmorItem(EquipmentSlot.HEAD);
	}
	public static LightArmorItem newChestplate() {
		return new LightArmorItem(EquipmentSlot.CHEST);
	}
	public static LightArmorItem newLegs() {
		return new LightArmorItem(EquipmentSlot.LEGS);
	}
	public static LightArmorItem newBoots() {
		return new LightArmorItem(EquipmentSlot.FEET);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		InteractionResultHolder<ItemStack> result = ISpiritLightRepairableItem.super.useForSelfRepair(pLevel, pPlayer, pUsedHand);
		return (result != null) ? result : super.use(pLevel, pPlayer, pUsedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		ISpiritLightRepairableItem.super.appendDefaultRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}
	
	@Override
	public int getBarColor(ItemStack pStack) {
		return ISpiritLightRepairableItem.super.getBarColor(pStack);
	}
	
	@Override
	public Component getName(ItemStack pStack) {
		return StaticData.getNameAsLight(super.getName(pStack));
	}
}
