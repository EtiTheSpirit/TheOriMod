package etithespirit.orimod.networking.player;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.networking.ReplicationData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ReplicatePlayerMovement {
	
	
	public static final Function<FriendlyByteBuf, MovementReplicationPacket> BUFFER_TO_PACKET = ReplicatePlayerMovement::bufferToPacket;
	public static final BiConsumer<MovementReplicationPacket, FriendlyByteBuf> PACKET_TO_BUFFER = ReplicatePlayerMovement::packetToBuffer;
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(OriMod.MODID, "spirit_movement_replicator"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	private static MovementReplicationPacket bufferToPacket(FriendlyByteBuf buffer) {
		MovementReplicationPacket pkt = new MovementReplicationPacket();
		pkt.type = MovementReplicationPacket.EventType.fromByte(buffer.readByte());
		if (pkt.type == MovementReplicationPacket.EventType.DASH) {
			pkt.dashDirection = new Vec3(
				buffer.readDouble(),
				buffer.readDouble(),
				buffer.readDouble()
			);
		} else if (pkt.type == MovementReplicationPacket.EventType.AIR_JUMP || pkt.type == MovementReplicationPacket.EventType.WALL_JUMP) {
			pkt.playerReportedLeftImpulse = buffer.readFloat();
			pkt.playerReportedForwardImpulse = buffer.readFloat();
		} else if (pkt.type == MovementReplicationPacket.EventType.CHANGE_WALL_CLING_STATE) {
			pkt.desiredWallClingState = buffer.readBoolean();
		} else {
			throw new IllegalArgumentException();
		}
		return pkt;
	}
	
	private static void packetToBuffer(MovementReplicationPacket pkt, FriendlyByteBuf buffer) {
		buffer.writeByte(pkt.type.toByte());
		if (pkt.type == MovementReplicationPacket.EventType.DASH) {
			buffer.writeDouble(pkt.dashDirection.x);
			buffer.writeDouble(pkt.dashDirection.y);
			buffer.writeDouble(pkt.dashDirection.z);
		} else if (pkt.type == MovementReplicationPacket.EventType.AIR_JUMP || pkt.type == MovementReplicationPacket.EventType.WALL_JUMP) {
			buffer.writeFloat(pkt.playerReportedLeftImpulse);
			buffer.writeFloat(pkt.playerReportedForwardImpulse);
		} else if (pkt.type == MovementReplicationPacket.EventType.CHANGE_WALL_CLING_STATE) {
			buffer.writeBoolean(pkt.desiredWallClingState);
		}
	}
	
	private static void onServerEvent() {
	
	}
	
}
