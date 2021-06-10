package etithespirit.mixininterfaces.hacks;

import net.minecraft.util.SoundEvent;

/**
 * Exists as a mirror of SoundEvent that serves the express purpose of NOT being a class identical to SoundEvent.
 * It is used in the custom override to PlaySound, and allows the system to not enter a stack overflow scenario.
 * Said stack overflow would be caused because I re-fire the method after modifying the sound via a custom event impl.
 * Using this object class allows the system to know that I've already performed my edits, and abort before trying to rerun that event.
 * @author Eti
 *
 */
public class DuplicateSoundEvent extends SoundEvent {
	
	// TODO: Remove this? Do / will I ever use it?
	public final SoundEvent original;
	
	// Basically just wrap the SoundEvent. Expose "original" for future-proofing for now, but it's kinda useless.
	public DuplicateSoundEvent(SoundEvent base) {
		super(base.location);
		original = base;
	}

}
