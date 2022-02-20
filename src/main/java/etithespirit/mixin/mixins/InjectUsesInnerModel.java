package etithespirit.mixin.mixins;

import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidArmorLayer.class)
public abstract class InjectUsesInnerModel<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
	public InjectUsesInnerModel(RenderLayerParent<T, M> p_117346_) { super(p_117346_); }
	
	@Inject (
		method = "usesInnerModel(Lnet/minecraft/world/entity/EquipmentSlot;)Z",
		at = @At ("RETURN"), // No particular return ordinal is required here
		// And to be honest, it's best for the interests of this mixin to
		// override all return statements.
		cancellable = true
	)
	public void usesInnerModel$spirit(EquipmentSlot slotType, CallbackInfoReturnable<Boolean> ci) {
		if (SpiritIdentifier.isSpirit(Minecraft.getInstance().player)) {
			ci.setReturnValue(false);
		}
	}
}
