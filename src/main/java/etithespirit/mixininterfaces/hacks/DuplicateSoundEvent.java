package etithespirit.mixininterfaces.hacks;

import net.minecraft.util.SoundEvent;

/**
 * Exists as a mirror of SoundEvent that serves the express purpose of NOT being a class identical to SoundEvent. It is used in the custom override to PlaySound
 * that allows the system to not enter a stack overflow scenario, as I re-fire the method with one of these.
 * @author Eti
 *
 */
public class DuplicateSoundEvent extends SoundEvent {
	
	public final SoundEvent original;
	
	public DuplicateSoundEvent(SoundEvent base) {
		super(base.name);
		original = base;
	}

}
