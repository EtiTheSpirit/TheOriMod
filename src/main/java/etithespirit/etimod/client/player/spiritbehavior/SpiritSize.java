package etithespirit.etimod.client.player.spiritbehavior;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import etithespirit.etimod.info.spirit.SpiritData;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * All code pertaining to spirit size and collision modifications. This executes with the help of a mixin.
 *
 * @author Eti
 */
public final class SpiritSize {

	/** A spirit's width relative to that of a player's width. */
	public static final float X_SCALE = 0.79F;
	
	/** A spirit's height relative to that of a player's height. */
	public static final float Y_SCALE = 0.53F;
	
	/** The size of a standing player. */
	public static final EntitySize PLAYER_STANDING_SIZE = new EntitySize(0.6F, 1.8F, false);
	
	/** The size of a player flying with an Elytra. */
	public static final EntitySize PLAYER_FALL_FLYING_SIZE = new EntitySize(0.6F, 0.6F, false);
	
	/** The size of a player who is swimming. */
	public static final EntitySize PLAYER_SWIMMING_SIZE = new EntitySize(0.6F, 0.6F, false);
	
	/** The size of a player who SPEEN (I don't actually know what this pose is lol) */
	public static final EntitySize PLAYER_SPIN_ATTACK_SIZE = new EntitySize(0.6F, 0.6F, false);
	
	/** The size of a player who is sneaking. */
	public static final EntitySize PLAYER_CROUCHING_SIZE = new EntitySize(0.6F, 1.5F, false);
	
	/** The size of a standing spirit. */
	public static final EntitySize SPIRIT_STANDING_SIZE = PLAYER_STANDING_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** The size of a spirit flying with an Elytra. */
	public static final EntitySize SPIRIT_FALL_FLYING_SIZE = PLAYER_FALL_FLYING_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** The size of a spirit who is swimming. */
	public static final EntitySize SPIRIT_SWIMMING_SIZE = PLAYER_SWIMMING_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** The size of a spirit who SPEEN */
	public static final EntitySize SPIRIT_SPIN_ATTACK_SIZE = PLAYER_SPIN_ATTACK_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** The size of a spirit who is sneaking. */
	public static final EntitySize SPIRIT_CROUCHING_SIZE = PLAYER_CROUCHING_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** A lookup from {@link Pose} to {@link EntitySize} for spirits. Binds to the static final {@link EntitySize} instances in this class. */
	public static final Map<Pose, EntitySize> SPIRIT_SIZE_BY_POSE = new HashMap<>();
	
	/**
	 * Occurs when an entity's size is tested.
	 * @param event The event. Obviously. I'm only writing this here so IntelliJ doesn't throw a fit.
	 */
	public static void onGetEntitySizeCommon(EntityEvent.Size event) {
		if (!(event.getEntity() instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity)event.getEntity();
		if (SpiritData.isSpirit(player)) {
			Pose targetPose = updatePose(player);
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
	 * @param player The player to alter.
	 * @return Their desired pose.
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
	 * @param player The player to test for.
	 * @return The proper pose given the context of the player.
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
	 * @param at The location of the center of the axis-aligned bounding box.
	 * @param size The size of this bounding box.
	 * @return An {@link AxisAlignedBB} located around {@code at} with size {@code size}.
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
	 * @param player The player to test.
	 * @param entSize The size of the player.
	 * @return Whether or not that player is intersecting any blocks in the world.
	 */
	@SuppressWarnings ("BooleanMethodIsAlwaysInverted")
	// ^ Because quite frankly, it's not. I think it's just seeing "is" on this method and "no" on the other one. Understandable.
	public static boolean isPoseClear(PlayerEntity player, EntitySize entSize) {
		return player.getCommandSenderWorld().noCollision(null, atWithSize(player.position(), entSize));
	}
	
	/**
	 * Given a pose, this returns the Spirit playermodel size associated with it.
	 * @param pose The pose to use for reference.
	 * @return An {@link EntitySize} suited for this pose.
	 */
	public static @Nullable EntitySize getSpiritSizeFrom(Pose pose) {
		if (SPIRIT_SIZE_BY_POSE.containsKey(pose)) {
			return SPIRIT_SIZE_BY_POSE.get(pose);
		}
		return null;
	}
	
	public static void onPlayerTickedCommon(PlayerTickEvent evt) {
		if (evt.player == null) return;
		if (evt.phase == TickEvent.Phase.END) return;
		if (SpiritData.isSpirit(evt.player)) {
			updatePose(evt.player);
		}
	}
	
	static {
		SPIRIT_SIZE_BY_POSE.put(Pose.STANDING, SPIRIT_STANDING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.FALL_FLYING, SPIRIT_FALL_FLYING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.SWIMMING, SPIRIT_SWIMMING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.SPIN_ATTACK, SPIRIT_SPIN_ATTACK_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.CROUCHING, SPIRIT_CROUCHING_SIZE);
	}
}
