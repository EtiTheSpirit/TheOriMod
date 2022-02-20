package etithespirit.mixin.mixins;

import etithespirit.mixin.helpers.DuplicateSoundEvent;
import etithespirit.mixin.helpers.ISelfProvider;
import etithespirit.orimod.event.EntityEmittedSoundEvent;
import etithespirit.orimod.event.EntityEmittedSoundEventProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/***/
@Mixin(Entity.class)
public abstract class InjectEntityPlaySound extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements ISelfProvider {
	/***/
	protected InjectEntityPlaySound(Class<Entity> baseClass) { super(baseClass); }
	/***/
	@Shadow
	public abstract boolean isSilent();
	/***/
	@Shadow
	public abstract double getX();
	/***/
	@Shadow
	public abstract double getY();
	/***/
	@Shadow
	public abstract double getZ();
	/***/
	@Shadow
	public abstract SoundSource getSoundSource();
	/***/
	@Shadow
	public abstract void playSound(SoundEvent soundIn, float volume, float pitch);
	/***/
	@Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
	public void onPlaySoundCalled(SoundEvent soundIn, float volume, float pitch, CallbackInfo ci) {
		// Now this is probably a really shitty idea, but basically I want to ensure maximum compatibility with other mixins.
		// The basic gist is that when Entity.playSound is called, it goes here instead, right?
		// So what if some other guy's class also wants to edit the sound?
		// Here, I have two choices:
		// 1: Just call my own play method (what I originally did) - Destructive, but allows a lot of control!
		// 2: Re-call playSound - Compatible, but I can't change things like category or position (oh well)!
		
		// I want to do #2 instead of #1, but because of this, I have to write up a bit of a hack to know when it's a re-entered call.
		// I can do this with a lazy subclass implementation. DuplicateSoundEvent will extend SoundEvent and basically just be a SoundEvent
		// with a different face.
		if (soundIn instanceof DuplicateSoundEvent) {
			// And so if it's one of my custom instances, oh well, we already handled it. Cool. Move on.
			return;
		}
		
		// Run it though my custom event provider. This is designed to mimic PR #7491 behaviorally,
		// The big difference is that it's just self-implemented in my own mod code rather than as a patch to vanilla MC.
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(selfProvider$self(), null, this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
		if (evt.isCanceled()) {
			ci.cancel();
			return;
		}
		
		if (evt.wasModified()) {
			// ^ If the event caused the sound data to be modified in any capacity, we will call playSound again with an instance of
			// DuplicateSoundEvent (which, as seen earlier, will come back here but immediately abort).
			// This allows other Mixins to fiddle with my sound too.
			playSound(new DuplicateSoundEvent(evt.getSound()), evt.getVolume(), evt.getPitch());
			
			// And since ^ was recalled, go ahead and mark it as cancelled for this time around.
			ci.cancel();
		}
	}
}
