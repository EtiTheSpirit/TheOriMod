package etithespirit.orimod.networking.spirit;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.annotation.ServerUseOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpiritStateReplicationPacket {
	
	/** The type of packet that this is. */
	public @Nonnull EventType type = EventType.INVALID;
	
	/**
	 * When a client is requesting players from the server, keys should be populated with the desired players' GUIDs
	 * and values can be anything, but should be false for the sake of uniformity.<br/>
	 * <br/>
	 * When the server is telling a client (or several clients) about player model statuses, The server will respond with a
	 * list of players with true/false set accordingly.<br/>
	 * <br/>
	 * When the client wants to change their model, the key should be the local player's GUID and the value should be their
	 * desired state. Any attempts to input an ID that does not match the local player will be ignored.
	 */
	public @Nonnull Map<UUID, Boolean> playerSpiritStateMappings = new HashMap<>();
	
	/**
	 * If set to true, this packet will attempt to bypass limits where possible.
	 * This can only be set when the player sending the packet is the op of a server.
	 */
	public boolean force = false;
	
	public SpiritStateReplicationPacket() { }
	public SpiritStateReplicationPacket(EventType type, Map<UUID, Boolean> stateMappings) {
		this.type = type;
		this.playerSpiritStateMappings = Map.copyOf(stateMappings);
	}
	
	/**
	 * Constructs a packet that aims to change this player's model. If the player is not authorized to make this change,
	 * the server will send an {@link EventType#UPDATE_PLAYER_MODELS} call to inform the client of the correct type (the client
	 * will make a local change to themselves immediately as to hide network latency, so this is used to undo the change).
	 * @param beSpirit Whether or not the player wants to be a spirit.
	 * @return A packet that can be sent to the server to change this player's spirit state.
	 */
	@ClientUseOnly
	public static SpiritStateReplicationPacket toChangeMyModel(boolean beSpirit) {
		LocalPlayer player = Minecraft.getInstance().player;
		return new SpiritStateReplicationPacket(EventType.TRY_CHANGE_MODEL, Map.of(player.getUUID(), beSpirit));
	}
	
	/**
	 * Constructs a packet that aims to change the given player's model. If the player is not authorized to make this change,
	 * the server will send an {@link EventType#UPDATE_PLAYER_MODELS} call to inform the client of the correct type (the client
	 * will make a local change to themselves immediately as to hide network latency, so this is used to undo the change).
	 * @param beSpirit Whether or not the player wants to be a spirit.
	 * @return A packet that can be sent to the server to change this player's spirit state.
	 */
	public static SpiritStateReplicationPacket toChangeModelOf(Player player, boolean beSpirit) {
		boolean isClient = player.level.isClientSide;
		EventType evt = isClient ? EventType.TRY_CHANGE_MODEL : EventType.UPDATE_PLAYER_MODELS;
		return new SpiritStateReplicationPacket(evt, Map.of(player.getUUID(), beSpirit));
	}
	
	/**
	 * Constructs a packet that asks the server what every player in the server's state is with respect to being a spirit.
	 * @return A packet that can be sent to the server to acquire whether or not every player in the server is a spirit (individually).
	 */
	@ClientUseOnly
	public static SpiritStateReplicationPacket toGetModelsOfAll() {
		return new SpiritStateReplicationPacket(EventType.REQUEST_PLAYER_MODELS, new HashMap<>());
	}
	
	/**
	 * Constructs a packet that asks the server what the given players are with respect to being a spirit.
	 * @param players The players to acquire information of. This can be empty to get all players,
	 *                however it is recommended to use {@link #toGetModelsOfAll()} for this purpose.
	 * @return A packet that can be sent to the server to acquire whether or not the given players are spirits or not.
	 */
	@ClientUseOnly
	public static SpiritStateReplicationPacket toGetModelsOf(UUID... players) {
		if (players.length == 0) {
			OriMod.LOG.debug("Something called SpiritStateReplicationPacket::toGetModelsOf with no arguments! Use toGetModelsOfAll instead.");
			return toGetModelsOfAll();
		}
		HashMap<UUID, Boolean> map = new HashMap<>();
		for (UUID player : players) {
			map.put(player, false);
		}
		return new SpiritStateReplicationPacket(EventType.REQUEST_PLAYER_MODELS, map);
	}
	
	/**
	 * Constructs a packet that asks the server what the given players are with respect to being a spirit.
	 * @param players The players to acquire information of. This can be empty to get all players,
	 *                however it is recommended to use {@link #toGetModelsOfAll()} for this purpose.
	 * @return A packet that can be sent to the server to acquire whether or not the given players are spirits or not.
	 */
	@ClientUseOnly
	public static SpiritStateReplicationPacket toGetModelsOf(Player... players) {
		if (players.length == 0) {
			OriMod.LOG.debug("Something called SpiritStateReplicationPacket::toGetModelsOf with no arguments! Use toGetModelsOfAll instead.");
			return toGetModelsOfAll();
		}
		HashMap<UUID, Boolean> map = new HashMap<>();
		for (Player player : players) {
			map.put(player.getUUID(), false);
		}
		return new SpiritStateReplicationPacket(EventType.REQUEST_PLAYER_MODELS, map);
	}
	
	public boolean wantsToBeSpirit(UUID byID) {
		return playerSpiritStateMappings.getOrDefault(byID, false);
	}
	
	public boolean wantsToBeSpirit(Player player) {
		return wantsToBeSpirit(player.getUUID());
	}
	
	@Override
	public int hashCode() {
		return type.toByte(); // Not *exactly* unique, but computationally cheap and for the sake of hash sets/maps, the buckets are by type.
	}
	
	public enum EventType {
		/** A player wants to change their model, and is politely asking the server to propagate said change. */
		@ClientUseOnly
		TRY_CHANGE_MODEL,
		
		/** A player is asking the server what someone's model is (or what multiple peoples' models are) due to not receiving the information earlier. */
		@ClientUseOnly
		REQUEST_PLAYER_MODELS,
		
		/** The server's means of updating someone's status (or multiple peoples' statuses) as a spirit */
		@ServerUseOnly
		UPDATE_PLAYER_MODELS,
		
		/** An invalid event type. This is used to catch garbage data and filter it out. */
		INVALID;
		
		/**
		 * Converts this enum value into a byte equivalent.
		 * @return The byte equivalent of this enum item.
		 * @throws IllegalArgumentException If this is called on {@link #INVALID}.
		 */
		public byte toByte() throws IllegalArgumentException {
			return switch(this) {
				case TRY_CHANGE_MODEL -> (byte)0;
				case REQUEST_PLAYER_MODELS -> (byte)1;
				case UPDATE_PLAYER_MODELS -> (byte)2;
				default -> throw new IllegalArgumentException("Cannot convert EventType.Invalid to byte.");
			};
		}
		
		/**
		 * Creates an enum item from the given byte value. If the byte value is not recognized, {@link #INVALID} is returned.
		 * @param b The byte value to capture.
		 * @return The equivalent enum item, or {@link #INVALID} if the value does not have a known association.
		 */
		public static EventType fromByte(byte b) {
			return switch(b) {
				case 0 -> TRY_CHANGE_MODEL;
				case 1 -> REQUEST_PLAYER_MODELS;
				case 2 -> UPDATE_PLAYER_MODELS;
				default -> INVALID;
			};
		}
	}
	
}
