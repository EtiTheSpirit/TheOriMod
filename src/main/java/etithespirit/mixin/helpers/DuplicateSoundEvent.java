package etithespirit.mixin.helpers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class DuplicateSoundEvent extends SoundEvent {
	public DuplicateSoundEvent(ResourceLocation p_11659_) { super(p_11659_); }
	
	public DuplicateSoundEvent(SoundEvent template) { super(template.getLocation()); }
	
}
