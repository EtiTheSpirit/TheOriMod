package etithespirit.mixin.mixins;


import etithespirit.mixin.helpers.ISelfProvider;
import etithespirit.orimod.common.item.data.IOriModItemTierProvider;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.common.item.data.UniversalOriModItemTier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

/**
 * Injects just before setting the result stack of the anvil.
 * This mixin is being used despite the existence of the (closely) appropriate Forge event because the exact behavior I want to achieve
 * is a simple NBT tag change on my own mod's items - using Forge events would require completely reimplementing
 * vanilla behavior verbatim, which defeats the entire purpose of Forge's events. It is much better to quiertly intercept and
 * subsequently alter the result stack if it matches a condition only true on my mod's items.
 */
@Mixin(AnvilMenu.class)
public abstract class InjectAnvilMenuCreateResult extends ItemCombinerMenu implements ISelfProvider {
	public InjectAnvilMenuCreateResult(@Nullable MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
		super(pType, pContainerId, pPlayerInventory, pAccess);
	}
	
	@Inject(
		method = "createResult()V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/inventory/ResultContainer;setItem(ILnet/minecraft/world/item/ItemStack;)V",
			shift = At.Shift.AFTER
		)
	)
	private void orimod$tweakOutputStack(CallbackInfo ci) {
		ItemStack stack = resultSlots.getItem(0);
		if (stack.getItem() instanceof IOriModItemTierProvider tierProvider && tierProvider.getOriModTier().hasRepairLimits()) {
			ItemStack theoreticalResult = stack.copy();
			int currentDamage = stack.getDamageValue();
			
			CompoundTag tag = theoreticalResult.getOrCreateTag();
			tag.putInt(UniversalOriModItemTier.LAST_KNOWN_MAX_DAMAGE_KEY, currentDamage);
			
			// Let them cash in on any missed repairs, plus some of the intrinsic reward from using physical repairs.
			int oldLuxenDamageLimit = SpiritItemCustomizations.getMinLuxenReconstructionDamage(theoreticalResult);
			int diff = Math.max(currentDamage - oldLuxenDamageLimit, 0);
			
			// Update tag to be that of the result item.
			tag = stack.getOrCreateTag();
			
			currentDamage -= diff; // Here is said "cashing in"
			if (currentDamage < 0) currentDamage = 0;
			tag.putInt(UniversalOriModItemTier.LAST_KNOWN_MAX_DAMAGE_KEY, currentDamage);
		}
	}
}
