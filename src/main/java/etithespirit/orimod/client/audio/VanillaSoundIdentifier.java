package etithespirit.orimod.client.audio;

import etithespirit.exception.ArgumentNullException;
import joptsimple.util.RegexMatcher;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Identifies sounds played by vanilla and loosely categorizes them. The intent of this action is
 * for vanilla sounds to be replacable by sounds oriented towards Spirit players.
 * @author Eti
 *
 */
public class VanillaSoundIdentifier {
	
	private static final Map<String, CustomSoundType> KNOWN_TYPE_BINDINGS = new HashMap<>();
	
	/**
	 * Returns the associated sound type for the given sound event name. If this is SoundType.NO_OVERWRITE, then it should not be overridden.
	 * @param soundEventName The vanilla SoundEvent
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static CustomSoundType getTypeOf(@Nonnull String soundEventName) {
		if (soundEventName == null) throw new ArgumentNullException("soundEventName");
		
		if (KNOWN_TYPE_BINDINGS.containsKey(soundEventName)) {
			return KNOWN_TYPE_BINDINGS.get(soundEventName);
		}
		
		if (soundEventName.matches("block\\..+\\.step")) {
			KNOWN_TYPE_BINDINGS.put(soundEventName, CustomSoundType.STEP);
			return CustomSoundType.STEP;
		}
		
		if (soundEventName.matches("block\\..+\\.fall")) {
			KNOWN_TYPE_BINDINGS.put(soundEventName, CustomSoundType.FALL);
			return CustomSoundType.FALL;
		}
		
		return CustomSoundType.NO_OVERWRITE;
	}
	
	private static void registerAs(@Nonnull String soundEventName, @Nonnull CustomSoundType type) {
		if (soundEventName == null) throw new ArgumentNullException("soundEventName");
		if (type == null) throw new ArgumentNullException("type");
		
		KNOWN_TYPE_BINDINGS.put(soundEventName, type);
	}
	
	static {
		/*
		// STEP
		registerAs("block.anvil.step", CustomSoundType.STEP);
		registerAs("block.bamboo.step", CustomSoundType.STEP);
		registerAs("block.glass.step", CustomSoundType.STEP);
		registerAs("block.grass.step", CustomSoundType.STEP);
		registerAs("block.wet_grass.step", CustomSoundType.STEP);
		registerAs("block.coral_block.step", CustomSoundType.STEP);
		registerAs("block.gravel.step", CustomSoundType.STEP);
		registerAs("block.honey_block.step", CustomSoundType.STEP);
		registerAs("block.ladder.step", CustomSoundType.STEP);
		registerAs("block.lantern.step", CustomSoundType.STEP);
		registerAs("block.lodestone.step", CustomSoundType.STEP);
		registerAs("block.chain.step", CustomSoundType.STEP);
		registerAs("block.metal.step", CustomSoundType.STEP);
		registerAs("block.sand.step", CustomSoundType.STEP);
		registerAs("block.scaffolding.step", CustomSoundType.STEP);
		registerAs("block.slime_block.step", CustomSoundType.STEP);
		registerAs("block.snow.step", CustomSoundType.STEP);
		registerAs("block.soul_sand.step", CustomSoundType.STEP);
		registerAs("block.soul_soil.step", CustomSoundType.STEP);
		registerAs("block.nylium.step", CustomSoundType.STEP);
		registerAs("block.roots.step", CustomSoundType.STEP);
		registerAs("block.fungus.step", CustomSoundType.STEP);
		registerAs("block.stem.step", CustomSoundType.STEP);
		registerAs("block.vine.step", CustomSoundType.STEP);
		registerAs("block.weeping_vines.step", CustomSoundType.STEP);
		registerAs("block.shroomlight.step", CustomSoundType.STEP);
		registerAs("block.basalt.step", CustomSoundType.STEP);
		registerAs("block.bone_block.step", CustomSoundType.STEP);
		registerAs("block.nether_bricks.step", CustomSoundType.STEP);
		registerAs("block.netherrack.step", CustomSoundType.STEP);
		registerAs("block.nether_sprouts.step", CustomSoundType.STEP);
		registerAs("block.wart_block.step", CustomSoundType.STEP);
		registerAs("block.nether_ore.step", CustomSoundType.STEP);
		registerAs("block.nether_gold_ore.step", CustomSoundType.STEP);
		registerAs("block.netherite_block.step", CustomSoundType.STEP);
		registerAs("block.ancient_debris.step", CustomSoundType.STEP);
		registerAs("block.gilded_blackstone.step", CustomSoundType.STEP);
		registerAs("block.stone.step", CustomSoundType.STEP);
		registerAs("block.wood.step", CustomSoundType.STEP);
		registerAs("block.wool.step", CustomSoundType.STEP);
		
		// BLOCK FALL
		registerAs("block.anvil.fall", CustomSoundType.FALL);
		registerAs("block.bamboo.fall", CustomSoundType.FALL);
		registerAs("block.glass.fall", CustomSoundType.FALL);
		registerAs("block.grass.fall", CustomSoundType.FALL);
		registerAs("block.wet_grass.fall", CustomSoundType.FALL);
		registerAs("block.coral_block.fall", CustomSoundType.FALL);
		registerAs("block.gravel.fall", CustomSoundType.FALL);
		registerAs("block.honey_block.fall", CustomSoundType.FALL);
		registerAs("block.ladder.fall", CustomSoundType.FALL);
		registerAs("block.lantern.fall", CustomSoundType.FALL);
		registerAs("block.lodestone.fall", CustomSoundType.FALL);
		registerAs("block.chain.fall", CustomSoundType.FALL);
		registerAs("block.metal.fall", CustomSoundType.FALL);
		registerAs("block.sand.fall", CustomSoundType.FALL);
		registerAs("block.scaffolding.fall", CustomSoundType.FALL);
		registerAs("block.slime_block.fall", CustomSoundType.FALL);
		registerAs("block.snow.fall", CustomSoundType.FALL);
		registerAs("block.soul_sand.fall", CustomSoundType.FALL);
		registerAs("block.soul_soil.fall", CustomSoundType.FALL);
		registerAs("block.nylium.fall", CustomSoundType.FALL);
		registerAs("block.roots.fall", CustomSoundType.FALL);
		registerAs("block.fungus.fall", CustomSoundType.FALL);
		registerAs("block.stem.fall", CustomSoundType.FALL);
		registerAs("block.weeping_vines.fall", CustomSoundType.FALL);
		registerAs("block.shroomlight.fall", CustomSoundType.FALL);
		registerAs("block.basalt.fall", CustomSoundType.FALL);
		registerAs("block.bone_block.fall", CustomSoundType.FALL);
		registerAs("block.nether_bricks.fall", CustomSoundType.FALL);
		registerAs("block.netherrack.fall", CustomSoundType.FALL);
		registerAs("block.nether_sprouts.fall", CustomSoundType.FALL);
		registerAs("block.wart_block.fall", CustomSoundType.FALL);
		registerAs("block.nether_ore.fall", CustomSoundType.FALL);
		registerAs("block.nether_gold_ore.fall", CustomSoundType.FALL);
		registerAs("block.netherite_block.fall", CustomSoundType.FALL);
		registerAs("block.ancient_debris.fall", CustomSoundType.FALL);
		registerAs("block.gilded_blackstone.fall", CustomSoundType.FALL);
		registerAs("block.stone.fall", CustomSoundType.FALL);
		registerAs("block.wood.fall", CustomSoundType.FALL);
		registerAs("block.wool.fall", CustomSoundType.FALL);
		*/
		
