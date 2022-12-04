package etithespirit.orimod.api.environment.defaultimpl;

import etithespirit.orimod.api.environment.EnvironmentalAffinity;
import etithespirit.orimod.api.spirit.SpiritAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * An affinity extension class that has built in behavior for what is expected of an environmentally affine biome
 */
public class AffinityWithFX extends EnvironmentalAffinity {
	
	/** A pseudorandomizer for doing whatever it is you do with pseudorandomizers when you aren't worshipping RNJesus. */
	protected static final Random RNG = new Random();
	
	/** The chance of causing the Decay effect per tick on a standard player. If negative, it is the chance of curing Decay instead. */
	public final double chanceOfCausingDecayPlayer;
	
	/** The chance of causing the Decay effect per tick on a Spirit player. If negative, it is the chance of curing Decay instead. */
	public final double chanceOfCausingDecaySpirit;
	
	/** The amplifier of the Decay effect once applied. */
	public final int decaySeverity;
	
	/** The chance for Decay to spread in this biome. */
	public final double decaySpreadChanceMultiplier;
	
	/** The chance for Decay to die in this biome (in place of spreading). */
	public final double decayDieChanceMultiplier;
	
	/** A soft reference to the Decay effect. */
	public static final SetOnce<MobEffect> DECAY_EFFECT_REF = new SetOnce<>();
	
	/**
	 * Create a new preset affinity with effects and damage.
	 * @param biome The biome this affects.
	 * @param efficiencyPercentage The efficiency of light devices in this biome.
	 * @param chanceOfCausingDecayPlayer The chance of causing Decay on a normal player. A negative value will switch it to mean the chance of <em>curing</em> Decay instead.
	 * @param chanceOfCausingDecaySpirit The chance of causing Decay on a Spirit. A negative value will switch it to mean the chance of <em>curing</em> Decay instead.
	 * @param decaySpreadChanceMult The multiplier applied to any given Decay block's spread chance.
	 * @param decayDieChanceMult The multiplier applied to any Decay block's death chance.
	 * @param decaySeverity The amplifier of the Decay effect if it is added.
	 */
	public AffinityWithFX(ResourceLocation biome, double efficiencyPercentage, double chanceOfCausingDecayPlayer, double chanceOfCausingDecaySpirit, double decaySpreadChanceMult, double decayDieChanceMult, int decaySeverity) {
		super(biome, efficiencyPercentage);
		this.chanceOfCausingDecayPlayer = chanceOfCausingDecayPlayer;
		this.chanceOfCausingDecaySpirit = chanceOfCausingDecaySpirit;
		this.decaySpreadChanceMultiplier = decaySpreadChanceMult;
		this.decayDieChanceMultiplier = decayDieChanceMult;
		this.decaySeverity = decaySeverity;
	}
	
	@Override
	public void onPlayerTick(Player player) {
		if (!player.level.isClientSide) {
			double appropriateChance = SpiritAccessor.isSpirit(player) ? chanceOfCausingDecaySpirit : chanceOfCausingDecayPlayer;
			MobEffect decay = DECAY_EFFECT_REF.get();
			if (appropriateChance > 0) {
				if (RNG.nextDouble() < appropriateChance) {
					MobEffectInstance instance = player.getEffect(decay);
					MobEffectInstance newInstance;// MobEffect pEffect, int pDuration, int pAmplifier, boolean pAmbient, boolean pVisible, boolean pShowIcon
					if (instance != null) {
						newInstance = new MobEffectInstance(
							// MobEffect pEffect, int pDuration, int pAmplifier, boolean pAmbient, boolean pVisible, boolean pShowIcon
							decay,
							Math.max(instance.getDuration(), 20*30),
							Math.max(instance.getAmplifier(), decaySeverity),
							instance.isVisible(),
							instance.showIcon()
						);
					} else {
						newInstance = new MobEffectInstance(
							// MobEffect pEffect, int pDuration, int pAmplifier, boolean pAmbient, boolean pVisible, boolean pShowIcon
							decay,
							20*30,
							decaySeverity,
							true,
							true
						);
					}
					player.addEffect(newInstance);
				}
			} else if (appropriateChance < 0) {
				if (RNG.nextDouble() < -appropriateChance) {
					player.removeEffect(decay);
				}
			}
		}
	}
	
	@Override
	public void onWorldTick(Level world) { }
	
	/**
	 * A single use storage class.
	 * @param <T> The type of data stored.
	 */
	public static final class SetOnce<T> {
		
		private boolean hasSet = false;
		private T value = null;
		
		/**
		 * Set the value stored within.
		 * @param value The value to store.
		 * @throws IllegalStateException If a value has already been stored.
		 */
		public void set(T value) throws IllegalStateException {
			if (hasSet) throw new IllegalStateException("This value has already been set.");
			this.value = value;
			hasSet = true;
		}
		
		/**
		 * Acquire the value stored within.
		 * @return The value stored within.
		 * @throws NullPointerException If the value has not been set.
		 */
		public T get() throws NullPointerException {
			if (!hasSet) throw new NullPointerException("Cannot access the stored value! It has not been set.");
			return this.value;
		}
		
		/**
		 * Signifies whether or not the value has been set.
		 * @return True if the value has been set, false if it has not.
		 */
		public boolean has() {
			return hasSet;
		}
		
	}
}
