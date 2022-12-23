package etithespirit.orimod.networking.player;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.annotation.ServerUseOnly;
import etithespirit.orimod.common.capabilities.SpiritCapabilities;
import etithespirit.orimod.networking.ReplicationData;
import etithespirit.orimod.spirit.abilities.SpiritDashAbility;
import etithespirit.orimod.spirit.abilities.SpiritJumpAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ReplicateKnownAbilities {
	
	public static final Function<FriendlyByteBuf, Packet> BUFFER_TO_PACKET = ReplicateKnownAbilities::bufferToPacket;
	public static final BiConsumer<Packet, FriendlyByteBuf> PACKET_TO_BUFFER = ReplicateKnownAbilities::packetToBuffer;
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		OriMod.rsrc("capability_replicator"),
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
			msg.target = buffer.readUUID();
			msg.dashType = buffer.readByte();
			msg.airJumpType = buffer.readByte();
			msg.canWallJump = buffer.readBoolean();
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
		buffer.writeUUID(packet.target);
		buffer.writeByte(packet.dashType);
		buffer.writeByte(packet.airJumpType);
		buffer.writeBoolean(packet.canWallJump);
	}
	
	
	public static final class Server {
		
		public static void registerServerPackets() {
			INSTANCE.registerMessage(ReplicationData.nextID(), Packet.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ReplicateKnownAbilities.Server::onServerEvent);
		}
		
		public static void onServerEvent(Packet msg, Supplier<NetworkEvent.Context> ctx) {
			NetworkEvent.Context context = ctx.get();
			if (msg.type == Packet.EventType.GET_CAPABILITIES && msg.target != null) {
				context.enqueueWork(() -> {
					ServerPlayer sender = context.getSender();
					Player desired = sender.getCommandSenderWorld().getPlayerByUUID(msg.target);
					if (desired == null) return;
					if (sender.getUUID().equals(desired.getUUID())) return;
					
					Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(desired);
					if (capsCtr.isPresent()) {
						SpiritCapabilities caps = capsCtr.get();
						INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), Packet.toSetDash(sender, caps.getRawDash()));
						INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), Packet.toSetAirJumpType(sender, caps.getRawJump()));
						INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), Packet.toSetWallJump(sender, caps.getRawCanWallJump()));
					}
				});
			}
			context.setPacketHandled(true);
		}
		
		/**
		 * Tells the recipient player what the current dash state is.
		 * @param recipient
		 * @param caps
		 */
		public static void sendNewDash(ServerPlayer recipient, SpiritCapabilities caps) {
			INSTANCE.send(PacketDistributor.PLAYER.with(() -> recipient), Packet.toSetDash(recipient, caps.getRawDash()));
		}
		
		/**
		 *Tells the recipient player what the current air jump state is.
		 * @param recipient
		 * @param caps
		 */
		public static void sendNewAirJump(ServerPlayer recipient, SpiritCapabilities caps) {
			INSTANCE.send(PacketDistributor.PLAYER.with(() -> recipient), Packet.toSetAirJumpType(recipient, caps.getRawJump()));
		}
		
		/**
		 * Tells the recipient player what the current wall jump state is.
		 * @param recipient
		 * @param caps
		 */
		public static void sendNewWallJump(ServerPlayer recipient, SpiritCapabilities caps) {
			INSTANCE.send(PacketDistributor.PLAYER.with(() -> recipient), Packet.toSetWallJump(recipient, caps.getRawCanWallJump()));
		}
		
		/**
		 * An alias method that calls {@link #sendNewDash(ServerPlayer, SpiritCapabilities)}, {@link #sendNewAirJump(ServerPlayer, SpiritCapabilities)}, and {@link #sendNewWallJump(ServerPlayer, SpiritCapabilities)} all at once.
		 * @param recipient The player to send the data to.
		 */
		public static void tellAllCapsTo(ServerPlayer recipient) {
			Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(recipient);
			if (capsCtr.isPresent()) {
				SpiritCapabilities caps = capsCtr.get();
				sendNewDash(recipient, caps);
				sendNewAirJump(recipient, caps);
				sendNewWallJump(recipient, caps);
			}
		}
		
	}
	
	public static final class Client {
		
		public static void registerClientPackets() {
			INSTANCE.registerMessage(ReplicationData.nextID(), Packet.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ReplicateKnownAbilities.Client::onClientEvent);
		}
		
		public static void onClientEvent(Packet msg, Supplier<NetworkEvent.Context> ctx) {
			NetworkEvent.Context context = ctx.get();
			if (msg.type == Packet.EventType.CHANGE_DASH) {
				context.enqueueWork(() -> {
					LocalPlayer client = Minecraft.getInstance().player;
					Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(client);
					if (capsCtr.isPresent()) {
						SpiritCapabilities caps = capsCtr.get();
						caps.setDashType(SpiritDashAbility.fromByte(msg.dashType));
					}
 				});
			} else if (msg.type == Packet.EventType.CHANGE_AIRJUMP) {
				context.enqueueWork(() -> {
					LocalPlayer client = Minecraft.getInstance().player;
					Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(client);
					if (capsCtr.isPresent()) {
						SpiritCapabilities caps = capsCtr.get();
						caps.setAirJumpType(SpiritJumpAbility.fromByte(msg.airJumpType));
					}
				});
			} else if (msg.type == Packet.EventType.CHANGE_WALLJUMP) {
				context.enqueueWork(() -> {
					LocalPlayer client = Minecraft.getInstance().player;
					Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(client);
					if (capsCtr.isPresent()) {
						SpiritCapabilities caps = capsCtr.get();
						caps.setCanWallJump(msg.canWallJump);
					}
				});
			}
			context.setPacketHandled(true);
		}
		
		/**
		 * Requests that the server send the calling client (this game client) a copy of their capabilities to ensure parity.
		 */
		public static void getAllMyCapsAsync() {
			INSTANCE.sendToServer(Packet.toGetAllCaps(Minecraft.getInstance().player));
		}
		
	}
	
	private static final class Packet {
		
		public @Nonnull EventType type = EventType.INVALID;
		
		public UUID target;
		
		public byte dashType;
		
		public byte airJumpType;
		
		public boolean canWallJump;
		
		public Packet() {}
		
		public static Packet toSetDash(Player target, SpiritDashAbility dash) {
			Packet pkt = new Packet();
			pkt.target = target.getUUID();
			pkt.type = EventType.CHANGE_DASH;
			pkt.dashType = dash.toByte();
			return pkt;
		}
		
		public static Packet toSetAirJumpType(Player target, SpiritJumpAbility jumpType) {
			Packet pkt = new Packet();
			pkt.target = target.getUUID();
			pkt.type = EventType.CHANGE_AIRJUMP;
			pkt.airJumpType = jumpType.toByte();
			return pkt;
		}
		
		public static Packet toSetWallJump(Player target, boolean canWallJump) {
			Packet pkt = new Packet();
			pkt.target = target.getUUID();
			pkt.type = EventType.CHANGE_WALLJUMP;
			pkt.canWallJump = canWallJump;
			return pkt;
		}
		
		public static Packet toGetAllCaps(Player target) {
			Packet pkt = new Packet();
			pkt.target = target.getUUID();
			pkt.type = EventType.GET_CAPABILITIES;
			return pkt;
		}
		
		public enum EventType {
			
			INVALID,
			
			@ServerUseOnly
			CHANGE_DASH,
			
			@ServerUseOnly
			CHANGE_AIRJUMP,
			
			@ServerUseOnly
			CHANGE_WALLJUMP,
			
			@ClientUseOnly
			GET_CAPABILITIES;
			
			public byte toByte() {
				return switch(this) {
					default -> (byte)0;
					case CHANGE_DASH -> (byte)1;
					case CHANGE_AIRJUMP -> (byte)2;
					case CHANGE_WALLJUMP -> (byte)3;
					case GET_CAPABILITIES -> (byte)4;
				};
			}
			
			public static EventType fromByte(byte value) {
				return switch(value) {
					default -> INVALID;
					case 1 -> CHANGE_DASH;
					case 2 -> CHANGE_AIRJUMP;
					case 3 -> CHANGE_WALLJUMP;
					case 4 -> GET_CAPABILITIES;
				};
			}
			
		}
	}
}
