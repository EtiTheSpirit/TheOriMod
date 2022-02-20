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
/***/
@Mixin(Player.class)
public abstract class InjectPlayerPlaySound extends LivingEntity implements ISelfProvider {
	/***/
	protected InjectPlayerPlaySound(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) { super(p_20966_, p_20967_); }
	
	/***/
	@Inject(method="playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at=@At ("HEAD"), cancellable = true)
	public void onPlaySoundCalled(SoundEvent soundIn, float volume, float pitch, CallbackInfo ci) {
		// See OverrideEntityPlaySound for what this garbage is.
		if (soundIn instanceof DuplicateSoundEvent) {
			return;
		}
		
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(selfProvider$self(), null, this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
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
