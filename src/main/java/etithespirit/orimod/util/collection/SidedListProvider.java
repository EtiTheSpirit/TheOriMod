package etithespirit.orimod.util.collection;


import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * A provider of two, distinct {@link List} instances, one for each side of the game.
 * This is strictly useful in singleplayer setups, where a statically bound list benefits a class
 * and a different list is needed on the client and server for thread safety or to differentiate
 * between a reference for the server vs for the client.
 * @param <T> The type of element to store.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class SidedListProvider<T> {
	
	/** The list for the dedicated or integrated server. */
	private final List<T> server;
	
	/** The list for the clientside. */
	private final List<T> client;
	
	/**
	 * Construct a new provider without strict element tracking.
	 */
	public SidedListProvider() {
		if (FMLEnvironment.dist.isDedicatedServer()) {
			client = null;
		} else {
			client = new ArrayList<>();
		}
		server = new ArrayList<>();
	}
	
	/**
	 * Returns the list appropriate for this side.
	 * @param isClient Whether or not to return the client list.
	 * @return The list appropriate for the current side, be it a client vs. dedicated/integrated server.
	 * @throws IllegalArgumentException If {@code isClient} is true and this is running in a dedicated (not integrated) server build of Minecraft.
	 */
	public List<T> getListForSide(boolean isClient) throws IllegalArgumentException {
		if (isClient && FMLEnvironment.dist.isDedicatedServer())
			throw new IllegalArgumentException("Cannot access client arrays from a dedicated server (which never has a clientside)!");
		
		if (isClient) return client;
		return server;
	}
	
}
