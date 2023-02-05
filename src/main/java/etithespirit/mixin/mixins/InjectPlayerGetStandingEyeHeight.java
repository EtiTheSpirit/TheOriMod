package etithespirit.mixin.mixins;


import etithespirit.orimod.spirit.SpiritSize;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin injects the eye height getter of the player, as it fails for non-spirit players.
 */
@Mixin(Player.class)
public abstract class InjectPlayerGetStandingEyeHeight {
	
	
	@Inject(
		method = "getStandingEyeHeight",
		at = @At("HEAD")
	)
	public void orimod$getStandingEyeHeight(Pose pPose, EntityDimensions pSize, CallbackInfoReturnable<Float> cir) {
	
	}
	
}
