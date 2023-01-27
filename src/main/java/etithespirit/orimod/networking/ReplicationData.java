package etithespirit.orimod.networking;

import net.minecraftforge.api.distmarker.Dist;

/**
 * Provides the protocol version and channel IDs to various networking systems.
 *
 * @author Eti
 */
public final class ReplicationData {
	
	private static int currentIDC = 0;
	private static int currentIDS = 0;
	public static final String PROTOCOL_VERSION = "RELEASE-1.19.2-1.2.2";
	
	public static int nextID(boolean isLogicalClient) {
		if (isLogicalClient) {
			return currentIDC++;
		} else {
			return currentIDS++;
		}
	}
	
}
