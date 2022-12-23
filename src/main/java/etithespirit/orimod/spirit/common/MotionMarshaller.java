package etithespirit.orimod.spirit.common;

import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import etithespirit.orimod.common.capabilities.SpiritCapabilities;
import etithespirit.orimod.modinterop.overdrive_that_matters.OTMAndroidInterop;
import etithespirit.orimod.networking.player.ReplicatePlayerMovement;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Manages all code that performs on both the client and the server to move players using certain abilities.
 */
public final class MotionMarshaller {
	
	/** The upward force applied to wall / multi jumps. */
	private static final float AIRJUMP_UPWARD_FORCE = 0.55f;
	
	/** AIRJUMP_UPWARD_FORCE ^ 2 */
	private static final float AIRJUMP_UPWARD_FORCE_POW2 = AIRJUMP_UPWARD_FORCE * AIRJUMP_UPWARD_FORCE;
	
	/** Force of a dash whilst on the ground. */
	private static final float DASH_SPEED_GND = 3.0f;
	
	/** Force of a dash whilst in the air. */
	private static final float DASH_SPEED_AIR = 0.5f;
	
	/** Speed on the Y axis will be divided by this every tick when clinging to a wall and sneaking. */
	private static final float VERTICAL_SPEED_DECAY = 1.17f;
	
	/** Speed on the X and Z axes will be divided by this when performing a double or triple jump. */
	private static final float LATERAL_DECAY_JUMP = 1.5f;
	
	
	/**
	 * Attempts to cling to a wall, which reduces Y velocity quickly over time. As such, this should be called in a ticker.
	 * @param player The player who is doing this.
	 */
	public static void tryDoWallClingSlowdown(Player player) {
		Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
		if (capsCtr.isEmpty()) return;
		
		SpiritCapabilities caps = capsCtr.get();
		boolean ok = caps.isClinging(player);
		if (!ok) return;
		
		Set<Direction> walls = Utilities.getWalls(player);
		if (walls.isEmpty()) return;
		
		float y = Utilities.getDeltaMovementY(player);
		if (y <= -0.1) {
			Utilities.setDeltaMovementY(player, y / VERTICAL_SPEED_DECAY);
		} else if (y < 0 && y > -0.1) {
			Utilities.setDeltaMovementY(player, -0.1f);
		}
	}
	
	public static void tryPerformJump(Player player, float leftImpulse, float forwardImpulse, @Nullable BlockPos wallPos) {
		if (player.isSwimming()) return;
		
		Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
		if (capsCtr.isEmpty()) return;
		
		SpiritCapabilities caps = capsCtr.get();
		boolean doWallJump = wallPos != null;
		boolean ok = doWallJump ? caps.tryWallJump(player) : caps.tryAirJump(player);
		if (!ok && doWallJump) {
			// Player could not wall jump. Maybe they can air jump as a substitute?
			ok = caps.tryAirJump(player);
			doWallJump = false; // Set this to false so that the right multijump sound plays.
		}
		if (ok) {
			float strafe = Math.signum(leftImpulse) * AIRJUMP_UPWARD_FORCE_POW2;
			float forward = Math.signum(forwardImpulse) * AIRJUMP_UPWARD_FORCE_POW2;
			float jumpPower = Mth.fastInvSqrt((strafe * strafe) + AIRJUMP_UPWARD_FORCE_POW2 + (forward * forward));
			strafe *= jumpPower;
			forward *= jumpPower;
			
			float f1 = Mth.sin(player.yHeadRot * 0.017453292F) * 0.45f;
			float f2 = Mth.cos(player.yHeadRot * 0.017453292F) * 0.45f;
			
			int jumpBoostLevel = 0;
			MobEffectInstance jumpBoostEffect = player.getEffect(MobEffect.byId(8));
			if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;
			
			float newMotionY = AIRJUMP_UPWARD_FORCE + (jumpBoostLevel * .125f);
			float newMotionX = (float) (player.getDeltaMovement().x + strafe * f2 - forward * f1) / LATERAL_DECAY_JUMP;
			float newMotionZ = (float) (player.getDeltaMovement().z + forward * f2 + strafe * f1) / LATERAL_DECAY_JUMP;
			player.setDeltaMovement(newMotionX, newMotionY, newMotionZ);
			
			if (doWallJump) {
				SpiritSoundPlayer.playWallJumpSound(player, wallPos);
			} else {
				SpiritSoundPlayer.playJumpSound(player, caps.getAirJumpIndex() + 1); // +1 turns it from air jumps to land jumps
			}
		}
		
	}
	
