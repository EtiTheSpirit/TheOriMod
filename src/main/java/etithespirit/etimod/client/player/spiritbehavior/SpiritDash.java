package etithespirit.etimod.client.player.spiritbehavior;

import etithespirit.etimod.client.audio.SpiritSoundPlayer;
import etithespirit.etimod.util.spirit.SpiritIdentificationType;
import etithespirit.etimod.util.spirit.SpiritIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Controls all functions of spirits performing a dash.
 * @author Eti
 *
 */
public class SpiritDash {

	public static final KeyBinding DASH_BIND = new KeyBinding("input.etimod.dash", 341, "key.categories.movement"); // Bind dash to l-ctrl
	
	// private static boolean IsHoldingDash = false;
	private static int ticksSinceLastDash = 10;
	private static final int DASH_DELAY = ticksSinceLastDash;
	private static final float DASH_SPEED_GND = 3.0f;
	private static final float DASH_SPEED_AIR = 0.5f;
	private static boolean hasAirDashed = false;
	private static boolean needsToReleaseDash = false;
	private static Vector3d velocityBeforeLatestDash = null;
	
	@SubscribeEvent
	public static void onKeyPressed(InputEvent.KeyInputEvent evt) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		if (player == null) return;
		if (minecraft.screen != null) return;
		
		
		boolean isInFluid = player.isInWaterOrBubble() || player.isInLava();
		if (player.isOnGround() || isInFluid) hasAirDashed = false; // On the ground? Reset air dash.
		if (!SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) return;
		
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
	
	@SubscribeEvent
	public static void onClientUpdated(ClientTickEvent evt) {
		if (evt.phase == TickEvent.Phase.START) return;
		
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
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
	
	private static void performDash(PlayerEntity player) {
		boolean isInFluid = player.isInWaterOrBubble() || player.isInLava();
		
		Vector3d movementClamp = isInFluid ? new Vector3d(1, 1, 1) : new Vector3d(1, 0, 1);
		Vector3d movement = player.getLookAngle().multiply(movementClamp).normalize();		
		
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
		
		
		// TODO: Detect close walls.
		final boolean willImmediatelyImpactWall = false;
		
		if (!isInFluid) {
			SpiritSoundPlayer.playDashSound(player, willImmediatelyImpactWall);
		}
		ticksSinceLastDash = 0;
	}
	
}
