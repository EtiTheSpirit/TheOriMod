package etithespirit.orimod.spirit.abilities;

import etithespirit.orimod.common.chat.ChatHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum SpiritDashAbility {
	NO_DASH(false, false, false),
	GROUND_ONLY(true, false, false),
	AIR_DASH(true, true, false),
	WATER_DASH(true, false, true),
	AIR_AND_WATER_DASH(true, true, true);
	
	private final boolean canDashInternal;
	private final boolean inAir;
	private final boolean inWater;
	
	/**
	 * Returns true if dash is possible.
	 * @return True if dash is possible, false if not.
	 */
	public boolean canDash() {
		return canDashInternal;
	}
	
	/**
	 * Returns true if dashing in air is possible. Always returns false if {@link #canDash()} is false.
	 * @return True if dashing in the air is possible.
	 */
	public boolean canDashInAir() {
		return canDashInternal && inAir;
	}
	
	/**
	 * Returns true if dashing underwater is possible. Always returns false if {@link #canDash()} is false.
	 * @return True if dashing underwater is possible.
	 */
	public boolean canDashInWater() {
		return canDashInternal && inWater;
	}
	
	SpiritDashAbility(boolean capable, boolean inAir, boolean inWater) {
		this.canDashInternal = capable;
		this.inAir = inAir;
		this.inWater = inWater;
	}
	
	public byte toByte() {
		return switch(this) {
			default -> (byte)0;
			case GROUND_ONLY -> (byte)0b001;
			case AIR_DASH -> (byte)0b011;
			case WATER_DASH -> (byte)0b101;
			case AIR_AND_WATER_DASH -> (byte)0b111;
		};
	}
	
	public MutableComponent dumpToComponent() {
		if (!canDashInternal) {
			return Component.literal("DashSettings[").withStyle(ChatFormatting.WHITE)
				.append(ChatHelper.keyToValue("canDash", ChatHelper.ofBooleanYN(false)))
				.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				.append(ChatHelper.keyToValue("allowAirDash", Component.literal("N/A").withStyle(ChatFormatting.DARK_GRAY)))
				.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				.append(ChatHelper.keyToValue("allowWaterDash", Component.literal("N/A").withStyle(ChatFormatting.DARK_GRAY)))
				.append(Component.literal("]").withStyle(ChatFormatting.WHITE));
		} else {
			return Component.literal("DashSettings[").withStyle(ChatFormatting.WHITE)
				.append(ChatHelper.keyToValue("canDash", ChatHelper.ofBooleanYN(true)))
				.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				.append(ChatHelper.keyToValue("allowAirDash", ChatHelper.ofBooleanYN(inAir)))
				.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				.append(ChatHelper.keyToValue("allowWaterDash", ChatHelper.ofBooleanYN(inWater)))
				.append(Component.literal("]").withStyle(ChatFormatting.WHITE));
		}
	}
	
	public static SpiritDashAbility fromByte(byte b) {
		return switch(b) {
			default -> NO_DASH;
			case 0b001 -> GROUND_ONLY;
			case 0b011 -> AIR_DASH;
			case 0b101 -> WATER_DASH;
			case 0b111 -> AIR_AND_WATER_DASH;
		};
	}
	
	public static SpiritDashAbility fromBooleans(boolean canDash, boolean inAir, boolean inWater) {
		if (!canDash) return NO_DASH;
		if (inAir && inWater) return AIR_AND_WATER_DASH;
		if (inAir) return AIR_DASH;
		if (inWater) return WATER_DASH;
		return GROUND_ONLY;
	}
}
