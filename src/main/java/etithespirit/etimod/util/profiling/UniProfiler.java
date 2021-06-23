package etithespirit.etimod.util.profiling;

import etithespirit.etimod.EtiMod;
import net.minecraft.profiler.IProfiler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;

/**
 * A provider of an instance of {@link IProfiler} appropriate for the current distribution of the game (client vs. dedicated server)
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class UniProfiler {

	private UniProfiler()  { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static IProfiler clientProfiler;
	private static IProfiler serverProfiler;
	
	/**
	 * @return An {@link IProfiler} instance appropriate for the current distribution of the game (client vs. dedicated server)
	 */
	public static IProfiler getProfiler() {
		if (FMLEnvironment.dist.isClient()) {
			return clientProfiler;
		} else {
			return serverProfiler;
		}
	}
	
	/**
	 * Set the {@link IProfiler} associated with the given distribution.
	 * @param profiler The profiler to set.
	 * @param distribution The distribution to set it for.
	 */
	public static void setProfiler(IProfiler profiler, Dist distribution) {
		if (distribution.isClient()) {
			clientProfiler = profiler;
		} else if (distribution.isDedicatedServer()) {
			serverProfiler = profiler;
		}
	}
	
	/**
	 * References the {@link IProfiler} suitable for this distribution and calls its push method with the given parameter.
	 * @param inClass The class pushing this profiler section.
	 * @param method The name of the method this is executing in, ctor for a constructor, and cctor for a static constructor.
	 * @param action An optional action specific to this part of the method.
	 */
	public static void push(Class<?> inClass, String method, @Nullable String action) {
		String term = EtiMod.MODID + "::" + inClass.getSimpleName() + "#" + method;
		if (action != null) {
			term += ": " + action;
		}
		getProfiler().push(term);
	}
	
	
	/**
	 * References the {@link IProfiler} suitable for this distribution and calls its popPush method with the given parameter.
	 * @param inClass The class pushing this profiler section.
	 * 	 * @param method The name of the method this is executing in, ctor for a constructor, and cctor for a static constructor.
	 * 	 * @param action An optional action specific to this part of the method.
	 */
	public static void popPush(Class<?> inClass, String method, @Nullable String action) {
		String term = EtiMod.MODID + "::" + inClass.getSimpleName() + "#" + method;
		if (action != null) {
			term += ": " + action;
		}
		getProfiler().popPush(term);
	}
	
	/**
	 * References the {@link IProfiler} suitable for this distribution and calls its pop method.
	 */
	public static void pop() {
		getProfiler().pop();
	}

}
