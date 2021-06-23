package etithespirit.etimod.networking.morph;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;

/**
 * A packet used to replicate intents with the player's Spirit state.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public class ModelReplicationPacket {
	
	/** Whether or not this packet is invalid because the associated network data was malformed. */
	public boolean invalid = false;
	
	/** The type of packet that this is. */
	public EventType type;
	
	/** The ID of the player this event pertains to. */
	public int playerID;
	
	/** Whether or not this player wants to be a spirit. For event types AllowDecay and DenyDecay, the meaning of this is changed. If this is false, the setting only applies to mushroom biomes. If this is true, the setting only applies to mycelium blocks. Two packets must be sent to change both. */
	public boolean wantsToBeSpirit;
	
	/** Only exists for {@link EventType#TELL_EVERY_PLAYER_MODEL} and will be null otherwise. */
	public @Nullable Map<Integer, Boolean> playersWhoAreSpirits;
	
	public ModelReplicationPacket() { }
	
	public ModelReplicationPacket(int playerID) {
		this.playerID = playerID;
	}
	
	/**
	 * SERVER ONLY<br/>
	 * An alias method that constructs a ModelReplicationPacket with the data necessary to respond to EventType.GetPlayerModel
	 * @param refPlayerID The ID of the player in question.
	 * @param beSpirit Whether or not this player is a spirit.
	 * @return A packet with the necessary data set.
	 */
	public static ModelReplicationPacket asResponseToGetPlayerModel(int refPlayerID, boolean beSpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(refPlayerID);
		pack.type = EventType.IS_SPIRIT;
		pack.wantsToBeSpirit = beSpirit;
		return pack;
	}
	
	/**
	 * SERVER ONLY<br/>
	 * An alias method used to tell all clients that the given player is using the given model.
	 * @return A packet with the necessary data set.
	 */
	public static ModelReplicationPacket toTellAllClientsSomeoneIsA(int refPlayerID, boolean beSpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(refPlayerID);
		pack.type = EventType.UPDATE_PLAYER_MODEL;
		pack.wantsToBeSpirit = beSpirit;
		return pack;
	}
	
	/**
	 * CLIENT ONLY<br/>
	 * An alias method that constructs a ModelReplicationPacket with the data necessary for a client to ask what someone's model is.
	 * @param refPlayerID The ID of the player I need the state of.
	 * @return A packet with the necessary data set.
	 */
	public static ModelReplicationPacket asRequestGetPlayerModel(int refPlayerID) {
		ModelReplicationPacket pack = new ModelReplicationPacket(refPlayerID);
		pack.type = EventType.GET_PLAYER_MODEL;
		return pack;
	}
	
	/**
	 * CLIENT ONLY<br/>
	 * An alias method that requests I turn into the given model.
	 * @param beSpirit Whether or not I want to be a spirit.
	 * @return A packet with the necessary data set.
	 */
	@SuppressWarnings("resource")
	public static ModelReplicationPacket asRequestSetModel(boolean beSpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(Minecraft.getInstance().player.getId());
		pack.type = EventType.REQUEST_CHANGE_PLAYER_MODEL;
		pack.wantsToBeSpirit = beSpirit;
		return pack;
	}
	
	/**
	 * CLIENT ONLY<br/>
	 * An alias method that requests I turn the given player into the given model.
	 * @param playerId The ID iof the player whose model should be set.
	 * @param beSpirit Whether or not it should be set to a spirit.
	 * @return A packet with the necessary data set.
	 */
	@SuppressWarnings("resource")
	public static ModelReplicationPacket asRequestSetModel(int playerId, boolean beSpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(Minecraft.getInstance().player.getId());
		pack.type = EventType.REQUEST_CHANGE_PLAYER_MODEL;
		pack.wantsToBeSpirit = beSpirit;
		pack.playerID = playerId;
		return pack;
	}
	
	
	/**
	 * CLIENT ONLY<br/>
	 * An alias method that requests a list of who is a spirit.
	 * @return A packet with the necessary data set.
	 */
	@SuppressWarnings("resource")
	public static ModelReplicationPacket asRequestGetAllModels() {
		ModelReplicationPacket pack = new ModelReplicationPacket(Minecraft.getInstance().player.getId());
		pack.type = EventType.GET_EVERY_PLAYER_MODEL;
		return pack;
	}
	
	/**
	 * SERVER ONLY<br/>
	 * Tells a given client the status of every player.
	 * @param playerSendingTo The player that will receive this data.
	 * @param whoIsASpirit A lookup from player entity ID to boolean representing which of them is or isn't a spirit.
	 * @return A packet with the necessary data set.
	 */
	public static ModelReplicationPacket toTellClientWhatEveryoneIs(int playerSendingTo, Map<Integer, Boolean> whoIsASpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(playerSendingTo);
		pack.type = EventType.TELL_EVERY_PLAYER_MODEL;
		pack.playersWhoAreSpirits = whoIsASpirit;
		return pack;
		
	}
	
}

