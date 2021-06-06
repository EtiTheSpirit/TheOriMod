package etithespirit.etimod.client.audio;

import java.util.Random;

import javax.annotation.Nonnull;

import etithespirit.etimod.exception.ArgumentNullException;
import etithespirit.etimod.util.EtiUtils;
import etithespirit.etimod.util.spirit.SpiritIdentificationType;
import etithespirit.etimod.util.spirit.SpiritIdentifier;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

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
	 * Plays a sound in the given player's world at the player's location with appropriate client/server handling.
	 * @param player The player to play the sound for
	 * @param sound The sound to play
	 * @param category The category of the sound
	 * @param volume The volume of the sound
	 * @param pitch The pitch of the sound
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playSoundAtPlayer(@Nonnull PlayerEntity player, @Nonnull SoundEvent sound, @Nonnull SoundCategory category, float volume, float pitch) {
		if (player == null) throw new ArgumentNullException("player");
		if (sound == null) throw new ArgumentNullException("sound");
		if (category == null) throw new ArgumentNullException("category");
		
		PlayerEntity passIn = null;
		if (EtiUtils.isClient(player)) passIn = player;
		
		player.getEntityWorld().playSound(passIn, player.getPosX(), player.getPosY(), player.getPosZ(), sound, category, volume, pitch);
	}
	
	/**
	 * Plays a sound in the given player's world at the player's location with appropriate client/server handling. Provides a random pitch of 1 +- 0.025
	 * @param player The player to play the sound for
	 * @param sound The sound to play
	 * @param category The category of the sound
	 * @param volume The volume of the sound
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playSoundAtPlayer(@Nonnull PlayerEntity player, @Nonnull SoundEvent sound, @Nonnull SoundCategory category, float volume) {
		if (player == null) throw new ArgumentNullException("player");
		if (sound == null) throw new ArgumentNullException("sound");
		if (category == null) throw new ArgumentNullException("category");
		
		playSoundAtPlayer(player, sound, category, volume, getRandomPitch());
	}
	
	/**
	 * Plays a sound associated with the dash ability.
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param willImmediatelyImpactWall Whether or not they will immediately impact a wall if they dash.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playDashSound(@Nonnull PlayerEntity player, boolean willImmediatelyImpactWall) {
		if (player == null) throw new ArgumentNullException("player");
		
		if (player instanceof PlayerEntity) {
			if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
				SoundEvent sound = SpiritSoundProvider.getSpiritDashSound(willImmediatelyImpactWall);
				if (sound != null) playSoundAtPlayer(player, sound, SoundCategory.PLAYERS, 0.125f, getRandomPitch());
			}
		}
	}

	/**
	 * Plays the sound associated with the given jump count.
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param jumps The amount of jumps they have performed when this sound is called, which should either be 1, 2, or 3.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playJumpSound(@Nonnull PlayerEntity player, int jumps) {
		if (player == null) throw new ArgumentNullException("player");
		
		if (player instanceof PlayerEntity) {
			if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
				SoundEvent sound = SpiritSoundProvider.getSpiritJumpSound(jumps);
				if (sound != null) playSoundAtPlayer(player, sound, SoundCategory.PLAYERS, 0.1f, getRandomPitch());
			}
		}
	}
	
	/**
	 * Plays the sound associated with jumping against the given block.
	 * @param player The player to play for. Whether or not they are a spirit is validated in this method.
	 * @param The block being impacted.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static void playWallJumpSound(@Nonnull PlayerEntity player, @Nonnull BlockPos block) {
		if (player == null) throw new ArgumentNullException("player");
		if (block == null) throw new ArgumentNullException("block");
		
		if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
			SoundEvent sound = SpiritSoundProvider.getSpiritWallJumpSound(block);
			if (sound != null) playSoundAtPlayer(player, sound, SoundCategory.PLAYERS, 0.1f, getRandomPitch());
			playStepSoundForWallJump(player, block);
		}
	}
	
    
    /**
     * Plays the sound associated with stepping on the given block.
     * @param player The player to play for. Whether or not they are a spirit is validated in this method.
     * @param block The block being stepped on.
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
     */
    private static void playStepSoundForWallJump(@Nonnull PlayerEntity player, @Nonnull BlockPos block) {
    	if (player == null) throw new ArgumentNullException("player");
		if (block == null) throw new ArgumentNullException("block");
		
    	if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
	        SoundEvent fromSpiritProvider = SpiritSoundProvider.getSpiritStepSound(player, block, null, null);
	        if (fromSpiritProvider != null) {
	        	player.playSound(fromSpiritProvider, 0.025f, 1f);
	        } else {
	        	BlockState state = player.world.getBlockState(block);
	        	SoundType soundtype = state.getBlock().getSoundType(state, player.getEntityWorld(), null, player);
	        	SoundEvent hit = soundtype.getHitSound();
	        	if (hit != null) playSoundAtPlayer(player, hit, SoundCategory.PLAYERS, soundtype.getVolume() * 0.25f, soundtype.getPitch());
	        }
    	}
    }
    
    /**
     * Plays the sound associated with falling a given distance.
     * @param player The player to play for. Whether or not they are a spirit is validated in this method.
     * @param fallDistance
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null. 
     */
    @Deprecated
    public static void playFallSound(@Nonnull PlayerEntity player, float fallDistance) {
    	if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
			SoundEvent sound = SpiritSoundProvider.getSpiritFallSound(fallDistance, player, null);
			if (sound != null) playSoundAtPlayer(player, sound, SoundCategory.PLAYERS, 0.1f, getRandomPitch());
		}
    }
    
    /**
     * Plays the sound associated with being hurt.
     * @param player The player to play for. Whether or not they are a spirit is validated in this method.
     * @param source
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
     */
    public static void playHurtSound(@Nonnull PlayerEntity player, @Nonnull DamageSource source) {
    	if (player == null) throw new ArgumentNullException("player");
    	if (source == null) throw new ArgumentNullException("source");
    	
    	if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
    		SoundEvent sound = SpiritSoundProvider.getSpiritHurtSound(source);
    		if (sound != null) playSoundAtPlayer(player, sound, SoundCategory.PLAYERS, 0.4f, getRandomPitch());
    	}
    }
 
    /**
     * Plays the sound associated with being killed.
     * @param player The player to play for. Whether or not they are a spirit is validated in this method.
     * @param source
     * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
     */
    public static void playDeathSound(@Nonnull PlayerEntity player, @Nonnull DamageSource source) {
    	if (player == null) throw new ArgumentNullException("player");
    	if (source == null) throw new ArgumentNullException("source");
    	
    	if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
    		SoundEvent sound = SpiritSoundProvider.getSpiritDeathSound(source);
    		if (sound != null) playSoundAtPlayer(player, sound, SoundCategory.PLAYERS, 0.2f, getRandomPitch());
    	}
	}
}
