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

/**
 * This mixin injects into the usesInnerModel value for armor items to improve armor rendering on Spirits.
 * This is because Spirits use a custom rendering scheme involving a terribly stitched together mess based on the vanilla UV map to lazily allow spirits to use any armor type.
 * @param <T> T!
 * @param <M> M!
 * @param <A> A!
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class InjectUsesInnerModel<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
	
	private InjectUsesInnerModel(RenderLayerParent<T, M> pRenderer) { super(pRenderer); }
	
	/**
	 * The custom inected method.
	 * @param slotType The slot type for the armor.
	 * @param ci Mixin callback information.
	 */
	@Inject (
		method = "usesInnerModel(Lnet/minecraft/world/entity/EquipmentSlot;)Z",
		at = @At ("RETURN"), // No particular return ordinal is required here
		// And to be honest, it's best for the interests of this mixin to
		// override all return statements.
		cancellable = true
	)
	public void orimod$usesInnerModel$spirit(EquipmentSlot slotType, CallbackInfoReturnable<Boolean> ci) {
		if (SpiritIdentifier.isSpirit(Minecraft.getInstance().player)) {
			ci.setReturnValue(false);
		}
	}
}
