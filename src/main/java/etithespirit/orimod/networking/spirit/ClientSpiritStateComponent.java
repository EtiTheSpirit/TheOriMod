package etithespirit.orimod.networking.spirit;

import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.networking.ReplicationData;
import etithespirit.orimod.player.EffectEnforcement;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ClientSpiritStateComponent {
	
	public static void registerClientPackets() {
		ReplicateSpiritStatus.INSTANCE.registerMessage(ReplicationData.nextID(), SpiritStateReplicationPacket.class, ReplicateSpiritStatus.PACKET_TO_BUFFER, ReplicateSpiritStatus.BUFFER_TO_PACKET, ClientSpiritStateComponent::onClientEvent);
	}
	
	
	public static void onClientEvent(SpiritStateReplicationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (msg.type == SpiritStateReplicationPacket.EventType.UPDATE_PLAYER_MODELS) {
			ctx.get().enqueueWork(() -> {
				// We've received word from the server of one or more players' models changing.
				// Let's update our data.
				// The server sent this, so it's safe to assume the value is acceptable.
				Level world = Minecraft.getInstance().level;
				if (world != null) {
					msg.playerSpiritStateMappings.forEach((uuid, isSpirit) -> {
						SpiritIdentifier.setSpirit(uuid, isSpirit);
						Player player = world.getPlayerByUUID(uuid);
						if (player != null) {
							player.refreshDimensions();
							EffectEnforcement.updatePlayerAttrs(player);
						}
					});
				}
			});
		}
		
		ctx.get().setPacketHandled(true);
	}
	
	
	
	/**
	 * Politely asks the server if I can become a spirit (or no longer be one).
	 * @param isSpirit Whether or not I want to be a spirit.
	 */
	@ClientUseOnly
	public static void askToSetSpiritStatusAsync(boolean isSpirit) {
		LocalPlayer client = Minecraft.getInstance().player;
		SpiritIdentifier.setSpirit(client.getUUID(), isSpirit);
		ReplicateSpiritStatus.INSTANCE.send(PacketDistributor.SERVER.noArg(), SpiritStateReplicationPacket.toChangeModelOf(client, isSpirit));
	}
	
	/**
	 * Politely asks the server which people are spirits right now.
	 */
	@ClientUseOnly
	public static void askWhoIsASpiritAsync() {
		ReplicateSpiritStatus.INSTANCE.send(PacketDistributor.SERVER.noArg(), SpiritStateReplicationPacket.toGetModelsOfAll());
	}
	
}
