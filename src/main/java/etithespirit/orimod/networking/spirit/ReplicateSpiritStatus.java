package etithespirit.orimod.networking.spirit;


import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ServerUseOnly;
import etithespirit.orimod.networking.ReplicationData;
import etithespirit.orimod.registry.AdvancementRegistry;
import etithespirit.orimod.server.persistence.SpiritPermissions;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static etithespirit.orimod.networking.spirit.SpiritStateReplicationPacket.EventType;

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
	
	public static final Function<FriendlyByteBuf, SpiritStateReplicationPacket> BUFFER_TO_PACKET = ReplicateSpiritStatus::bufferToPacket;
	public static final BiConsumer<SpiritStateReplicationPacket, FriendlyByteBuf> PACKET_TO_BUFFER = ReplicateSpiritStatus::packetToBuffer;
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(OriMod.MODID, "spirit_state_replicator"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	public static void registerServerPackets() {
		INSTANCE.registerMessage(ReplicationData.nextID(), SpiritStateReplicationPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ReplicateSpiritStatus::onServerEvent);
	}
	
	/**
	 * Spits out a ReplicationPacket from the buffer.
	 * @param buffer The buffer to read from.
	 */
	private static SpiritStateReplicationPacket bufferToPacket(FriendlyByteBuf buffer) {
		SpiritStateReplicationPacket msg = new SpiritStateReplicationPacket();
		try {
			msg.type = EventType.fromByte(buffer.readByte());
			msg.force = buffer.readBoolean();
			msg.playerSpiritStateMappings.putAll(buffer.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readBoolean));
		} catch (IllegalArgumentException exc) {
			msg.type = EventType.INVALID;
		}
		return msg;
	}
	
	/**
	 * Populates the given replication packet into the buffer.
	 * @param buffer The buffer to write to.
	 * @param packet The packet storing the data.
	 */
	private static void packetToBuffer(SpiritStateReplicationPacket packet, FriendlyByteBuf buffer) {
		byte typeByte = packet.type.toByte();
		buffer.writeByte(typeByte);
		buffer.writeBoolean(packet.force);
		buffer.writeMap(
			packet.playerSpiritStateMappings,
			FriendlyByteBuf::writeUUID,
			FriendlyByteBuf::writeBoolean
		);
	}
	
	private static void onServerEvent(SpiritStateReplicationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		ServerPlayer sender = context.getSender();
		if (msg.type == EventType.REQUEST_PLAYER_MODELS) {
			context.enqueueWork(() -> {
				if (sender == null) {
					return;
				}
				
				SpiritStateReplicationPacket response = new SpiritStateReplicationPacket();
				response.type = EventType.UPDATE_PLAYER_MODELS;
				
				if (msg.playerSpiritStateMappings.isEmpty()) {
					sender.server.getPlayerList().getPlayers().forEach(player -> {
						UUID uuid = player.getUUID();
						response.playerSpiritStateMappings.put(uuid, SpiritIdentifier.isSpirit(uuid));
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
		} else if (msg.type == EventType.TRY_CHANGE_MODEL) {
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
				
				SpiritStateReplicationPacket response = new SpiritStateReplicationPacket();
				response.type = EventType.UPDATE_PLAYER_MODELS;
				response.playerSpiritStateMappings.put(sender.getUUID(), desiredState);
				INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), response);
				SpiritIdentifier.setSpirit(sender.getUUID(), desiredState);
			});
		}
		
		// else { // Client bullshittin on network services }
		context.setPacketHandled(true);
	}
	
	/**
	 * Relays a message to all players whether or not the given player is a spirit.
	 * @param player The player who changed.
	 * @param isSpirit The new state of whether or not they are a spirit.
	 */
	@ServerUseOnly
	public static void tellEveryonePlayerSpiritStatus(Player player, boolean isSpirit) {
		SpiritIdentifier.setSpirit(player.getUUID(), isSpirit);
		INSTANCE.send(PacketDistributor.ALL.noArg(), SpiritStateReplicationPacket.toChangeModelOf(player, isSpirit));
		AdvancementRegistry.BECOME_SPIRIT.trigger((ServerPlayer)player);
	}
}
