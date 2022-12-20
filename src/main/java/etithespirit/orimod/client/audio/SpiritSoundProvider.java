package etithespirit.orimod.client.audio;


import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.client.audio.variation.BreathLevel;
import etithespirit.orimod.client.audio.variation.DamageLevel;
import etithespirit.orimod.client.audio.variation.SpecialAttackType;
import etithespirit.orimod.combat.ExtendedDamageSource;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.spiritmaterial.BlockToMaterialBinding;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Provides methods to acquire context-specific sounds for spirits performing various actions.
 * @author Eti
 *
 */
public class SpiritSoundProvider {
	
	/**
	 * Returns the BlockPos of the block the entity is standing on.
	 * @param entity The entity that provides the position.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static BlockPos getBlockOnPos(@Nonnull Entity entity) {
		if (entity == null) throw new ArgumentNullException("entity");
		return entity.blockPosition().below();
	}
	
	/**
	 * Returns the BlockPos of the block the entity is standing in.
	 * @param entity The entity that provides the position.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static BlockPos getBlockInPos(@Nonnull Entity entity) {
		if (entity == null) throw new ArgumentNullException("entity");
		return entity.blockPosition();
	}
	
	/*
	 * Returns a unique sound for falling onto a given block as a spirit.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	/*
    @Deprecated
    public static SoundEvent getSpiritFallSound(float height, @Nonnull LivingEntity entity, @Nullable SoundEvent vanilla) {
    	if (entity == null) throw new ArgumentNullException("entity");
    	
		
    	SpiritMaterial spiritMtl = BlockToMaterialBinding.getMaterialFor(entity, getBlockOnPos(entity), getBlockInPos(entity));
    	if (spiritMtl.useVanillaInstead) return vanilla;
    	SoundEvent spiritSound = getSound(spiritMtl,true);
    	return spiritSound != null ? spiritSound : vanilla;
    }
    */
	
	
	/**
	 * Returns a unique sound for stepping on a given block as a spirit.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static SoundEvent getSpiritStepSound(@Nonnull LivingEntity entity, @Nullable SoundEvent vanilla) {
		if (entity == null) throw new ArgumentNullException("entity");
		if (vanilla == null) throw new ArgumentNullException("vanilla");
		
		return getSpiritStepSound(entity, getBlockOnPos(entity), getBlockInPos(entity), vanilla);
	}
	
	/**
	 * Returns a unique sound for stepping on a given block as a spirit.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static SoundEvent getSpiritStepSound(@Nonnull LivingEntity entity, @Nonnull BlockPos on, @Nonnull BlockPos in, @Nullable SoundEvent vanilla) {
		if (on == null) throw new ArgumentNullException("on");
		
		// BUG FIX: Sounds for slabs and other blocks the player sinks into will play the sound of the block beneath it instead of the block they are on.
		if (Mth.frac(entity.getY()) >= 0.0620) {
			// Just under 1/16th of a block, this is the width of a carpet block, the thinnest block.
			on = on.above();
			in = in.above();
		}
		
		SpiritMaterial spiritMtl = BlockToMaterialBinding.getMaterialFor(entity, on, in);
		if (spiritMtl.useVanillaInstead) return vanilla;
		SoundEvent spiritSound = getSound(spiritMtl, false);
		return spiritSound != null ? spiritSound : vanilla;
	}
	
	/**
	 * Returns the spirit damage sound.
	 * @param damageType The type of damage inflicted.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static SoundEvent getSpiritHurtSound(@Nonnull DamageSource damageType) {
		if (damageType == null) throw new ArgumentNullException("damageType");
		
		return SoundRegistry.get("entity.spirit.hurt");
	}
	
	/**
	 * Returns a death sound that is contextual to the given damage source.
	 * @param damageType The type of damage inflicted.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static SoundEvent getSpiritDeathSound(@Nonnull DamageSource damageType) {
		if (damageType == null) throw new ArgumentNullException("damageType");
		
		if (damageType != null) {
			if (damageType.isFire() || damageType == DamageSource.DRAGON_BREATH || damageType == DamageSource.LIGHTNING_BOLT)
			{
				return SoundRegistry.get("entity.spirit.death.burn");
			} else if (damageType == DamageSource.DROWN) {
				return SoundRegistry.get("entity.spirit.death.drown");
			} else if (damageType == ExtendedDamageSource.DECAY) {
				return SoundRegistry.get("entity.spirit.death.decay");
			}
		}
		return SoundRegistry.get("entity.spirit.death.generic");
	}
	
	/**
	 * Returns a sound for dashing. willImpactWallClosely should be true if a wall that will block the dash is within a short distance (around 1.5 blocks) of the player's direction.
	 * @param willImpactWallClosely Whether or not the player will impact a wall immediately after dashing.
	 */
	public static SoundEvent getSpiritDashSound(boolean willImpactWallClosely) {
		if (willImpactWallClosely) {
			return SoundRegistry.get("entity.spirit.dash.impactwall");
		}
		return SoundRegistry.get("entity.spirit.dash");
	}
	
