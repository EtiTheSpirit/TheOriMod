package etithespirit.orimod.common.chat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ChatHelper {
	
	public static MutableComponent keyToValue(String key, Component value) {
		return Component.literal(key).withStyle(ChatFormatting.GRAY).append(Component.literal("=").withStyle(ChatFormatting.GRAY)).append(value);
	}
	
	public static MutableComponent ofBoolean(boolean value) {
		if (value) {
			return Component.literal("true").withStyle(ChatFormatting.GREEN);
		}
		return Component.literal("false").withStyle(ChatFormatting.RED);
	}
	
	
	public static MutableComponent ofBooleanYN(boolean value) {
		if (value) {
			return Component.translatable("gui.yes").withStyle(ChatFormatting.GREEN);
		}
		return Component.translatable("gui.no").withStyle(ChatFormatting.RED);
	}
	
	
	public static boolean canMyChinchillaPlaceThisDirtOnTopOfYourBlockOnTuesday() {
		return false;
	}
	
}
