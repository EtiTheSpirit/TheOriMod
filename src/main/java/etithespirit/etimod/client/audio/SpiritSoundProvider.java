package etithespirit.etimod.client.audio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import etithespirit.etimod.client.audio.variation.BreathLevel;
import etithespirit.etimod.client.audio.variation.DamageLevel;
import etithespirit.etimod.client.audio.variation.SpecialAttackType;
import etithespirit.etimod.data.EtiModDamageSource;
import etithespirit.etimod.exception.ArgumentNullException;
import etithespirit.etimod.registry.SoundRegistry;
import etithespirit.etimod.util.blockmtl.BlockToMaterialBinding;
import etithespirit.etimod.util.blockmtl.SpiritMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Provides methods to acquire context specific sounds for spirits walking and falling.
 * @author Eti
 *
 */
public class SpiritSoundProvider {
	
	/**
	 * Returns the BlockPos of the block the entity is standing on by returning the BlockPos closest to (posX, posY-0.2, posZ)
	 * @param entity
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static BlockPos getBlockOnPos(@Nonnull Entity entity) {
		if (entity == null) throw new ArgumentNullException("entity");
		
		int x = MathHelper.floor(entity.getPosX());
        int y = MathHelper.floor(entity.getPosY() - 0.2);
        int z = MathHelper.floor(entity.getPosZ());
        return new BlockPos(x, y, z);
	}
	
	/**
	 * Returns the BlockPos of the block the entity is standing in by returning the BlockPos closest to (posX, posY+0.2, posZ)
	 * @param entity
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static BlockPos getBlockInPos(@Nonnull Entity entity) {
		if (entity == null) throw new ArgumentNullException("entity");
		
		int x = MathHelper.floor(entity.getPosX());
        int y = MathHelper.floor(entity.getPosY() + 0.2);
        int z = MathHelper.floor(entity.getPosZ());
        return new BlockPos(x, y, z);
	}

	/**
     * @return the block the entity is standing on.
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
     */
    public static BlockState getBlockOn(@Nonnull Entity entity) {
    	if (entity == null) throw new ArgumentNullException("entity");
    	
        BlockState blockState = entity.getEntityWorld().getBlockState(getBlockOnPos(entity));
        return blockState;
    }
    
    /**
     * @return the block the entity is standing in.
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
     */
    public static BlockState getBlockIn(@Nonnull Entity entity) {
    	if (entity == null) throw new ArgumentNullException("entity");
    	
        BlockState blockState = entity.getEntityWorld().getBlockState(getBlockInPos(entity));
        return blockState;
    }
	
    /**
     * Returns a unique sound for falling onto a given block as a spirit.
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
     */
    @Deprecated
    public static SoundEvent getSpiritFallSound(float height, @Nonnull LivingEntity entity, @Nullable SoundEvent vanilla) {
    	if (entity == null) throw new ArgumentNullException("entity");
    	
		
    	SpiritMaterial spiritMtl = BlockToMaterialBinding.getMaterialFor(entity, getBlockOnPos(entity), getBlockInPos(entity));
    	if (spiritMtl.useVanillaInstead) return vanilla;
    	SoundEvent spiritSound = spiritMtl.getSound(true);
    	return spiritSound != null ? spiritSound : vanilla;
    }
    
    
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
    	
    	SpiritMaterial spiritMtl = BlockToMaterialBinding.getMaterialFor(entity, on, in);
    	if (spiritMtl.useVanillaInstead) return vanilla;
    	SoundEvent spiritSound = spiritMtl.getSound(false);
    	return spiritSound != null ? spiritSound : vanilla;
    }
    
    /**
     * Returns the spirit damage sound.
     * @param damageType
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
     */
    public static SoundEvent getSpiritHurtSound(@Nonnull DamageSource damageType) {
    	if (damageType == null) throw new ArgumentNullException("damageType");
    	
    	return SoundRegistry.get("entity.spirit.hurt");
    }
    
    /**
     * Returns a death sound that is contextual to the given damage source.
     * @param damageType
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
     */
    public static SoundEvent getSpiritDeathSound(@Nonnull DamageSource damageType) {
    	if (damageType == null) throw new ArgumentNullException("damageType");
    	
    	if (damageType != null) {
    		if (damageType.isFireDamage() || damageType == DamageSource.DRAGON_BREATH || damageType == DamageSource.LIGHTNING_BOLT)
    		{
    			return SoundRegistry.get("entity.spirit.death.burn");
    		} else if (damageType == DamageSource.DROWN) {
    			return SoundRegistry.get("entity.spirit.death.drown");
    		} else if (damageType == EtiModDamageSource.DECAY) {
    			return SoundRegistry.get("entity.spirit.death.decay");
    		}
    	}
    	return SoundRegistry.get("entity.spirit.death.generic");
    }
    
    /**
     * Returns a sound for dashing. willImpactWallClosely should be true if a wall that will block the dash is within a short distance (around 1.5 blocks) of the player's direction.
     * @param willImpactWallClosely
     */
    public static SoundEvent getSpiritDashSound(boolean willImpactWallClosely) {
    	if (willImpactWallClosely) {
    		return SoundRegistry.get("entity.spirit.dash.impactwall");
    	}
    	return SoundRegistry.get("entity.spirit.dash");
    }
    
    /**
     * Returns a swimming sound for spirits.
     * @param belowWater
     */
    public static SoundEvent getSpiritSwimSound(boolean belowWater) {
    	if (belowWater) return SoundRegistry.get("entity.spirit.aquatic.swim");
    	return SoundRegistry.get("entity.spirit.aquatic.swim_above");
    }
    
    /**
     * Returns a splashing sound for when a spirit falls into water.
     * @param isBigSplash
     */
    public static SoundEvent getSpiritSplashSound(boolean isBigSplash) {
    	// if (isBig) return SoundRegistry.get("entity.spirit.fall.water");
    	// return SoundRegistry.get("entity.spirit.fall.shallow_water");
    	return SoundRegistry.get("nullsound"); // temp
    }
    
    /**
     * Returns a sound based on the amount of times the player has jumped. Expected to be 1, 2, or 3. Any value outside of that range will return a silent sound.
     * @param numberOfJumps
     */
    public static SoundEvent getSpiritJumpSound(int numberOfJumps) {
    	if (numberOfJumps == 1) {
    		return SoundRegistry.get("entity.spirit.jump.single");
    	} else if (numberOfJumps == 2) {
    		return SoundRegistry.get("entity.spirit.jump.double");
    	} else if (numberOfJumps == 3) {
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
     * @param level
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
     * @param type
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
     * @param level
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
}
