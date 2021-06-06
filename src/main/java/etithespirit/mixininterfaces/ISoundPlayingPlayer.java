package etithespirit.mixininterfaces;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

/**
 * An extension to {@link etithespirit.mixininterfaces.ISoundPlayingEntity} that exists for the implementation of {@link net.minecraft.entity.player.PlayerEntity#playSound(SoundEvent, SoundCategory, float, float)}
 * @author Eti
 *
 */
@Deprecated
public interface ISoundPlayingPlayer extends ISoundPlayingEntity {

	
	/** Mirrors playerEntity.playSound(SoundEvent, SoundCategory, float, float) */
	public void playSound(SoundEvent sound, SoundCategory cat, float volume, float pitch);
	
}
