package etithespirit.etimod.apiimpl;

import java.util.UUID;
import java.util.function.Function;

import etithespirit.etimod.api.ISpiritStateAPI;
import etithespirit.etimod.common.morph.PlayerToSpiritBinding;
import etithespirit.etimod.exception.ArgumentNullException;
import etithespirit.etimod.networking.morph.EventType;
import etithespirit.etimod.networking.morph.ModelReplicationPacket;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import etithespirit.etimod.routine.SimplePromise;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class SpiritStateAPI implements ISpiritStateAPI {
	
	public SpiritStateAPI() { }

	@Override
	public boolean isInstalled() {
		return true;
	}
	
	@Override
	public SimplePromise<Boolean> isPlayerSpirit(UUID playerId, boolean forceSkipLocal) throws ArgumentNullException, IllegalStateException {
		if (FMLEnvironment.dist.isDedicatedServer()) {
			if (playerId == null) throw new ArgumentNullException("playerId");
			return new SimplePromise<Boolean>(PlayerToSpiritBinding.get(playerId));
		}
		
		// We are not in a dedicated server. That means we can do this.
		UUID id = playerId;
		if (id == null) {
			Minecraft game = Minecraft.getInstance();
			id = game.player.getUUID();
		}
		if (forceSkipLocal) {
			return createOnClientEventWaiter(id);
		} else {
			return new SimplePromise<Boolean>(PlayerToSpiritBinding.get(id));
		}
	}
	
	/**
	 * A utility function to create a promise that returns whether or not the given player ID is a spirit as soon as the server replies.
	 * @param id
	 * @return
	 */
	private static SimplePromise<Boolean> createOnClientEventWaiter(UUID id) {
		SimplePromise<Boolean> promise = new SimplePromise<Boolean>();
		Function<ModelReplicationPacket, Boolean> receiver = packet -> {
			if (promise.hasResult()) return true;
			if (packet.type == EventType.IsPlayerModel || packet.type == EventType.UpdatePlayerModel) {
				promise.setResult(packet.wantsToBeSpirit);
				return true;
			}
			return false;
		};
		ReplicateMorphStatus.CLIENT_RECEIVED_EVENT_SINGLEFIRE_CALLBACKS.add(receiver);
		ReplicateMorphStatus.askIfSomeoneIsASpiritAsync(id);
		return promise;
	}

}