package etithespirit.mixininterfaces;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;

/**
 * Represents an entity that can play a sound (effectively all entities).<br/>
 * This is used for Mixin compatibility, namely in that it provides all of the necessary methods via duck typing.
 * @author Eti
 *
 */
@Deprecated
public interface ISoundPlayingEntity {
	
	/** Mirrors {@link net.minecraft.entity.Entity#playSound}. */
	public void playSound(SoundEvent soundIn, float volume, float pitch);
	
	/**
	 * Provides a copy of the {@link net.minecraft.entity.Entity} this represents. Using {@code self()} is effectively identical to using {@code this} if this were an entity (which through mixins, is possible).
	 * @return
	 */
	public default Entity self() {
		return (Entity)this;
	}
	
}
