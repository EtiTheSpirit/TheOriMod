package etithespirit.mixin.mixins;

import etithespirit.mixin.helpers.ISelfProvider;
import etithespirit.orimod.client.audio.SpiritSoundProvider;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class ModifyArgHandleEntityEvent extends Entity implements ISelfProvider {
	public ModifyArgHandleEntityEvent(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}
	
	private static boolean isUsingHardlightShield(LivingEntity self) {
		ItemStack mainHand = self.getMainHandItem();
		ItemStack offHand = self.getOffhandItem();
		Item lightShield = ItemRegistry.LIGHT_SHIELD.get();
		boolean isHolding = mainHand.is(lightShield); // Holding if its in main hand
		if (!isHolding) {
			isHolding = offHand.is(lightShield) && !mainHand.is(Items.SHIELD); // ... or offhand when the main shield is not being held.
			// Dual Weilding shields prefers the mainhand
		}
		return isHolding;
	}
	
	@ModifyArg(
		method = "handleEntityEvent(B)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"
		)
	)
	public SoundEvent onShieldSoundPlayed(SoundEvent sound) {
		if (isUsingHardlightShield(selfProvider$any())) {
			if (sound.equals(SoundEvents.SHIELD_BLOCK)) {
				return SpiritSoundProvider.getSpiritShieldImpactSound(false);
			} else if (sound.equals(SoundEvents.SHIELD_BREAK)) {
				return SpiritSoundProvider.getSpiritShieldImpactSound(true);
			}
		}
		return sound;
	}
}
