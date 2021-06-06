package etithespirit.etimod.networking.morph;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.morph.PlayerToSpiritBinding;
import etithespirit.etimod.networking.ReplicationData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ReplicateMorphStatus {
	
	// Common
	private static final Function<PacketBuffer, ModelReplicationPacket> BUFFER_TO_PACKET;
	private static final BiConsumer<ModelReplicationPacket, PacketBuffer> PACKET_TO_BUFFER;
	
	@OnlyIn(Dist.CLIENT)
	private static BiConsumer<ModelReplicationPacket, Supplier<NetworkEvent.Context>> ON_CLIENT_EVENT;
	
	private static BiConsumer<ModelReplicationPacket, Supplier<NetworkEvent.Context>> ON_SERVER_EVENT;
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(EtiMod.MODID, "model_replication"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	public static void registerPackets(Dist side) {
		if (side.isClient()) {
			ON_CLIENT_EVENT = new BiConsumer<ModelReplicationPacket, Supplier<NetworkEvent.Context>>() {
				@Override
				public void accept(ModelReplicationPacket t, Supplier<Context> u) {
					onClientEvent(t, u);
				}
			};
			
			// INSTANCE.registerMessage(index, messageType, encoder, decoder, messageConsumer, networkDirection);
			INSTANCE.registerMessage(ReplicationData.nextID(), ModelReplicationPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ON_CLIENT_EVENT);
		} else {
			ON_SERVER_EVENT = new BiConsumer<ModelReplicationPacket, Supplier<NetworkEvent.Context>>() {
				@Override
				public void accept(ModelReplicationPacket t, Supplier<Context> u) {
					onServerEvent(t, u);
				}
			};
			
			// INSTANCE.registerMessage(index, messageType, encoder, decoder, messageConsumer, networkDirection);
			INSTANCE.registerMessage(ReplicationData.nextID(), ModelReplicationPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ON_SERVER_EVENT);
		}
	}
	
	/**
	 * Spits out a ReplicationPacket from the buffer. 
	 * @param msg
	 * @param buffer
	 */
	protected static ModelReplicationPacket bufferToPacket(PacketBuffer buffer) {
		ModelReplicationPacket msg = new ModelReplicationPacket();
		try {
			byte typeAndFlags = buffer.readByte();
			byte type = (byte)(typeAndFlags & 0x0F);
			byte flags = (byte)((typeAndFlags & 0xF0) >> 4);
			msg.type = EventType.FromByte(type);
			msg.wantsToBeSpirit = buffer.readBoolean();
			
			if ((flags & 1) == 1) msg.playerID = UUID.fromString(buffer.readString());
			// if ((flags & 2) == 2) msg.TargetEntity = new ResourceLocation(buffer.readString());
			
		} catch (IllegalArgumentException exc) {
			msg.invalid = true;
			msg.type = EventType.Invalid;
			msg.playerID = null;
			msg.wantsToBeSpirit = false;
			// msg.TargetEntity = null;
		}
		return msg;
	}
	
	/**
	 * Populates the given replication packet into the buffer.
	 * @param buffer
	 * @param packet
	 */
	protected static void packetToBuffer(ModelReplicationPacket packet, PacketBuffer buffer) {
		byte typeByte = packet.type.ToByte();
		byte flagsByte = 0;
		if (packet.playerID != null) flagsByte |= 0b0001;
		// if (packet.TargetEntity != null) flagsByte |= 0b0010;
		flagsByte <<= 4; // Use the upper nibble.
		
		buffer.writeByte(typeByte | flagsByte);
		buffer.writeBoolean(packet.wantsToBeSpirit);
		
		if (packet.playerID != null) {
			buffer.writeString(packet.playerID.toString());
		}
		
		//if (packet.TargetEntity != null) {
		//	buffer.writeString(packet.TargetEntity.toString());
		//}
	}
	
	protected static void onServerEvent(ModelReplicationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (msg.type == EventType.GetPlayerModel) {
			ctx.get().enqueueWork(() -> {
				NetworkEvent.Context context = ctx.get();
				ServerPlayerEntity sender = context.getSender();
				UUID id = msg.playerID;
				MinecraftServer server = sender.getServer();
				ServerPlayerEntity referencedPlayer = server.getPlayerList().getPlayerByUUID(id);
				
				if (referencedPlayer != null) {
					// TODO: Model!
					boolean isSpirit = PlayerToSpiritBinding.get(id);
					ModelReplicationPacket toSend = ModelReplicationPacket.AsResponseToGetPlayerModel(id, isSpirit);
					INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), toSend);
				}
		    });
		} else if (msg.type == EventType.RequestChangePlayerModel) {
			ctx.get().enqueueWork(() -> {
				NetworkEvent.Context context = ctx.get();
				// Right now there's no actual requirements in place so....
				ServerPlayerEntity sender = context.getSender();
				UUID id = sender.getUniqueID();
				if (!sender.getUniqueID().equals(msg.playerID)) {
					if (sender.hasPermissionLevel(sender.server.getOpPermissionLevel())) {
						// Are they an op? If so, we'll allow the different ID.
						id = msg.playerID;
					} else {
						EtiMod.LOG.warn("A client (" + sender.getName().getString() + "/" + sender.getUniqueID().toString() + ") sent a model change request and the player ID field was populated with someone else's ID, but they are not an Op and cannot do this!");
						return;
					}
				}
				
				//PlayerToSpiritBinding.Put(id, msg.TargetEntity);
				PlayerToSpiritBinding.put(id, msg.wantsToBeSpirit);
				sender.recalculateSize(); // Potentially fix bug with jittering. EDIT: Nope lol
				INSTANCE.send(PacketDistributor.ALL.noArg(), ModelReplicationPacket.ToTellAllClientsSomeoneIsA(id, msg.wantsToBeSpirit));
		    });
		} else if (msg.type == EventType.GetEveryPlayerModel) {
			ctx.get().enqueueWork(() -> {
				NetworkEvent.Context context = ctx.get();
				ServerPlayerEntity sender = context.getSender();
				MinecraftServer server = sender.getServer();
				for (ServerPlayerEntity other : server.getPlayerList().getPlayers()) {
					if (other.equals(sender)) continue;
					UUID id = other.getUniqueID();
					boolean isSpirit = PlayerToSpiritBinding.get(id);
					ModelReplicationPacket toSend = ModelReplicationPacket.AsResponseToGetPlayerModel(id, isSpirit);
					INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), toSend);
				}
			});
		}
		
		// else { // Client bullshittin on network services }
		ctx.get().setPacketHandled(true);
	}
	
	
	@OnlyIn(Dist.CLIENT)
	protected static void onClientEvent(ModelReplicationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (msg.type == EventType.IsPlayerModel || msg.type == EventType.UpdatePlayerModel) {
			ctx.get().enqueueWork(() -> {
				// We've received word from the server of a player's model after asking for it.
				// Let's update our data.
				PlayerToSpiritBinding.put(msg.playerID, msg.wantsToBeSpirit);
		    });
		} else {
			// This could be singleplayer too, which means that serverside events will fire on the client.
			if (msg.type == EventType.GetPlayerModel) {
				ctx.get().enqueueWork(() -> {
					NetworkEvent.Context context = ctx.get();
					ServerPlayerEntity sender = context.getSender();
					UUID id = msg.playerID;
					MinecraftServer server = sender.getServer();
					ServerPlayerEntity referencedPlayer = server.getPlayerList().getPlayerByUUID(id);
					
					if (referencedPlayer != null) {
						boolean isSpirit = PlayerToSpiritBinding.get(id);
						ModelReplicationPacket toSend = ModelReplicationPacket.AsResponseToGetPlayerModel(id, isSpirit);
						INSTANCE.send(PacketDistributor.PLAYER.with(() -> {return sender;}), toSend);
					}
			    });
			} else if (msg.type == EventType.RequestChangePlayerModel) {
				ctx.get().enqueueWork(() -> {
					NetworkEvent.Context context = ctx.get();
					// Right now there's no actual requirements in place so....
					ServerPlayerEntity sender = context.getSender();
					UUID id = sender.getUniqueID();
					if (!sender.getUniqueID().equals(msg.playerID)) {
						if (sender.hasPermissionLevel(sender.server.getOpPermissionLevel())) {
							// Are they an op? If so, we'll allow the different ID.
							id = msg.playerID;
						} else {
							EtiMod.LOG.warn("A client (" + sender.getName().getString() + "/" + sender.getUniqueID().toString() + ") sent a model change request and the player ID field was populated with someone else's ID, but they are not an Op and cannot do this!");
							return;
						}
					}
					
					//PlayerToSpiritBinding.Put(id, msg.TargetEntity);
					PlayerToSpiritBinding.put(id, msg.wantsToBeSpirit);
					INSTANCE.send(PacketDistributor.ALL.noArg(), ModelReplicationPacket.ToTellAllClientsSomeoneIsA(id, msg.wantsToBeSpirit));
			    });
			}
		}
		ctx.get().setPacketHandled(true);
	}
	
	/**
	 * Relays a message to all players that the given player is now a spirit.
	 * @param playerId
	 * @param isSpirit
	 */
	public static void tellEveryonePlayerSpiritStatus(UUID playerId, boolean isSpirit) {
		PlayerToSpiritBinding.put(playerId, isSpirit);
		INSTANCE.send(PacketDistributor.ALL.noArg(), ModelReplicationPacket.ToTellAllClientsSomeoneIsA(playerId, isSpirit));
	}
	
	/**
	 * Politely asks the server if I can become a spirit (or no longer be one).
	 * @param isSpirit
	 */
	@OnlyIn(Dist.CLIENT)
	public static void askToSetSpiritStatus(boolean isSpirit) {
		INSTANCE.send(PacketDistributor.SERVER.noArg(), ModelReplicationPacket.AsRequestSetModel(isSpirit));
	}
	
	/**
	 * Politely asks the server if I can become a spirit (or no longer be one).
	 * @param isSpirit
	 */
	@OnlyIn(Dist.CLIENT)
	@Deprecated
	public static void askToSetSpiritStatus(UUID playerId, boolean isSpirit) {
		INSTANCE.send(PacketDistributor.SERVER.noArg(), ModelReplicationPacket.AsRequestSetModel(isSpirit));
	}
	
	/**
	 * Politely asks the server who is a spirit.
	 */
	@OnlyIn(Dist.CLIENT)
	public static void askWhoIsASpirit() {
		INSTANCE.send(PacketDistributor.SERVER.noArg(), ModelReplicationPacket.AsRequestGetAllModels());
	}
	
	static {
		BUFFER_TO_PACKET = new Function<PacketBuffer, ModelReplicationPacket>() {
			@Override
			public ModelReplicationPacket apply(PacketBuffer buffer) {
				return bufferToPacket(buffer);
			}
		};
		PACKET_TO_BUFFER = new BiConsumer<ModelReplicationPacket, PacketBuffer>() {
			@Override
			public void accept(ModelReplicationPacket t, PacketBuffer u) {
				packetToBuffer(t, u);
			}
		};
	}
}
