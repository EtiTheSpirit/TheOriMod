package etithespirit.etimod.networking;

/**
 * Provides the protocol version and channel IDs to various networking systems.
 *
 * @author Eti
 */
public final class ReplicationData {

	private static int currentID = 0;
	public static final String PROTOCOL_VERSION = "5";
	
	public static int nextID() {
		return currentID++;
	}
	
}
