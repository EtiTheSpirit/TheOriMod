package etithespirit.etimod.util.collection;

import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * A provider of two, distinct {@link CachedImmutableSetWrapper} instances, one for each side of the game.
 * @param <T> The type of element to store.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class SidedListProvider<T> {
	
	/** The list for the dedicated or integrated server. */
	private final CachedImmutableSetWrapper<T> server;
	
	/** The list for the clientside. */
	private final CachedImmutableSetWrapper<T> client;
	
	/**
	 * Construct a new provider without strict element tracking.
	 */
	public SidedListProvider() {
		this(false);
	}
	
	/**
	 * Construct a new provider with optional strict element tracking.
	 * @param strictElementTracking Whether or not the strictElementTracking property on the internal {@link CachedImmutableSetWrapper} instances is set,
	 *                              which prevents duplicate instances from being added and prevents missing elements from being removed
	 *                              (both by raising an exception).
	 */
	public SidedListProvider(boolean strictElementTracking) {
		if (FMLEnvironment.dist.isDedicatedServer()) {
			client = null;
		} else {
			client = new CachedImmutableSetWrapper<>(strictElementTracking);
		}
		server = new CachedImmutableSetWrapper<>(strictElementTracking);
	}
	
	/**
	 * Returns the list appropriate for this side.
	 * @param isClient Whether or not to return the client list.
	 * @return The list appropriate for the current side, be it a client vs. dedicated/integrated server.
	 * @throws IllegalArgumentException If {@code isClient} is true and this is running in a dedicated server build of Minecraft.
	 */
	public CachedImmutableSetWrapper<T> getListForSide(boolean isClient) {
		if (isClient && FMLEnvironment.dist.isDedicatedServer())
			throw new IllegalArgumentException("Cannot access client arrays from a dedicated server (which never has a clientside)!");
		
		if (isClient) return client;
		return server;
	}
	
}
