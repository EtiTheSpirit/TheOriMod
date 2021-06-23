package etithespirit.etimod.common.morph;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;

@Deprecated
public class PlayerToSpiritBinding {
	
	
	/**
	 * A mapping from player UUID to a boolean representing whether or not they are a Spirit.
	 */
	@Deprecated
	public static final Map<UUID, Boolean> BINDINGS = new HashMap<UUID, Boolean>();
	
	@Deprecated
	public static void put(UUID id, boolean isSpirit) {
		if (id == null) return;
		BINDINGS.put(id, isSpirit);
	}
	
	@Deprecated
	public static void put_(PlayerEntity player, boolean isSpirit) {
		if (player == null) return;
		put(player.getUUID(), isSpirit);
	}
	
	@Deprecated
	public static boolean get(PlayerEntity player) {
		if (player == null) return false;
		return get(player.getUUID());
	}
	
	@Deprecated
	public static boolean get(UUID id) {
		if (id == null) return false;
		if (BINDINGS.containsKey(id)) return BINDINGS.get(id);
		return false;
	}
	
}
