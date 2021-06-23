package etithespirit.etimod.valuetypes;

/**
 * Boolean, but better! This can be set to three distinct values.
 */
public final class Trilean {
	
	/** The zero state of a trilean, best comparable to false. */
	public static final Trilean ZERO = new Trilean();
	
	/** The one state of a trilean, best comparable to true. */
	public static final Trilean ONE = new Trilean();
	
	/** The two state of a trilean, best comparable to true 2: electric boogaloo */
	public static final Trilean TWO = new Trilean();
	
	private Trilean() { }
	
	/**
	 * Returns this {@link Trilean} as a numeric value (either 0, 1, or 2).
	 */
	public byte toByte() {
		if (this == ZERO) return 0;
		if (this == ONE) return 1;
		return 2;
	}
	
	/**
	 * Returns a {@link Trilean} value corresponding to the given numeric value, 0, 1, or 2.
	 * @param v The numeric value representing this trilean.
	 * @return The {@link Trilean} associated with the given numeric value.
	 * @throws IllegalArgumentException If the given numeric value is not 0, 1, or 2.
	 */
	public static Trilean fromByte(byte v) {
		if (v == 0) return ZERO;
		if (v == 1) return ONE;
		if (v == 2) return TWO;
		throw new IllegalArgumentException(String.format("Invalid input for parameter 'v' (expected 0, 1, or 2, got %s)", String.valueOf(v)));
	}
	
}
