package etithespirit.orimod.event;


import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * An implementation of <a href="https://github.com/MinecraftForge/MinecraftForge/pull/7491">Forge PR #7941</a>
 * @author Eti
 *
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
	
	/** Returns whether or not some data was modified in this event, which determines if it should override vanilla sound playing behaviors. The cancelation state does not affect this return value. */
	public boolean wasModified() {
		return !sound.equals(originalSound) ||
			!category.equals(originalCategory) ||
			!(volume == originalVolume) ||
			!(pitch == originalPitch);
	}
	
	public Vec3 getPosition() { return this.originalPosition; }
	
	/** Returns the sound that should be played. */
	public SoundEvent getSound() { return this.sound; }
	
	/** Returns the category of the sound that should be played. */
	public SoundSource getCategory() { return this.category; }
	
	/** Returns the current override volume for this sound. */
	public float getVolume() { return this.volume; }
	
	/** Returns the current override pitch for this sound. */
	public float getPitch() { return this.pitch; }
	
	/** Returns the entity that is responsible for playing this sound. */
	public Entity getEntity() { return this.entity; }
	
	/** Returns true if this sound should no longer play. */
	public boolean isCanceled() { return this.cancel; }
	
	
	/** Returns the sound that this event had when the event was first constructed. */
	public SoundEvent getDefaultSound() { return this.originalSound; }
	
	/** Returns the sound category that this sound started with when the event was constructed. */
	public SoundSource getDefaultCategory() { return this.originalCategory; }
	
	/** Returns the volume that this sound started with when the event was constructed. */
	public float getDefaultVolume() { return this.originalVolume; }
	
	/** Returns the pitch that this sound started with when the event was constructed. */
	public float getDefaultPitch() { return this.originalPitch; }
	
	public void setSound(SoundEvent value) { this.sound = value; }
	public void setCategory(SoundSource category) { this.category = category; }
	public void setVolume(float value) { this.volume = value; }
	public void setPitch(float value) { this.pitch = value; }
	public void setCanceled(boolean cancel) { this.cancel = cancel; }
}
