package etithespirit.orimod.api.spirit;

import etithespirit.orimod.annotation.NetworkReplicated;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This class allows API users to access whether or not players are spirits.
 */
@Mod.EventBusSubscriber
public final class SpiritAccessor {
	
	private static boolean real = false;
	private static boolean locked = false;
	
	private static Function<Player, Boolean> isSpiritMtd = null;
	private static BiConsumer<Player, Boolean> setSpiritMtd = null;
	
	/**
	 * Returns whether or not the real methods are present, as opposed to dummy methods that always return default values or do nothing.
	 * @return Whether or not the mod is installed and has registered the real-deal methods for this.
	 */
	public static boolean hasRealMethods() {
		return real;
	}
	
	/**
	 * Returns whether or not the given player, by ID, is a spirit.
	 * @param playerId The UUID of the player.
	 * @return Whether or not the given player ID is registered as a spirit. Always returns false if {@link #hasRealMethods()} returns false.
	 */
	public static boolean isSpirit(Player playerId) {
		if (isSpiritMtd != null) {
			return isSpiritMtd.apply(playerId);
		}
		return false;
	}
	
	/**
	 * Attempts to set the Spirit state of the player.
	 * @param playerId The ID of the player to change.
	 * @param isSpirit Whether or not they should be a spirit.
	 */
	public static @NetworkReplicated(clientToServer = true) void setSpirit(Player playerId, boolean isSpirit) {
		if (setSpiritMtd != null) {
			setSpiritMtd.accept(playerId, isSpirit);
		}
	}
	
	/**
	 * <strong>Strictly for internal use only, attempting to call this method will raise an exception.</strong>
	 * @param isSpirit The isSpirit method.
	 * @param setSpirit The networked setSpirit method.
	 */
	public static void _setMethods(Function<Player, Boolean> isSpirit, BiConsumer<Player, Boolean> setSpirit) throws IllegalCallerException {
		if (locked) throw new IllegalCallerException("This method is strictly for internal use only.");
		locked = true;
		real = true;
		isSpiritMtd = isSpirit;
		setSpiritMtd = setSpirit;
	}
	
	@SubscribeEvent
	public static void onInit(FMLCommonSetupEvent evt) {
		evt.enqueueWork(() -> {
			real = locked; // The system is only locked if _setMethods has been called so this works nicely.
		});
	}
}
