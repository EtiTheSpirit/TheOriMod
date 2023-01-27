package etithespirit.mixin.helpers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Literally just SoundEvent as a different class.
 */
@Deprecated(forRemoval = true)
public class DuplicateSoundEvent extends SoundEvent {
	
	private final ResourceLocation location;
	private final float range;
	private final boolean newSystem;
	public final boolean isDuplicate;
	
	/**
	 * Create a new instance from an existing SoundEvent
	 * @param template The existing event.
	 */
	public DuplicateSoundEvent(SoundEvent template) {
		super(template.getLocation());
		this.location = template.getLocation();
		this.range = template.getRange(Float.POSITIVE_INFINITY); // If this uses the new system, this will return a real number that has nothing to do with the input arg.
		this.newSystem = Float.isFinite(this.range); // However, because the old system multiplies ranges >1 by 16f, range will be set to infinity because inf*16 = inf.
		// A finite value returned means it uses the new system, and an infinite value means it uses the old system.
		this.isDuplicate = true;
	}
	
	/**
	 * Create a new instance from an existing SoundEvent
	 * @param template The existing event.
	 */
	public DuplicateSoundEvent(DuplicateSoundEvent template) {
		super(template.location);
		this.location = template.location;
		this.range = template.range;
		this.newSystem = template.newSystem;
		this.isDuplicate = true;
	}
	
	public DuplicateSoundEvent(ResourceLocation id) {
		super(id);
		this.location = id;
		this.range = 16;
		this.newSystem = false;
		this.isDuplicate = false;
	}
	
	public DuplicateSoundEvent(ResourceLocation id, float range) {
		super(id, range);
		this.location = id;
		this.range = range;
		this.newSystem = true;
		this.isDuplicate = false;
	}
	
	@Override
	public ResourceLocation getLocation() {
		return this.location;
	}
	
	@Override
	public float getRange(float valuePercent) {
		if (this.newSystem) {
			return this.range;
		} else {
			return valuePercent > 1.0F ? 16.0F * valuePercent : 16.0F;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof SoundEvent sound) {
			boolean hasSameId = sound.getLocation().equals(location);
			if (!hasSameId) return false;
			
			boolean otherIsNewSystem = Float.isFinite(sound.getRange(Float.POSITIVE_INFINITY));
			if (newSystem != otherIsNewSystem) return false;
			
			// Compare the ranges if they use the new system (the input param to getRange is meaningless in this case)
			// OR if they *do not* use the new system, from which the range field is completely ignored.
			return (!newSystem) || (range == sound.getRange(0));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return location.hashCode(); // This is important because the registry uses a BiHashmap
	}
}
