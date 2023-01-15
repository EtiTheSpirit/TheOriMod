package etithespirit.orimod.networking.spirit;


import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.annotation.ServerUseOnly;
import etithespirit.orimod.networking.ReplicationData;
import etithespirit.orimod.player.EffectEnforcement;
import etithespirit.orimod.registry.advancements.AdvancementRegistry;
import etithespirit.orimod.server.persistence.SpiritPermissions;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The core networking code used to dispatch and receive network events for player models.
 *
 * @author Eti
 */
public final class ReplicateSpiritStatus {
	
	public static final Function<FriendlyByteBuf, Packet> BUFFER_TO_PACKET = ReplicateSpiritStatus::bufferToPacket;
	public static final BiConsumer<Packet, FriendlyByteBuf> PACKET_TO_BUFFER = ReplicateSpiritStatus::packetToBuffer;
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		OriMod.rsrc("spirit_state_replicator"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	/**
	 * Spits out a ReplicationPacket from the buffer.
	 * @param buffer The buffer to read from.
	 */
	private static Packet bufferToPacket(FriendlyByteBuf buffer) {
		Packet msg = new Packet();
		try {
			msg.type = Packet.EventType.fromByte(buffer.readByte());
			msg.force = buffer.readBoolean();
			msg.playerSpiritStateMappings.putAll(buffer.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readBoolean));
		} catch (IllegalArgumentException exc) {
			msg.type = Packet.EventType.INVALID;
		}
		return msg;
	}
	
	/**
	 * Populates the given replication packet into the buffer.
	 * @param buffer The buffer to write to.
	 * @param packet The packet storing the data.
	 */
	private static void packetToBuffer(Packet packet, FriendlyByteBuf buffer) {
		byte typeByte = packet.type.toByte();
		buffer.writeByte(typeByte);
		buffer.writeBoolean(packet.force);
		buffer.writeMap(
			packet.playerSpiritStateMappings,
			FriendlyByteBuf::writeUUID,
			FriendlyByteBuf::writeBoolean
		);
	}
	
	
	public static final class Server {
		
		public static void registerServerPackets() {
			INSTANCE.registerMessage(ReplicationData.nextID(), Packet.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ReplicateSpiritStatus.Server::onServerEvent);
		}
		
