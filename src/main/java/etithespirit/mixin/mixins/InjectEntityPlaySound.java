package etithespirit.mixin.mixins;

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

/**
 * This covers the entity-wide sound interception system, for the sake of completeness.
 */
@Mixin(Entity.class)
public abstract class InjectEntityPlaySound extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements ISelfProvider {
	private InjectEntityPlaySound(Class<Entity> baseClass) { super(baseClass); }
	
	/**
	 * Shadows {@link Entity#isSilent()}
	 * @return True if this entity does not play sound.
	 */
	@Shadow
	public abstract boolean isSilent();
	
	/**
	 * Shadows {@link Entity#getX()}
	 * @return The X component of this entity's position.
	 */
	@Shadow
	public abstract double getX();
	
	/**
	 * Shadows {@link Entity#getY()}
	 * @return The Y component of this entity's position.
	 */
	@Shadow
	public abstract double getY();
	
	/**
	 * Shadows {@link Entity#getZ()}
	 * @return The Z component of this entity's position.
	 */
	@Shadow
	public abstract double getZ();
	
	/**
	 * Shadows {@link Entity#getSoundSource()}
	 * @return The category of sound that this entity emits.
	 */
	@Shadow
	public abstract SoundSource getSoundSource();
	
	/**
	 * Shadows {@link Entity#playSound(SoundEvent, float, float)}
	 * @param soundIn The sound to play.
	 * @param volume The volume of the sound.
	 * @param pitch The pitch of the sound.
	 */
	@Shadow
	public abstract void playSound(SoundEvent soundIn, float volume, float pitch);
	
	
	private boolean orimod$playedSoundAlready = false;
	
	
	/**
	 * This injects into the beginning of {@link Entity#playSound(SoundEvent, float, float)} and pipes the event through my custom event handler.
	 * @param soundIn The sound that was played.
	 * @param volume The volume the sound was played with.
	 * @param pitch The pitch the sound was played with.
	 * @param ci The Mixin callback info.
	 */
	@Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
	public void orimod$onPlaySoundCalled(SoundEvent soundIn, float volume, float pitch, CallbackInfo ci) {
		if (orimod$playedSoundAlready) return;
		// Run it though my custom event provider. This is designed to mimic PR #7491 behaviorally,
		// The big difference is that it's just self-implemented in my own mod code rather than as a patch to vanilla MC.
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(selfProvider$entity(), null, this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
		if (evt.isCanceled()) {
			ci.cancel();
			return;
		}
		
		if (evt.wasModified()) {
			// ^ If the event caused the sound data to be modified in any capacity, we will call playSound again with an instance of
			// DuplicateSoundEvent (which, as seen earlier, will come back here but immediately abort).
			// This allows other Mixins to fiddle with my sound too.
			//playSound(new DuplicateSoundEvent(evt.getSound()), evt.getVolume(), evt.getPitch());
			orimod$playedSoundAlready = true;
			playSound(evt.getSound(), evt.getVolume(), evt.getPitch());
			orimod$playedSoundAlready = false;
			
			// And since ^ was recalled, go ahead and mark it as cancelled for this time around.
			ci.cancel();
		}
	}
}