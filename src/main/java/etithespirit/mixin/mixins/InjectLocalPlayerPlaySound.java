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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class InjectLocalPlayerPlaySound extends AbstractClientPlayer implements ISelfProvider {
	
	public InjectLocalPlayerPlaySound(ClientLevel p_108548_, GameProfile p_108549_) { super(p_108548_, p_108549_); }
	
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
