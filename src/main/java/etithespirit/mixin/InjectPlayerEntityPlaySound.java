package etithespirit.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import etithespirit.etimod.event.EntityEmittedSoundEvent;
import etithespirit.etimod.event.EntityEmittedSoundEventProvider;
import etithespirit.mixininterfaces.ISelfProvider;
import etithespirit.mixininterfaces.hacks.DuplicateSoundEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Modifies PlayerEntity's playSound method to fire the custom EntityEmittedSoundEvent handler.
 * @author Eti
 *
 */
@Mixin(PlayerEntity.class)
public abstract class InjectPlayerEntityPlaySound extends LivingEntity implements ISelfProvider {

	protected InjectPlayerEntityPlaySound(EntityType<? extends LivingEntity> type, World worldIn) { super(type, worldIn); }
	
	@Inject(method = "playSound(Lnet/minecraft/util/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
	public void onPlaySoundCalled(SoundEvent soundIn, float volume, float pitch, CallbackInfo ci) {
		// See OverrideEntityPlaySound for what this garbage is.
		if (soundIn instanceof DuplicateSoundEvent) {
			return;
		}
		
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(self(), null, this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
		if (evt.isCanceled()) {
			ci.cancel();
			return;
		}
		
		if (evt.wasModified()) {
			// See OverrideEntityPlaySound for what this garbage is.
			playSound(new DuplicateSoundEvent(evt.getSound()), evt.getVolume(), evt.getPitch());
			ci.cancel();
			return;
		}
	}
}
