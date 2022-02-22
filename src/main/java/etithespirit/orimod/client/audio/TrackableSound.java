package etithespirit.orimod.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class TrackableSound extends AbstractTickableSoundInstance {
	
	private boolean stopAudio = false;
	private SoundEvent evt;
	private SoundSource cat;
	
	public TrackableSound(SoundEvent sound, SoundSource category) {
		super(sound, category);
	}
	
	/**
	 * Clones this instance so that it can be played again. Stops this instance if it is not stopped already.
	 * @param inheritVolume If true, the volume of this sound will be applied to the new one.
	 * @param inheritPitch If true, the pitch of this sound will be applied to the new one.
	 * @return A new instance of {@link TrackableSound} with identical properties
	 */
	public TrackableSound resetGetNew(boolean inheritVolume, boolean inheritPitch) {
		stopAudio();
		TrackableSound newSound = new TrackableSound(evt, cat);
		if (inheritVolume) newSound.volume = this.volume;
		if (inheritPitch) newSound.pitch = this.pitch;
		return newSound;
	}
	
	@Override
	public void tick() { }
	
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	@Override
	public boolean isStopped() {
		return stopAudio;
	}
	
	public void stopAudio() {
		stopAudio = true;
		super.stop();
	}
	
	@Override
	public boolean canStartSilent() {
		return true;
	}
	
	@Override
	public boolean canPlaySound() {
		return !stopAudio;
	}
}
