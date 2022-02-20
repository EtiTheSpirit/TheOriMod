package etithespirit.orimod.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public final class EffectConstructors {
	
	/**
	 * A simple method of constructing an effect instance from this effect that allows for editing the most common flags.<br/>
	 * Sets duration to 0 and particles to false.
	 * <br/>
	 * For help in the EffectInstance constructor, its parameters are: {@code Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticles, boolean showIcon}
	 *
	 * @return An EffectInstance for this Effect constructed with the given duration, amplifier 0, and no particles.
	 */
	public static MobEffectInstance constructEffect(MobEffect effect, int duration) {
		return constructEffect(effect, duration, 0, false);
	}
	
	/**
	 * A simple method of constructing an effect instance from this effect that allows for editing the most common flags.<br/>
	 * <br/>
	 * For help in the EffectInstance constructor, its parameters are: {@code Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticles, boolean showIcon}
	 *
	 * @return An EffectInstance for this Effect constructed with the given duration and amplifier, and with no particles.
	 */
	public static MobEffectInstance constructEffect(MobEffect effect, int duration, int amplifier) {
		return constructEffect(effect, duration, amplifier, false);
	}
	
	/**
	 * A simple method of constructing an effect instance from this effect that allows for editing the most common flags.<br/>
	 *
	 * For help in the EffectInstance constructor, its parameters are: {@code Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticles, boolean showIcon}
	 *
	 * @return An EffectInstance for this Effect.
	 */
	public static MobEffectInstance constructEffect(MobEffect effect, int duration, int amplifier, boolean particles) {
		return new MobEffectInstance(effect, duration, amplifier, false, particles, true);
	}
	
	/**
	 * Constructs this potion effect with amplifier 0. Whether or not it renders particles is determined by {@code PreferParticles()}. It has a timer duration that is effectively infinite.
	 * @return A new EffectInstance for this potion with amp=0, particles=false, duration=0x7FFFFFFF
	 */
	public static MobEffectInstance constructInfiniteEffect(MobEffect effect) {
		return constructEffect(effect, Integer.MAX_VALUE, 0, false);
	}
	
	/**
	 * Constructs this potion effect with the given amplifier. Whether or not it renders particles is determined by {@code PreferParticles()}. It has a timer duration that is effectively infinite.
	 * @param amplifier The amplifier of the potion.
	 * @return A new EffectInstance for this potion with amp=(amplifier), particles=false, duration=0x7FFFFFFF
	 */
	public static MobEffectInstance constructInfiniteEffect(MobEffect effect, int amplifier) {
		return constructEffect(effect, Integer.MAX_VALUE, amplifier, false);
	}
	
	/**
	 * Constructs this potion effect with the given amplifier, particle state, and a timer duration that is effectively infinite.
	 * @param amplifier The amplifier of the potion.
	 * @param particles Whether or not to render particles.
	 * @return A new EffectInstance for this potion with amp=(amplifier), particles=(particles), duration=0x7FFFFFFF
	 */
	public static MobEffectInstance constructInfiniteEffect(MobEffect effect, int amplifier, boolean particles) {
		return constructEffect(effect, Integer.MAX_VALUE, amplifier, particles);
	}
}
