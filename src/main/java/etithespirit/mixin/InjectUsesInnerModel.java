package etithespirit.mixin;

import etithespirit.etimod.info.spirit.SpiritData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A special system that injects into {@link BipedArmorLayer}'s {@code usesInnerModel} method for spirits (which never use
 * the inner model.)
 *
 * @author Eti
 */
@SuppressWarnings({"rawtypes", "unused", "unchecked"})
@Mixin(BipedArmorLayer.class)
public abstract class InjectUsesInnerModel extends LayerRenderer {
	public InjectUsesInnerModel(IEntityRenderer p_i50926_1_) {
		super(p_i50926_1_);
	}
	
	@Inject(
		method = "usesInnerModel(Lnet/minecraft/inventory/EquipmentSlotType;)Z",
		at = @At("RETURN") // No particular return ordinal is required here
		// And to be honest, it's best for the interests of this mixin to
		// override all return statements.
	)
	public void usesInnerModel$spirit(EquipmentSlotType slotType, CallbackInfoReturnable<Boolean> ci) {
		if (SpiritData.isSpirit(Minecraft.getInstance().player)) {
			ci.setReturnValue(false);
		}
	}
}
