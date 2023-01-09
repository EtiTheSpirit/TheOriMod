package etithespirit.orimod.common.item.tools;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.data.IOriModItemTierProvider;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.common.item.data.UniversalOriModItemTier;
import etithespirit.orimod.common.tags.OriModBlockTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class OriModToolItem {
	
	private OriModToolItem() {}
	
	@SuppressWarnings("unchecked")
	public static RegistryObject<Item>[] autoRegisterEntireTier(DeferredRegister<Item> registry, UniversalOriModItemTier tier) {
		String itemBase = tier.getPath();
		return new RegistryObject[] {
			registry.register(itemBase + "_pickaxe", () -> newPickaxe(tier)),
			registry.register(itemBase + "_shovel", () -> newShovel(tier)),
			registry.register(itemBase + "_axe", () -> newAxe(tier)),
			registry.register(itemBase + "_sword", () -> newSword(tier)),
			registry.register(itemBase + "_hoe", () -> newHoe(tier)),
		};
	}
	
	public static PickaxeItem newPickaxe(UniversalOriModItemTier tier) {
		return new OriModPickaxeItem(tier, 1, -2.8f, tier.getItemProperties().tab(OriModCreativeModeTabs.SPIRIT_TOOLS));
	}
	
	public static ShovelItem newShovel(UniversalOriModItemTier tier) {
		return new OriModShovelItem(tier, 1.5f, -3f, tier.getItemProperties().tab(OriModCreativeModeTabs.SPIRIT_TOOLS));
	}
	
	public static AxeItem newAxe(UniversalOriModItemTier tier) {
		return new OriModAxeItem(tier, 5f, -3f, tier.getItemProperties().tab(OriModCreativeModeTabs.SPIRIT_TOOLS));
	}
	
	public static SwordItem newSword(UniversalOriModItemTier tier) {
		return new OriModSwordItem(tier, 3, -2.4f, tier.getItemProperties().tab(OriModCreativeModeTabs.SPIRIT_COMBAT));
	}
	
	public static HoeItem newHoe(UniversalOriModItemTier tier) {
		return new OriModHoeItem(tier, -3, 0, tier.getItemProperties().tab(OriModCreativeModeTabs.SPIRIT_TOOLS));
	}
	
	/**  Utility method to identify whether or not a tiered item is one created by this mod that is Light-based. */
	private static boolean isLightItem(TieredItem tieredItem) {
		if (tieredItem.getTier() instanceof UniversalOriModItemTier spiritTier) {
			return spiritTier.getLuxenRepairCost().isPresent();
			// TODO: For now, all items that are repairable classify as Light. This is mutually inclusive (light = light-repairable, light-repairable = light) but this may not be in the future. Look into this.
		}
		return false;
	}
	
	private static class OriModPickaxeItem extends PickaxeItem implements IOriModItemTierProvider {
		
		private final UniversalOriModItemTier oriModItemTier;
		
		public OriModPickaxeItem(UniversalOriModItemTier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
			super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
			this.oriModItemTier = pTier;
		}
		
		@Override
		public UniversalOriModItemTier getOriModTier() {
			return oriModItemTier;
		}
		
		@Override
		public float getDestroySpeed(ItemStack pStack, BlockState pState) {
			float baseSpeed = super.getDestroySpeed(pStack, pState);
			if (isLightItem(this) && pState.is(OriModBlockTags.ALIGNED_DECAY)) baseSpeed += 1;
			return baseSpeed;
		}
		
		@Override
		public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.useStackForSelfRepair(pLevel, pPlayer, pUsedHand, super::use);
			}
			return super.use(pLevel, pPlayer, pUsedHand);
		}
		
		@Override
		public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
			if (isLightItem(this)) {
				SpiritItemCustomizations.appendDefaultLightToolRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
			}
		}
		
		@Override
		public int getBarColor(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getLightToolBarColor(pStack);
			}
			return super.getBarColor(pStack);
		}
		
		@Override
		public Component getName(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
			}
			return super.getName(pStack);
		}
		
		@Override
		public void setDamage(ItemStack stack, int damage) {
			SpiritItemCustomizations.setDamageWithTracking(stack, damage);
		}
	}
	
	private static class OriModShovelItem extends ShovelItem implements IOriModItemTierProvider {
		
		private final UniversalOriModItemTier oriModItemTier;
		
		public OriModShovelItem(UniversalOriModItemTier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
			super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
			this.oriModItemTier = pTier;
		}
		
		@Override
		public UniversalOriModItemTier getOriModTier() {
			return oriModItemTier;
		}
		
		@Override
		public float getDestroySpeed(ItemStack pStack, BlockState pState) {
			float baseSpeed = super.getDestroySpeed(pStack, pState);
			if (isLightItem(this) && pState.is(OriModBlockTags.ALIGNED_DECAY)) baseSpeed += 1;
			return baseSpeed;
		}
		
		@Override
		public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.useStackForSelfRepair(pLevel, pPlayer, pUsedHand, super::use);
			}
			return super.use(pLevel, pPlayer, pUsedHand);
		}
		
		@Override
		public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
			if (isLightItem(this)) {
				SpiritItemCustomizations.appendDefaultLightToolRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
			}
		}
		
		@Override
		public int getBarColor(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getLightToolBarColor(pStack);
			}
			return super.getBarColor(pStack);
		}
		
		@Override
		public Component getName(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
			}
			return super.getName(pStack);
		}
		
		@Override
		public void setDamage(ItemStack stack, int damage) {
			SpiritItemCustomizations.setDamageWithTracking(stack, damage);
		}
	}
	
	private static class OriModAxeItem extends AxeItem implements IOriModItemTierProvider {
		
		private final UniversalOriModItemTier oriModItemTier;
		
		public OriModAxeItem(UniversalOriModItemTier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
			super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
			this.oriModItemTier = pTier;
		}
		
		@Override
		public UniversalOriModItemTier getOriModTier() {
			return oriModItemTier;
		}
		
		@Override
		public float getDestroySpeed(ItemStack pStack, BlockState pState) {
			float baseSpeed = super.getDestroySpeed(pStack, pState);
			if (isLightItem(this) && pState.is(OriModBlockTags.ALIGNED_DECAY)) baseSpeed += 1;
			return baseSpeed;
		}
		
		@Override
		public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.useStackForSelfRepair(pLevel, pPlayer, pUsedHand, super::use);
			}
			return super.use(pLevel, pPlayer, pUsedHand);
		}
		
		@Override
		public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
			if (isLightItem(this)) {
				SpiritItemCustomizations.appendDefaultLightToolRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
			}
		}
		
		@Override
		public int getBarColor(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getLightToolBarColor(pStack);
			}
			return super.getBarColor(pStack);
		}
		
		@Override
		public Component getName(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
			}
			return super.getName(pStack);
		}
		
		@Override
		public void setDamage(ItemStack stack, int damage) {
			SpiritItemCustomizations.setDamageWithTracking(stack, damage);
		}
	}
	
	private static class OriModSwordItem extends SwordItem implements IOriModItemTierProvider {
		
		private final UniversalOriModItemTier oriModItemTier;
		
		public OriModSwordItem(UniversalOriModItemTier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
			super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
			this.oriModItemTier = pTier;
		}
		
		@Override
		public UniversalOriModItemTier getOriModTier() {
			return oriModItemTier;
		}
		
		@Override
		public float getDestroySpeed(ItemStack pStack, BlockState pState) {
			float baseSpeed = super.getDestroySpeed(pStack, pState);
			if (isLightItem(this) && pState.is(OriModBlockTags.ALIGNED_DECAY)) baseSpeed += 1;
			return baseSpeed;
		}
		
		@Override
		public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.useStackForSelfRepair(pLevel, pPlayer, pUsedHand, super::use);
			}
			return super.use(pLevel, pPlayer, pUsedHand);
		}
		
		@Override
		public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
			if (isLightItem(this)) {
				SpiritItemCustomizations.appendDefaultLightToolRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
			}
		}
		
		@Override
		public int getBarColor(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getLightToolBarColor(pStack);
			}
			return super.getBarColor(pStack);
		}
		
		@Override
		public Component getName(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
			}
			return super.getName(pStack);
		}
		
		@Override
		public void setDamage(ItemStack stack, int damage) {
			SpiritItemCustomizations.setDamageWithTracking(stack, damage);
		}
	}
	
	private static class OriModHoeItem extends HoeItem implements IOriModItemTierProvider {
		
		private final UniversalOriModItemTier oriModItemTier;
		
		public OriModHoeItem(UniversalOriModItemTier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
			super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
			this.oriModItemTier = pTier;
		}
		
		@Override
		public UniversalOriModItemTier getOriModTier() {
			return oriModItemTier;
		}
		
		@Override
		public float getDestroySpeed(ItemStack pStack, BlockState pState) {
			float baseSpeed = super.getDestroySpeed(pStack, pState);
			if (isLightItem(this) && pState.is(OriModBlockTags.ALIGNED_DECAY)) baseSpeed += 1;
			return baseSpeed;
		}
		
		@Override
		public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.useStackForSelfRepair(pLevel, pPlayer, pUsedHand, super::use);
			}
			return super.use(pLevel, pPlayer, pUsedHand);
		}
		
		@Override
		public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
			if (isLightItem(this)) {
				SpiritItemCustomizations.appendDefaultLightToolRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
			}
		}
		
		@Override
		public int getBarColor(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getLightToolBarColor(pStack);
			}
			return super.getBarColor(pStack);
		}
		
		@Override
		public Component getName(ItemStack pStack) {
			if (isLightItem(this)) {
				return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
			}
			return super.getName(pStack);
		}
		
		@Override
		public void setDamage(ItemStack stack, int damage) {
			SpiritItemCustomizations.setDamageWithTracking(stack, damage);
		}
	}
	
}
