package etithespirit.mixin.helpers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Literally just SoundEvent as a different class.
 */
public class DuplicateSoundEvent extends SoundEvent {

	/**
	 * Create a new instance from an existing SoundEvent
	 * @param template The existing event.
	 */
	public DuplicateSoundEvent(SoundEvent template) { super(template.getLocation()); }
	
}