	public static void tryPerformDashCommon(Player player) {
		
		Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
		if (capsCtr.isEmpty()) return;
		
		SpiritCapabilities caps = capsCtr.get();
		
		boolean isInFluid = player.isInWaterOrBubble() || player.isInLava();
		Vec3 movementClamp = isInFluid ? new Vec3(1, 1, 1) : new Vec3(1, 0, 1);
		Vec3 movement = player.getLookAngle().multiply(movementClamp).normalize();
		
		Vec3 velocityBeforeLatestDash = player.getDeltaMovement();
		if (player.isOnGround() || isInFluid) {
			boolean dashResult = isInFluid ? caps.tryWaterDash(player) : caps.tryGroundDash(player);
			if (!dashResult) return;
			// on ground
			movement.scale(DASH_SPEED_GND * OTMAndroidInterop.getDashSpeedBoost(player));
		} else {
			if (!caps.tryAirDash(player)) return;
			// in air
			movement.scale(DASH_SPEED_AIR * OTMAndroidInterop.getDashSpeedBoost(player));
		}
		
		if (!isInFluid) {
			player.setDeltaMovement(velocityBeforeLatestDash.x + movement.x, 0.02, velocityBeforeLatestDash.z + movement.z);
		} else {
			player.setDeltaMovement(velocityBeforeLatestDash.x + movement.x, velocityBeforeLatestDash.y + movement.y, velocityBeforeLatestDash.z + movement.z);
		}
		
		if (!isInFluid) {
			SpiritSoundPlayer.playDashSound(player, false);
		}
	}
	
	public static void trySetWallCling(Player player, boolean wantsToCling) {
		Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
		if (capsCtr.isEmpty()) return;
		
		SpiritCapabilities caps = capsCtr.get();
		caps.setClingDesire(wantsToCling);
	}
	
