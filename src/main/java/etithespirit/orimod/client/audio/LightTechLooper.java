package etithespirit.orimod.client.audio;

import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.util.timing.TimeKeeper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

@ClientUseOnly
/**
 * The Light Tech Looper is a sound system designed to play looping audio while a block is active.
 * Unlike traditional loops, this allows seamlessly blending a startup and shutdown sound into the mix.
 */
public class LightTechLooper {
	
	protected static final RandomSource RNG = RandomSource.create();
	
	public final BlockEntity source;
	
	public final SoundEvent startSound;
	
	public final LoopPartHandler loopSound;
	
	public final SoundEvent stopSound;
	
	public final SoundSource soundType;
	
	private float baseVolume = 1f;
	
	private boolean lastState = false;
	
	/**
	 * Construct a new looper for the given {@link BlockEntity} using the given start, loop, and end sounds. The sound type is set to {@link SoundSource#BLOCKS}.
	 * @param src The {@link BlockEntity} to create this for.
	 * @param start The sound effect that plays when starting up the audio.
	 * @param loop The loop effect that plays while this is active.
	 * @param end The sound effect that plays when stopping the audio.
	 */
	public LightTechLooper(BlockEntity src, SoundEvent start, SoundEvent loop, SoundEvent end) {
		this(src, start, loop, end, SoundSource.BLOCKS);
	}
	
	
	/**
	 * Construct a new looper for the given {@link BlockEntity} using the given start, loop, and end sounds.
	 * @param src The {@link BlockEntity} to create this for.
	 * @param start The sound effect that plays when starting up the audio.
	 * @param loop The loop effect that plays while this is active.
	 * @param end The sound effect that plays when stopping the audio.
	 * @param type The type of sound that this is. In 99% of cases this will be {@link SoundSource#BLOCKS}. If it is, consider not including this argument. Or don't. I'm not your daddy (as much as some of you would like me to be) (nohomo).
	 */
	public LightTechLooper(BlockEntity src, SoundEvent start, SoundEvent loop, SoundEvent end, SoundSource type) {
		startSound = start;
		loopSound = new LoopPartHandler(src, loop, type);
		stopSound = end;
		soundType = type;
		source = src;
	}
	
	public void setBaseVolume(float volume) {
		baseVolume = volume;
		loopSound.setBaseVolume(volume);
	}
	
	public void play() {
		if (lastState) return;
		lastState = true;
		if (loopSound.hasFullyStopped()) {
			Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
				startSound,
				soundType,
				baseVolume,
				1,
				RNG,
				source.getBlockPos()
			));
			loopSound.easeIntoStart();
		}
	}
	
	public void stop() {
		if (!lastState) return;
		lastState = false;
		if (!loopSound.hasFullyStopped()) {
			Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
				stopSound,
				soundType,
				baseVolume,
				1,
				RNG,
				source.getBlockPos()
			));
			loopSound.easeIntoStop();
		}
	}
	
	@Deprecated
	public void setRange(float range) {
		loopSound.setRange(range);
	}
	
	@Deprecated
	public float getRange() {
		return loopSound.getRange();
	}
	
	private static final class LoopPartHandler extends AbstractTickableSoundInstance {
		
		private float baseVolume = 0;
		
		/** Warmth is a measure of how "warmed up" the sound is, in that "colder" loops will play quieter. This is used to ease the transition from the start and end phases. */
		private float warmth = 0;
		
		private float range = 8;
		
		private final BlockEntity source;
		
		/** The desired state of this sound. If false, the sound wants to stop. If true, the sound wants to start. This property determines the warmth. */
		private boolean desiredState = false;
		
		private boolean needsToDequeue = false;
		
		/** Keeps track of delta-time for real time scaling of effects. */
		private final TimeKeeper timer = new TimeKeeper();
		
		
		public boolean hasFullyStopped() {
			return !desiredState && warmth == 0;
		}
		
		
		void setBaseVolume(float vol) {
			baseVolume = vol;
		}
		
		public LoopPartHandler(BlockEntity source, SoundEvent loop, SoundSource type) {
			super(loop, type, RNG);
			this.looping = true;
			this.source = source;
			this.x = (float)source.getBlockPos().getX() + 0.5f;
			this.y = (float)source.getBlockPos().getY() + 0.5f;
			this.z = (float)source.getBlockPos().getZ() + 0.5f;
			this.attenuation = Attenuation.LINEAR; // I handle this manually.
		}
		
		/** For {@link LightTechLooper}, this instructs the loop to fade in. */
		public void easeIntoStart() {
			if (desiredState) return;
			warmth = 0.01f; // Bump this up so canPlaySound is true
			Minecraft.getInstance().getSoundManager().queueTickingSound(this);
			desiredState = true;
			timer.tick();
		}
		
		/** For {@link LightTechLooper}, this instructs the loop to fade out. */
		public void easeIntoStop() {
			if (!desiredState) return;
			desiredState = false;
			timer.tick();
		}
		
		public void setRange(float range) {
			this.range = range;
		}
		
		public float getRange() {
			return range;
		}
		
		/**
		 * Updates the state of this sound.
		 */
		@Override
		public void tick() {
			if (source == null || source.isRemoved() || !source.hasLevel()) {
				if (needsToDequeue) {
					Minecraft.getInstance().getSoundManager().stop(this);
					needsToDequeue = false;
				}
				return;
			}
			// volume = getVolumeForDistance(range, baseVolume) * warmth;
			volume = baseVolume * warmth;
			if (desiredState) {
				warmth += timer.tick();
			} else {
				warmth -= timer.tick();
			}
			if (warmth <= 0) {
				warmth = 0;
				if (needsToDequeue) {
					Minecraft.getInstance().getSoundManager().stop(this);
					needsToDequeue = false;
				}
			} else if (warmth >= 1) {
				warmth = 1;
				needsToDequeue = true;
			} else if (warmth > 0) {
				needsToDequeue = true;
			}
		}
		
		/**
		 * @return Whether or not this sound can still be enqueued or continue playing with a zero volume.
		 */
		@Override
		public boolean canStartSilent() {
			return true;
		}
		
		/**
		 * @return Whether or not this sound should be playing.
		 */
		@Override
		public boolean canPlaySound() {
			return warmth > 0 && baseVolume > 0;
		}
		
		/**
		 * Returns the volume for the given distance via an inverse quadratic (roughly) falloff.
		 * This uses single precision in place of double precision for the sake of speed over accuracy.
		 * @param maxDistance The distance from which the volume will be 0.
		 * @param max The maximum volume (at distance=0)
		 * @return A value between 0 and (max) based on the camera entity's distance.
		 */
		private float getVolumeForDistance(float maxDistance, float max) {
			Entity camEntity = Minecraft.getInstance().cameraEntity;
			if (camEntity == null) return 0;
			float maxDistanceSqr = maxDistance * maxDistance;
			float dst = Mth.clamp(distanceToSqrF(camEntity, (float)x, (float)y, (float)z) / maxDistanceSqr, 0f, 1f);
			
			return (Mth.fastInvSqrt((dst * 3f) + 1f) - 0.5f) * 2f * max;
			// Wonder what archaic math this is? Look at it here. https://www.desmos.com/calculator/a2rdec7jhu
		}
		
		private static float distanceToSqrF(Entity ent, float pX, float pY, float pZ) {
			float d0 = (float)ent.getX() - pX;
			float d1 = (float)ent.getY() - pY;
			float d2 = (float)ent.getZ() - pZ;
			return d0 * d0 + d1 * d1 + d2 * d2;
		}
	}
	
}
