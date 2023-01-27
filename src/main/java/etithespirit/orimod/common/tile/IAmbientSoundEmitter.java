package etithespirit.orimod.common.tile;

import etithespirit.orimod.client.audio.LightTechLooper;

import java.util.Optional;

/**
 * Represents this tile entity as one that has an ambient sound that loops.
 */
public interface IAmbientSoundEmitter {
	
	/**
	 * Returns a reference to the sound(s) that this emits. This should be cached on creation of the Block Entity,
	 * and then returned here. Do not create a new instance when this is called.
	 * @return A reference to the sound that this emits.
	 */
	Optional<LightTechLooper> getSoundInstance();
	
	/**
	 * Whether or not the system believes the sound should be playing right now.
	 * @return Whether or not the sound should be playing right now.
	 */
	boolean soundShouldBePlaying();
	
}
