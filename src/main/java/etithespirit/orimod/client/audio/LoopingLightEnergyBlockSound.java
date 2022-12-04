package etithespirit.orimod.client.audio;

import etithespirit.orimod.energy.ILightEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;

/**
 * This class serves as the housing for a Light tech device's ambient loop sound. It is intended for use where there is a startup, loop, and shutdown sound.
 */
public class LoopingLightEnergyBlockSound extends AbstractTickableSoundInstance {
	
	/** The block this exists for. */
	public final BlockEntity block;
	
	/** Identical to {@link #block} with the exception that it is stored as an {@link ILightEnergyStorage}. */
	public final ILightEnergyStorage energy;
	
	/** This action executes when the sound begins due to energy going from zero to nonzero */
	public Runnable onStartup = null;
	
	/** This action executes when the sound ends due to energy going from nonzero to zero */
	public Runnable onShutdown = null;
	
	/** This action executes when the sound ends due to its removal */
	public Runnable onTermination = null;
	
	/**
	 * Because some devices may gain and spend energy at different intervals, it is possible for start/stop calls to be spammed.
	 * This value is a threshold for how full the energy device must be (as a percentage) to trigger a start/stop.
	 */
	public double energyPercentThreshold = 0;
	
	/** The baseline volume that is used when playing any of the sounds involved. */
	public float baseVolume = 0.2f;
	
	/** If true, the startup action will be called when this instance is enqueued. */
	public boolean playStartupOnLoad = false;
	
	/** The last tracked ratio of energy. */
	protected double lastRatio = 0;
	
	/** Whether or not the startup sound has played. */
	protected boolean hasPlayedStartup = false;
	
	/** The "warmth" of the audio is used for a brief fade-in period to make the transition from the start sound to the loop sound less jarring. */
	protected float warmth = 0;
	
	/**
	 * Create a new looping energy sound for the given block entity.
	 * @param blockEntity The block entity to play for.
	 * @param loop The loop sound.
	 * @throws IllegalArgumentException If the given input block entity does not implement {@link ILightEnergyStorage}
	 */
	public LoopingLightEnergyBlockSound(BlockEntity blockEntity, SoundEvent loop) throws IllegalArgumentException {
		super(loop, SoundSource.BLOCKS, blockEntity.getLevel().getRandom());
		if (blockEntity instanceof ILightEnergyStorage energyStorage) {
			this.energy = energyStorage;
		} else {
			throw new IllegalArgumentException("The given BlockEntity does not extend " + ILightEnergyStorage.class.getSimpleName());
		}
		this.block = blockEntity;
		this.looping = true;
		this.delay = 0;
		this.volume = 0.0F;
		this.attenuation = Attenuation.NONE;
		reset();
		
		BlockPos location = blockEntity.getBlockPos();
		this.x = (float)location.getX() + 0.5f;
		this.y = (float)location.getY() + 0.5f;
		this.z = (float)location.getZ() + 0.5f;
	}
	
	public boolean canPlaySound() {
		return this.energy.getLightStored() > 0;
	}
	
	public boolean canStartSilent() {
		return true;
	}
	
	public void tick() {
		if (block.isRemoved()) {
			this.stop();
			if (onTermination != null) onTermination.run();
		} else {
			BlockPos location = block.getBlockPos();
			this.x = (float)location.getX() + 0.5f;
			this.y = (float)location.getY() + 0.5f;
			this.z = (float)location.getZ() + 0.5f;
			double ratio = this.energy.getLightStored() / this.energy.getMaxLightStored();
			
			warmth = Mth.lerp(0.04f, warmth, 1);
			float newVolume = (float)(getVolumeForDistance(12, 1)) * warmth * baseVolume;
			if (lastRatio > energyPercentThreshold && ratio <= energyPercentThreshold) {
				if (onShutdown != null) onShutdown.run();
			} else if (lastRatio <= energyPercentThreshold && ratio > energyPercentThreshold) {
				if (onStartup != null) onStartup.run();
			}
			if (ratio >= energyPercentThreshold) {
				if (playStartupOnLoad && !hasPlayedStartup) {
					if (onStartup != null) onStartup.run();
					hasPlayedStartup = true;
				}
			}
			this.volume = newVolume;
			lastRatio = ratio;
		}
	}
	
	/**
	 * Resets the audio data associated with this sound, intended for use once it has stopped for whatever reason.
	 */
	public void reset() {
		warmth = 0;
		hasPlayedStartup = false;
		lastRatio = this.energy.getLightStored() / this.energy.getMaxLightStored();
	}
	
	/**
	 * Returns the volume for the given distance via a linear falloff.
	 * @param maxDistance The distance from which the volume will be 0.
	 * @param max The maximum volume (at distance=0)
	 * @return A value between 0 and (max) based on the camera entity's distance.
	 */
	protected double getVolumeForDistance(double maxDistance, double max) {
		Entity camEntity = Minecraft.getInstance().cameraEntity;
		if (camEntity == null) return 0;
		
		return (1D - Mth.clamp(Math.sqrt(camEntity.distanceToSqr(x, y, z)) / maxDistance, 0, 1)) * max;
	}
}
