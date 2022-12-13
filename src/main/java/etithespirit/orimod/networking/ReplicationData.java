package etithespirit.orimod.networking;

/**
 * Provides the protocol version and channel IDs to various networking systems.
 *
 * @author Eti
 */
public final class ReplicationData {
	
	private static int currentID = 0;
	public static final String PROTOCOL_VERSION = "RELEASE-1.19.2-1.2.1";
	
	public static int nextID() {
		return currentID++;
	}
	
}
