package etithespirit.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import etithespirit.etimod.event.EntityEmittedSoundEvent;
import etithespirit.etimod.event.EntityEmittedSoundEventProvider;
import etithespirit.mixininterfaces.ISelfProvider;
import etithespirit.mixininterfaces.hacks.DuplicateSoundEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Modifies ServerPlayerEntity's playSound method to fire the custom EntityEmittedSoundEvent handler.
 * @author Eti
 *
 */
@Mixin(ServerPlayerEntity.class)
@SuppressWarnings("unused")
public abstract class InjectServerPlayerEntityPlaySound extends PlayerEntity implements ISelfProvider {

	public InjectServerPlayerEntityPlaySound(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
		super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
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
