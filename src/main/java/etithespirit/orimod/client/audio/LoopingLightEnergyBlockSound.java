package etithespirit.orimod.client.audio;

import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.util.delegates.IAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LoopingLightEnergyBlockSound extends AbstractTickableSoundInstance {
	public final BlockEntity block;
	public final ILightEnergyStorage energy;
	
	/** This action executes when the sound begins due to energy going from zero to nonzero */
	public IAction onStartup = null;
	
	/** This action executes when the sound ends due to energy going from nonzero to zero */
	public IAction onShutdown = null;
	
	/** This action executes when the sound ends due to its removal */
	public IAction onTermination = null;
	
	/**
	 * Because some devices may gain and spend energy at different intervals, it is possible for start/stop calls to be spammed.
	 * This value is a threshold for how full the energy device must be (as a percentage) to trigger a start/stop.
	 */
	public double energyPercentThreshold = 0;
	
	public float baseVolume = 0.2f;
	
	/** If true, the startup action will be called when this instance is enqueued. */
	public boolean playStartupOnLoad = false;
	protected double lastRatio = 0;
	protected boolean hasPlayedStartup = false;
	
	protected float warmth = 0;
	
	/**
	 * Create a new looping energy sound for the given block entity.
	 * @param blockEntity The block entity to play for.
	 * @param loop The loop sound.
	 * @throws IllegalArgumentException If the given input block entity does not implement {@link ILightEnergyStorage}
	 */
	public LoopingLightEnergyBlockSound(BlockEntity blockEntity, SoundEvent loop) throws IllegalArgumentException {
		super(loop, SoundSource.BLOCKS);
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
			if (onTermination != null) onTermination.execute();
		} else {
			BlockPos location = block.getBlockPos();
			this.x = (float)location.getX() + 0.5f;
			this.y = (float)location.getY() + 0.5f;
			this.z = (float)location.getZ() + 0.5f;
			double ratio = this.energy.getLightStored() / this.energy.getMaxLightStored();
			
			warmth = Mth.lerp(0.04f, warmth, 1);
			float newVolume = (float)(getVolumeForDistance(24, 1)) * warmth * baseVolume;
			if (lastRatio > energyPercentThreshold && ratio <= energyPercentThreshold) {
				if (onShutdown != null) onShutdown.execute();
			} else if (lastRatio <= energyPercentThreshold && ratio > energyPercentThreshold) {
				if (onStartup != null) onStartup.execute();
			}
			if (ratio >= energyPercentThreshold) {
				if (playStartupOnLoad && !hasPlayedStartup) {
					if (onStartup != null) onStartup.execute();
					hasPlayedStartup = true;
				}
			}
			this.volume = newVolume;
			lastRatio = ratio;
		}
	}
	
	public void reset() {
		warmth = 0;
		hasPlayedStartup = false;
		lastRatio = this.energy.getLightStored() / this.energy.getMaxLightStored();
	}
	
	protected double getVolumeForDistance(double maxDistance, double max) {
		Entity camEntity = Minecraft.getInstance().cameraEntity;
		if (camEntity == null) return 0;
		
		return (1D - Mth.clamp(Math.sqrt(camEntity.distanceToSqr(x, y, z)) / maxDistance, 0, 1)) * max;
	}
}
