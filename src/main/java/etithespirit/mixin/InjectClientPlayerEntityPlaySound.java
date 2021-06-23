package etithespirit.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import etithespirit.etimod.event.EntityEmittedSoundEvent;
import etithespirit.etimod.event.EntityEmittedSoundEventProvider;
import etithespirit.mixininterfaces.hacks.DuplicateSoundEvent;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * Modifies ClientPlayerEntity's playSound method to fire the custom EntityEmittedSoundEvent handler.
 * @author Eti
 *
 */
@Mixin(ClientPlayerEntity.class)
@SuppressWarnings("unused")
public abstract class InjectClientPlayerEntityPlaySound extends AbstractClientPlayerEntity {

	public InjectClientPlayerEntityPlaySound(ClientWorld world, GameProfile profile) { super(world, profile); }
	
	@Inject(method = "playSound(Lnet/minecraft/util/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
	public void onPlaySoundCalled(SoundEvent soundIn, float volume, float pitch, CallbackInfo ci) {
		// See OverrideEntityPlaySound for what this garbage is.
		if (soundIn instanceof DuplicateSoundEvent) {
			return;
		}
		
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(this, (PlayerEntity)this, this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
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
	
	
	@Inject(method = "playNotifySound(Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V", at = @At("HEAD"), cancellable = true)
	public void onPlaySoundCalledWithCategory(SoundEvent soundIn, SoundCategory category, float volume, float pitch, CallbackInfo ci) {
		// See OverrideEntityPlaySound for what this garbage is.
		if (soundIn instanceof DuplicateSoundEvent) {
			return;
		}
		
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(this, (PlayerEntity)this, this.getX(), this.getY(), this.getZ(), soundIn, category, volume, pitch);
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
