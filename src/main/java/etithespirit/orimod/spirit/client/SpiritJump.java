package etithespirit.orimod.spirit.client;


import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages all behaviors pertaining to how spirits jump.
 *
 * @author Eti
 */
public final class SpiritJump {
	
	//public static final KeyBinding CLIMB_BIND = new KeyBinding("input.etimod.climb", 340, "key.categories.movement"); // Bind climb to l-shift
	public static final KeyMapping CLING_BIND = new KeyMapping("input.etimod.cling", 32, "key.categories.movement"); // Bind cling to space
	
	/* Whether or not the player is currently clinging to a wall. */
	private static boolean isClingingToWall = false;
	
	/** The maximum amount of jumps that the player can perform, including the ground jump. */
	private static final int MAX_JUMPS = 3;
	
	/** The amount of jumps the player has performed, counting their ground jump. */
	private static int currentJumps = 0;
	
	/** Speed on the Y axis will be divided by this every tick when clinging to a wall and sneaking. */
	private static final float VERTICAL_SPEED_DECAY = 1.17f;
	
	/* Speed on the X and Z axes will be divided by this when determining the velocity to jump on walls at. */
	//private static final float LATERAL_DECAY = 2.5f;
	
	/** Speed on the X and Z axes will be divided by this when performing a double or triple jump. */
	private static final float LATERAL_DECAY_JUMP = 1.5f;
	
	/* The maximum descending speed (so it should be negative) for wall sliding. */
	//private static final float WALL_SLIDE_SPEED = -0.1f;
	
	/** The upward force applied to wall / multi jumps. */
	private static final float UPWARD_FORCE = 0.55f;
	
	/** Storage of the walls adjacent to the player. */
	private static final Set<Direction> WALLS = new HashSet<>();
	
	/** Whether or not the system is waiting on space to be released. */
	private static boolean waitingOnSpaceRelease = false;
	
	/**
	 * Returns whether or not the given AxisAlignedBB collides with any blocks in the given world.
	 * @param world The world to test in.
	 * @param box The box to test for collision.
	 * @return Whether or not the AABB collides with the world.
	 */
	private static boolean collidesWithBlock(Level world, AABB box) {
		return !world.noCollision(box);
	}
	
	/**
	 * Returns the Y motion of the player as a float.
	 * @param player The player to test.
	 * @return The motion of the player on the Y axis.
	 */
	private static float getDeltaMovementY(Player player) {
		return (float)player.getDeltaMovement().y;
	}
	
	/**
	 * Sets the Y motion of the player.
	 * @param player The player to edit.
	 * @param y The new y component of the player's movement.
	 */
	private static void setDeltaMovementY(Player player, float y) {
		player.setDeltaMovement(player.getDeltaMovement().x, y, player.getDeltaMovement().z);
	}
	
	/**
	 * New wall jump behavior derived from the Wall Jump mod. Returns whether or not a wall jump was actually performed.
	 * Generally speaking, if this returns false, PerformMultiJump should be called instead.
	 * @param player The player to perform with.
	 * @return Whether or not the wall jump was actually performed.
	 */
	public static boolean performWallJump(LocalPlayer player) {
		if (!canWallCling(player)) return false;
		updateWalls(player);
		
		for (int idx = 0; idx < WALLS.size(); idx++) {
			Direction clingDir = getNextClingDirection();
			if (clingDir == Direction.UP) return false;
			BlockPos wallPos = getWallPos(player, clingDir);
			if (!isWalkingTowards(player, wallPos)) continue;
			performJump(player, UPWARD_FORCE, wallPos, true);
			return true;
		}
		return false;
	}
	
	/**
	 * Attempts to perform a multi-jump.
	 * @param client The client performing this action.
	 */
	private static void performMultiJump(LocalPlayer client) {
		if (currentJumps >= MAX_JUMPS) return;
		if (client.isOnGround()) return; // No special stuffs if we're on the ground.
		if (getDeltaMovementY(client) > 0.25) return; // Prevent spam
		performJump(client, UPWARD_FORCE, null, false);
	}
	
