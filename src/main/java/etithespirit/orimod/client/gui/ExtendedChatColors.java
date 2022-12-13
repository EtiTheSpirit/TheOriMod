package etithespirit.orimod.client.gui;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;

import java.util.Optional;


public class ExtendedChatColors {
	
	public static final StylePair GRAY = new StylePair(0x7F7F7F);
	public static final StylePair DUSTY_RED = new StylePair(0xFF5959);
	public static final StylePair SWARM_PINK = new StylePair(0xFF0062);
	public static final StylePair FORLORN_BLUE = new StylePair(0xD0FAFA);
	public static final StylePair FORLORN_ORANGE = new StylePair(0xFF8C23);
	public static final StylePair LUXEN = FORLORN_BLUE;
	public static final StylePair RF = new StylePair(0xA6222F);
	
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
