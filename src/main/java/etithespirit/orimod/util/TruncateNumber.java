package etithespirit.orimod.util;


import net.minecraft.util.Mth;

/**
 * A class used to truncate numeric values to use SI endings.
 * @author Eti
 */
@SuppressWarnings("unused")
public final class TruncateNumber {
	
	// Fun fact: This was adapted from a module I wrote in Lua a very long time ago.
	
	// Prevent instances
	private TruncateNumber() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static final String[] TRUNCATE_LABELS = {"k", "M", "B", "T", "P"};
	
	/**
	 * Returns the number 10^value
	 * @param value The value to use for the exponent.
	 * @return 10^value
	 */
	private static double tenToPowerOf(double value) {
		return Math.pow(10, value);
	}
	
	/**
	 * A variant of {@link #tenToPowerOf(double)} that runs much faster for values 0-5 via a LUT instead of using math.pow
	 * @param value
	 * @return
	 */
	private static double tenToPowerOfFast(int value) {
		return switch (value) {
			case 0 -> 1D;
			case 1 -> 10D;
			case 2 -> 100D;
			case 3 -> 1000D;
			case 4 -> 10000D;
			case 5 -> 100000D;
			default -> Math.pow(10, value);
		};
	}
	
	/**
	 * Rounds the given number and preserves the given number of places in the decimals.
	 * @param value The value to round.
	 * @param places The number of places to preserve.
	 * @return The given value rounded down to the number of places.
	 */
	private static double roundPlaces(double value, int places) {
		double e = tenToPowerOfFast(places);
		return Math.round(value * e) / e;
	}
	
	/**
	 * Return a string representing the given number truncated with SI endings. For instance, 1000 will be returned as 1K, 1000000 will be returned as 1M, etc.
	 * @param number The value to truncate.
	 * @return A string representing this value truncated with SI endings, down to one decimal place of accuracy.
	 */
	public static String truncateNumber(double number) {
		return truncateNumber(number, 1);
	}
	
	/**
	 * Return a string representing the given number truncated with SI endings. For instance, 1000 will be returned as 1K, 1000000 will be returned as 1M, etc.<br/>
	 * This version supports a limited place count as well, so if given the number 54321, and if placeCount = 2, then this method will return "54.32K"
	 * @param number The number to truncate.
	 * @return A string representing this value truncated with SI endings, down to the given amount of decimal places of accuracy.
	 */
	public static String truncateNumber(double number, int placeCount) {
		if (Double.isNaN(number)) return "NaN";
		if (Double.isInfinite(number)) return number > 0 ? "∞" : "-∞";
		
		int div = 3; // The 10^div factor that this number will be tested to fit into.
		int count = 0;
		// int letterCount = 0; // If we're over the limit of the truncate letter pool, this will increase, allowing for things like "1KY" for 1000Y. It is tested by Count.
		
		double resultingValue = number / tenToPowerOfFast(div);
		if (resultingValue < 1) {
			return String.valueOf(roundPlaces(number, placeCount));
		}
		
		while (resultingValue >= 1) {
			// letterCount = (int) (1 + Math.floor(count / TRUNCATE_LABELS.length));
			div += 3;
			count += 1;
			resultingValue = number / tenToPowerOfFast(div);
		}
		String letterChain = "";
		resultingValue = roundPlaces(resultingValue * 1000, placeCount);
		
		do {
			int letterIndex = Mth.clamp(count, 1, TRUNCATE_LABELS.length); // This was written for Lua indexing, so it's from 1=>len, not 0=>(len-1)
			letterChain += TRUNCATE_LABELS[letterIndex - 1]; // sub 1 from here instead. do NOT offset the clamp.
			count -= TRUNCATE_LABELS.length;
		} while (count > 0);
		
		return resultingValue + letterChain;
	}
	
}
