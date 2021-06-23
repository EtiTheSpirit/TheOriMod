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
	UPDATE_PLAYER_MODEL,
	
	/** A player wants to change their model, and is asking the server to propagate said change. */
	REQUEST_CHANGE_PLAYER_MODEL,
	
	/** A player is asking the server what someone's model is. */
	GET_PLAYER_MODEL,
	
	/** A response to GetPlayerModel. If the target entity is null, the player is using their stock player model. */
	IS_SPIRIT,
	
	/** A player wants to figure out who is a spirit and who isn't. */
	GET_EVERY_PLAYER_MODEL,
	
	/** A response to GetEveryPlayerModel. This packet type sends a list of every player's UUID coupled with a Boolean representing whether or not they are a spirit. */
	TELL_EVERY_PLAYER_MODEL,
	
	/** An invalid event type. This cannot be used in ToByte and will never be returned by FromByte. */
	Invalid;
	
	public byte toByte() throws IllegalArgumentException {
		if (this == UPDATE_PLAYER_MODEL) return 0;
		if (this == REQUEST_CHANGE_PLAYER_MODEL) return 1;
		if (this == GET_PLAYER_MODEL) return 2;
		if (this == IS_SPIRIT) return 3;
		if (this == GET_EVERY_PLAYER_MODEL) return 4;
		if (this == TELL_EVERY_PLAYER_MODEL) return 5;
		throw new IllegalArgumentException("Cannot convert EventType.Invalid to byte.");
	}
	
	public static EventType fromByte(byte b) throws IllegalArgumentException {
		if (b == 0) return UPDATE_PLAYER_MODEL;
		if (b == 1) return REQUEST_CHANGE_PLAYER_MODEL;
		if (b == 2) return GET_PLAYER_MODEL;
		if (b == 3) return IS_SPIRIT;
		if (b == 4) return GET_EVERY_PLAYER_MODEL;
		if (b == 5) return TELL_EVERY_PLAYER_MODEL;
		throw new IllegalArgumentException("The argument [b] was out of the expected range of [0, 5].");
	}
	
}