package etithespirit.etimod.networking.morph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.info.spirit.SpiritData;
import etithespirit.etimod.networking.ReplicationData;
import etithespirit.etimod.util.collection.ConcurrentBag;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * The core networking code used to dispatch and receive network events for player models.
 *
 * @author Eti
 */
public class ReplicateMorphStatus {
	
	private static final Function<PacketBuffer, ModelReplicationPacket> BUFFER_TO_PACKET = ReplicateMorphStatus::bufferToPacket;
	private static final BiConsumer<ModelReplicationPacket, PacketBuffer> PACKET_TO_BUFFER = ReplicateMorphStatus::packetToBuffer;
	
	/** Callbacks that are registered, and will unregister if they return true. */
	public static final ConcurrentBag<Function<ModelReplicationPacket, Boolean>> CLIENT_RECEIVED_EVENT_SINGLEFIRE_CALLBACKS = new ConcurrentBag<>();
	
	/** Callbacks that are registered, and will unregister if they return true. Note that this accepts garbage data too. Please verify data. */
	public static final ConcurrentBag<Function<ModelReplicationPacket, Boolean>> SERVER_RECEIVED_EVENT_SINGLEFIRE_CALLBACKS = new ConcurrentBag<>();
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(EtiMod.MODID, "model_replication"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	public static void registerPackets(Dist side) {
		if (side.isClient()) {
			INSTANCE.registerMessage(ReplicationData.nextID(), ModelReplicationPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ReplicateMorphStatus::onClientEvent);
		} else {
			INSTANCE.registerMessage(ReplicationData.nextID(), ModelReplicationPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ReplicateMorphStatus::onServerEvent);
		}
	}
	
	/**
	 * Spits out a ReplicationPacket from the buffer. 
	 * @param buffer The buffer to read from.
	 */
	protected static ModelReplicationPacket bufferToPacket(PacketBuffer buffer) {
		ModelReplicationPacket msg = new ModelReplicationPacket();
		try {
			msg.type = EventType.fromByte(buffer.readByte());
			msg.wantsToBeSpirit = buffer.readBoolean();
			msg.playerID = buffer.readInt();
		} catch (IllegalArgumentException exc) {
			msg.invalid = true;
			msg.type = EventType.Invalid;
		}
		return msg;
	}
	
	/**
	 * Populates the given replication packet into the buffer.
	 * @param buffer The buffer to write to.
	 * @param packet The packet storing the data.
	 */
	protected static void packetToBuffer(ModelReplicationPacket packet, PacketBuffer buffer) {
		byte typeByte = packet.type.toByte();
		buffer.writeByte(typeByte);
		buffer.writeBoolean(packet.wantsToBeSpirit);
		buffer.writeInt(packet.playerID);
	}
	
	protected static void onServerEvent(ModelReplicationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (msg.type == EventType.GET_PLAYER_MODEL) {
			ctx.get().enqueueWork(() -> {
				NetworkEvent.Context context = ctx.get();
				ServerPlayerEntity sender = context.getSender();
				int id = msg.playerID;
				
				boolean isSpirit = SpiritData.isSpirit(sender);
				ModelReplicationPacket toSend = ModelReplicationPacket.asResponseToGetPlayerModel(id, isSpirit);
				INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), toSend);
		    });
		} else if (msg.type == EventType.REQUEST_CHANGE_PLAYER_MODEL) {
			ctx.get().enqueueWork(() -> {
				NetworkEvent.Context context = ctx.get();
				
				// Right now there's no actual requirements in place so....
				ServerPlayerEntity sender = context.getSender();
				int id = sender.getId();
				if (id != msg.playerID) {
					if (sender.hasPermissions(sender.server.getOperatorUserPermissionLevel())) {
						// Are they an op? If so, we'll allow the different ID.
						id = msg.playerID;
					} else {
						EtiMod.LOG.warn("A client (" + sender.getName().getString() + "/" + sender.getUUID() + ") sent a model change request and the player ID field was populated with someone else's ID, but they are not an Op and cannot do this!");
						return;
					}
				}
				
				SpiritData.setSpirit(sender, msg.wantsToBeSpirit);
				INSTANCE.send(PacketDistributor.ALL.noArg(), ModelReplicationPacket.toTellAllClientsSomeoneIsA(id, msg.wantsToBeSpirit));
		    });
		} else if (msg.type == EventType.GET_EVERY_PLAYER_MODEL) {
			ctx.get().enqueueWork(() -> {
				NetworkEvent.Context context = ctx.get();
				ServerPlayerEntity sender = context.getSender();
				MinecraftServer server = sender.getServer();
				Map<Integer, Boolean> whoIsASpirit = new HashMap<>();
				for (ServerPlayerEntity other : server.getPlayerList().getPlayers()) {
					if (other.equals(sender)) continue;
					int id = other.getId();
					boolean isSpirit = SpiritData.isSpirit(other);
					whoIsASpirit.put(id, isSpirit);
				}
				ModelReplicationPacket toSend = ModelReplicationPacket.toTellClientWhatEveryoneIs(sender.getId(), whoIsASpirit);
				INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), toSend);
			});
		}
		
		ctx.get().enqueueWork(() -> {
			ArrayList<Function<ModelReplicationPacket, Boolean>> toRemove = new ArrayList<>();
			for (Function<ModelReplicationPacket, Boolean> func : SERVER_RECEIVED_EVENT_SINGLEFIRE_CALLBACKS) {
				if (func.apply(msg)) {
					toRemove.add(func);
				}
			}
			for (Function<ModelReplicationPacket, Boolean> func : toRemove) {
				SERVER_RECEIVED_EVENT_SINGLEFIRE_CALLBACKS.remove(func);
			}
		});
		
		// else { // Client bullshittin on network services }
		ctx.get().setPacketHandled(true);
	}
	
	protected static void onClientEvent(ModelReplicationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		World world = Minecraft.getInstance().level;
		
		if (msg.type == EventType.IS_SPIRIT || msg.type == EventType.UPDATE_PLAYER_MODEL) {
			ctx.get().enqueueWork(() -> {
				// We've received word from the server of a player's model after asking for it.
				// Let's update our data.
				// The server sent this, so it's safe to assume the value is sane.
				SpiritData.setSpirit((PlayerEntity)world.getEntity(msg.playerID), msg.wantsToBeSpirit);
		    });
			
		} else if (msg.type == EventType.TELL_EVERY_PLAYER_MODEL) {
			ctx.get().enqueueWork(() -> {
				Set<Integer> keys = msg.playersWhoAreSpirits.keySet();
				for (Integer key : keys) {
					boolean isSpirit = msg.playersWhoAreSpirits.get(key);
					Entity target = world.getEntity(key);
					SpiritData.setSpirit((PlayerEntity)target, isSpirit);
				}
			});
		}
		
		ctx.get().enqueueWork(() -> {
			ArrayList<Function<ModelReplicationPacket, Boolean>> toRemove = new ArrayList<>();
			for (Function<ModelReplicationPacket, Boolean> func : CLIENT_RECEIVED_EVENT_SINGLEFIRE_CALLBACKS) {
				if (func.apply(msg)) {
					toRemove.add(func);
				}
			}
			for (Function<ModelReplicationPacket, Boolean> func : toRemove) {
				CLIENT_RECEIVED_EVENT_SINGLEFIRE_CALLBACKS.remove(func);
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	/**
	 * Relays a message to all players whether or not the given player is a spirit.
	 * @param player The player who changed.
	 * @param isSpirit The new state of whether or not they are a spirit.
	 */
	public static void tellEveryonePlayerSpiritStatus(PlayerEntity player, boolean isSpirit) {
		SpiritData.setSpirit(player, isSpirit);
		INSTANCE.send(PacketDistributor.ALL.noArg(), ModelReplicationPacket.toTellAllClientsSomeoneIsA(player.getId(), isSpirit));
	}
	
	/**
	 * Politely asks the server if I can become a spirit (or no longer be one).
	 * @param isSpirit Whether or not I want to be a spirit.
	 */
	public static void askToSetSpiritStatusAsync(boolean isSpirit) {
		INSTANCE.send(PacketDistributor.SERVER.noArg(), ModelReplicationPacket.asRequestSetModel(isSpirit));
	}
	
	/**
	 * Politely asks the server which people are spirits right now.
	 */
	public static void askWhoIsASpiritAsync() {
		INSTANCE.send(PacketDistributor.SERVER.noArg(), ModelReplicationPacket.asRequestGetAllModels());
	}
}