		private static void onServerEvent(Packet msg, Supplier<NetworkEvent.Context> ctx) {
			NetworkEvent.Context context = ctx.get();
			ServerPlayer sender = context.getSender();
			if (msg.type == Packet.EventType.REQUEST_PLAYER_MODELS) {
				context.enqueueWork(() -> {
					if (sender == null) {
						return;
					}
					
					Packet response = new Packet();
					response.type = Packet.EventType.UPDATE_PLAYER_MODELS;
					
					if (msg.playerSpiritStateMappings.isEmpty()) {
						sender.server.getPlayerList().getPlayers().forEach(player -> {
							UUID uuid = player.getUUID();
							response.playerSpiritStateMappings.put(uuid, SpiritIdentifier.isSpirit(player));
						});
					} else {
						msg.playerSpiritStateMappings.forEach((uuid, aBoolean) -> {
							ServerPlayer otherPlayer = sender.server.getPlayerList().getPlayer(uuid);
							if (otherPlayer == null)
								return; // Discard garbage UUIDs. The player *MUST* be online in order for them to query.
							response.playerSpiritStateMappings.put(uuid, SpiritIdentifier.isSpirit(otherPlayer));
						});
					}
					INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), response);
				});
			} else if (msg.type == Packet.EventType.TRY_CHANGE_MODEL) {
				context.enqueueWork(() -> {
					if (sender == null) {
						return;
					}
					
					if (!msg.playerSpiritStateMappings.containsKey(sender.getUUID())) {
						OriMod.LOG.warn(
							"Player '{}' ({}) attempted to change the spirit model of another player or no player (they were not present in the list of changes to make). Because this data is potentially malicious or garbage, it will not be read.",
							sender.getName().getString(),
							sender.getStringUUID()
						);
					}
					
					SpiritPermissions.ChangePermissions changePermissions = SpiritPermissions.getPermissions().get(sender);
					boolean desiredState;
					if (changePermissions.canChange()) {
						desiredState = msg.wantsToBeSpirit(sender); // Invert the current state on this side and send that to them.
					} else {
						// The player cannot change. Reject it, and inform the client of the necessary change back to their previous state.
						// This can be done by capturing their current state on this side, and just sending it back to them.
						desiredState = SpiritIdentifier.isSpirit(sender);
					}
					
					Packet response = new Packet();
					response.type = Packet.EventType.UPDATE_PLAYER_MODELS;
					response.playerSpiritStateMappings.put(sender.getUUID(), desiredState);
					INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), response);
					SpiritIdentifier.setSpirit(sender, desiredState);
				});
			}
			
			// else { // Client bullshittin on network services }
			context.setPacketHandled(true);
		}
		
		/**
		 * Relays a message to all players whether or not the given player is a spirit.
		 *
		 * @param player   The player who changed.
		 * @param isSpirit The new state of whether or not they are a spirit.
		 */
		@ServerUseOnly
		public static void tellEveryonePlayerSpiritStatus(Player player, boolean isSpirit) {
			SpiritIdentifier.setSpirit(player, isSpirit);
			INSTANCE.send(PacketDistributor.ALL.noArg(), Packet.toChangeModelOf(player, isSpirit));
			AdvancementRegistry.BECOME_SPIRIT.trigger((ServerPlayer) player);
		}
	}

	public static final class Client {
		
		public static void registerClientPackets() {
			ReplicateSpiritStatus.INSTANCE.registerMessage(ReplicationData.nextID(), Packet.class, ReplicateSpiritStatus.PACKET_TO_BUFFER, ReplicateSpiritStatus.BUFFER_TO_PACKET, ReplicateSpiritStatus.Client::onClientEvent);
		}
		
		
		public static void onClientEvent(Packet msg, Supplier<NetworkEvent.Context> ctx) {
			if (msg.type == Packet.EventType.UPDATE_PLAYER_MODELS) {
				ctx.get().enqueueWork(() -> {
					// We've received word from the server of one or more players' models changing.
					// Let's update our data.
					// The server sent this, so it's safe to assume the value is acceptable.
					Level world = Minecraft.getInstance().level;
					if (world != null) {
						msg.playerSpiritStateMappings.forEach((uuid, isSpirit) -> {
							Player player = world.getPlayerByUUID(uuid);
							if (player != null) {
								SpiritIdentifier.setSpirit(player, isSpirit);
							}
						});
					}
				});
			}
			
			ctx.get().setPacketHandled(true);
		}
		
		
		
		/**
		 * Politely asks the server if I can become a spirit (or no longer be one).
		 * @param isSpirit Whether or not I want to be a spirit.
		 */
		@ClientUseOnly
		public static void askToSetSpiritStatusAsync(boolean isSpirit) {
			LocalPlayer client = Minecraft.getInstance().player;
			SpiritIdentifier.setSpirit(client, isSpirit);
			ReplicateSpiritStatus.INSTANCE.send(PacketDistributor.SERVER.noArg(), Packet.toChangeModelOf(client, isSpirit));
		}
		
		/**
		 * Politely asks the server which people are spirits right now.
		 */
		@ClientUseOnly
		public static void askWhoIsASpiritAsync() {
			ReplicateSpiritStatus.INSTANCE.send(PacketDistributor.SERVER.noArg(), Packet.toGetModelsOfAll());
		}
		
		/**
		 * Politely asks the server if the given player is a spirit.
		 * @param person The player to check.
		 */
		public static void askIfPersonIsASpiritAsync(Player person) {
			ReplicateSpiritStatus.INSTANCE.send(PacketDistributor.SERVER.noArg(), Packet.toGetModelsOf(person));
		}
		
		public static void onPlayerExist(PlayerEvent.StartTracking evt) {
			if (evt.getTarget() instanceof Player plr) {
				OriMod.logCustomTrace(plr.getDisplayName().getString() + " now exists. Asking the server if they are a spirit...");
				askIfPersonIsASpiritAsync(plr);
			}
		}
	}
	
	private static final class Packet {
		
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
		
		public Packet() { }
		public Packet(EventType type, Map<UUID, Boolean> stateMappings) {
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
		public static Packet toChangeMyModel(boolean beSpirit) {
			LocalPlayer player = Minecraft.getInstance().player;
			return new Packet(EventType.TRY_CHANGE_MODEL, Map.of(player.getUUID(), beSpirit));
		}
		
		/**
		 * Constructs a packet that aims to change the given player's model. If the player is not authorized to make this change,
		 * the server will send an {@link EventType#UPDATE_PLAYER_MODELS} call to inform the client of the correct type (the client
		 * will make a local change to themselves immediately as to hide network latency, so this is used to undo the change).
		 * @param beSpirit Whether or not the player wants to be a spirit.
		 * @return A packet that can be sent to the server to change this player's spirit state.
		 */
		public static Packet toChangeModelOf(Player player, boolean beSpirit) {
			boolean isClient = player.level.isClientSide;
			EventType evt = isClient ? EventType.TRY_CHANGE_MODEL : EventType.UPDATE_PLAYER_MODELS;
			return new Packet(evt, Map.of(player.getUUID(), beSpirit));
		}
		
		/**
		 * Constructs a packet that asks the server what every player in the server's state is with respect to being a spirit.
		 * @return A packet that can be sent to the server to acquire whether or not every player in the server is a spirit (individually).
		 */
		@ClientUseOnly
		public static Packet toGetModelsOfAll() {
			return new Packet(EventType.REQUEST_PLAYER_MODELS, new HashMap<>());
		}
		
		/**
		 * Constructs a packet that asks the server what the given players are with respect to being a spirit.
		 * @param players The players to acquire information of. This can be empty to get all players,
		 *                however it is recommended to use {@link #toGetModelsOfAll()} for this purpose.
		 * @return A packet that can be sent to the server to acquire whether or not the given players are spirits or not.
		 */
		@ClientUseOnly
		public static Packet toGetModelsOf(UUID... players) {
			if (players.length == 0) {
				OriMod.logCustomTrace("Something called SpiritStateReplicationPacket::toGetModelsOf with no arguments! Use toGetModelsOfAll instead.");
				return toGetModelsOfAll();
			}
			HashMap<UUID, Boolean> map = new HashMap<>();
			for (UUID player : players) {
				map.put(player, false);
			}
			return new Packet(EventType.REQUEST_PLAYER_MODELS, map);
		}
		
		/**
		 * Constructs a packet that asks the server what the given players are with respect to being a spirit.
		 * @param players The players to acquire information of. This can be empty to get all players,
		 *                however it is recommended to use {@link #toGetModelsOfAll()} for this purpose.
		 * @return A packet that can be sent to the server to acquire whether or not the given players are spirits or not.
		 */
		@ClientUseOnly
		public static Packet toGetModelsOf(Player... players) {
			if (players.length == 0) {
				OriMod.logCustomTrace("Something called SpiritStateReplicationPacket::toGetModelsOf with no arguments! Use toGetModelsOfAll instead.");
				return toGetModelsOfAll();
			}
			HashMap<UUID, Boolean> map = new HashMap<>();
			for (Player player : players) {
				map.put(player.getUUID(), false);
			}
			return new Packet(EventType.REQUEST_PLAYER_MODELS, map);
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
}
