package etithespirit.orimod.spirit.common;

import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.HashMap;
import java.util.UUID;

public final class Motion {
	
	private static final HashMap<UUID, Motion> MOTION_TRACKER_BINDINGS = new HashMap<>();
	
	private static final int DASH_DELAY = 10;
	private static final float DASH_SPEED_GND = 3.0f;
	private static final float DASH_SPEED_AIR = 0.5f;
	
	private int ticksSinceLastDash = DASH_DELAY;
	private boolean hasAirDashed = false;
	private Vec3 velocityBeforeLatestDash = null;
	private final Player player;
	
	private Motion(Player plr) {
		MinecraftForge.EVENT_BUS.addListener(this::onPlayerUpdated);
		player = plr;
		MOTION_TRACKER_BINDINGS.put(plr.getUUID(), this);
	}
	
	public static Motion get(Player plr) {
		return MOTION_TRACKER_BINDINGS.getOrDefault(plr.getUUID(), new Motion(plr));
	}
	
	public void onPlayerUpdated(TickEvent.PlayerTickEvent evt) {
		if (evt.phase == TickEvent.Phase.START) return;
		if (player == null) return;
		if (player.isRemoved()) return;
		
		if (ticksSinceLastDash == 8 && velocityBeforeLatestDash != null) {
			player.setDeltaMovement(velocityBeforeLatestDash.x, player.getDeltaMovement().y, velocityBeforeLatestDash.z);
		}
		ticksSinceLastDash++;
		
		// Just to prevent overflow. Keep the number from counting too high.
		if (ticksSinceLastDash >= 1000) {
			ticksSinceLastDash = DASH_DELAY;
		}
	}
	
	private void tryPerformDash() {
		boolean isInFluid = player.isInWaterOrBubble() || player.isInLava();
		
		Vec3 movementClamp = isInFluid ? new Vec3(1, 1, 1) : new Vec3(1, 0, 1);
		Vec3 movement = player.getLookAngle().multiply(movementClamp).normalize();
		
		if (player.isOnGround() || isInFluid) {
			// on ground
			movement.scale(DASH_SPEED_GND);
		} else {
			if (!hasAirDashed) {
				movement.scale(DASH_SPEED_AIR);
				hasAirDashed = true;
			} else {
				return; // Abort.
			}
		}
		velocityBeforeLatestDash = player.getDeltaMovement();
		if (!isInFluid) {
			player.setDeltaMovement(velocityBeforeLatestDash.x + movement.x, 0.02, velocityBeforeLatestDash.z + movement.z);
		} else {
			player.setDeltaMovement(velocityBeforeLatestDash.x + movement.x, velocityBeforeLatestDash.y + movement.y, velocityBeforeLatestDash.z + movement.z);
		}
		
		
		// TODO: Detect close walls and swap sounds based on whether or not the player will hit it.
		final boolean willImmediatelyImpactWall = false;
		
		if (!isInFluid) {
			SpiritSoundPlayer.playDashSound(player, willImmediatelyImpactWall);
		}
		ticksSinceLastDash = 0;
	}
	
}
