package etithespirit.etimod.networking.morph;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModelReplicationPacket {
	
	/** Whether or not this packet is invalid because the associated network data was malformed. */
	public boolean invalid = false;
	
	/** The type of packet that this is. */
	public EventType type;
	
	/** The ID of the player this event pertains to. */
	public @Nonnull UUID playerID;
	
	/** Whether or not this player wants to be a spirit. For event types AllowDecay and DenyDecay, the meaning of this is changed. If this is false, the setting only applies to mushroom biomes. If this is true, the setting only applies to mycelium blocks. Two packets must be sent to change both. */
	public boolean wantsToBeSpirit;
	
	public ModelReplicationPacket() { }
	
	public ModelReplicationPacket(@Nonnull UUID playerID) {
		this.playerID = playerID;
	}
	
	/**
	 * SERVER ONLY<br/>
	 * An alias method that constructs a ModelReplicationPacket with the data necessary to respond to EventType.GetPlayerModel
	 * @param model
	 * @return
	 */
	public static ModelReplicationPacket AsResponseToGetPlayerModel(UUID refPlayerID, boolean beSpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(refPlayerID);
		pack.type = EventType.IsPlayerModel;
		pack.wantsToBeSpirit = beSpirit;
		return pack;
	}
	
	/**
	 * SERVER ONLY<br/>
	 * An alias method used to tell all clients that the given player is using the given model.
	 * @param model
	 * @return
	 */
	public static ModelReplicationPacket ToTellAllClientsSomeoneIsA(UUID refPlayerID, boolean beSpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(refPlayerID);
		pack.type = EventType.UpdatePlayerModel;
		pack.wantsToBeSpirit = beSpirit;
		return pack;
	}
	
	/**
	 * CLIENT ONLY<br/>
	 * An alias method that constructs a ModelReplicationPacket with the data necessary for a client to ask what someone's model is.
	 * @param refPlayerID
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public static ModelReplicationPacket AsRequestGetPlayerModel(UUID refPlayerID) {
		ModelReplicationPacket pack = new ModelReplicationPacket(refPlayerID);
		pack.type = EventType.GetPlayerModel;
		return pack;
	}
	
	/**
	 * CLIENT ONLY<br/>
	 * An alias method that requests I turn into the given model.
	 * @param model
	 * @return
	 */
	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static ModelReplicationPacket AsRequestSetModel(boolean beSpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(Minecraft.getInstance().player.getUniqueID());
		pack.type = EventType.RequestChangePlayerModel;
		pack.wantsToBeSpirit = beSpirit;
		return pack;
	}
	
	/**
	 * CLIENT ONLY<br/>
	 * An alias method that requests I turn the given player into the given model.
	 * @param model
	 * @return
	 */
	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static ModelReplicationPacket AsRequestSetModel(UUID playerId, boolean beSpirit) {
		ModelReplicationPacket pack = new ModelReplicationPacket(Minecraft.getInstance().player.getUniqueID());
		pack.type = EventType.RequestChangePlayerModel;
		pack.wantsToBeSpirit = beSpirit;
		pack.playerID = playerId;
		return pack;
	}
	
	
	/**
	 * CLIENT ONLY<br/>
	 * An alias method that requests a list of who is a spirit.
	 * @param model
	 * @return
	 */
	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static ModelReplicationPacket AsRequestGetAllModels() {
		ModelReplicationPacket pack = new ModelReplicationPacket(Minecraft.getInstance().player.getUniqueID());
		pack.type = EventType.GetEveryPlayerModel;
		return pack;
	}
	
}

