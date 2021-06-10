package etithespirit.etimod.networking.morph;

/**
 * Represents a type of morph replication packet, which describes the behavior that this packet should have on the associated side.
 * @author Eti the Spirit
 */
public enum EventType {
	
	/** A player has changed their model or joined with a model on.<br/>
	 * Update the client registry to the packet's defined data to determine how to render this player.
	 * Additionally, the player that requested this morph should actually morph at this point.<br/><br/>
	 * TODO: Provide means of client-feedback for slower connections / long processing time on the serverside to prevent people from getting confused at the lack of any immediate changes.
	 */
	UpdatePlayerModel,
	
	/** A player wants to change their model, and is asking the server to propagate said change. */
	RequestChangePlayerModel,
	
	/** A player is asking the server what someone's model is. */
	GetPlayerModel,
	
	/** A response to GetPlayerModel. If the target entity is null, the player is using their stock player model. */
	IsPlayerModel,
	
	/** A player wants to figure out who is a spirit and who isn't. */
	GetEveryPlayerModel,
	
	/** A response to GetEveryPlayerModel. This packet type sends a list of every player's UUID coupled with a Boolean representing whether or not they are a spirit. */
	TellEveryPlayerModel,
	
	/** An invalid event type. This cannot be used in ToByte and will never be returned by FromByte. */
	Invalid;
	
	public byte ToByte() throws IllegalArgumentException {
		if (this == UpdatePlayerModel) return 0;
		if (this == RequestChangePlayerModel) return 1;
		if (this == GetPlayerModel) return 2;
		if (this == IsPlayerModel) return 3;
		if (this == GetEveryPlayerModel) return 4;
		if (this == TellEveryPlayerModel) return 5;
		throw new IllegalArgumentException("Cannot convert EventType.Invalid to byte.");
	}
	
	public static EventType FromByte(byte b) throws IllegalArgumentException {
		if (b == 0) return UpdatePlayerModel;
		if (b == 1) return RequestChangePlayerModel;
		if (b == 2) return GetPlayerModel;
		if (b == 3) return IsPlayerModel;
		if (b == 4) return GetEveryPlayerModel;
		if (b == 5) return TellEveryPlayerModel;
		throw new IllegalArgumentException("The argument [b] was out of the expected range of [0, 5].");
	}
	
}