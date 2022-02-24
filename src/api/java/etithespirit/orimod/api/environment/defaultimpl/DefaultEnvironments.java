package etithespirit.orimod.api.environment.defaultimpl;

import etithespirit.orimod.api.energy.FluxBehavior;
import etithespirit.orimod.api.util.valuetypes.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Function;

/**
 * All default biome types.
 */
public final class DefaultEnvironments {
	
	/**
	 * This can be used to create an instance for environments where Decay is the dominant force by far. These environments are extremely deadly as a result. It is only for the most extreme of edge cases. They can cause extreme damage, and wreak havoc on the functions of Light tech.
	 */
	public static final Function<ResourceLocation, AffinityWithFX> VERY_STRONG_DECAY;
	
	/**
	 * This can be used to create an instance for environments where Decay has established itself very well. These environments are dangerous, and are relatively rare. They can cause a lot of damage, and negatively affect performance of Light tech.
	 */
	public static final Function<ResourceLocation, AffinityWithFX> STRONG_DECAY;
	
	/**
	 * This can be used to create an instance for environments where Decay has begun to notably establish itself. These environments are hazardous.
	 */
	public static final Function<ResourceLocation, AffinityWithFX> DECAY;
	
	/**
	 * This can be used to create an instance for environments where Light is the dominant force by far. These environments are very safe and well shielded from Decay, and actively benefit the functions of Light based technology.
	 */
	public static final Function<ResourceLocation, AffinityWithFX> VERY_STRONG_LIGHT;
	
	/**
	 * This can be used to create an instance for environments where Light is well established. These environments are generally safe and shielded from Decay. I can benefit the functions of Light technology.
	 */
	public static final Function<ResourceLocation, AffinityWithFX> STRONG_LIGHT;
	
	/**
	 * This can be used to create an instance for environments where Light has begun to establish itself. These environments are safer than neutral zones and can slightly benefit Light technology.
	 */
	public static final Function<ResourceLocation, AffinityWithFX> LIGHT;
	
	/**
	 * Sets the Decay effect reference and references this class which causes its static initializer to execute.
	 * @param decayEffect The effect reference.
	 */
	public static void init(MobEffect decayEffect) {
		AffinityWithFX.DECAY_EFFECT_REF.set(decayEffect);
	}
	
	static {
		VERY_STRONG_DECAY = (biome) -> new AffinityWithFX(
			biome,
			new FluxBehavior(new NumberRange(-1, 0.05)),
			0.25,
			0.6,
			0.5,
			Double.POSITIVE_INFINITY,
			0,
			2
		);
		STRONG_DECAY = (biome) -> new AffinityWithFX(
			biome,
			new FluxBehavior(new NumberRange(-0.5, 0.1)),
			0.5,
			0.35,
			0.2,
			2.5,
			0,
			1
		);
		DECAY = (biome) -> new AffinityWithFX(
			biome,
			new FluxBehavior(new NumberRange(-0.25, 0.15)),
			0.75,
			0.6,
			0.5,
			1.5,
			0,
			0
		);
		
		VERY_STRONG_LIGHT = (biome) -> new AffinityWithFX(
			biome,
			new FluxBehavior(new NumberRange(-0.05, 1)),
			1.25,
			-0.2,
			-0.45,
			0,
			Double.POSITIVE_INFINITY,
			0
		);
		STRONG_LIGHT = (biome) -> new AffinityWithFX(
			biome,
			new FluxBehavior(new NumberRange(-0.025, 0.5)),
			1.50,
			-0.5,
			-0.8,
			0,
			2.5,
			0
		);
		LIGHT = (biome) -> new AffinityWithFX(
			biome,
			new FluxBehavior(new NumberRange(-0.00125, 0.25)),
			2,
			-0.8,
			-1,
			0,
			1.5,
			0
		);
	}
	
	private DefaultEnvironments() {}
	
}