	/**
	 * Attempts to cling to a wall, which reduces Y velocity quickly over time. As such, this should be called in a ticker.
	 * @param player The player who is doing this.
	 */
	public static void tryPerformWallCling(Player player) {
		if (!canWallCling(player)) {
			isClingingToWall = false;
			return;
		}
		updateWalls(player);
		if (WALLS.size() == 0) {
			isClingingToWall = false;
			return;
		}
		
		isClingingToWall = true;
		float y = getDeltaMovementY(player);
		if (y <= -0.1) {
			setDeltaMovementY(player, y / VERTICAL_SPEED_DECAY);
		} else if (y < 0 && y > -0.1) {
			setDeltaMovementY(player, -0.1f);
		}
	}
	
	/**
	 * Returns whether or not the current player is walking towards the given block position.
	 * @param player The player to check.
	 * @param pos The position to check.
	 * @return Whether or not the given player is walking towards the given position.
	 */
	private static boolean isWalkingTowards(Player player, BlockPos pos) {
		Vec3 moveDir = player.getDeltaMovement();//pl.movementInput.getMoveVector();
		if (moveDir.x == 0 && moveDir.z == 0) return false; // Not moving.
		
		Vec3 plPos = player.position();
		if (pos.getX() == plPos.x && pos.getY() == plPos.y && pos.getZ() == plPos.z) return false; // Position is identical.
		Vec3 moveDir3D = new Vec3(moveDir.x, 0, moveDir.z).normalize();
		
		
		// Below: use player pos Y to get 2D direction (set the Y to be identical)
		Vec3 dirToBlock = (new Vec3(pos.getX(), plPos.y, pos.getZ())).subtract(plPos).normalize();
		
		return (moveDir3D.dot(dirToBlock) > 0.2);
	}
	
	/**
	 * Observes the player's motion and determines if it's possible for them to cling to the wall.
	 * @param player The player to test.
	 * @return Whether or not it's possible to cling to a wall.
	 */
	private static boolean canWallCling(Player player) {
		if (player.onClimbable() || getDeltaMovementY(player) > 0.8) return false; // On a ladder or moving up super fast upward
		if (player.isInWaterOrBubble() || player.isInLava()) return false; // In water, lava, or a bubble column
		return !collidesWithBlock(player.getCommandSenderWorld(), player.getBoundingBox().move(0, -0.8, 0)); // Too close to the ground.
	}
	
	/**
	 * Observes the walls that are surrounding the player and populates them into the walls array.
	 * @param player The player to do this update for.
	 */
	private static void updateWalls(Player player) {
		AABB box = new AABB(player.getX() - 0.001, player.getY(), player.getZ() - 0.001, player.getX() + 0.001, player.getY() + player.getEyeHeight(), player.getZ() + 0.001);
		
		double dist = (player.getBbWidth() / 2) + (isClingingToWall ? 0.2 : 0.06);
		AABB[] axes = {box.expandTowards(0, 0, dist), box.expandTowards(-dist, 0, 0), box.expandTowards(0, 0, -dist), box.expandTowards(dist, 0, 0)};
		
		int i = 0;
		Direction direction;
		WALLS.clear();
		for (AABB axis : axes) {
			direction = Direction.from2DDataValue(i++);
			if (collidesWithBlock(player.getCommandSenderWorld(), axis)) {
				WALLS.add(direction);
				player.horizontalCollision = true;
			}
		}
		
	}
	
	/**
	 * Returns the direction of the next wall that can being clinged to relative to the player.
	 * @return The direction of the next wall that can being clinged to relative to the player.
	 */
	private static Direction getNextClingDirection() {
		return WALLS.isEmpty() ? Direction.UP : WALLS.iterator().next();
	}
	
	/**
	 * Returns the {@link BlockPos} of the wall block that is being jumped against.
	 * @param player The player to get the position of.
	 * @return The {@link BlockPos} of the wall block that is being jumped against.
	 */
	private static BlockPos getWallPos(Player player, Direction clingDir) {
		BlockPos pos = new BlockPos(player.position()).offset(clingDir.getNormal());
		return player.getCommandSenderWorld().getBlockState(pos).getMaterial().isSolid() ? pos : pos.offset(Direction.UP.getNormal());
	}
	
