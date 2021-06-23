package etithespirit.etimod.client.player.spiritbehavior;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import etithespirit.etimod.client.audio.SpiritSoundPlayer;
import etithespirit.etimod.info.spirit.SpiritData;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public final class SpiritJump {
	
	//public static final KeyBinding CLIMB_BIND = new KeyBinding("input.etimod.climb", 340, "key.categories.movement"); // Bind climb to l-shift
	public static final KeyBinding CLING_BIND = new KeyBinding("input.etimod.cling", 32, "key.categories.movement"); // Bind cling to space
	
	/* Whether or not the player is currently clinging to a wall. */
    private static boolean isClingingToWall = false;
	
	/** The maximum amount of jumps that the player can perform, including the ground jump. */
	private static final int MAX_JUMPS = 3;
	
	/** The amount of jumps the player has performed, counting their ground jump. */
	private static int currentJumps = 0;
	
	/** Speed on the Y axis will be divided by this every tick when clinging to a wall and sneaking. */
	private static final float VERTICAL_SPEED_DECAY = 1.17f;
	
	/** Speed on the X and Z axes will be divided by this when determining the velocity to jump on walls at. */
	private static final float LATERAL_DECAY = 2.5f;
	
	/** Speed on the X and Z axes will be divided by this when performing a double or triple jump. */
	private static final float LATERAL_DECAY_JUMP = 1.5f;
	
	/** The maximum descending speed (so it should be negative) for wall sliding. */
	private static final float WALL_SLIDE_SPEED = -0.1f;
	
	/** The upward force applied to wall / multi jumps. */
	private static final float UPWARD_FORCE = 0.55f;
	
	/** Storage of the walls adjacent to the player. */
	private static final Set<Direction> WALLS = new HashSet<Direction>();
	
	/** Whether or not the system is waiting on space to be released. */
	private static boolean waitingOnSpaceRelease = false;
	
	/**
	 * Returns whether or not the given AxisAlignedBB collides with any blocks in the given world.
	 * @param world
	 * @param box
	 * @return
	 */
	private static boolean CollidesWithBlock(World world, AxisAlignedBB box) {
		return !world.noCollision(box);
    }
	
	/**
	 * Returns the Y motion of the player as a float.
	 * @param player
	 * @return
	 */
    private static float getDeltaMovementY(ClientPlayerEntity player) {
    	return (float)player.getDeltaMovement().y;
    }
    
    /**
     * Sets the Y motion of the player.
     * @param player
     * @param y
     */
    private static void setDeltaMovementY(ClientPlayerEntity player, float y) {
    	player.setDeltaMovement(player.getDeltaMovement().x, y, player.getDeltaMovement().z);
    }

    /**
     * New wall jump behavior derived from the Wall Jump mod. Returns whether or not a wall jump was actually performed.
     * Generally speaking, if this returns false, PerformMultiJump should be called instead.
     * @param pl
     * @return
     */
    public static boolean PerformWallJump(ClientPlayerEntity pl) {
    	if (!CanWallCling(pl)) return false;
    	UpdateWalls(pl);
    	
    	for (int idx = 0; idx < WALLS.size(); idx++) {
	    	Direction clingDir = GetNextClingDirection();
	    	if (clingDir == Direction.UP) return false;
	    	BlockPos wallPos = GetWallPos(pl, clingDir);
	    	if (!IsWalkingTowards(pl, wallPos)) continue;
			BlockState wallBlock = GetWallBlock(pl, clingDir);
			PerformJump(pl, UPWARD_FORCE, wallBlock, wallPos, true);
			return true;
    	}
    	return false;
    }
    
	private static void PerformMultiJump(ClientPlayerEntity client) {
		if (currentJumps >= MAX_JUMPS) return;
		if (client.isOnGround()) return; // No special stuffs if we're on the ground.
		if (getDeltaMovementY(client) > 0.25) return; // Prevent spam
		PerformJump(client, UPWARD_FORCE, null, null, false);
	}
	
	/**
	 * Attempts to cling to a wall, which reduces Y velocity quickly over time. As such, this should be called in a ticker.
	 * @param pl
	 */
	public static void TryPerformWallCling(ClientPlayerEntity pl) {
		if (!CanWallCling(pl)) {
			isClingingToWall = false;
			return;
		}
		UpdateWalls(pl);
		if (WALLS.size() == 0) {
			isClingingToWall = false;
			return;
		}
		
		isClingingToWall = true;
		float y = getDeltaMovementY(pl);
		if (y <= -0.1) {
			setDeltaMovementY(pl, y / VERTICAL_SPEED_DECAY);
		} else if (y < 0 && y > -0.1) {
			setDeltaMovementY(pl, -0.1f);
		}
	}
    
	/**
	 * Returns whether or not the current player is walking towards the given block position.
	 * @param pl
	 * @param pos
	 * @return
	 */
    private static boolean IsWalkingTowards(ClientPlayerEntity pl, BlockPos pos) {
    	Vector3d moveDir = pl.getDeltaMovement();//pl.movementInput.getMoveVector();
    	if (moveDir.x == 0 && moveDir.z == 0) return false; // Not moving.
    	
    	Vector3d plPos = pl.position();
    	if (pos.getX() == plPos.x && pos.getY() == plPos.y && pos.getZ() == plPos.z) return false; // Position is identical.
    	Vector3d moveDir3D = new Vector3d(moveDir.x, 0, moveDir.z);
    	moveDir3D.normalize();
    	
    	
    	// Below: use player pos Y to get 2D direction (set the Y to be identical)
    	Vector3d dirToBlock = (new Vector3d(pos.getX(), plPos.y, pos.getZ())).subtract(plPos);
    	dirToBlock.normalize();
    	
    	return (moveDir3D.dot(dirToBlock) > 0.2);
    }
    
    /**
     * Observes the player's motion and determines if it's possible for them to cling to the wall.
     * @param pl
     * @return
     */
    private static boolean CanWallCling(ClientPlayerEntity pl) {
    	if (pl.onClimbable() || getDeltaMovementY(pl) > 0.8) return false; // On a ladder or moving up super fast upward
    	if (pl.isInWaterOrBubble() || pl.isInLava()) return false; // In water, lava, or a bubble column
        if (CollidesWithBlock(pl.getCommandSenderWorld(), pl.getBoundingBox().move(0, -0.8, 0))) return false; // Too close to the ground.
        return true;
    }

    /**
     * Observes the walls that are surrounding the player and populates them into the walls array.
     * @param pl
     */
    private static void UpdateWalls(ClientPlayerEntity pl) {
        AxisAlignedBB box = new AxisAlignedBB(pl.getX() - 0.001, pl.getY(), pl.getZ() - 0.001, pl.getX() + 0.001, pl.getY() + pl.getEyeHeight(), pl.getZ() + 0.001);

        double dist = (pl.getBbWidth() / 2) + (isClingingToWall ? 0.2 : 0.06);
        AxisAlignedBB[] axes = {box.expandTowards(0, 0, dist), box.expandTowards(-dist, 0, 0), box.expandTowards(0, 0, -dist), box.expandTowards(dist, 0, 0)};

        int i = 0;
        Direction direction;
        WALLS.clear();
        for (AxisAlignedBB axis : axes) {
            direction = Direction.from2DDataValue(i++);
            if (CollidesWithBlock(pl.getCommandSenderWorld(), axis)) {
            	WALLS.add(direction);
                pl.horizontalCollision = true;
            }
        }

    }

    /**
     * Returns the direction of the next wall that can being clinged to relative to the player.
     * @return
     */
    private static Direction GetNextClingDirection() {
        return WALLS.isEmpty() ? Direction.UP : WALLS.iterator().next();
    }

    /**
     * Returns the BlockPos of the wall block that is being jumped against.
     * @param player
     * @return
     */
    private static BlockPos GetWallPos(ClientPlayerEntity player, Direction clingDir) {
        BlockPos pos = new BlockPos(player.position()).offset(clingDir.getNormal());
        return player.getCommandSenderWorld().getBlockState(pos).getMaterial().isSolid() ? pos : pos.offset(Direction.UP.getNormal());
    }
    
    /**
     * Returns the wall block that is being jumped against.
     * @param player
     * @return
     */
    private static BlockState GetWallBlock(ClientPlayerEntity player, Direction clingDir) {
        BlockPos pos = new BlockPos(player.position()).offset(clingDir.getNormal());
        return player.getCommandSenderWorld().getBlockState(pos);
    }

    /**
 	 * Emulates jumping with the given upward force, changing the sound based on isWallJump. If isWallJump is false, this increments CurrentJumps.
     * @param pl
     * @param up
     * @param wallBlock Only needed for isWallJump = true, used to get the sound.
     * @param isWallJump Whether or not this jump was prompted by a wall jump.
     */
    public static void PerformJump(ClientPlayerEntity pl, float up, @Nullable BlockState wallBlock, @Nullable BlockPos wallPos, boolean isWallJump) {
        float strafe = (float)Math.signum(pl.input.leftImpulse) * up * up;
        float forward = (float)(Math.signum(pl.input.forwardImpulse) * up * up);

        float f = 1.0F / MathHelper.sqrt(strafe * strafe + up * up + forward * forward);
        
        strafe = strafe * f;
        forward = forward * f;
        
        float f1 = MathHelper.sin(pl.yHeadRot * 0.017453292F) * 0.45f;
        float f2 = MathHelper.cos(pl.yHeadRot * 0.017453292F) * 0.45f;

        int jumpBoostLevel = 0;
        EffectInstance jumpBoostEffect = pl.getEffect(Effect.byId(8));
        if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;

        float newMotionY = up + (jumpBoostLevel * .125f);
        float newMotionX = (float) (pl.getDeltaMovement().x + strafe * f2 - forward * f1) / LATERAL_DECAY_JUMP;
        float newMotionZ = (float) (pl.getDeltaMovement().z + forward * f2 + strafe * f1) / LATERAL_DECAY_JUMP;
        pl.setDeltaMovement(newMotionX, newMotionY, newMotionZ);
        
        if (isWallJump) {
        	SpiritSoundPlayer.playWallJumpSound(pl, wallPos);
        } else {
        	currentJumps++;
        	SpiritSoundPlayer.playJumpSound(pl, currentJumps);
        }
    }
    
	public static void onKeyPressed(InputUpdateEvent evt) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		if (player == null) return;
		if (!SpiritData.isSpirit(player)) return;
		
		if (evt.getMovementInput().jumping && !waitingOnSpaceRelease) {
			waitingOnSpaceRelease = true;
			if (player.isOnGround() || player.isInWaterOrBubble() || player.isInLava()) {
	    		isClingingToWall = false;
		    	return;
	    	}
			if (!SpiritJump.PerformWallJump(player)) PerformMultiJump(player);
		} else if (!evt.getMovementInput().jumping) {
			waitingOnSpaceRelease = false;
		}
	}
    
    public static void onPlayerTicked(PlayerTickEvent evt) {
    	Minecraft minecraft = Minecraft.getInstance();
    	if (evt.player != minecraft.player) return;
    	
    	if (CLING_BIND.isDown()) {
    		if (minecraft.screen != null) return;
    		if (evt.player.isOnGround() || evt.player.isInWaterOrBubble() || evt.player.isInLava()) {
	    		isClingingToWall = false;
		    	return;
	    	}
    		TryPerformWallCling((ClientPlayerEntity)evt.player);
    	} else {
    		isClingingToWall = false;
    	}
    	
    	if (evt.player.isOnGround() || isClingingToWall) {
    		currentJumps = 0;
    	}
    }
    
	// Wall jumping and multi-jumping should not raise this event.
    @SubscribeEvent
	public static void onEntityJumped(LivingJumpEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)entity;
			if (SpiritData.isSpirit(player) && currentJumps == 0) {
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
