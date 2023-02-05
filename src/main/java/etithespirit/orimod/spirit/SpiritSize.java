package etithespirit.orimod.spirit;


import etithespirit.orimod.util.SidedValue;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * All code pertaining to spirit size and collision modifications. This executes with the help of a mixin.
 *
 * @author Eti
 */
public final class SpiritSize {
	
	/** A value storing whether or not the player was a spirit on the previous game tick. */
	private static final SidedValue<Boolean> WAS_SPIRIT_ON_LAST_TICK = new SidedValue<>(false);
	
	/** A spirit's width relative to that of a player's width. */
	public static final float X_SCALE = 0.79F;
	
	/** A spirit's height relative to that of a player's height. */
	public static final float Y_SCALE = 0.53F;
	
	/** The size of a standing player. */
	public static final EntityDimensions PLAYER_STANDING_SIZE = new EntityDimensions(0.6F, 1.8F, false);
	
	/** The size of a player flying with an Elytra. */
	public static final EntityDimensions PLAYER_FALL_FLYING_SIZE = new EntityDimensions(0.6F, 0.6F, false);
	
	/** The size of a player who is swimming. */
	public static final EntityDimensions PLAYER_SWIMMING_SIZE = new EntityDimensions(0.6F, 0.6F, false);
	
	/** The size of a player who SPEEN (I don't actually know what this pose is lol) */
	public static final EntityDimensions PLAYER_SPIN_ATTACK_SIZE = new EntityDimensions(0.6F, 0.6F, false);
	
	/** The size of a player who is sneaking. */
	public static final EntityDimensions PLAYER_CROUCHING_SIZE = new EntityDimensions(0.6F, 1.5F, false);
	
	/** The size of a standing spirit. */
	public static final EntityDimensions SPIRIT_STANDING_SIZE = PLAYER_STANDING_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** The size of a spirit flying with an Elytra. */
	public static final EntityDimensions SPIRIT_FALL_FLYING_SIZE = PLAYER_FALL_FLYING_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** The size of a spirit who is swimming. */
	public static final EntityDimensions SPIRIT_SWIMMING_SIZE = PLAYER_SWIMMING_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** The size of a spirit who SPEEN */
	public static final EntityDimensions SPIRIT_SPIN_ATTACK_SIZE = PLAYER_SPIN_ATTACK_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** The size of a spirit who is sneaking. */
	public static final EntityDimensions SPIRIT_CROUCHING_SIZE = PLAYER_CROUCHING_SIZE.scale(X_SCALE, Y_SCALE);
	
	/** A lookup from {@link Pose} to {@link EntityDimensions} for spirits. Binds to the static final {@link EntityDimensions} instances in this class. */
	public static final Map<Pose, EntityDimensions> SPIRIT_SIZE_BY_POSE = new HashMap<>();
	
	/**
	 * Occurs when an entity's size is tested.
	 * @param event The event. Obviously. I'm only writing this here so IntelliJ doesn't throw a fit.
	 */
	public static void onGetEntitySizeCommon(EntityEvent.Size event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (SpiritIdentifier.isSpirit(player)) {
			Pose targetPose = updatePose(player);
			EntityDimensions newSize = getSpiritSizeFrom(targetPose);
			
			if (targetPose == Pose.STANDING) {
				event.setNewEyeHeight(0.86f);
			} else if (targetPose == Pose.CROUCHING) {
				event.setNewEyeHeight(0.75f);
			} else if (targetPose == Pose.SWIMMING) {
				event.setNewEyeHeight(0.35f);
			} else {
				event.setNewEyeHeight(0.86f);
			}
			
			if (newSize != null) {
				event.setNewSize(newSize);
			}
		}
	}
	
	/**
	 * Updates the forced pose of the player. Returns the desired pose.
	 * @param player The player to alter.
	 * @return Their desired pose.
	 */
	private static Pose updatePose(Player player) {
		if (!player.isAddedToWorld()) return Pose.STANDING;
		
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
	private static Pose getDesiredForcedPose(Player player) {
		if (!player.isAddedToWorld()) return Pose.STANDING;
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
	 * @return An {@link AABB} located around {@code at} with size {@code size}.
	 */
	public static AABB atWithSize(Vec3 at, EntityDimensions size) {
		float widthDiv2 = size.width / 2;
		return new AABB(
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
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	// ^ IntelliJ sees "is" here and "no" on the other method, raising this warning.
	public static boolean isPoseClear(Player player, EntityDimensions entSize) {
		return player.getCommandSenderWorld().noCollision(null, atWithSize(player.position(), entSize));
	}
	
	/**
	 * Given a pose, this returns the Spirit playermodel size associated with it.
	 * @param pose The pose to use for reference.
	 * @return An {@link EntityDimensions} suited for this pose.
	 */
	public static @Nullable EntityDimensions getSpiritSizeFrom(Pose pose) {
		if (SPIRIT_SIZE_BY_POSE.containsKey(pose)) {
			return SPIRIT_SIZE_BY_POSE.get(pose);
		}
		return null;
	}
	
	public static void onPlayerTickedCommon(TickEvent.PlayerTickEvent evt) {
		if (evt.player == null) return;
		if (!evt.player.isAddedToWorld()) return;
		if (evt.phase == TickEvent.Phase.END) return;
		boolean isSpirit = SpiritIdentifier.isSpirit(evt.player);
		boolean isClient = evt.player.getCommandSenderWorld().isClientSide;
		if (isSpirit) {
			updatePose(evt.player);
		} else {
			if (WAS_SPIRIT_ON_LAST_TICK.get(isClient)) {
				evt.player.setForcedPose(null);
			}
		}
		WAS_SPIRIT_ON_LAST_TICK.set(isClient, isSpirit);
	}
	
	static {
		SPIRIT_SIZE_BY_POSE.put(Pose.STANDING, SPIRIT_STANDING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.FALL_FLYING, SPIRIT_FALL_FLYING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.SWIMMING, SPIRIT_SWIMMING_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.SPIN_ATTACK, SPIRIT_SPIN_ATTACK_SIZE);
		SPIRIT_SIZE_BY_POSE.put(Pose.CROUCHING, SPIRIT_CROUCHING_SIZE);
	}
}
