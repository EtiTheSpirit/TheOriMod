package etithespirit.orimod.modinterop.overdrive_that_matters;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.fml.ModList;
import ru.dbotthepony.mc.otm.android.AndroidFeature;
import ru.dbotthepony.mc.otm.android.AndroidFeatureType;
import ru.dbotthepony.mc.otm.android.feature.LimbOverclockingFeature;
import ru.dbotthepony.mc.otm.capability.MatteryPlayerCapability;
import ru.dbotthepony.mc.otm.registry.AndroidFeatures;

import java.util.Optional;

public final class OTMAndroidInterop {
	
	private static Boolean hasOTM = null;
	private static Capability<MatteryPlayerCapability> otmCapsKey = CapabilityManager.get(new CapabilityToken<>() {});
	
	/**
	 * Returns whether or not the game has Overdrive That Matters installed, updating the cached value if necessary.
	 * @return True if the mod is installed, false if not.
	 */
	private static boolean getHasOTM() {
		if (hasOTM == null) {
			hasOTM = ModList.get().isLoaded("overdrive_that_matters");
		}
		return hasOTM;
	}
	
	/**
	 * This uses OTM's research tree to determine if the player is an android and is capable of swimming via the air bags upgrade.
	 * @param player The player to check.
	 * @return True if the player is not an android, or if the player <em>is</em> an android and has the air bags upgrade.
	 */
	public static boolean androidPlayerHasAirBags(Player player) {
		if (!getHasOTM()) return true;
		return implAndroidPlayerHasAirBags(player);
	}
	
	/**
	 * This uses OTM's research tree to determine what level of movement upgrades the player has. This is used to improve the efficiency of Dash.
	 * The returned value is a float multiplier for the player's speed.
	 * @param player The player to check.
	 * @return A float that is always &gt;= 1, intended to be multiplied with the preset dash speed.
	 */
	public static float getDashSpeedBoost(Player player) {
		int level = getHasOTM() ? implGetAndroidMovementUpgradeLevel(player) : -1;
		// Mod default max is 3. Scale around this.
		if (level == -1) return 1;
		
		level++; // Make it 1-indexed, for math
		return level * 0.625f;
	}
	
	private static boolean implAndroidPlayerHasAirBags(Player player) {
		Optional<MatteryPlayerCapability> capsCtr = player.getCapability(otmCapsKey).resolve();
		if (capsCtr.isPresent()) {
			MatteryPlayerCapability caps = capsCtr.get();
			if (!caps.isAndroid()) return true; // Player is not an android, so they can swim.
			return caps.hasFeature(AndroidFeatures.INSTANCE.getAIR_BAGS());
		}
		
		return true; // If missing for whatever reason, do not limit the player.
	}
	
	private static int implGetAndroidMovementUpgradeLevel(Player player) {
		Optional<MatteryPlayerCapability> capsCtr = player.getCapability(otmCapsKey).resolve();
		if (capsCtr.isPresent()) {
			MatteryPlayerCapability caps = capsCtr.get();
			if (!caps.isAndroid()) return -1; // Player is not an android, so they can swim.
			LimbOverclockingFeature feature = (LimbOverclockingFeature)caps.getFeature(AndroidFeatures.INSTANCE.getLIMB_OVERCLOCKING());
			if (feature == null) return -1;
			
			return feature.getLevel();
		}
		
		return -1;
	}
	
}
