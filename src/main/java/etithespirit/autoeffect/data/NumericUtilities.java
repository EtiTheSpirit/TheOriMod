package etithespirit.autoeffect.data;

import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;

/**
 * Provides numeric utilities that can technically be used anywhere, but are mostly designed for this system.
 * @author Eti
 *
 */
public final class NumericUtilities {
	
	/**
	 * 96 hours in ticks, which is 6912000 ticks (96 * 60 * 60 * 20).
	 */
	public static final long HOURS_96_IN_TICKS = 6912000;
	
	/**
	 * The highest value that duration factor can be before it causes problems.
	 */
	private static final float MAX_DURATION_FACTOR = (float)Math.pow(2, 32);
	
	/**
	 * This is a variation of {@link net.minecraft.potion.EffectUtils.getPotionDurationString} that is designed to handle potion durations longer than Minecraft's general time threshold. It is mostly intended for use of large-scale effects, for instance, if the player has some passive buff for a couple days and you are displaying it via a potion effect, this is perfect.<br/>
	 * <br/>
	 * <strong>This derives standard timer functionality in that it does NOT use EffectInstance.getIsPotionDurationMax() to decide to return **:** for the duration</strong>. Rather, this has a cap of 96 hours (which should be more than enough for even most edge cases). If the time is beyond 96 hours, this will return **:**:**.
	 * @param effect The effect that will provide the given duration in ticks. Throws a {@link java.lang.NullPointerException} if this is null.
	 * @param durationFactor The remaining ticks will be multiplied by this value, and the result will be used to display time. Throws a {@link java.lang.IllegalArgumentException} if it is greater than or equal to 4294967296, or less than or equal to 0. 
	 * @return A string in the format of HH:MM:SS, or if the duration is less than one hour, MM:SS.
	 */
	public static String getLongPotionDurationString(EffectInstance effect, float durationFactor) {
		if (durationFactor >= MAX_DURATION_FACTOR || durationFactor <= 0) throw new IllegalArgumentException("[durationFactor] cannot be >= 4294967296 or <= 0");
		
		int ticks = effect.getDuration();
		long factoredTicks = MathHelper.lfloor(ticks * durationFactor);
		if (factoredTicks > HOURS_96_IN_TICKS) return "**:**:**";
		return convertTicksToClock(factoredTicks);
	}
	
	/**
	 * This is a variation of {@link net.minecraft.potion.EffectUtils.getPotionDurationString} that is designed to handle potion durations longer than Minecraft's general time threshold. It is mostly intended for use of large-scale effects, for instance, if the player has some passive buff for a couple days and you are displaying it via a potion effect, this is perfect.<br/>
	 * <br/>
	 * <strong>This derives standard timer functionality in that it does NOT use EffectInstance.getIsPotionDurationMax() to decide to return **:** for the duration</strong>. Rather, this has a cap of 96 hours (which should be more than enough for even most edge cases). If the time is beyond 96 hours, this will return **:**:**.
	 * @param effect The effect that will provide the given duration in ticks. Throws a {@link java.lang.NullPointerException} if this is null.
	 * @return A string in the format of HH:MM:SS, or if the duration is less than one hour, MM:SS.
	 */
	public static String getLongPotionDurationString(EffectInstance effect) { return getLongPotionDurationString(effect, 1f); }
	
	/**
	 * Converts the given number of ticks to a timer in the format of HH:MM:SS, or if there is less than one hour, MM:SS. Throws {@link java.lang.IllegalArgumentException} if ticks is greater than Long.MAX_VALUE or less than 0.
	 * @param ticks
	 * @return
	 */
	public static String convertTicksToClock(long ticks) {
		// Gotta love how Java handles signed/unsigned values.
		if (ticks < 0) throw new IllegalArgumentException("[ticks] cannot be a value less than 0 or greater than 9223372036854775807!");
		long seconds = Math.floorDiv(ticks, 20);
		
		long minutes = Math.floorDiv(seconds, 60);
		seconds %= 60;
		
		long hours = Math.floorDiv(minutes, 60);
		minutes %= 60;
		seconds %= 60;
		
		if (hours > 0) {
			return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
		} else {
			return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
		}
	}
	
	/**
	 * Converts the input value to Roman numerals, capping at a maximum value of 1000. <em>Technically</em> this violates standards in that it does not respect localization, so if you care about that, you'll need to implement some special handling.<br/>
	 * <br/>
	 * Values greater than 1000 or less than 0 will throw an {@link java.lang.IllegalArgumentException}. A value of 0 will return an empty string, as Roman numeral zero 0 not exist.
	 * @param number
	 * @return
	 */
	public static String toRomanNumerals(int number) {
		if (number > 1000 || number < 0) throw new IllegalArgumentException("The input number cannot be greater than 1000 or less than 0.");
		if (number >= 1000) return "M" + toRomanNumerals(number - 1000);
		if (number >= 900) return "CM" + toRomanNumerals(number - 900);
		if (number >= 500) return "D" + toRomanNumerals(number - 500);
		if (number >= 400) return "CD" + toRomanNumerals(number - 400);
		if (number >= 100) return "C" + toRomanNumerals(number - 100);
		if (number >= 90) return "XC" + toRomanNumerals(number - 90);
		if (number >= 50) return "L" + toRomanNumerals(number - 50);
		if (number >= 40) return "XL" + toRomanNumerals(number - 40);
		if (number >= 10) return "X" + toRomanNumerals(number - 10);
		if (number >= 9) return "IX" + toRomanNumerals(number - 9);
		if (number >= 5) return "V" + toRomanNumerals(number - 5);
		if (number >= 4) return "IV" + toRomanNumerals(number - 4);
		if (number >= 1) return "I" + toRomanNumerals(number - 1);
		return "";
	}

}
