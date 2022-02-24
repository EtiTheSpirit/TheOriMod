package etithespirit.orimod.spirit;

import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.networking.spirit.ReplicateSpiritStatus;
import etithespirit.orimod.networking.spirit.SpiritStateReplicationPacket;
import etithespirit.orimod.server.persistence.SpiritPermissions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This class manages the identification of spirits. It also stores which players are opting to use a spirit playermodel.
 */
public final class SpiritIdentifier {
	
	private static final boolean DBG_SPIRIT_ALWAYS_FORCED = false;
	
	/**
	 * Stores the bindings from players (by ID) to whether or not they are a spirit. This map aims to be network-synchronized as best as possible, however
	 * this will not reflect upon changes made by the permissions system (such as forcing everyone to be a spirit).
	 */
	private static final Map<UUID, Boolean> SPIRIT_BINDINGS = new HashMap<>();
	
	/**
	 * Returns whether or not the given entity classifies as a spirit.
	 * @param entity The entity to test.
	 * @return True if the entity is either an instance of the Spirit mob, or is a player who has elected to use the spirit playermodel.
	 */
	public static boolean isSpirit(Entity entity) {
		if (DBG_SPIRIT_ALWAYS_FORCED) return true;
		boolean defaultState = OriModConfigs.DEFAULT_SPIRIT_STATE.get();
		if (OriModConfigs.FORCE_STATE.get()) return defaultState;
		return SPIRIT_BINDINGS.getOrDefault(entity.getUUID(), defaultState);
	}
	
	/**
	 * Returns whether or not the given UUID classifies as a spirit.
	 * @param id The UUID to test.
	 * @return True if the entity is either an instance of the Spirit mob, or is a player who has elected to use the spirit playermodel.
	 */
	public static boolean isSpirit(UUID id) {
		if (DBG_SPIRIT_ALWAYS_FORCED) return true;
		boolean defaultState = OriModConfigs.DEFAULT_SPIRIT_STATE.get();
		if (OriModConfigs.FORCE_STATE.get()) return defaultState;
		return SPIRIT_BINDINGS.getOrDefault(id, defaultState);
	}
	
	/**
	 * Tells the system that the given player does (not) want to be a spirit.
	 * @param player The player to change.
	 * @param isSpirit The new state, whether or not they are a spirit.
	 */
	public static void setSpirit(Player player, boolean isSpirit) {
		SPIRIT_BINDINGS.put(player.getUUID(), isSpirit);
	}
	
	/**
	 * Tells the system that the given player does (not) want to be a spirit.
	 * @param playerID The UUID of the player to change.
	 * @param isSpirit The new state, whether or not they are a spirit.
	 */
	public static void setSpirit(UUID playerID, boolean isSpirit) {
		SPIRIT_BINDINGS.put(playerID, isSpirit);
	}
	
	/**
	 * Identical to {@link #setSpirit(Player, boolean)}, but this will send the appropriate network request for both sides.
	 * @param player The player to change.
	 * @param isSpirit The new spirit state.
	 */
	public static void setSpiritNetworked(Player player, boolean isSpirit) {
		setSpirit(player, isSpirit);
		if (player.level.isClientSide) {
			if (!player.isLocalPlayer()) {
				throw new IllegalArgumentExceptionButItHasAComicallyLargeNameToDrasticallyIncreaseTheIronyPresentInThisReallyTerribleForcedExceptionMethodBecauseYouCalledTheNetworkingMethodWithTheWrongArgument();
			}
			ReplicateSpiritStatus.askToSetSpiritStatusAsync(isSpirit);
		} else {
			ReplicateSpiritStatus.tellEveryonePlayerSpiritStatus(player, isSpirit);
		}
	}
	
	private static final class IllegalArgumentExceptionButItHasAComicallyLargeNameToDrasticallyIncreaseTheIronyPresentInThisReallyTerribleForcedExceptionMethodBecauseYouCalledTheNetworkingMethodWithTheWrongArgument extends IllegalArgumentException {}
	
}
