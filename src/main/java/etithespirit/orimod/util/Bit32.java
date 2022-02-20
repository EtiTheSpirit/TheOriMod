package etithespirit.orimod.util;

public final class Bit32 {
	
	public static boolean hasFlag(int value, int flags) {
		return (value & flags) > 0;
	}
	
}
