package etithespirit.mixin.mixins;

import com.mojang.authlib.GameProfile;
import etithespirit.mixin.helpers.ISelfProvider;
import etithespirit.orimod.event.EntityEmittedSoundEvent;
import etithespirit.orimod.event.EntityEmittedSoundEventProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin covers the serverside component of the sound overrides made for Spirits.
 */
@Mixin(ServerPlayer.class)
public abstract class InjectServerPlayerPlaySound extends Player implements ISelfProvider {
	
	
	public InjectServerPlayerPlaySound(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile, @Nullable ProfilePublicKey pProfilePublicKey) {
		super(pLevel, pPos, pYRot, pGameProfile, pProfilePublicKey);
	}
	
	
	private boolean orimod$playedNotifySoundAlready = false;
	
	/**
	 * This method is injected to pipe sound events to my custom event handler.
	 * @param soundIn The sound being played.
	 * @param category The category this sound is playing in.
	 * @param volume The volume of this sound.
	 * @param pitch The pitch of this sound.
	 * @param ci The Mixin callback info.
	 */
	@Inject(method="playNotifySound(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at=@At("HEAD"), cancellable = true)
	public void orimod$onPlaySoundCalledWithCategory(SoundEvent soundIn, SoundSource category, float volume, float pitch, CallbackInfo ci) {
		if (orimod$playedNotifySoundAlready) return;
		EntityEmittedSoundEvent evt = EntityEmittedSoundEventProvider.getSound(this, selfProvider$player(), this.getX(), this.getY(), this.getZ(), soundIn, category, volume, pitch);
		if (evt.isCanceled()) {
			ci.cancel();
			return;
		}
		
		if (evt.wasModified()) {
			// See OverrideEntityPlaySound for what this garbage is.
			//playNotifySound(new DuplicateSoundEvent(evt.getSound()), evt.getCategory(), evt.getVolume(), evt.getPitch());
			orimod$playedNotifySoundAlready = true;
			playNotifySound(evt.getSound(), evt.getCategory(), evt.getVolume(), evt.getPitch());
			orimod$playedNotifySoundAlready = false;
			ci.cancel();
		}
	}
}