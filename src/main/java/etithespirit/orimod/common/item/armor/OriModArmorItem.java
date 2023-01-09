package etithespirit.orimod.common.item.armor;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.data.IOriModItemTierProvider;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.common.item.data.UniversalOriModItemTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OriModArmorItem extends ArmorItem implements IOriModItemTierProvider {
	
	private final UniversalOriModItemTier material;
	private final boolean isLight;
	
	protected OriModArmorItem(UniversalOriModItemTier mtl, EquipmentSlot slot) {
		super(mtl, slot, mtl.getItemProperties().tab(OriModCreativeModeTabs.SPIRIT_COMBAT));
		material = mtl;
		isLight = material.getLuxenRepairCost().isPresent();
	}
	
	// TODO: Limit repairs on luxen alloy armor via the reconstructor. Track the damage taken and require it to be repaired using alloy ingots too.
	
	@SuppressWarnings("unchecked")
	public static RegistryObject<Item>[] autoRegisterAllSlotsOfType(DeferredRegister<Item> itemRegistry, UniversalOriModItemTier type) {
		String itemBase = type.getPath();
		return new RegistryObject[] {
			itemRegistry.register(itemBase + "_helmet", () -> new OriModArmorItem(type, EquipmentSlot.HEAD)),
			itemRegistry.register(itemBase + "_chestplate", () -> new OriModArmorItem(type, EquipmentSlot.CHEST)),
			itemRegistry.register(itemBase + "_leggings", () -> new OriModArmorItem(type, EquipmentSlot.LEGS)),
			itemRegistry.register(itemBase + "_boots", () -> new OriModArmorItem(type, EquipmentSlot.FEET))
		};
	}
	
	@Override
	public UniversalOriModItemTier getOriModTier() {
		return material;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		if (isLight) {
			return SpiritItemCustomizations.useStackForSelfRepair(pLevel, pPlayer, pUsedHand, super::use);
		}
		return super.use(pLevel, pPlayer, pUsedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		if (isLight) {
			SpiritItemCustomizations.appendDefaultLightToolRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		}
	}
	
	@Override
	public int getBarColor(ItemStack pStack) {
		if (isLight) {
			return SpiritItemCustomizations.getLightToolBarColor(pStack);
		}
		return super.getBarColor(pStack);
	}
	
	@Override
	public Component getName(ItemStack pStack) {
		if (isLight) {
			return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
		}
		return super.getName(pStack);
	}
	
	@Override
	public void setDamage(ItemStack stack, int damage) {
		SpiritItemCustomizations.setDamageWithTracking(stack, damage);
	}
}
