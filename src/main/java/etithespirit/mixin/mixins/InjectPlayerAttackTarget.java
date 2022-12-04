package etithespirit.mixin.mixins;

import etithespirit.exception.ArgumentNullException;
import etithespirit.mixin.helpers.ISelfProvider;
import etithespirit.orimod.client.audio.SpiritSoundProvider;
import etithespirit.orimod.client.audio.variation.DamageLevel;
import etithespirit.orimod.client.audio.variation.SpecialAttackType;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This mixin intercepts attack sounds and allows Spirit special sounds to play instead.
 */
@Mixin(Player.class)
public abstract class InjectPlayerAttackTarget extends LivingEntity implements ISelfProvider {
	
	private InjectPlayerAttackTarget(EntityType<? extends LivingEntity> pEntityType, Level pLevel) { super(pEntityType, pLevel); }
	
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
	@Redirect (
		method = "attack(Lnet/minecraft/world/entity/Entity;)V",
		at = @At (
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
		)
	)
	public void interceptWorldPlaySound(Level world, @Nullable Player player, double x, double y, double z, SoundEvent soundIn, SoundSource category, float volume, float pitch) {
		Player localPlayer = selfProvider$player();
		if (soundIn != null && SpiritIdentifier.isSpirit(localPlayer)) {
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
		if (sound == null) throw new ArgumentNullException("sound");
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
		if (sound == null) throw new ArgumentNullException("sound");
		if (sound.equals(SoundEvents.PLAYER_ATTACK_KNOCKBACK)) return SpecialAttackType.KNOCKBACK;
		if (sound.equals(SoundEvents.PLAYER_ATTACK_SWEEP)) return SpecialAttackType.SWEEP;
		return null;
	}
	
}