	// Wall jumping and multi-jumping should not raise this event.
	public static void onEntityJumped(LivingEvent.LivingJumpEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player player) {
			player.push(0, 0.2, 0);
			SpiritSoundPlayer.playJumpSound(player, 1);
			
			Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
			if (capsCtr.isEmpty()) return;
			SpiritCapabilities caps = capsCtr.get();
			caps.triggerAirJumpCooldown();
		}
	}
	
	public static class Client {
		
		public static void tryPerformDash() {
			LocalPlayer plr = Minecraft.getInstance().player;
			if (plr == null) return;
			ReplicatePlayerMovement.Client.doDash();
			MotionMarshaller.tryPerformDashCommon(plr);
		}
		
		public static void tryJump() {
			LocalPlayer plr = Minecraft.getInstance().player;
			if (plr == null) return;
			
			BlockPos wallPos = Utilities.getBlockForBestWall(plr);
			if (wallPos == null) {
				ReplicatePlayerMovement.Client.doAirJump();
			} else {
				ReplicatePlayerMovement.Client.doWallJump();
			}
			MotionMarshaller.tryPerformJump(plr, plr.input.leftImpulse, plr.input.forwardImpulse, wallPos);
		}
		
		public static void tryCling(boolean cling) {
			LocalPlayer plr = Minecraft.getInstance().player;
			if (plr == null) return;
			LazyOptional<SpiritCapabilities> capsCtr = plr.getCapability(SpiritCapabilities.INSTANCE);
			if (capsCtr.isPresent()) {
				Optional<SpiritCapabilities> capsOpt = capsCtr.resolve();
				if (capsOpt.isEmpty()) return;
				capsOpt.get().setClingDesire(cling);
				ReplicatePlayerMovement.Client.replicateClingDesire(cling);
			}
		}
		
		public static void onClientTick(TickEvent.PlayerTickEvent.ClientTickEvent tickEvent) {
			if (tickEvent.phase == TickEvent.Phase.END) return;
			LocalPlayer plr = Minecraft.getInstance().player;
			if (plr == null) return;
			MotionMarshaller.tryDoWallClingSlowdown(plr);
			Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(plr);
			if (capsCtr.isPresent()) {
				SpiritCapabilities caps = capsCtr.get();
				if (plr.isOnGround() || plr.isInWater()) {
					caps.landed();
				}
				caps.tick(plr);
			}
		}
		
	}
	
	public static class Server {
	
		public static void onServerTick(TickEvent.PlayerTickEvent.ServerTickEvent tickEvent) {
			tickEvent.getServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
				MotionMarshaller.tryDoWallClingSlowdown(serverPlayer);
				Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(serverPlayer);
				if (capsCtr.isPresent()) {
					SpiritCapabilities caps = capsCtr.get();
					if (serverPlayer.isOnGround() || serverPlayer.isInWater()) {
						caps.landed();
					}
					caps.tick(serverPlayer);
				}
			});
		}
	}
	
	
	public static class Utilities {
		
		/**
		 * Returns whether or not the given AxisAlignedBB collides with any blocks in the given world.
		 * @param world The world to test in.
		 * @param box The box to test for collision.
		 * @return Whether or not the AABB collides with the world.
		 */
		public static boolean collidesWithBlock(Level world, AABB box) {
			return !world.noCollision(box);
		}
		
		/**
		 * Returns the Y motion of the player as a float.
		 * @param player The player to test.
		 * @return The motion of the player on the Y axis.
		 */
		public static float getDeltaMovementY(Player player) {
			return (float)player.getDeltaMovement().y;
		}
		
		/**
		 * Sets the Y motion of the player.
		 * @param player The player to edit.
		 * @param y The new y component of the player's movement.
		 */
		public static void setDeltaMovementY(Player player, float y) {
			player.setDeltaMovement(player.getDeltaMovement().x, y, player.getDeltaMovement().z);
		}
		
		
		/**
		 * Observes the walls that are surrounding the player and populates them into the walls array.
		 * @param player The player to do this update for.
		 */
		public static Set<Direction> getWalls(Player player) {
			AABB box = new AABB(player.getX() - 0.001, player.getY(), player.getZ() - 0.001, player.getX() + 0.001, player.getY() + player.getEyeHeight(), player.getZ() + 0.001);
			
			double dist = (player.getBbWidth() / 2) + 0.13;
			AABB[] axes = {box.expandTowards(0, 0, dist), box.expandTowards(-dist, 0, 0), box.expandTowards(0, 0, -dist), box.expandTowards(dist, 0, 0)};
			
			int i = 0;
			Direction direction;
			Set<Direction> walls = new HashSet<>();
			for (AABB axis : axes) {
				direction = Direction.from2DDataValue(i++);
				if (collidesWithBlock(player.getCommandSenderWorld(), axis)) {
					walls.add(direction);
					player.horizontalCollision = true;
				}
			}
			return walls;
		}
		
		
		/**
		 * Returns the direction of the next wall that can being clinged to relative to the player.
		 * @return The direction of the next wall that can being clinged to relative to the player.
		 */
		public static Direction getNextClingDirection(Set<Direction> walls) {
			return walls.isEmpty() ? Direction.UP : walls.iterator().next();
		}
		
		/**
		 * Returns the next best available block to wall jump off of by its position.
		 * @param plr The player to check for.
		 * @return The best position to use for the block that is being jumped off of, for sounds mainly.
		 */
		public static @Nullable BlockPos getBlockForBestWall(Player plr) {
			Set<Direction> walls = Utilities.getWalls(plr);
			if (walls.isEmpty()) return null;
			
			Direction nextDir = Utilities.getNextClingDirection(walls);
			if (nextDir == Direction.UP) return null;
			
			return plr.blockPosition().relative(nextDir);
		}
	}
}
