package etithespirit.orimod.networking.player;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.common.capabilities.SpiritCapabilities;
import etithespirit.orimod.networking.ReplicationData;
import etithespirit.orimod.spirit.common.MotionMarshaller;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ReplicatePlayerMovement {
	
	
	public static final Function<FriendlyByteBuf, Packet> BUFFER_TO_PACKET = ReplicatePlayerMovement::bufferToPacket;
	public static final BiConsumer<Packet, FriendlyByteBuf> PACKET_TO_BUFFER = ReplicatePlayerMovement::packetToBuffer;
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		OriMod.rsrc("spirit_movement_replicator"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	private static Packet bufferToPacket(FriendlyByteBuf buffer) {
		Packet pkt = new Packet();
		pkt.type = Packet.EventType.fromByte(buffer.readByte());
		if (pkt.type == Packet.EventType.DASH) {
			pkt.dashDirection = new Vec3(
				buffer.readDouble(),
				buffer.readDouble(),
				buffer.readDouble()
			);
		} else if (pkt.type == Packet.EventType.AIR_JUMP || pkt.type == Packet.EventType.WALL_JUMP) {
			pkt.playerReportedLeftImpulse = buffer.readFloat();
			pkt.playerReportedForwardImpulse = buffer.readFloat();
		} else if (pkt.type == Packet.EventType.CHANGE_WALL_CLING_STATE) {
			pkt.desiredWallClingState = buffer.readBoolean();
		} else {
			throw new IllegalArgumentException();
		}
		return pkt;
	}
	
	private static void packetToBuffer(Packet pkt, FriendlyByteBuf buffer) {
		buffer.writeByte(pkt.type.toByte());
		if (pkt.type == Packet.EventType.DASH) {
			buffer.writeDouble(pkt.dashDirection.x);
			buffer.writeDouble(pkt.dashDirection.y);
			buffer.writeDouble(pkt.dashDirection.z);
		} else if (pkt.type == Packet.EventType.AIR_JUMP || pkt.type == Packet.EventType.WALL_JUMP) {
			buffer.writeFloat(pkt.playerReportedLeftImpulse);
			buffer.writeFloat(pkt.playerReportedForwardImpulse);
		} else if (pkt.type == Packet.EventType.CHANGE_WALL_CLING_STATE) {
			buffer.writeBoolean(pkt.desiredWallClingState);
		}
	}
	
	public static final class Server {
		
		public static void registerServerPackets() {
			INSTANCE.registerMessage(ReplicationData.nextID(false), Packet.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ReplicatePlayerMovement.Server::onServerEvent);
		}
		
		private static void onServerEvent(Packet packet, Supplier<NetworkEvent.Context> ctx) {
			NetworkEvent.Context context = ctx.get();
			ServerPlayer sender = context.getSender();
			if (packet.type == Packet.EventType.AIR_JUMP) {
				context.enqueueWork(() -> {
					float left = Mth.clamp(packet.playerReportedLeftImpulse, -1, 1);
					float forward = Mth.clamp(packet.playerReportedForwardImpulse, -1, 1);
					
					MotionMarshaller.tryPerformJump(sender, left, forward, null);
				});
			} else if (packet.type == Packet.EventType.WALL_JUMP) {
				context.enqueueWork(() -> {
					float left = Mth.clamp(packet.playerReportedLeftImpulse, -1, 1);
					float forward = Mth.clamp(packet.playerReportedForwardImpulse, -1, 1);
					
					MotionMarshaller.tryPerformJump(sender, left, forward, MotionMarshaller.Utilities.getBlockForBestWall(sender));
				});
			} else if (packet.type == Packet.EventType.DASH) {
				context.enqueueWork(() -> {
					MotionMarshaller.tryPerformDashCommon(sender);
				});
			} else if (packet.type == Packet.EventType.CHANGE_WALL_CLING_STATE) {
				context.enqueueWork(() -> {
					MotionMarshaller.trySetWallCling(sender, packet.desiredWallClingState);
				});
			}
			context.setPacketHandled(true);
		}
		
	}
	
	public static final class Client {
		
		public static void doAirJump() {
			LocalPlayer plr = Minecraft.getInstance().player;
			ReplicatePlayerMovement.INSTANCE.sendToServer(Packet.toAirJump(plr.input.leftImpulse, plr.input.forwardImpulse));
		}
		
		public static void doDash() {
			LocalPlayer plr = Minecraft.getInstance().player;
			ReplicatePlayerMovement.INSTANCE.sendToServer(Packet.toDash(plr.getLookAngle()));
		}
		
		public static void doWallJump() {
			LocalPlayer plr = Minecraft.getInstance().player;
			ReplicatePlayerMovement.INSTANCE.sendToServer(Packet.toWallJump(plr.input.leftImpulse, plr.input.forwardImpulse));
		}
		
		public static void replicateClingDesire(boolean clinging) {
			ReplicatePlayerMovement.INSTANCE.sendToServer(Packet.toWallCling(clinging));
		}
	}
	
	private static final class Packet {
		
		public @Nonnull EventType type = EventType.INVALID;
		public float playerReportedLeftImpulse;
		public float playerReportedForwardImpulse;
		public boolean desiredWallClingState;
		public Vec3 dashDirection = Vec3.ZERO;
		
		public Packet() {}
		public Packet(float left, float forward) {
			this.playerReportedLeftImpulse = left;
			this.playerReportedForwardImpulse = forward;
		}
		public Packet(boolean isClinging) {
			this.desiredWallClingState = isClinging;
		}
		public Packet(Vec3 dashDir) {
			this.dashDirection = dashDir;
		}
		
		
		public static Packet toWallJump(float left, float forward) {
			Packet packet = new Packet(left, forward);
			packet.type = EventType.WALL_JUMP;
			return packet;
		}
		
		public static Packet toAirJump(float left, float forward) {
			Packet packet = new Packet(left, forward);
			packet.type = EventType.AIR_JUMP;
			return packet;
		}
		
		public static Packet toWallCling(boolean isClinging) {
			Packet packet = new Packet(isClinging);
			packet.type = EventType.CHANGE_WALL_CLING_STATE;
			return packet;
		}
		
		public static Packet toDash(Vec3 dashDir) {
			Packet packet = new Packet(dashDir);
			packet.type = EventType.DASH;
			return packet;
		}
		
		
		public enum EventType {
			
			INVALID,
			
			@ClientUseOnly
			DASH,
			
			@ClientUseOnly
			WALL_JUMP,
			
			@ClientUseOnly
			AIR_JUMP,
			
			@ClientUseOnly
			CHANGE_WALL_CLING_STATE;
			
			/**
			 * Converts this enum value into a byte equivalent.
			 * @return The byte equivalent of this enum item.
			 * @throws IllegalArgumentException If this is called on {@link #INVALID}.
			 */
			public byte toByte() throws IllegalArgumentException {
				return switch(this) {
					case DASH -> (byte)0;
					case WALL_JUMP -> (byte)1;
					case AIR_JUMP -> (byte)2;
					case CHANGE_WALL_CLING_STATE -> (byte)3;
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
					case 0 -> DASH;
					case 1 -> WALL_JUMP;
					case 2 -> AIR_JUMP;
					case 3 -> CHANGE_WALL_CLING_STATE;
					default -> INVALID;
				};
			}
			
		}
		
	}
	
}
