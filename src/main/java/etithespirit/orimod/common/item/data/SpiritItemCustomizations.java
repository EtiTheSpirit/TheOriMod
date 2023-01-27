package etithespirit.orimod.common.item.data;

import com.mojang.datafixers.util.Function3;
import etithespirit.orimod.combat.damage.OriModDamageSources;
import etithespirit.orimod.common.chat.ExtendedChatColors;
import etithespirit.orimod.common.tags.OriModItemTags;
import etithespirit.orimod.common.tile.light.implementations.LightRepairBoxTile;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class SpiritItemCustomizations {
	
	/**
	 * A preset method to display a customized durability bar on Hardlight tools.
	 * @param pStack The stack to display the durability of.
	 * @return A color for the health bar of the item.
	 */
	public static int getLightToolBarColor(ItemStack pStack) {
		float stackMaxDamage = pStack.getMaxDamage();
		float healthPercent = Math.max(0.0F, (stackMaxDamage - (float)pStack.getDamageValue()) / stackMaxDamage);
		float fourthHealth = (1 - healthPercent) * 0.4f;
		return Mth.hsvToRgb(0.536f + fourthHealth, 0.6f + fourthHealth, 1f);
		// Result: A nice cyan going down to a sickly red/magenta shade.
	}
	
	public static void appendDefaultLightToolRepairHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		if (!pStack.is(OriModItemTags.LIGHT_REPAIRABLE)) return;
		
		// THIS ITEM IS REPAIRABLE IN LUXEN CTOR TEXT:
		pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.1").withStyle(ChatFormatting.DARK_AQUA));
		pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.2").withStyle(ChatFormatting.DARK_AQUA));
		
		float repairCostMult = 1;
		if (pStack.getItem() instanceof IOriModItemTierProvider spiritTier) {
			repairCostMult = spiritTier.getOriModTier().getLuxenRepairCost().orElse(1f);
		}
		float repairCost = LightRepairBoxTile.CONSUMPTION_RATE * repairCostMult;
		
		String repairKey;
		float displayedRepairAmount = repairCost;
		if (repairCost < 1) {
			// lum
			repairKey = "waila.orimod.energy.lum_amount";
			displayedRepairAmount *= ILightEnergyStorage.LUM_PER_LUX;
		} else {
			// lux
			repairKey = "waila.orimod.energy.luxen_amount";
		}
		
		// REPAIR COST: X UNIT/TICK
		pTooltipComponents.add(
			Component.translatable("tooltip.orimod.focuslight.3").withStyle(ExtendedChatColors.GRAY)
			.append(Component.translatable(repairKey, displayedRepairAmount).withStyle(ExtendedChatColors.LIGHT_PAIR.dark))
			.append(Component.literal("/").withStyle(ExtendedChatColors.GRAY))
			.append(Component.translatable("waila.orimod.energy.word_tick").withStyle(ExtendedChatColors.GRAY))
		);
		// "waila.orimod.energy.luxen_amount"
		// "waila.orimod.energy.lum_amount"
		// "waila.orimod.energy.word_tick"
		
		if (OriModConfigs.SELF_REPAIR_LIMITS.get().allowed) {
			/*
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.self.1a").withStyle(ChatFormatting.BLUE).append(Component.translatable("tooltip.orimod.focuslight.self.1b").withStyle(ChatFormatting.AQUA)));
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.self.2a").withStyle(ChatFormatting.BLUE).append(Component.translatable("tooltip.orimod.focuslight.self.2b", OriModConfigs.SELF_REPAIR_DAMAGE.get()).withStyle(ChatFormatting.AQUA)));
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.self.3a").withStyle(ChatFormatting.BLUE).append(Component.translatable("tooltip.orimod.focuslight.self.3b", OriModConfigs.SELF_REPAIR_EARNINGS.get()).withStyle(ChatFormatting.AQUA)));
			*/
			pTooltipComponents.add(CommonComponents.EMPTY);
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.self.1"));
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.self.2", OriModConfigs.SELF_REPAIR_DAMAGE.get()));
			pTooltipComponents.add(Component.translatable("tooltip.orimod.focuslight.self.3", OriModConfigs.SELF_REPAIR_EARNINGS.get()));
		}
	}
	
	public static void overrideDurabilityTooltip(ItemTooltipEvent tooltipEvent) {
		ItemStack stack = tooltipEvent.getItemStack();
		if (stack.getItem() instanceof IOriModItemTierProvider tierProvider) {
			List<Component> tips = tooltipEvent.getToolTip();
			
			UniversalOriModItemTier tier = tierProvider.getOriModTier();
			if (tier.getLuxenRepairCost().isPresent() && tier.getLuxenRepairLimit().isPresent()) {
				int durabilityIndex = -1;
				for (int index = tips.size() - 1; index >= 0; index--) {
					MutableComponent tip = (MutableComponent) tips.get(index);
					if (tip.getContents() instanceof TranslatableContents contents) {
						if (contents.getKey().equals("item.durability")) {
							tips.remove(index);
							durabilityIndex = index;
							break;
						}
					}
				}
				
				int maxDamage = stack.getMaxDamage();
				int health = maxDamage - stack.getDamageValue();
				int maxHealth = getMaxLuxenReconstructionDurability(stack);
				boolean isDamaged = stack.isDamaged();
				if (durabilityIndex != -1 && isDamaged) {
					tips.add(durabilityIndex + 1, Component.translatable("tooltip.orimod.durability.physical", health, maxDamage));
					tips.remove(durabilityIndex);
					tips.add(durabilityIndex + 2, Component.translatable("tooltip.orimod.durability.repairlimit.1", maxHealth).withStyle(ExtendedChatColors.LIGHT));
					tips.add(durabilityIndex + 3, Component.translatable("tooltip.orimod.durability.repairlimit.2").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
					tips.add(durabilityIndex + 4, Component.translatable("tooltip.orimod.durability.repairlimit.3").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
					tips.add(durabilityIndex + 5, Component.translatable("tooltip.orimod.durability.repairlimit.4").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
				} else if (isDamaged) {
					tips.add(Component.translatable("tooltip.orimod.durability.physical", health, maxDamage));
					tips.add(Component.translatable("tooltip.orimod.durability.repairlimit.1", maxHealth).withStyle(ExtendedChatColors.LIGHT));
					tips.add(Component.translatable("tooltip.orimod.durability.repairlimit.2").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
					tips.add(Component.translatable("tooltip.orimod.durability.repairlimit.3").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
					tips.add(Component.translatable("tooltip.orimod.durability.repairlimit.4").withStyle(ExtendedChatColors.GRAY_PAIR.dark));
				}
			}
		}
	}
	
	public static int getMaxLuxenReconstructionDurability(ItemStack stack) {
		if (stack.getItem() instanceof IOriModItemTierProvider tierProvider) {
			UniversalOriModItemTier tier = tierProvider.getOriModTier();
			
			CompoundTag nbt = stack.getOrCreateTag();
			int value = stack.getMaxDamage();
			int maxDamage = value;
			if (nbt.contains(UniversalOriModItemTier.LAST_KNOWN_MAX_DAMAGE_KEY)) {
				value = nbt.getInt(UniversalOriModItemTier.LAST_KNOWN_MAX_DAMAGE_KEY);
			}
			
			int health = maxDamage - stack.getDamageValue();
			int lastHealthBeforeIngotRepair = maxDamage - value;
			
			float repairBonus = (float)lastHealthBeforeIngotRepair * tier.getLuxenRepairLimit().orElse((float)maxDamage);
			if (repairBonus >= maxDamage) return maxDamage;
			return Mth.clamp(health, (int)Math.ceil(repairBonus), maxDamage);
		}
		throw new IllegalArgumentException("The given ItemStack does not represent an item implementing IOriModItemTierProvider.");
	}
	
	/**
	 * The opposite of {@link #getMaxLuxenReconstructionDurability(ItemStack)} in that this returns damage instead of durability (lower values = better)
	 * @param stack
	 * @return
	 */
	public static int getMinLuxenReconstructionDamage(ItemStack stack) {
		return Math.max(stack.getMaxDamage() - getMaxLuxenReconstructionDurability(stack), 0);
	}
	
	public static @Nonnull InteractionResultHolder<ItemStack> useStackForSelfRepair(Level pLevel, Player pPlayer, InteractionHand pUsedHand, Function3<Level, Player, InteractionHand, InteractionResultHolder<ItemStack>> superUse) {
		InteractionResultHolder<ItemStack> holder = useStackForSelfRepairNullable(pLevel, pPlayer, pUsedHand, superUse);
		return holder != null ? holder : InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
	}
	
	@Nullable
	private static InteractionResultHolder<ItemStack> useStackForSelfRepairNullable(Level pLevel, Player pPlayer, InteractionHand pUsedHand, Function3<Level, Player, InteractionHand, InteractionResultHolder<ItemStack>> superUse) {
		if (pPlayer.isSecondaryUseActive()) {
			ItemStack item = pPlayer.getItemInHand(pUsedHand);
			if (item.getDamageValue() > 0) {
				SelfRepairLimit limit = OriModConfigs.SELF_REPAIR_LIMITS.get();
				if (!limit.allowed) return null;
				
				boolean isCapable = SpiritIdentifier.isSpirit(pPlayer) || OriModConfigs.ANYONE_CAN_SELF_REPAIR.get();
				boolean isSneaking = pPlayer.isCrouching();
				boolean hasHealthNeeded;
				float selfRepairDamage = (float) OriModConfigs.SELF_REPAIR_DAMAGE.get().doubleValue();
				if (limit.canKillSelf) {
					hasHealthNeeded = true;
				} else {
					hasHealthNeeded = pPlayer.getHealth() > selfRepairDamage;
				}
				
				
				if (!isCapable) return null;
				if (!isSneaking) return null;
				if (!hasHealthNeeded) return null;
				item.setDamageValue(item.getDamageValue() - OriModConfigs.SELF_REPAIR_EARNINGS.get());
				pPlayer.hurt(OriModDamageSources.USE_SELF_FOR_ENERGY, selfRepairDamage);
				return InteractionResultHolder.success(item);
			}
		}
		return superUse.apply(pLevel, pPlayer, pUsedHand);
	}
	
	
	public static void setDamageWithTracking(ItemStack stack, int damage) {
		damage = Math.max(damage, 0);
		CompoundTag tag = stack.getOrCreateTag();
		if (stack.is(OriModItemTags.LIGHT_REPAIRABLE)) {
			if (!tag.contains(UniversalOriModItemTier.LAST_KNOWN_MAX_DAMAGE_KEY)) {
				tag.putInt(UniversalOriModItemTier.LAST_KNOWN_MAX_DAMAGE_KEY, stack.getDamageValue());
			}
			int lastKnownBeforeRepair = tag.getInt(UniversalOriModItemTier.LAST_KNOWN_MAX_DAMAGE_KEY);
			if (damage > lastKnownBeforeRepair) {
				tag.putInt(UniversalOriModItemTier.LAST_KNOWN_MAX_DAMAGE_KEY, damage);
			}
		}
		tag.putInt("Damage", damage);
	}
	
	/**
	 * An alias for use in all Decay-related objects that colors the name to match the theme.
	 * @param superGetName The result of the call to <code>super.getName</code>
	 * @return The same result, but colored differently.
	 */
	public static MutableComponent getNameAsDecay(Component superGetName) {
		return ((MutableComponent)superGetName).withStyle(ExtendedChatColors.DECAY);
	}
	
	/**
	 * An alias for use in all Light-related objects that colors the name to match the theme.
	 * @param superGetName The result of the call to <code>super.getName</code>
	 * @return The same result, but colored differently.
	 */
	public static MutableComponent getNameAsLight(Component superGetName) {
		return ((MutableComponent)superGetName).withStyle(ExtendedChatColors.LIGHT);
	}
}
