package etithespirit.orimod.util;

/**
 * A collection of utilities for 32 bit integers.
 */
public final class Bit32 {
	
	/**
	 * Returns true if the given value has at least one of the bits in flags set.
	 * @param value The value to check.
	 * @param flags The flags to check the value against.
	 * @return True if at least one of the bits of flags is also set in value.
	 */
	public static boolean hasFlag(int value, int flags) {
		return (value & flags) > 0;
	}
	
}
