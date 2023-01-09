package etithespirit.mixin.mixins;

import etithespirit.mixin.helpers.DuplicateSoundEvent;
import etithespirit.mixin.helpers.ISelfProvider;
import etithespirit.orimod.event.EntityEmittedSoundEvent;
import etithespirit.orimod.event.EntityEmittedSoundEventProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin affects the common portion of the Spirit sound injector.
 */
@Mixin(Player.class)
public abstract class InjectPlayerPlaySound extends LivingEntity implements ISelfProvider {
	private InjectPlayerPlaySound(EntityType<? extends LivingEntity> pEntityType, Level pLevel) { super(pEntityType, pLevel); }
	
	/**
	 * This method is injected to intercept sounds and pipe them to my custom sound event.
	 * @param soundIn The sound being played.
	 * @param volume The volume of the sound.,
	 * @param pitch The pitch of the sound.
	 * @param ci The Mixin callback info.
	 */
	@Inject(method="playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at=@At("HEAD"), cancellable = true)
	public void onPlaySoundCalled(SoundEvent soundIn, float volume, float pitch, CallbackInfo ci) {
		// See OverrideEntityPlaySound for what this garbage is.
		if (soundIn instanceof DuplicateSoundEvent dup && dup.isDuplicate) {
			return;
		}
		
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(selfProvider$entity(), null, this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
		if (evt.isCanceled()) {
			ci.cancel();
			return;
		}
		
		if (evt.wasModified()) {
			// See OverrideEntityPlaySound for what this garbage is.
			playSound(new DuplicateSoundEvent(evt.getSound()), evt.getVolume(), evt.getPitch());
			ci.cancel();
		}
	}
}
