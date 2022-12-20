package etithespirit.orimod.spirit;

import etithespirit.orimod.annotation.NetworkReplicated;
import etithespirit.orimod.annotation.NotNetworkReplicated;
import etithespirit.orimod.common.capabilities.SpiritCapabilities;
import etithespirit.orimod.networking.spirit.ReplicateSpiritStatus;
import etithespirit.orimod.player.EffectEnforcement;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;


/**
 * This class manages the identification of spirits. It also stores which players are opting to use a spirit playermodel.
 */
public final class SpiritIdentifier {
	
	/**
	 * Returns whether or not the given entity classifies as a spirit.
	 * @param entity The entity to test.
	 * @return True if the entity is either an instance of the Spirit mob, or is a player who has elected to use the spirit playermodel.
	 */
	@NetworkReplicated
	public static boolean isSpirit(Entity entity) {
		if (entity instanceof Player player) {
			Optional<SpiritCapabilities> caps = SpiritCapabilities.getCaps(player);
			return caps.map(SpiritCapabilities::isSpirit).orElse(false);
		}
		return false;
	}
	
	/**
	 * Tells the system that the given player does (not) want to be a spirit.
	 * @param player The player to change.
	 * @param isSpirit The new state, whether or not they are a spirit.
	 * @returns True if the player was changed on this side, false if not due to missing capabilities.
	 */
	@NotNetworkReplicated
	public static boolean setSpirit(Player player, boolean isSpirit) {
		Optional<SpiritCapabilities> caps = SpiritCapabilities.getCaps(player);
		if (caps.isPresent()) {
			caps.get().setSpirit(isSpirit);
			player.refreshDimensions();
			EffectEnforcement.updatePlayerAttrs(player);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Identical to {@link #setSpirit(Player, boolean)}, but this will send the appropriate network request for both sides.
	 * @param player The player to change.
	 * @param isSpirit The new spirit state.
	 */
	@NetworkReplicated
	public static void setSpiritNetworked(Player player, boolean isSpirit) {
		if (player.level.isClientSide) {
			if (!player.isLocalPlayer()) {
				throw new IllegalArgumentExceptionButItHasAComicallyLargeNameToDrasticallyIncreaseTheIronyPresentInThisReallyTerribleForcedExceptionMethodBecauseYouCalledTheNetworkingMethodWithTheWrongArgument();
			}
			setSpirit(player, isSpirit);
			ReplicateSpiritStatus.Client.askToSetSpiritStatusAsync(isSpirit);
		} else {
			ReplicateSpiritStatus.Server.tellEveryonePlayerSpiritStatus(player, isSpirit);
		}
	}
	
	// evil code be like
	// TODO: don't fix
	private static final class IllegalArgumentExceptionButItHasAComicallyLargeNameToDrasticallyIncreaseTheIronyPresentInThisReallyTerribleForcedExceptionMethodBecauseYouCalledTheNetworkingMethodWithTheWrongArgument extends IllegalArgumentException {}
	
}
