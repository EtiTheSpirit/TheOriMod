package etithespirit.etimod.networking;

public final class ReplicationData {

	private static int currentID = 0;
	public static final String PROTOCOL_VERSION = "4";
	
	public static final int nextID() {
		return currentID++;
	}
	
}
