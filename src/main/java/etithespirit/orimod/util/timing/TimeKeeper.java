package etithespirit.orimod.util.timing;

/**
 * A lazy tool to keep track of delta time with millisecond resolution.
 */
public final class TimeKeeper {
	
	private long lastTime = System.currentTimeMillis();
	
	/**
	 * Ticks the time keeper, returning the amount of time since either the last time {@link #tick()} was called, or since construction if it has
	 * never been called before.
	 * @return The time since the last call to {@link #tick()}, or since construction if it has never been called.
	 */
	public float tick() {
		long now = System.currentTimeMillis();
		long delta = now - lastTime;
		lastTime = now;
		return delta / 1000f;
	}
	
}
