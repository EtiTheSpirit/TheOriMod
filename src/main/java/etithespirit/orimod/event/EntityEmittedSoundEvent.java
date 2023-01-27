package etithespirit.orimod.event;


import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * An implementation of <a href="https://github.com/MinecraftForge/MinecraftForge/pull/7491">Forge PR #7941</a>, this event is used
 * to intercept when a sound is played <em>by a specific entity</em> (as opposed to in general, which is what Forge's existing event does)
 * and potentially modify it using the context as to who/what played it.
 * @author Eti
 */
@SuppressWarnings("unused")
public class EntityEmittedSoundEvent {
	
	private final Entity entity;
	private final SoundEvent originalSound;
	private final SoundSource originalCategory;
	private final float originalVolume;
	private final float originalPitch;
	private final Vec3 originalPosition;
	
	private SoundEvent sound;
	private SoundSource category;
	private float volume;
	private float pitch;
	private boolean cancel;
	
	/**
	 * Create a new event describing that an entity has just played a sound.
	 * @param source The entity responsible for the sound.
	 * @param position The position at which this sound played in the world.
	 * @param sound The sound that is playing.
	 * @param category The category that this sound is a part of.
	 * @param volume The volume of this sound.
	 * @param pitch The pitch of this sound.
	 */
	public EntityEmittedSoundEvent(Entity source, Vec3 position, SoundEvent sound, SoundSource category, float volume, float pitch)
	{
		this.originalSound = sound;
		this.originalCategory = category;
		this.originalVolume = volume;
		this.originalPitch = pitch;
		this.originalPosition = position;
		
		this.entity = source;
		this.sound = sound;
		this.category = category;
		this.volume = volume;
		this.pitch = pitch;
		this.cancel = false;
	}
	
	/** @return Whether or not some data was modified in this event, which determines if it should override vanilla sound playing behaviors.
	 * <strong>The cancellation state does not affect this return value.</strong>
	 */
	public boolean wasModified() {
		return !sound.equals(originalSound) ||
			!category.equals(originalCategory) ||
			!(volume == originalVolume) ||
			!(pitch == originalPitch);
	}
	
	/** @return The position at which this sound will be played. */
	public Vec3 getPosition() { return this.originalPosition; }
	
	/** @return The sound that should be played. */
	public SoundEvent getSound() { return this.sound; }
	
	/** @return The category of the sound that should be played. */
	public SoundSource getCategory() { return this.category; }
	
	/** @return The current override volume for this sound. */
	public float getVolume() { return this.volume; }
	
	/** @return The current override pitch for this sound. */
	public float getPitch() { return this.pitch; }
	
	/** @return The entity that is responsible for playing this sound. */
	public Entity getEntity() { return this.entity; }
	
	/** @return True if this sound should no longer play. */
	public boolean isCanceled() { return this.cancel; }
	
	/** @return The sound that this event had when the event was first constructed. */
	public SoundEvent getDefaultSound() { return this.originalSound; }
	
	/** @return The sound category that this sound started with when the event was constructed. */
	public SoundSource getDefaultCategory() { return this.originalCategory; }
	
	/** @return The volume that this sound started with when the event was constructed. */
	public float getDefaultVolume() { return this.originalVolume; }
	
	/** @return The pitch that this sound started with when the event was constructed. */
	public float getDefaultPitch() { return this.originalPitch; }
	
	/**
	 * Change the sound associated with this event.
	 * @param value The new sound.
	 */
	public void setSound(SoundEvent value) { this.sound = value; }
	
	/**
	 * Change the type of sound associated with this event.
	 * @param category The new category this sound should play in.
	 */
	public void setCategory(SoundSource category) { this.category = category; }
	
	/**
	 * Change the volume of this sound.
	 * @param value The new volume.
	 */
	public void setVolume(float value) { this.volume = value; }
	
	/**
	 * Change the pitch of this sound.
	 * @param value The new pitch.
	 */
	public void setPitch(float value) { this.pitch = value; }
	
	/**
	 * Set whether or not to cancel this sound and stop it from playing
	 * @param cancel Whether or not this sound should be canceled.
	 */
	public void setCanceled(boolean cancel) { this.cancel = cancel; }
}