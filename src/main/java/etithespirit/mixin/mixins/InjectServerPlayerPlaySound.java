package etithespirit.mixin.mixins;

import com.mojang.authlib.GameProfile;
import etithespirit.mixin.helpers.DuplicateSoundEvent;
import etithespirit.mixin.helpers.ISelfProvider;
import etithespirit.orimod.event.EntityEmittedSoundEvent;
import etithespirit.orimod.event.EntityEmittedSoundEventProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/***/
@Mixin(ServerPlayer.class)
public abstract class InjectServerPlayerPlaySound extends Player implements ISelfProvider {
	/***/ public InjectServerPlayerPlaySound(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_) { super(p_36114_, p_36115_, p_36116_, p_36117_); }
	
	/***/
	@Inject(method="playNotifySound(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at=@At("HEAD"), cancellable = true)
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
