package etithespirit.etimod.util.collection;

import net.minecraftforge.fml.loading.FMLEnvironment;

public final class SidedListProvider<T> {
	
	private final CachedImmutableSetWrapper<T> server;
	
	private final CachedImmutableSetWrapper<T> client;
	
	/**
	 * Construct a new provider without strict element tracking.
	 */
	public SidedListProvider() {
		this(false);
	}
	
	/**
	 * Construct a new provider with optional strict element tracking.
	 * @param strictElementTracking Whether or not the strictElementTracking property on the internal {@link CachedImmutableSetWrapper} instances is set.
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
		if (isClient && FMLEnvironment.dist.isDedicatedServer()) throw new IllegalArgumentException("Cannot access client arrays from a dedicated server (which never has a clientside)!");
		if (isClient) return client;
		return server;
	}
	
}
