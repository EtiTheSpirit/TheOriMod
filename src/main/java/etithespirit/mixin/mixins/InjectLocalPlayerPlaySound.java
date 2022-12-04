package etithespirit.mixin.mixins;

import com.mojang.authlib.GameProfile;
import etithespirit.mixin.helpers.DuplicateSoundEvent;
import etithespirit.mixin.helpers.ISelfProvider;
import etithespirit.orimod.event.EntityEmittedSoundEvent;
import etithespirit.orimod.event.EntityEmittedSoundEventProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class covers the clientside portion of the Spirit sound injector.
 */
@Mixin(LocalPlayer.class)
public abstract class InjectLocalPlayerPlaySound extends AbstractClientPlayer implements ISelfProvider {
	
	
	public InjectLocalPlayerPlaySound(ClientLevel pClientLevel, GameProfile pGameProfile, @Nullable ProfilePublicKey pProfilePublicKey) {
		super(pClientLevel, pGameProfile, pProfilePublicKey);
	}
	
	/**
	 * This method intercepts generic playSound calls and pipes the event through my custom event handler.
	 * @param soundIn The sound that was played.
	 * @param volume The volume the sound was played with.
	 * @param pitch The pitch the sound was played with.
	 * @param ci The Mixin callback info.
	 */
	@Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
	public void onPlaySoundCalled(SoundEvent soundIn, float volume, float pitch, CallbackInfo ci) {
		// See OverrideEntityPlaySound for what this garbage is.
		if (soundIn instanceof DuplicateSoundEvent) {
			return;
		}
		
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(this, selfProvider$player(), this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
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
	
	/**
	 * This method intercepts the more contextual sound method and pipes the event through my custom event handler.
	 * @param soundIn The sound that was played.
	 * @param category The category this sound is playing in.
	 * @param volume The volume the sound was played with.
	 * @param pitch The pitch the sound was played with.
	 * @param ci The Mixin callback info.
	 */
	@Inject(method = "playNotifySound(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"), cancellable = true)
	public void onPlaySoundCalledWithCategory(SoundEvent soundIn, SoundSource category, float volume, float pitch, CallbackInfo ci) {
		// See OverrideEntityPlaySound for what this garbage is.
		if (soundIn instanceof DuplicateSoundEvent) {
			return;
		}
		
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(this, selfProvider$player(), this.getX(), this.getY(), this.getZ(), soundIn, category, volume, pitch);
		if (evt.isCanceled()) {
			ci.cancel();
			return;
		}
		
		if (evt.wasModified()) {
			// See OverrideEntityPlaySound for what this garbage is.
			playNotifySound(new DuplicateSoundEvent(evt.getSound()), evt.getCategory(), evt.getVolume(), evt.getPitch());
			ci.cancel();
		}
	}
	
}
