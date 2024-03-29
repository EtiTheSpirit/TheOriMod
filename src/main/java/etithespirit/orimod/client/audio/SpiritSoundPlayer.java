package etithespirit.orimod.client.audio;


import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Plays sounds pertaining to Spirits.
 *
 * @author Eti
 */
public final class SpiritSoundPlayer {
	
	private static final Random RNG = new Random();
	
	/**
	 * Calls {@link #getRandomPitch(float)} with an value of 0.025f, the default deviation, which returns a value in the range of 1 &pm; 0.0125.
	 */
	public static float getRandomPitch() {
		return getRandomPitch(0.025f);
	}
	
	/**
	 * Returns 1 &pm; (deviation/2), which is intended to be used for the pitch of a sound.
	 * @param deviation The range from which a sound may have a different pitch. This is centered around 100% pitch, so the resulting pitch will range from {@code 1 - (deviation/2)} to {@code 1 + (deviation/2)}.
	 */
	public static float getRandomPitch(float deviation) {
		float rngVal = RNG.nextFloat(); // RANGE: [0, 1)
		// If deviation is, say, 0.2, then that means the returned value should be between 0.9 and 1.1
		rngVal *= deviation; // Scale the RNG value
		rngVal -= (deviation / 2); // Then cut it so half is below 0 and the other half is above 1
		return rngVal + 1;
	}
	
