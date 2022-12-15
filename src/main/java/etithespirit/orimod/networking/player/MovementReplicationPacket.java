package etithespirit.orimod.networking.player;

import etithespirit.orimod.annotation.ClientUseOnly;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public final class MovementReplicationPacket {
	
	public @Nonnull EventType type = EventType.INVALID;
	public float playerReportedLeftImpulse;
	public float playerReportedForwardImpulse;
	public boolean desiredWallClingState;
	public Vec3 dashDirection = Vec3.ZERO;
	
	public MovementReplicationPacket() {}
	public MovementReplicationPacket(float left, float forward) {
		this.playerReportedLeftImpulse = left;
		this.playerReportedForwardImpulse = forward;
	}
	public MovementReplicationPacket(boolean isClinging) {
		this.desiredWallClingState = isClinging;
	}
	public MovementReplicationPacket(Vec3 dashDir) {
		this.dashDirection = dashDir;
	}
	
	
	public static MovementReplicationPacket toWallJump(float left, float forward) {
		MovementReplicationPacket packet = new MovementReplicationPacket(left, forward);
		packet.type = EventType.WALL_JUMP;
		return packet;
	}
	
	public static MovementReplicationPacket toAirJump(float left, float forward) {
		MovementReplicationPacket packet = new MovementReplicationPacket(left, forward);
		packet.type = EventType.AIR_JUMP;
		return packet;
	}
	
	public static MovementReplicationPacket toWallCling(boolean isClinging) {
		MovementReplicationPacket packet = new MovementReplicationPacket(isClinging);
		packet.type = EventType.CHANGE_WALL_CLING_STATE;
		return packet;
	}
	
	public static MovementReplicationPacket toDash(Vec3 dashDir) {
		MovementReplicationPacket packet = new MovementReplicationPacket(dashDir);
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
