package etithespirit.etimod.common.morph;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerToSpiritBinding {
	
	/**
	 * A mapping from player UUID to a boolean representing whether or not they are a Spirit.
	 */
	public static final Map<UUID, Boolean> BINDINGS = new HashMap<UUID, Boolean>();
	
	public static void put(UUID id, boolean loc) {
		if (id == null) return;
		BINDINGS.put(id, loc);
	}
	
	public static void put(PlayerEntity player, boolean loc) {
		if (player == null) return;
		put(player.getUUID(), loc);
	}
	
	public static boolean get(PlayerEntity player) {
		if (player == null) return false;
		return get(player.getUUID());
	}
	
	public static boolean get(UUID id) {
		if (id == null) return false;
		if (BINDINGS.containsKey(id)) return BINDINGS.get(id);
		return false;
	}
	
}