	/**
	 * Emulates jumping with the given upward force, changing the sound based on isWallJump. If isWallJump is false, this increments CurrentJumps.
	 * @param player The player who is jumping.
	 * @param up The upward force.
	 * @param isWallJump Whether or not this jump was prompted by a wall jump.
	 */
	public static void performJump(LocalPlayer player, float up, @Nullable BlockPos wallPos, boolean isWallJump) {
		float strafe = Math.signum(player.input.leftImpulse) * up * up;
		float forward = Math.signum(player.input.forwardImpulse) * up * up;
		
		float f = 1.0F / Mth.sqrt(strafe * strafe + up * up + forward * forward);
		
		strafe = strafe * f;
		forward = forward * f;
		
		float f1 = Mth.sin(player.yHeadRot * 0.017453292F) * 0.45f;
		float f2 = Mth.cos(player.yHeadRot * 0.017453292F) * 0.45f;
		
		int jumpBoostLevel = 0;
		MobEffectInstance jumpBoostEffect = player.getEffect(MobEffect.byId(8));
		if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;
		
		float newMotionY = up + (jumpBoostLevel * .125f);
		float newMotionX = (float) (player.getDeltaMovement().x + strafe * f2 - forward * f1) / LATERAL_DECAY_JUMP;
		float newMotionZ = (float) (player.getDeltaMovement().z + forward * f2 + strafe * f1) / LATERAL_DECAY_JUMP;
		player.setDeltaMovement(newMotionX, newMotionY, newMotionZ);
		
		if (isWallJump) {
			SpiritSoundPlayer.playWallJumpSound(player, wallPos);
		} else {
			currentJumps++;
			SpiritSoundPlayer.playJumpSound(player, currentJumps);
		}
	}
	
	public static void onKeyPressed(MovementInputUpdateEvent evt) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null) return;
		if (!SpiritIdentifier.isSpirit(player)) return;
		
		if (evt.getInput().jumping && !waitingOnSpaceRelease) {
			waitingOnSpaceRelease = true;
			if (player.isOnGround() || player.isInWaterOrBubble() || player.isInLava()) {
				isClingingToWall = false;
				return;
			}
			if (!SpiritJump.performWallJump(player)) performMultiJump(player);
		} else if (!evt.getInput().jumping) {
			waitingOnSpaceRelease = false;
		}
	}
	
	public static void onPlayerTicked(TickEvent.PlayerTickEvent evt) {
		Minecraft minecraft = Minecraft.getInstance();
		if (evt.player != minecraft.player) return;
		
		if (CLING_BIND.isDown()) {
			if (minecraft.screen != null) return;
			if (evt.player.isOnGround() || evt.player.isInWaterOrBubble() || evt.player.isInLava()) {
				isClingingToWall = false;
				return;
			}
			tryPerformWallCling(evt.player);
		} else {
			isClingingToWall = false;
		}
		
		if (evt.player.isOnGround() || isClingingToWall) {
			currentJumps = 0;
		}
	}
	
	// Wall jumping and multi-jumping should not raise this event.
	public static void onEntityJumped(LivingEvent.LivingJumpEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity instanceof Player player) {
			if (SpiritIdentifier.isSpirit(player) && currentJumps == 0) {
				currentJumps++;
				player.push(0, 0.2, 0);
				SpiritSoundPlayer.playJumpSound(player, currentJumps);
			}
		}
	}
    
    /*
    @SubscribeEvent
    @Deprecated
	public static void onEntityFell(LivingFallEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity instanceof PlayerEntity) {
			if (SpiritIdentifier.isSpirit(entity, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
				PlayerEntity player = (PlayerEntity)entity;
				SpiritSoundPlayer.playFallSound(player, event.getDistance());
			}
			CurrentJumps = 0;
		}
	}
	*/
}