	/**
	 * Returns a swimming sound for spirits.
	 * @param belowWater Whether or not the player is swimming below water vs. treading above its surface.
	 */
	public static SoundEvent getSpiritSwimSound(boolean belowWater) {
		if (belowWater) return SoundRegistry.get("entity.spirit.aquatic.swim");
		return SoundRegistry.get("entity.spirit.aquatic.swim_above");
	}
	
	/**
	 * Returns a splashing sound for when a spirit falls into water.
	 * @param isBigSplash Whether or not the splash is big.
	 */
	@SuppressWarnings("unused")
	public static SoundEvent getSpiritSplashSound(boolean isBigSplash) {
		// if (isBig) return SoundRegistry.get("entity.spirit.fall.water");
		// return SoundRegistry.get("entity.spirit.fall.shallow_water");
		return SoundRegistry.get("nullsound"); // temp
	}
	
	/**
	 * Returns a sound based on the amount of times the player has jumped. Expected to be 1, 2, or 3. Any value outside of that range will return a silent sound.
	 * @param numberOfJumpsIncludingLand The amount of jumps that have been performed, counting the initial jump off of the ground.
	 */
	public static SoundEvent getSpiritJumpSound(int numberOfJumpsIncludingLand) {
		if (numberOfJumpsIncludingLand == 1) {
			return SoundRegistry.get("entity.spirit.jump.single");
		} else if (numberOfJumpsIncludingLand == 2) {
			return SoundRegistry.get("entity.spirit.jump.double");
		} else if (numberOfJumpsIncludingLand == 3) {
			return SoundRegistry.get("entity.spirit.jump.triple");
		}
		return SoundRegistry.get("nullsound");
	}
	
	/**
	 * Returns a sound for wall jumping. As of writing, this does not respect the block's material.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static SoundEvent getSpiritWallJumpSound(@Nonnull BlockPos onBlock) {
		if (onBlock == null) throw new ArgumentNullException("onBlock");
		
		return SoundRegistry.get("entity.spirit.jump.wall");
	}
	
	/**
	 * Returns a sound associated with a hard-light shield being impacted.
	 * @param broke Whether or not the shield broke as a result of this impact.
	 */
	public static SoundEvent getSpiritShieldImpactSound(boolean broke) {
		if (broke) return SoundRegistry.get("item.light_shield.break");
		return SoundRegistry.get("item.light_shield.impact");
	}
	
	/**
	 * Returns a sound associated with attacking an entity with the given damage level.
	 * @param level The damage level.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static SoundEvent getSpiritAttackSound(@Nonnull DamageLevel level) {
		if (level == null) throw new ArgumentNullException("level");
		
		if (level == DamageLevel.INEFFECTIVE) return SoundRegistry.get("entity.spirit.attack.nothing");
		if (level == DamageLevel.WEAK) return SoundRegistry.get("entity.spirit.attack.weak");
		if (level == DamageLevel.STANDARD) return SoundRegistry.get("entity.spirit.attack.standard");
		if (level == DamageLevel.STRONG) return SoundRegistry.get("entity.spirit.attack.strong");
		if (level == DamageLevel.CRITICAL) return SoundRegistry.get("entity.spirit.attack.crit");
		return null;
	}
	
	/**
	 * Returns a sound associated with attacking an entity alongside the given damage effect.
	 * @param type The damage type.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static SoundEvent getSpiritAttackTypeSound(@Nonnull SpecialAttackType type) {
		if (type == null) throw new ArgumentNullException("type");
		
		if (type == SpecialAttackType.KNOCKBACK) return SoundRegistry.get("entity.spirit.attack.knockback");
		if (type == SpecialAttackType.SWEEP) return SoundRegistry.get("entity.spirit.attack.sweep");
		return null;
	}
	
	/**
	 * Returns the breath sound corresponding to the depth passed in.
	 * @param level The urgency of the breath.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static SoundEvent getSpiritBreathSound(@Nonnull BreathLevel level) {
		if (level == null) throw new ArgumentNullException("level");
		
		if (level == BreathLevel.BIG) {
			return SoundRegistry.get("entity.spirit.aquatic.breath.big");
		} else if (level == BreathLevel.MEDIUM) {
			return SoundRegistry.get("entity.spirit.aquatic.breath.medium");
		}
		return SoundRegistry.get("entity.spirit.aquatic.breath.little");
	}
	
	private static SoundEvent getSound(SpiritMaterial mtl, boolean isFallingOn) {
		String key;
		if (isFallingOn && mtl.fallSoundKey != null && !Objects.equals(mtl.fallSoundKey, "nullsound")) key = mtl.fallSoundKey;
		else key = mtl.stepSoundKey;
		
		if (key == null) key = "nullsound";
		return SoundRegistry.get(key);
	}
}
