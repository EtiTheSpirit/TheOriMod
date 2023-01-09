package etithespirit.mixin.mixins;

import etithespirit.orimod.common.entity.DecayExploder;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.gameplay.EntityRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * This mixin modifies the argument passed into playLocalSound when {@link Explosion} does its clientside effects. It is used to customize the sound effect.
 * @deprecated The getSourceMob method is always null on the client. This must be replicated over the network, thus making a custom explosion class more suitable than this mixin.
 */
@Mixin(Explosion.class)
@Deprecated
public abstract class RedirectExplosionSound {
	
	@Shadow
	public abstract LivingEntity getSourceMob();
	
	@Redirect(
		method = "finalizeExplosion(Z)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
		)
	)
	public void redirectPlaySoundCall(Level instance, double pX, double pY, double pZ, SoundEvent pSound, SoundSource pCategory, float pVolume, float pPitch, boolean pDistanceDelay) {
		LivingEntity src = getSourceMob();
		if (src == null || (src.getType() != EntityRegistry.DECAY_EXPLODER.get())) {
			// Not a decay exploder, do not override.
			instance.playLocalSound(pX, pY, pZ, pSound, pCategory, pVolume, pPitch, pDistanceDelay);
			return;
		}
		
		float pitch = (instance.getRandom().nextFloat() / 10) + 0.95f;
		instance.playLocalSound(pX, pY, pZ, SoundRegistry.get("entity.decay_exploder.detonate_overtone"), pCategory, pVolume, pitch, pDistanceDelay);
		
		pitch = (instance.getRandom().nextFloat() / 10) + 0.95f;
		instance.playLocalSound(pX, pY, pZ, SoundRegistry.get("entity.decay_exploder.detonate_undertone"), pCategory, pVolume, pitch, pDistanceDelay);
	}
	
	/*
	@ModifyArgs (
		method = "finalizeExplosion(Z)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
		)
	)
	//public void playLocalSound(double pX, double pY, double pZ, SoundEvent pSound, SoundSource pCategory, float pVolume, float pPitch, boolean pDistanceDelay)
	public void modifyExplosionSound(Args args) {
		// sound is 3
		// volume is 5
		// pitch is 6
		
		double pX = args.get(0);
		double pY = args.get(1);
		double pZ = args.get(2);
		SoundEvent pSound = args.get(3);
		SoundSource pCategory = args.get(4);
		float pVolume = args.get(5);
		// float pPitch = args.get(6);
		boolean pDistanceDelay = args.get(7);
		
		LivingEntity ent = getSourceMob();
		if (ent instanceof DecayExploder) {
			// Two things here. First of all, play the secondary sound.
			float pitch = (ent.level.getRandom().nextFloat() / 10) - 0.05f;
			ent.level.playLocalSound(pX, pY, pZ, SoundRegistry.get("entity.decay_exploder.detonate_overtone"), pCategory, pVolume, pitch, pDistanceDelay);
			
			// Then second, replace the sound in the event, this will cause both to play.
			args.set(3, SoundRegistry.get("entity.decay_exploder.detonate_undertone"));
			args.set(6, pitch);
		}
	}
	*/
}
