package etithespirit.orimod.lighttechlgc;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.util.profiling.CriticalProfiler;
import net.minecraft.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Deprecated(forRemoval = true)
final class AssemblyCodeProfiler {
	
	private static final String LOGNAME = OriMod.MODID + "::AssemblyCodeProfiler";
	
	/** A logger used to track this helper's behavior. */
	private static final Logger LOG = LogManager.getLogger(LOGNAME);
	
	/** A profiler used to ensure this helper is performant. */
	private static final CriticalProfiler PROFILER = new CriticalProfiler(LOG, Util.timeSource, 2000000, 50000000);
	
	/**
	 * Attempts to call profileBegin on the profiler. Returns true if it started here (and should be stopped after the last pop), or false if it was started before this
	 * call, meaning that the caller of this should not end.
	 * @return True if the caller of this method should also end profiling, false if it should not.
	 */
	public static boolean tryProfileBegin() {
		if (PROFILER.isLive()) {
			return false;
		}
		PROFILER.profileBegin();
		return true;
	}
	
	public static void profileEnd() {
		PROFILER.profileEnd();
	}
	
	/**
	 * Calls and returns the value of tryProfileBegin, then pushes the given routine.
	 * @param routine
	 * @return
	 */
	public static boolean tryProfileBeginAndPush(String routine) {
		boolean shouldEnd = tryProfileBegin();
		push(routine);
		return shouldEnd;
	}
	
	/**
	 * Based on the input boolean (from tryProfileBegin), this will pop the latest profile routine then potentially end profiling.
	 * @param tryBeginResult
	 */
	public static void popAndEndIfNeeded(boolean tryBeginResult) {
		pop();
		if (tryBeginResult) profileEnd();
	}
	
	public static void push(String routine) {
		PROFILER.push(routine);
	}
	
	public static void pop() {
		PROFILER.pop();
	}
	
	public static void popPush(String newRoutine) {
		PROFILER.popPush(newRoutine);
	}
	
}
