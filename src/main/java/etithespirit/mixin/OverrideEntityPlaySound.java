package etithespirit.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import etithespirit.etimod.event.EntityEmittedSoundEvent;
import etithespirit.etimod.event.EntityEmittedSoundEventProvider;
import etithespirit.mixininterfaces.ISelfProvider;
import etithespirit.mixininterfaces.hacks.DuplicateSoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * Modifies Entity's playSound method to fire the custom EntityEmittedSoundEvent handler.
 * @author Eti
 *
 */
@Mixin(Entity.class)
public abstract class OverrideEntityPlaySound extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements ISelfProvider {
	protected OverrideEntityPlaySound(Class<Entity> baseClass) { super(baseClass); }
	
	@Shadow
	public abstract boolean isSilent();
	
	@Shadow
	public abstract double getPosX();
	
	@Shadow
	public abstract double getPosY();
	
	@Shadow
	public abstract double getPosZ();
	
	@Shadow
	public abstract SoundCategory getSoundCategory();
	
	@Shadow
	public abstract void playSound(SoundEvent soundIn, float volume, float pitch);
	
	@Inject(method = "playSound(Lnet/minecraft/util/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
	public void onPlaySoundCalled(SoundEvent soundIn, float volume, float pitch, CallbackInfo ci) {
		// Now this is probably a really REALLY ***REALLY*** shitty idea, but basically I want to ensure maximum compatibility with other mixins.
		// The basic gist is that when Entity.playSound is called, it goes here instead, right?
		// So what if some other guy's class also wants to edit the sound?
		// Here, I have two choices:
		// 1: Just call my own play method (what I originally did) - Destructive, but allows a lot of control!
		// 2: Re-call playSound - Compatible, but I can't change things like category or position (oh well)!
		
		// I want to do #2 instead of #1, but because of this, I have to write up a bit of a hack to know when it's a re-entered call.
		// I can do this with a lazy subclass implementation.
		if (soundIn instanceof DuplicateSoundEvent) {
			return;
		}
		
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(self(), null, this.getPosX(), this.getPosY(), this.getPosZ(), soundIn, this.getSoundCategory(), volume, pitch);
		if (evt.isCanceled()) {
			ci.cancel();
			return;
		}
		
		if (evt.wasModified()) {
			//EntityEmittedSoundEventProvider.playSoundClient(null, evt);
			playSound(new DuplicateSoundEvent(evt.getSound()), evt.getVolume(), evt.getPitch());
			ci.cancel();
			return;
		}
	}
}
