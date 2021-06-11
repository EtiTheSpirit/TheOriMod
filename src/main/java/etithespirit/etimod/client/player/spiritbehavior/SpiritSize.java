package etithespirit.etimod.client.player.spiritbehavior;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import etithespirit.etimod.info.spirit.SpiritIdentificationType;
import etithespirit.etimod.info.spirit.SpiritIdentifier;
import etithespirit.etimod.valuetypes.MutableEntitySize;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpiritSize {

	public static final float X_SCALE = 0.79F;
	public static final float Y_SCALE = 0.53F;
	
	public static final MutableEntitySize PLAYER_STANDING_SIZE = new MutableEntitySize(0.6F, 1.8F);
	public static final MutableEntitySize PLAYER_FALL_FLYING_SIZE = new MutableEntitySize(0.6F, 0.6F);
	public static final MutableEntitySize PLAYER_SWIMMING_SIZE = new MutableEntitySize(0.6F, 0.6F);
	public static final MutableEntitySize PLAYER_SPIN_ATTACK_SIZE = new MutableEntitySize(0.6F, 0.6F);
	public static final MutableEntitySize PLAYER_CROUCHING_SIZE = new MutableEntitySize(0.6F, 1.5F);
	
	public static final MutableEntitySize SPIRIT_STANDING_SIZE = PLAYER_STANDING_SIZE.scale(X_SCALE, Y_SCALE);
	public static final MutableEntitySize SPIRIT_FALL_FLYING_SIZE = PLAYER_FALL_FLYING_SIZE.scale(X_SCALE, Y_SCALE);
	public static final MutableEntitySize SPIRIT_SWIMMING_SIZE = PLAYER_SWIMMING_SIZE.scale(X_SCALE, Y_SCALE);
	public static final MutableEntitySize SPIRIT_SPIN_ATTACK_SIZE = PLAYER_SPIN_ATTACK_SIZE.scale(X_SCALE, Y_SCALE);
	public static final MutableEntitySize SPIRIT_CROUCHING_SIZE = PLAYER_CROUCHING_SIZE.scale(X_SCALE, Y_SCALE);
	
	public static final Map<Pose, MutableEntitySize> SPIRIT_SIZE_BY_POSE = new HashMap<Pose, MutableEntitySize>();
	public static final Map<Pose, MutableEntitySize> PLAYER_SIZE_BY_POSE = new HashMap<Pose, MutableEntitySize>();
	
	private static boolean wasSpiritOnLastTick = false;
	
	@SubscribeEvent
	public static void onGetEntitySize(EntityEvent.Size event) {
		if (!(event.getEntity() instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity)event.getEntity();
		if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
			Pose targetPose = updatePose(player);
			//Pose targetPose = event.getPose();
			EntitySize newSize = getSpiritSizeFrom(targetPose);
			
			if (targetPose == Pose.STANDING) {
				event.setNewEyeHeight(0.86f);
			} else if (targetPose == Pose.CROUCHING) {
				event.setNewEyeHeight(0.75f);
			} else if (targetPose == Pose.SWIMMING) {
				event.setNewEyeHeight(0.35f);
			} else {
				event.setNewEyeHeight(0.86f);
			}
			
			if (newSize != null) event.setNewSize(newSize);
		}
	}
	
	/**
	 * Updates the forced pose of the player. Returns the desired pose.
	 * @param player
	 * @return
	 */
	private static Pose updatePose(PlayerEntity player) {
		Pose targetPose = getDesiredForcedPose(player);
		if (player.getForcedPose() != targetPose) {
			player.setForcedPose(targetPose);
		}
		return targetPose;
	}
	
	/**
	 * Based on a number of conditions, this returns a pose most relevant to the player as a spirit.
	 * @param player
	 * @return
	 */
	private static Pose getDesiredForcedPose(PlayerEntity player) {
		boolean isSwimming = player.isSwimming() && player.isInWaterOrBubble();
		boolean isFlying = player.isFallFlying();
		boolean isSpinAttacking = player.isAutoSpinAttack();
		boolean isCrouching = player.isShiftKeyDown();
		
		if (isFlying) return Pose.FALL_FLYING;
		if (isSwimming) return Pose.SWIMMING;
		if (isSpinAttacking) return Pose.SPIN_ATTACK;
		
		if (!player.isSpectator() && !player.isPassenger() && player.isOnGround()) {
			if (!isPoseClear(player, SPIRIT_STANDING_SIZE)) {
				// Cannot fit here standing...
				if (!isPoseClear(player, SPIRIT_CROUCHING_SIZE)) {
					// And cannot fit here crouching...
					return Pose.SWIMMING; // Return laying down.
				} else {
					// But CAN fit here crouching...
					return Pose.CROUCHING;
				}
			}
		}
		
		// A block above will never collide with us. Use either crouching or standing based on the player's inputs.
		if (isCrouching) return Pose.CROUCHING; else return Pose.STANDING;
	}
	
	/**
	 * Returns a new AABB at the given location centered within the given size.
	 * @param at
	 * @param size
	 * @return
	 */
	public static AxisAlignedBB atWithSize(Vector3d at, EntitySize size) {
		float widthDiv2 = size.width / 2;
		return new AxisAlignedBB(
			at.subtract(widthDiv2, 0, widthDiv2),
			at.add(widthDiv2, size.height, widthDiv2)
		);
	}

	
	/**
	 * Returns true if the world does not collide with the given entity size located at the given player.
	 * @param player
	 * @param forGivenSize
	 * @return
	 */
	public static boolean isPoseClear(PlayerEntity player, EntitySize entSize) {
		// NEW TEST: Null entity changes anything?
		// TODO: Why shrink by 0.0000001? What effect or benefit does this have?
		return player.getCommandSenderWorld().noCollision(null, atWithSize(player.position(), entSize).deflate(1.0E-7D));
	}
	
	/**
	 * Returns whether or not there is a block above the player (based on if the default playermodel standing size clips with the world)
	 * @param player
	 * @return
	 */
	public static boolean isBlockAbove(PlayerEntity player) {
		return !isPoseClear(player, PLAYER_STANDING_SIZE);
	}
	
	/**
	 * Returns true if the block above the player would make a full-size player crouch. Used to fix an edge case where a top half-slab above the spirit would cause a false crouching state. 
	 * @param player
	 * @return
	 */
	public static boolean wouldBlockAboveMakeFullPlayerCrouch(PlayerEntity player) {
		return !isPoseClear(player, PLAYER_CROUCHING_SIZE);
	}
	
	public static @Nullable EntitySize getSpiritSizeFrom(Pose pose) {
		if (SPIRIT_SIZE_BY_POSE.containsKey(pose)) {
			return SPIRIT_SIZE_BY_POSE.get(pose);
		}
		return null;
	}
	
	public static @Nullable EntitySize getPlayerSizeFrom(Pose pose) {
		if (PLAYER_SIZE_BY_POSE.containsKey(pose)) {
			return PLAYER_SIZE_BY_POSE.get(pose);
		}
		return null;
	}
	
	@SuppressWarnings("resource")
	//@SubscribeEvent
	public static void onPlayerTicked(PlayerTickEvent evt) {
		if (evt.player == null) return;
		if (evt.player != net.minecraft.client.Minecraft.getInstance().player) return;
		if (evt.phase == TickEvent.Phase.END) return;
		if (SpiritIdentifier.isSpirit(evt.player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
			updatePose(evt.player);
			if (!wasSpiritOnLastTick) {
				evt.player.refreshDimensions();
				wasSpiritOnLastTick = true;
			}
		} else {
			if (wasSpiritOnLastTick) {
				evt.player.setForcedPose(null);
				evt.player.refreshDimensions();
				wasSpiritOnLastTick = false;
			}
		}
	}
	
	static {
		SPIRIT_SIZE_BY_POSE.put(Pose.STANDING, SPIRIT_STANDING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.FALL_FLYING, SPIRIT_FALL_FLYING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.SWIMMING, SPIRIT_SWIMMING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.SPIN_ATTACK, SPIRIT_SPIN_ATTACK_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.CROUCHING, SPIRIT_CROUCHING_SIZE);
		
		PLAYER_SIZE_BY_POSE.put(Pose.STANDING, PLAYER_STANDING_SIZE);
		PLAYER_SIZE_BY_POSE.put(Pose.FALL_FLYING, PLAYER_FALL_FLYING_SIZE);
		PLAYER_SIZE_BY_POSE.put(Pose.SWIMMING, PLAYER_SWIMMING_SIZE);
		PLAYER_SIZE_BY_POSE.put(Pose.SPIN_ATTACK, PLAYER_SPIN_ATTACK_SIZE);
		PLAYER_SIZE_BY_POSE.put(Pose.CROUCHING, PLAYER_CROUCHING_SIZE);
	}
}