	/**
	 * Identical to {@link #playSoundAtPlayer(Player, SoundEvent, SoundSource, float, float)} but this variation is server only, and will replicate the sound to the given player.
	 * @param player The player to play the sound for
	 * @param sound The sound to play
	 * @param category The category of the sound
	 * @param volume The volume of the sound
	 * @param pitch The pitch of the sound
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 * @exception IllegalStateException if this is called from a logical client.
	 */
	public static void playReplicatedSoundAtPlayer(@Nonnull Player player, @Nonnull SoundEvent sound, @Nonnull SoundSource category, float volume, float pitch) {
		ArgumentNullException.throwIfNull(player, "player");
		ArgumentNullException.throwIfNull(sound, "sound");
		ArgumentNullException.throwIfNull(category, "category");
		if (player.getCommandSenderWorld().isClientSide) throw new IllegalStateException("This method is only callable on the logical server.");
		player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), sound, category, volume, pitch);
	}
	
	/**
	 * Plays a sound in the given player's world at the player's location with appropriate client/server handling.
	 * @param player The player to play the sound for
	 * @param sound The sound to play
	 * @param category The category of the sound
	 * @param volume The volume of the sound
	 * @param pitch The pitch of the sound
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playSoundAtPlayer(@Nonnull Player player, @Nonnull SoundEvent sound, @Nonnull SoundSource category, float volume, float pitch) {
		ArgumentNullException.throwIfNull(player, "player");
		ArgumentNullException.throwIfNull(sound, "sound");
		ArgumentNullException.throwIfNull(category, "category");
		player.getCommandSenderWorld().playSound(player, player.getX(), player.getY(), player.getZ(), sound, category, volume, pitch);
	}
	
	/**
	 * Plays a sound in the given player's world at the player's location with appropriate client/server handling. Provides a random pitch of 1 +- 0.025
	 * @param player The player to play the sound for
	 * @param sound The sound to play
	 * @param category The category of the sound
	 * @param volume The volume of the sound
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playSoundAtPlayer(@Nonnull Player player, @Nonnull SoundEvent sound, @Nonnull SoundSource category, float volume) {
		ArgumentNullException.throwIfNull(player, "player");
		ArgumentNullException.throwIfNull(sound, "sound");
		ArgumentNullException.throwIfNull(category, "category");
		
		playSoundAtPlayer(player, sound, category, volume, getRandomPitch());
	}
	
	/**
	 * Plays a sound associated with the dash ability.
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param willImmediatelyImpactWall Whether or not they will immediately impact a wall if they dash.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playDashSound(@Nonnull Player player, boolean willImmediatelyImpactWall) {
		ArgumentNullException.throwIfNull(player, "player");
		
		if (SpiritIdentifier.isSpirit(player)) {
			SoundEvent sound = SpiritSoundProvider.getSpiritDashSound(willImmediatelyImpactWall);
			if (sound != null) playSoundAtPlayer(player, sound, SoundSource.PLAYERS, 0.125f, getRandomPitch());
		}
		
	}
	
	/**
	 * Plays the sound associated with the given jump count.
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param jumpsIncludingLand The amount of jumps they have performed when this sound is called, which should either be 1, 2, or 3.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playJumpSound(@Nonnull Player player, int jumpsIncludingLand) {
		ArgumentNullException.throwIfNull(player, "player");
		
		if (SpiritIdentifier.isSpirit(player)) {
			SoundEvent sound = SpiritSoundProvider.getSpiritJumpSound(jumpsIncludingLand);
			if (sound != null) playSoundAtPlayer(player, sound, SoundSource.PLAYERS, 0.1f, getRandomPitch());
		}
	}
	
	/**
	 * Plays the sound associated with jumping against the given block.
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param block The location of the block that should provide the sound to play.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playWallJumpSound(@Nonnull Player player, @Nonnull BlockPos block) {
		ArgumentNullException.throwIfNull(player, "player");
		ArgumentNullException.throwIfNull(block, "block");
		
		if (SpiritIdentifier.isSpirit(player)) {
			SoundEvent sound = SpiritSoundProvider.getSpiritWallJumpSound(block);
			if (sound != null) playSoundAtPlayer(player, sound, SoundSource.PLAYERS, 0.1f, getRandomPitch());
			playStepSoundForWallJump(player, block);
		}
	}
	
	
	/**
	 * Plays the sound associated with stepping on the given block.
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param block The block being stepped on.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	private static void playStepSoundForWallJump(@Nonnull Player player, @Nonnull BlockPos block) {
		ArgumentNullException.throwIfNull(player, "player");
		ArgumentNullException.throwIfNull(block, "block");
		
		if (SpiritIdentifier.isSpirit(player)) {
			SoundEvent fromSpiritProvider = SpiritSoundProvider.getSpiritStepSound(player, block, block.above(), null);
			if (fromSpiritProvider != null) {
				player.playSound(fromSpiritProvider, 0.025f, 1f);
			} else {
				BlockState state = player.level.getBlockState(block);
				SoundType soundtype = state.getBlock().getSoundType(state, player.getCommandSenderWorld(), null, player);
				SoundEvent hit = soundtype.getHitSound();
				if (hit != null) playSoundAtPlayer(player, hit, SoundSource.PLAYERS, soundtype.getVolume() * 0.25f, soundtype.getPitch());
			}
		}
	}
	
	/*
	 * Plays the sound associated with falling a given distance.
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param fallDistance
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	/*
    @Deprecated
    public static void playFallSound(@Nonnull PlayerEntity player, float fallDistance) {
    	if (SpiritData.isSpirit(player)) {
			SoundEvent sound = SpiritSoundProvider.getSpiritFallSound(fallDistance, player, null);
			if (sound != null) playSoundAtPlayer(player, sound, SoundCategory.PLAYERS, 0.1f, getRandomPitch());
		}
    }
    */
	
	/**
	 * Plays the sound associated with being hurt. <strong>This method is for the server only, as the client will not be playing the sound.</strong>
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param source The cause of the damage.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playHurtSound(@Nonnull Player player, @Nonnull DamageSource source) {
		ArgumentNullException.throwIfNull(player, "player");
		ArgumentNullException.throwIfNull(source, "source");
		
		if (SpiritIdentifier.isSpirit(player)) {
			SoundEvent sound = SpiritSoundProvider.getSpiritHurtSound(source);
			if (sound != null) playReplicatedSoundAtPlayer(player, sound, SoundSource.PLAYERS, 0.4f, getRandomPitch());
		}
	}
	
	/**
	 * Plays the sound associated with being killed. <strong>This method is for the server only, as the client will not be playing the sound.</strong>
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param source The cause of the damage.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playDeathSound(@Nonnull Player player, @Nonnull DamageSource source) {
		ArgumentNullException.throwIfNull(player, "player");
		ArgumentNullException.throwIfNull(source, "source");
		
		if (SpiritIdentifier.isSpirit(player)) {
			SoundEvent sound = SpiritSoundProvider.getSpiritDeathSound(source);
			if (sound != null) playReplicatedSoundAtPlayer(player, sound, SoundSource.PLAYERS, 0.2f, getRandomPitch());
		}
	}
}
