package etithespirit.etimod.util;

/**
 * A class used to truncate numeric values to use SI endings.
 * @author Eti
 *
 */
public final class TruncateNumber {
	
	// Fun fact: This was adapted from a module I wrote in Lua a very long time ago.
		
	// Prevent instances
	private TruncateNumber() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static final String[] TRUNCATE_LABELS = {"K", "M", "B", "T", "P"};
	
	/**
	 * Returns the number 10^value
	 * @param value
	 * @return
	 */
	private static double tenToPowerOf(double value) {
		return Math.pow(10, value);
	}
	
	/**
	 * Rounds the given number and preserves the given number of places in the decimals.
	 * @param value
	 * @param places
	 * @return
	 */
	private static double roundPlaces(double value, int places) {
		double e = tenToPowerOf(places);
		return Math.round(value * e) / e;
	}
	
	private static int clamp(int value, int min, int max) {
		return (int)Math.min(max, Math.max(min, value));
	}
	
	/**
	 * Return a string representing the given number truncated with SI endings. For instance, 1000 will be returned as 1K, 1000000 will be returned as 1M, etc.
	 * @param d
	 * @return
	 */
	public static String truncateNumber(double d) {
		return truncateNumber(d, 1);
	}
	
	/**
	 * Return a string representing the given number truncated with SI endings. For instance, 1000 will be returned as 1K, 1000000 will be returned as 1M, etc.<br/>
	 * This version supports a limited place count as well, so if given the number 54321, and if placeCount = 2, then this method will return "54.32K"
	 * @param number
	 * @return
	 */
	public static String truncateNumber(double number, int placeCount) {
		int div = 3; // The 10^div factor that this number will be tested to fit into.
		int count = 0;
		// int letterCount = 0; // If we're over the limit of the truncate letter pool, this will increase, allowing for things like "1KY" for 1000Y. It is tested by Count.
		
		double resultingValue = number / tenToPowerOf(div);
		if (resultingValue < 1) {
			return String.valueOf(roundPlaces(number, placeCount));
		}
		
		while (resultingValue >= 1) {
			// letterCount = (int) (1 + Math.floor(count / TRUNCATE_LABELS.length));
			div += 3;
			count += 1;
			resultingValue = number / tenToPowerOf(div);
		}
		String letterChain = "";
		resultingValue = roundPlaces(resultingValue * 1000, placeCount);
		
		do {
			int letterIndex = clamp(count, 1, TRUNCATE_LABELS.length); // This was written for Lua indexing, so it's from 1=>len, not 0=>(len-1)
			letterChain += TRUNCATE_LABELS[letterIndex - 1]; // sub 1 from here instead. do NOT offset the clamp.
			count -= TRUNCATE_LABELS.length;
		} while (count > 0);
		
		return String.valueOf(resultingValue) + letterChain;
	}

}
