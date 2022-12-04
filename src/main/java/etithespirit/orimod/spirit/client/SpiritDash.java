package etithespirit.orimod.spirit.client;


import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;

/**
 * Controls all functions of spirits performing a dash.
 * @author Eti
 *
 */
public final class SpiritDash {
	
	public static final KeyMapping DASH_BIND = new KeyMapping("input.etimod.dash", 341, "key.categories.movement"); // Bind dash to l-ctrl
	
	// private static boolean IsHoldingDash = false;
	private static int ticksSinceLastDash = 10;
	private static final int DASH_DELAY = ticksSinceLastDash;
	private static final float DASH_SPEED_GND = 3.0f;
	private static final float DASH_SPEED_AIR = 0.5f;
	private static boolean hasAirDashed = false;
	private static boolean needsToReleaseDash = false;
	private static Vec3 velocityBeforeLatestDash = null;
	
	@SuppressWarnings("unused")
	public static void onKeyPressed(InputEvent.Key evt) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null) return;
		if (minecraft.screen != null) return;
		
		
		boolean isInFluid = player.isInWaterOrBubble() || player.isInLava();
		if (player.isOnGround() || isInFluid) hasAirDashed = false; // On the ground? Reset air dash.
		if (!SpiritIdentifier.isSpirit(player)) return;
		
		// isPressed doesn't get along very well.
		if (DASH_BIND.isDown()) {
			if (needsToReleaseDash) return;
			needsToReleaseDash = true;
			if (ticksSinceLastDash >= DASH_DELAY) {
				performDash(player);
			}
		} else {
			needsToReleaseDash = false;
		}
	}
	
	public static void onClientUpdated(TickEvent.ClientTickEvent evt) {
		if (evt.phase == TickEvent.Phase.START) return;
		
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null) return;
		
		if (ticksSinceLastDash == 8 && velocityBeforeLatestDash != null) {
			player.setDeltaMovement(velocityBeforeLatestDash.x, player.getDeltaMovement().y, velocityBeforeLatestDash.z);
		}
		ticksSinceLastDash++;
		
		// Just to prevent overflow. Keep the number from counting too high.
		if (ticksSinceLastDash >= 1000) {
			ticksSinceLastDash = DASH_DELAY;
		}
	}
	
	private static void performDash(Player player) {
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
