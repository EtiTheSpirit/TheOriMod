package etithespirit.orimod.util;

import etithespirit.orimod.annotation.NotNetworkReplicated;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * A value that exists independently for both sides of the game. This is intended for static use, as instances of objects will
 * (or should) exist for either side no matter what.
 * @param <T> The type of value to store.
 */
public final class SidedValue<T> {
	
	/** The value on the clientside. */
	@NotNetworkReplicated
	protected T clientValue;
	
	/** The value on the serverside. */
	@NotNetworkReplicated
	protected T serverValue;
	
	/**
	 * Construct a new {@link SidedValue}. The specific side is determined by whether or not this game is an instance of
	 * the dedicated server.
	 * @param def The default value to store.
	 */
	public SidedValue(T def) {
		if (!FMLEnvironment.dist.isDedicatedServer()) {
			clientValue = def;
		}
		serverValue = def;
	}
	
	/**
	 * Sets the value for the given side.
	 * @param onClient Whether or not to set the client value.
	 * @param newValue The new value to use.
	 * @throws IllegalArgumentException If {@code isClient} is true, but this is running in a dedicated server (making that impossible).
	 */
	public void set(boolean onClient, T newValue) throws IllegalArgumentException {
		if (onClient) {
			if (FMLEnvironment.dist.isDedicatedServer()) throw new IllegalArgumentException("Cannot set onClient to true in a dedicated server!");
			clientValue = newValue;
		} else {
			serverValue = newValue;
		}
	}
	
	/**
	 * Gets the value for the given side.
	 * @param onClient Whether or not to get the client value.
	 * @return The value appropriate for the given side.
	 * @throws IllegalArgumentException If {@code isClient} is true, but this is running in a dedicated server (making that impossible).
	 */
	public T get(boolean onClient) throws IllegalArgumentException {
		if (onClient) {
			if (FMLEnvironment.dist.isDedicatedServer()) throw new IllegalArgumentException("Cannot set onClient to true in a dedicated server!");
			return clientValue;
		} else {
			return serverValue;
		}
	}
	
}
