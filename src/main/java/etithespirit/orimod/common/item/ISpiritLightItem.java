package etithespirit.orimod.common.item;


import etithespirit.orimod.combat.ExtendedDamageSource;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Denotes this item as one of a Spirit's Light, which implies it was made through themselves or some technology they were using.
 * This overrides getBarColor and causes a unique color to show.
 */
public interface ISpiritLightItem {
	/**
	 * Override and call <c>super.ISpiritLightItem.getBarColor</c> to use this custom logic.
	 * @param pStack
	 * @return
	 */
	default int getBarColor(ItemStack pStack) {
		float stackMaxDamage = pStack.getMaxDamage();
		float healthPercent = Math.max(0.0F, (stackMaxDamage - (float)pStack.getDamageValue()) / stackMaxDamage);
		float fourthHealth = (1 - healthPercent) * 0.4f;
		return Mth.hsvToRgb(0.536f + fourthHealth, 0.6f + fourthHealth, 1f);
		// Result: A nice cyan going down to a sickly red/magenta shade.
	}
	
	default void appendDefaultRepairHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		if (!pStack.is(OriModItemTags.LIGHT_REPAIRABLE)) throw new IllegalStateException("This item wants to tell the user how to repair it, but it is not in data/orimod/tags/items/light_repairable.json!");
		pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.1"));
		pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.2"));
		if (OriModConfigs.SELF_REPAIR_LIMITS.get().allowed) {
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.3"));
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.4", OriModConfigs.SELF_REPAIR_DAMAGE.get() / 2));
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.5", OriModConfigs.SELF_REPAIR_EARNINGS.get()));
		}
	}
	
	
	/**
	 * Call within {@link net.minecraft.world.item.Item#use(Level, Player, InteractionHand)} to perform self-repair. This automatically does
	 * all of the necessary checks.<br/>
	 * <strong>NOTE: THIS WILL RETURN NULL IF THE RESULT IS NOT VALID. THIS MUST BE FILLED IN BY THE IMPLEMENTOR.</strong>
	 * @param pLevel
	 * @param pPlayer
	 * @param pUsedHand
	 * @return
	 */
	default @Nullable InteractionResultHolder<ItemStack> useForSelfRepair(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		ItemStack item = pPlayer.getItemInHand(pUsedHand);
		if (item.getDamageValue() > 0) {
			SelfRepairLimit limit = OriModConfigs.SELF_REPAIR_LIMITS.get();
			if (!limit.allowed) return null;
			
			boolean isCapable = SpiritIdentifier.isSpirit(pPlayer) || OriModConfigs.ANYONE_CAN_SELF_REPAIR.get();
			boolean isSneaking = pPlayer.isCrouching();
			boolean hasHealthNeeded;
			float selfRepairDamage = (float)OriModConfigs.SELF_REPAIR_DAMAGE.get().doubleValue();
			if (limit.canKillSelf) {
				hasHealthNeeded = true;
			} else {
				hasHealthNeeded = pPlayer.getHealth() > selfRepairDamage;
			}
			
			
			if (!isCapable) return null;
			if (!isSneaking) return null;
			if (!hasHealthNeeded) return null;
			item.setDamageValue(item.getDamageValue() - OriModConfigs.SELF_REPAIR_EARNINGS.get());
			pPlayer.hurt(ExtendedDamageSource.USE_SELF_FOR_ENERGY, selfRepairDamage);
			return InteractionResultHolder.success(item);
		}
		return null;
	}
	
	enum SelfRepairLimit {
		NOT_ALLOWED(false, false),
		ALLOW_BUT_PREVENT_SUICIDE(true, false),
		ALLOW_WITHOUT_SAFEGUARDS(true, true);
		
		public final boolean canKillSelf;
		public final boolean allowed;
		
		SelfRepairLimit(boolean allow, boolean kill) {
			canKillSelf = kill;
			allowed = allow;
		}
	}
}
