package etithespirit.orimod.spirit.client;

import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.spirit.common.MotionMarshaller;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.InputEvent;

@ClientUseOnly
public final class SpiritInput {
	
	private static final KeyMapping DASH_BIND = new KeyMapping("input.etimod.dash", 341, KeyMapping.CATEGORY_MOVEMENT); // Bind dash to l-ctrl
	private static final KeyMapping CLING_BIND = new KeyMapping("input.etimod.cling", 32, KeyMapping.CATEGORY_MOVEMENT); // Bind cling to space
	private static KeyMapping JUMP = null;
	
	private static boolean waitingToReleaseDash = false;
	private static boolean waitingToReleaseJump = false;
	
	public static void onKeyPressed(InputEvent.Key evt) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (JUMP == null) JUMP = minecraft.options.keyJump;
		if (player == null) return;
		if (minecraft.screen != null) return;
		
		// isPressed doesn't get along very well, that's why I did this
		if (evt.getKey() == DASH_BIND.getKey().getValue()) {
			boolean isDown = DASH_BIND.isDown();
			if (isDown && !waitingToReleaseDash) {
				waitingToReleaseDash = true;
				MotionMarshaller.Client.tryPerformDash();
			} else if (!isDown) {
				waitingToReleaseDash = false;
			}
		}
		if (evt.getKey() == CLING_BIND.getKey().getValue()) {
			MotionMarshaller.Client.tryCling(CLING_BIND.isDown());
		}
		if (evt.getKey() == JUMP.getKey().getValue()) {
			boolean isDown = JUMP.isDown();
			if (isDown && !waitingToReleaseJump) {
				waitingToReleaseJump = true;
				MotionMarshaller.Client.tryJump();
			} else if (!isDown) {
				waitingToReleaseJump = false;
			}
		}
	}
}
