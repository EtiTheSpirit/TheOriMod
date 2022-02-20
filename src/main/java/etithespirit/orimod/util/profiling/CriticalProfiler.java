package etithespirit.orimod.util.profiling;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import etithespirit.orimod.OriMod;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.AbstractReferenceList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.Util;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.FilledProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ProfilerPathEntry;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * A profiler for the Ori mod. It is very similar to MC's ActiveProfiler but has some slight differences.
 */
public final class CriticalProfiler {
	private final LongArrayList starts = new LongArrayList();
	private final MergeableStringStack pathStack = new MergeableStringStack();
	private final Logger log;
	private final LongSupplier getRealTime;
	private long absStartTimeNano;
	private final long warnTimeNanos;
	private final long totalWarnTimeNanos;
	private boolean live;
	
	public CriticalProfiler(Logger log, LongSupplier nanoSupplier) {
		this(log, nanoSupplier, Long.MAX_VALUE, Long.MAX_VALUE);
	}
	
	public CriticalProfiler(Logger log, LongSupplier nanoSupplier, long warnTimeNanos, long totalWarnTimeNanos) {
		this.log = log;
		this.getRealTime = nanoSupplier;
		this.warnTimeNanos = warnTimeNanos;
		this.totalWarnTimeNanos = totalWarnTimeNanos;
	}
	
	public boolean isLive() {
		return live;
	}
	
	public void profileBegin() {
		if (live) {
			OriMod.LOG.error("Profiler tick was already running. Was a call to profileEnd() forgotten? Was profileBegin() called twice?");
		} else {
			absStartTimeNano = getRealTime.getAsLong();
			live = true;
			push(log.getName());
		}
	}
	
	public void profileEnd() {
		if (!this.live) {
			log.error("Profiler tick was not running. Was a call to profileBegin() forgotten? Was profileEnd() called twice?");
		} else {
			pop();
			live = false;
			if (!starts.isEmpty()) {
				log.error("Profiler tick ended before it was fully popped ({} elements remain on the stack). Did you forget to call pop?", starts.size());
			}
			long delta = getRealTime.getAsLong() - absStartTimeNano;
			if (delta > totalWarnTimeNanos) {
				log.warn("Profiler '{}' reported that, in total, it took too long to execute (took {} nanoseconds)!", log.getName(), delta);
			}
		}
	}
	
	public void push(String routine) {
		if (!live) {
			log.error("Cannot push '{}' - The profiler hasn't started! Is there a missing profileBegin() call?", routine);
		} else {
			starts.push(getRealTime.getAsLong());
			pathStack.push(routine);
		}
	}
	
	public void pop() {
		if (!live) {
			log.error("Cannot pop - The profiler hasn't started! Is there a missing profileBegin() call?");
		} else {
			long start = starts.popLong();
			long delta = getRealTime.getAsLong() - start;
			if (delta > warnTimeNanos) {
				log.warn("Profile section {} took too long ({} nanoseconds)!", pathStack.toString(), delta);
			}
			pathStack.pop();
		}
	}
	
	public void popPush(String newRoutine) {
		this.pop();
		this.push(newRoutine);
	}
	
	private static final class MergeableStringStack {
		
		private final java.util.Stack<String> stack = new java.util.Stack<>();
		
		public void push(String pathElement) {
			stack.push(pathElement);
		}
		
		public String pop() {
			return stack.pop();
		}
		
		@Override
		public String toString() {
			int size = stack.size();
			StringBuilder builder = new StringBuilder(size);
			int i = 1;
			for (String element : stack) {
				builder.append(element);
				if (i > 1 && i < size) {
					builder.append('/');
				}
				i++;
			}
			return builder.toString();
		}
		
	}
}
