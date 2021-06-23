package etithespirit.mixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import etithespirit.etimod.info.spirit.SpiritData;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import etithespirit.etimod.client.audio.SpiritSoundProvider;
import etithespirit.etimod.client.audio.variation.DamageLevel;
import etithespirit.etimod.client.audio.variation.SpecialAttackType;
import etithespirit.mixininterfaces.ISelfProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

/**
 * Hooks into {@link net.minecraft.entity.player.PlayerEntity#attack(Entity)} and intercepts all calls to
 * {@link net.minecraft.world.World#playSound(PlayerEntity, Entity, SoundEvent, SoundCategory, float, float)} playSound} to see if it needs to swap the sound out.
 * @author Eti
 *
 */
@Mixin(PlayerEntity.class)
@SuppressWarnings("unused")
public abstract class InjectPlayerEntityAttackTarget extends LivingEntity implements ISelfProvider {

	protected InjectPlayerEntityAttackTarget(EntityType<? extends LivingEntity> type, World worldIn) { super(type, worldIn); }
	
	/**
	 * Intercepts calls to world.playSound in PlayerEntity.attackTargetEntityWithCurrentItem
	 * @param world The world that playSound is being called on.
	 * @param player It's complicated, check world docs lol
	 * @param x The X position of the sound.
	 * @param y The Y position of the sound.
	 * @param z The Z position of the sound.
	 * @param soundIn The sound to play.
	 * @param category The category to play the sound in.
	 * @param volume The volume of the sound.
	 * @param pitch The pitch modifier for the sound.
	 */
	@Redirect(
		method = "attack(Lnet/minecraft/entity/Entity;)V", 
		at = @At(
			value = "INVOKE", 
			target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V"
		)
	)
	public void interceptWorldPlaySound(World world, @Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
		PlayerEntity localPlayer = player();
		if (soundIn != null && SpiritData.isSpirit(localPlayer)) {
			DamageLevel dLevel = damageLevelFromVanillaSoundEvent(soundIn);
			if (dLevel != null) {
				world.playSound(player, x, y, z, SpiritSoundProvider.getSpiritAttackSound(dLevel), category, volume / 5f, pitch);
				return;
			}
			SpecialAttackType atkType = attackTypeFromVanillaSoundEvent(soundIn);
			if (atkType != null) {
				world.playSound(player, x, y, z, SpiritSoundProvider.getSpiritAttackTypeSound(atkType), category, volume / 5f, pitch);
				return;
			}
		}
		world.playSound(player, x, y, z, soundIn, category, volume, pitch);
	}
	
	/**
	 * Given one of the vanilla sound events for a player attacking something, this returns the corresponding damage level for the amount of damage done.
	 * @param sound The sound being playd.
	 * @return A spirit DamageLevel suitable for the sound.
	 */
	private static DamageLevel damageLevelFromVanillaSoundEvent(@Nonnull SoundEvent sound) {
		if (sound == null) throw new NullPointerException();
		if (sound.equals(SoundEvents.PLAYER_ATTACK_NODAMAGE)) return DamageLevel.INEFFECTIVE;
		if (sound.equals(SoundEvents.PLAYER_ATTACK_WEAK)) return DamageLevel.WEAK;
		if (sound.equals(SoundEvents.PLAYER_ATTACK_STRONG)) return DamageLevel.STRONG;
		if (sound.equals(SoundEvents.PLAYER_ATTACK_CRIT)) return DamageLevel.CRITICAL;
		return null;
	}
	
	/**
	 * Given one of the vanilla sound events for a player attacking something, this returns the corresponding attack type for the damage done.
	 * @param sound The sound being played.
	 * @return A spirit SpecialAttackType suitable for the sound.
	 */
	private static SpecialAttackType attackTypeFromVanillaSoundEvent(@Nonnull SoundEvent sound) {
		if (sound == null) throw new NullPointerException();
		if (sound.equals(SoundEvents.PLAYER_ATTACK_KNOCKBACK)) return SpecialAttackType.KNOCKBACK;
		if (sound.equals(SoundEvents.PLAYER_ATTACK_SWEEP)) return SpecialAttackType.SWEEP;
		return null;
	}

}
