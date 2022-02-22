package etithespirit.orimod.common.tile;

import etithespirit.orimod.client.audio.StartLoopEndBlockSound;

/**
 * Represents this tile entity as one that has an ambient sound.
 */
public interface IAmbientSoundEmitter {
	
	/**
	 * @return A reference to the sound that this emits.
	 */
	StartLoopEndBlockSound getSoundInstance();
	
	/**
	 * Causes the sound that this emits to begin, refreshing it as if it has just started for the first time.
	 */
	void startSound();
	
	/**
	 * @return Whether or not the sound should be playing right now.
	 */
	boolean soundShouldBePlaying();
	
}
