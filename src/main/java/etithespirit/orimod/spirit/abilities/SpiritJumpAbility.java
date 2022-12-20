package etithespirit.orimod.spirit.abilities;

import etithespirit.orimod.common.chat.ChatHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum SpiritJumpAbility {
	SINGLE_JUMP(0),
	DOUBLE_JUMP(1),
	TRIPLE_JUMP(2);
	
	/** The amount of times the spirit can jump while suspended in the air. */
	public final int airJumps;
	
	SpiritJumpAbility(int numAirJumps) {
		airJumps = numAirJumps;
	}
	
	public byte toByte() {
		return (byte)airJumps;
	}
	
	public MutableComponent dumpToComponent() {
		return Component.literal("JumpSettings[").withStyle(ChatFormatting.WHITE)
			.append(ChatHelper.keyToValue("jumpType", Component.literal(toString()).withStyle(ChatFormatting.AQUA)))
			.append(Component.literal("]").withStyle(ChatFormatting.WHITE));
	}
	
	public MutableComponent dumpToComponent(boolean bundledWallJumpValue) {
		return Component.literal("JumpSettings[").withStyle(ChatFormatting.WHITE)
			.append(ChatHelper.keyToValue("jumpType", Component.literal(toString()).withStyle(ChatFormatting.AQUA)))
			.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
			.append(ChatHelper.keyToValue("canWallJump", ChatHelper.ofBoolean(bundledWallJumpValue)))
			.append(Component.literal("]").withStyle(ChatFormatting.WHITE));
	}
	
	public static SpiritJumpAbility fromByte(byte b) {
		return switch(b) {
			default -> SINGLE_JUMP;
			case 1 -> DOUBLE_JUMP;
			case 2 -> TRIPLE_JUMP;
		};
	}
	
	@Override
	public String toString() {
		return switch(this) {
			default -> "Single Jump";
			case DOUBLE_JUMP -> "Double Jump";
			case TRIPLE_JUMP -> "Triple Jump";
		};
	}
}
