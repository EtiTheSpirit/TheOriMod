package etithespirit.etimod.info.spirit;

import etithespirit.etimod.info.spirit.capabilities.ISpiritCapabilities;
import etithespirit.etimod.info.spirit.capabilities.SpiritGameAbilities;
import etithespirit.etimod.registry.CapabilityRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

/**
 * A class used to access a player's information pertaining to their Spirit state.
 */
public final class SpiritData {
	
	/**
	 * Returns any {@link ISpiritCapabilities} attached to the player, or null if they do not have this capability.
	 * @param player The player to test.
	 * @return The {@link ISpiritCapabilities} on this player, or null if they do not have it.
	 */
	private static ISpiritCapabilities getCapabilitiesFrom(PlayerEntity player) {
		LazyOptional<ISpiritCapabilities> capabilities = player.getCapability(CapabilityRegistry.SPIRIT_CAPABILITIES);
		if (capabilities.isPresent()) {
			return capabilities.resolve().get();
		}
		return null;
	}
	
	/**
	 * Returns whether or not the given player is a spirit. If the player does not have the proper capabilities registered, it returns false.
	 * @param player The player to test.
	 * @return Whether or not this player is a spirit. Always returns false if the player is missing the capabilities.
	 */
	public static boolean isSpirit(PlayerEntity player) {
		ISpiritCapabilities caps = getCapabilitiesFrom(player);
		if (caps == null) return false;
		return caps.getIsSpirit();
	}
	
	/**
	 * Sets the spirit state of the given player. If the player does not have the proper capabilities registered, this does nothing.
	 * @param player The player to modify.
	 * @param isSpirit Whether or not they should be considered a spirit.
	 */
	public static void setSpirit(PlayerEntity player, boolean isSpirit) {
		ISpiritCapabilities caps = getCapabilitiesFrom(player);
		if (caps == null) return;
		caps.setIsSpirit(isSpirit);
	}
	
	/**
	 * Provides access to the abilities of the given player, or null if they are not a spirit or don't have the proper capabilities registered.
	 * @param player The player to get the abilities of.
	 * @return The abilities this player has, or null if they are not a spirit and/or do not have the proper capabilities registered.
	 */
	public static SpiritGameAbilities getAbilityInfo(PlayerEntity player) {
		ISpiritCapabilities caps = getCapabilitiesFrom(player);
		if (caps == null) return null;
		if (!caps.getIsSpirit()) return null;
		return caps.getSpiritAbilities();
	}

}
