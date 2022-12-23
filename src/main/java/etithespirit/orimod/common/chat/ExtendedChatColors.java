package etithespirit.orimod.common.chat;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;

import java.util.Optional;

/**
 * Provides a set of custom formatting codes for messages displayed in menus or the chat that allows full RGB control rather than using
 * the set of predefined colors usually provided by Minecraft.
 */
public class ExtendedChatColors {
	
	/** Middle gray. */
	public static final StylePair GRAY_PAIR = new StylePair(0x7F7F7F);
	public static final Style GRAY = GRAY_PAIR.normal;
	
	/** A bright, desaturated red. */
	public static final StylePair DUSTY_RED_PAIR = new StylePair(0xFF5959);
	public static final Style DUSTY_RED  = DUSTY_RED_PAIR.normal;
	
	/** My favorite Spiral Knights color. It's a sickly magenta with red undertones. */
	public static final StylePair SWARM_PINK_PAIR = new StylePair(0xFF0062);
	public static final Style SWARM_PINK = SWARM_PINK_PAIR.normal;
	
	/** A whitish-blue color roughly identical to that as seen in the Forlorn Ruins after power has been restored. */
	public static final StylePair FORLORN_BLUE_PAIR = new StylePair(0xD0FAFA);
	public static final Style FORLORN_BLUE = FORLORN_BLUE_PAIR.normal;
	
	/** An orange color roughly identical to that as seen in the Forlorn Ruins technology. */
	public static final StylePair FORLORN_ORANGE_PAIR = new StylePair(0xFF8C23);
	public static final Style FORLORN_ORANGE = FORLORN_ORANGE_PAIR.normal;
	
	/** See {@link #FORLORN_BLUE_PAIR} */
	public static final StylePair LUXEN_PAIR = FORLORN_BLUE_PAIR;
	public static final Style LUXEN = LUXEN_PAIR.normal;
	
	/** A deep, bright, slightly faded maroon color to represent Redstone Flux. */
	public static final StylePair RF_PAIR = new StylePair(0xA6222F);
	public static final Style RF = RF_PAIR.normal;
	
	/** See {@link #FORLORN_BLUE_PAIR} */
	public static final StylePair LIGHT_PAIR = LUXEN_PAIR;
	public static final Style LIGHT = LIGHT_PAIR.normal;
	
	/** A half-saturated slightly red-biased violet at around 90% brightness. */
	public static final StylePair DECAY_PAIR = new StylePair(0xC26FDE);
	public static final Style DECAY = DECAY_PAIR.normal;
	
	
	public static class StylePair {
		
		private static final float DARK_SCALE = 0.75f;
		
		public final Style normal;
		
		public final Style dark;
		
		public StylePair(int normal, int dark) {
			this.normal = color(normal);
			this.dark = color(dark);
		}
		
		public StylePair(int clr) {
			this.normal = color(clr);
			int b = (clr >> 16) & 0xFF;
			int g = (clr >> 8) & 0xFF;
			int r = clr & 0xFF;
			
			b = Mth.clamp(Math.round(b * DARK_SCALE), 0, 0xFF);
			g = Mth.clamp(Math.round(g * DARK_SCALE), 0, 0xFF);
			r = Mth.clamp(Math.round(r * DARK_SCALE), 0, 0xFF);
			this.dark = color((b << 16) | (g << 8) | r);
		}
		
		
		@SuppressWarnings({"rawtypes", "unchecked"})
		private static Style color(int color) {
			final Optional NULL = Optional.empty();
			return Style.create(Optional.of(TextColor.fromRgb(color)), NULL, NULL, NULL, NULL, NULL, NULL, NULL);
		}
		
		
	}
	
}
