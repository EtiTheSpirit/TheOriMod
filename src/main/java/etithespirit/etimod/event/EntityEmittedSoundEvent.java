package etithespirit.etimod.event;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;

/**
 * An implementation of <a href="https://github.com/MinecraftForge/MinecraftForge/pull/7491">Forge PR #7941</a>
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public class EntityEmittedSoundEvent {
	
	private final Entity entity;
	private final SoundEvent originalSound;
	private final SoundCategory originalCategory;
	private final float originalVolume;
	private final float originalPitch;
	private final Vector3d originalPosition;
	
	private SoundEvent sound;
	private SoundCategory category;
	private float volume;
	private float pitch;
	private Vector3d position;
	private boolean cancel;

	public EntityEmittedSoundEvent(Entity source, Vector3d position, SoundEvent sound, SoundCategory category, float volume, float pitch)
	{
		this.originalSound = sound;
		this.originalCategory = category;
		this.originalVolume = volume;
		this.originalPitch = pitch;
		this.originalPosition = position;
		
		this.entity = source;
		this.sound = sound;
		this.category = category;
		this.position = position;
		this.volume = volume;
		this.pitch = pitch;
		this.cancel = false;
	}
	
	/** Returns whether or not some data was modified in this event, which determines if it should override vanilla sound playing behaviors. The cancelation state does not affect this return value. */
	public boolean wasModified() {
		return !sound.equals(originalSound) ||
			!category.equals(originalCategory) ||
			!(volume == originalVolume) ||
			!(pitch == originalPitch) ||
			!position.equals(originalPosition);
	}

	/** Returns the sound that should be played. */
	public SoundEvent getSound() { return this.sound; }
	
	/** Returns the category of the sound that should be played. */
	public SoundCategory getCategory() { return this.category; }
	
	/** Returns the current override volume for this sound. */
	public float getVolume() { return this.volume; }
	
	/** Returns the current override pitch for this sound. */
	public float getPitch() { return this.pitch; }
	
	/** Returns the position that this sound will play at. */
	@Deprecated public Vector3d getPosition() { return this.position; }
	
	/** Returns the entity that is responsible for playing this sound. */
	public Entity getEntity() { return this.entity; }
	
	/** Returns true if this sound should no longer play. */
	public boolean isCanceled() { return this.cancel; }
	
	
	/** Returns the sound that this event had when the event was first constructed. */
	public SoundEvent getDefaultSound() { return this.originalSound; }
	
	/** Returns the sound category that this sound started with when the event was constructed. */
	public SoundCategory getDefaultCategory() { return this.originalCategory; }
	
	/** Returns the volume that this sound started with when the event was constructed. */
	public float getDefaultVolume() { return this.originalVolume; }
	
	/** Returns the pitch that this sound started with when the event was constructed. */
	public float getDefaultPitch() { return this.originalPitch; }
	
	/** Returns the original location that this sound was going to emit at when the event was constructed. */
	@Deprecated public Vector3d getOriginalPosition() { return this.originalPosition; }
	
	public void setSound(SoundEvent value) { this.sound = value; }
	public void setCategory(SoundCategory category) { this.category = category; }
	public void setVolume(float value) { this.volume = value; }
	public void setPitch(float value) { this.pitch = value; }
	@Deprecated public void setPosition(Vector3d value) { this.position = value; }
	public void setCanceled(boolean cancel) { this.cancel = cancel; }
}