		// DEALING DAMAGE
		registerAs("entity.player.attack.crit", CustomSoundType.DAMAGE_CRITICAL_HIT);
		registerAs("entity.player.attack.knockback", CustomSoundType.DAMAGE_KNOCKBACK);
		registerAs("entity.player.attack.nodamage", CustomSoundType.DAMAGE_INEFFECTIVE);
		registerAs("entity.player.attack.strong", CustomSoundType.DAMAGE_STRONG);
		registerAs("emtity.player.attack.sweep", CustomSoundType.DAMAGE_SWEEP);
		
		// TAKING DAMAGE
		registerAs("entity.player.big_fall", CustomSoundType.OMIT);
		registerAs("entity.player.small_fall", CustomSoundType.OMIT);
		
		registerAs("entity.player.hurt", CustomSoundType.HURT);
		registerAs("entity.player.hurt_drown", CustomSoundType.HURT);
		registerAs("entity.player.hurt_on_fire", CustomSoundType.HURT);
		registerAs("entity.player.hurt_sweet_berry_bush", CustomSoundType.HURT);
		
		// AQUATIC
		registerAs("entity.player.splash", CustomSoundType.SPLASH);
		registerAs("entity.player.splash.high_speed", CustomSoundType.SPLASH_BIG);
		registerAs("entity.player.swim", CustomSoundType.SWIM);
		
		//RegisterAs("ambient.underwater.enter", CustomSoundType.OMIT);
		//RegisterAs("ambient.underwater.exit", CustomSoundType.OMIT);
		
		// ETC.
		registerAs("entity.player.breath", CustomSoundType.OMIT);
		
		// TODO: Ori level up sound?
		registerAs("entity.player.levelup", CustomSoundType.NO_OVERWRITE);
		
		
	}
	
	/**
	 * A loose categorization method of sounds based on how overrides are applied.
	 * @author Eti
	 *
	 */
	public enum CustomSoundType {
		/** This sound has not been bound to a specific type and will use the vanilla sound instead. */
		NO_OVERWRITE,
		
		/** This sound should be omitted (not played). */
		OMIT,
		
		/** This sound corresponds to walking on something. */
		STEP,
		
		/** This sound corresponds to falling onto something. */
		FALL,
		
		/** This sound corresponds to swimming. */
		SWIM,
		
		/** This sound corresponds to splashing. */
		SPLASH,
		
		/** This sound corresponds to splashing, but if you're fat. */
		SPLASH_BIG,
		
		/** This sound corresponds to taking damage. */
		HURT,
		
		/** This sound corresponds to dying. */
		DEATH,
		
		/** Take a deep breath. */
		BREATH,
		
		/** Damage that dealt knockback. */
		DAMAGE_KNOCKBACK,
		
		/** Damage that was completely ineffective against the target. */
		DAMAGE_INEFFECTIVE,
		
		/** Damage that was not very effective against the target. */
		DAMAGE_WEAK,
		
		/** Damage that was more effective than usual against the target. */
		DAMAGE_STRONG,
		
		/** KUHRITIKAL SHIT. HA. */
		DAMAGE_CRITICAL_HIT,
		
		/** Sweeping damage. */
		DAMAGE_SWEEP,
	}
	
}
