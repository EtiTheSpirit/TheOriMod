package etithespirit.etimod.api;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.meta.When;

import etithespirit.etimod.exception.ArgumentNullException;
import etithespirit.etimod.routine.SimplePromise;
/**
 * An API providing a means of determining various aspects of a player as a spirit. Use {@link #getAPI()} to acquire an instance of the API.
 * @author Eti
 */
public interface ISpiritStateAPI {
	
	/**
	 * @return A supplier that provides the instance of the API. If EtiMod is not installed (and only the API is), then a dummy API will be returned. Use {@link #isInstalled()} to determine whether or not the returned API is usable.
	 */
	public static ISpiritStateAPI getAPI() {
		return Storage.getSpiritStateAPI();
	}
	
	/** <strong>MUST BE CHECKED BEFORE OTHER METHODS ARE USED.</strong> This returns whether or not the API is installed. */
	boolean isInstalled();
	
	/**
	 * Returns whether or not a player with the given ID is a spirit using the custom registry. This can be called from both sides.<br/>
	 * This has a number of code flow potentials that <strong>must</strong> be factored in by you as a developer. Notably,<br/>
	 * <ul>
	 * <li>If this is called from a server (dedicated or integrated), the return value will be instantly populated and require no special handling.</li>
	 * <li>If this is called from the client, but the client is running singleplayer, the return value will be instantly populated.</li>
	 * <li>If this is called from the client, but the client is on multiplayer, and the {@code forceSkipLocal} parameter is FALSE, the return value will be instantly populated from the client's cached value.</li>
	 * <li>If this is called from the client, the client is on multiplayer, and the {@code forceSkipLocal} parameter is TRUE, the return value will not be populated until the server replies with the value, requiring special asynchronous handling.</li>
	 * </ul>
	 * @param playerId The ID of the player to check. This must be defined on a dedicated server.
	 * @param forceSkipLocal Skip checking the local cache, and ask the server for  value instead.
	 * @return A Promise that will eventually contain whether or not the player is a spirit.
	 * @throws IllegalStateException If {@link isInstalled()} returns false.
	 * @throws ArgumentNullException If the player ID is null and this is being called from a dedicated server.
	 */
	SimplePromise<Boolean> isPlayerSpirit(@Nonnull(when=When.MAYBE) UUID playerId, boolean forceSkipLocal) throws ArgumentNullException, IllegalStateException;
	
}
