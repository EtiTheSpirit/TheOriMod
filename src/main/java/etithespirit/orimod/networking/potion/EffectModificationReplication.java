package etithespirit.orimod.networking.potion;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ServerUseOnly;
import etithespirit.orimod.networking.ReplicationData;
import etithespirit.orimod.registry.PotionRegistry;
import etithespirit.orimod.util.extension.MobEffectDataStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A class that replicates changes in effect duration over the network.
 */
public class EffectModificationReplication {
	
	
	private static final Function<FriendlyByteBuf, EffectReplicationPacket> BUFFER_TO_PACKET = EffectModificationReplication::bufferToPacket;
	private static final BiConsumer<EffectReplicationPacket, FriendlyByteBuf> PACKET_TO_BUFFER = EffectModificationReplication::packetToBuffer;
	
	/**
	 * The networking channel for this packet.
	 */
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(OriMod.MODID, "replicate_effect"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	/**
	 * Register the packets for the given logical side.
	 * @param side The side that this packet exists on.
	 */
	public static void registerPackets(Dist side) {
		if (side.isClient()) {
			INSTANCE.registerMessage(ReplicationData.nextID(), EffectReplicationPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, EffectModificationReplication::onClientEvent);
		} else {
			INSTANCE.registerMessage(ReplicationData.nextID(), EffectReplicationPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, EffectModificationReplication::onServerEvent);
		}
	}
	
	private static EffectReplicationPacket bufferToPacket(FriendlyByteBuf buffer) {
		return new EffectReplicationPacket(new ResourceLocation(OriMod.MODID, buffer.readUtf()), buffer.readInt());
	}
	
	private static void packetToBuffer(EffectReplicationPacket packet, FriendlyByteBuf buffer) {
		buffer.writeUtf(packet.effect.getPath());
		buffer.writeInt(packet.addedDuration);
	}
	
	private static void onServerEvent(EffectReplicationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
	}
	
	private static void onClientEvent(EffectReplicationPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			MobEffect effect = PotionRegistry.get(msg.effect);
			MobEffectInstance fx = player.getEffect(effect);
			CompoundTag effectData = MobEffectDataStorage.accessData(fx);
			effectData.putInt("maxDuration", effectData.getInt("maxDuration") + msg.addedDuration);
			fx.duration += msg.addedDuration;
		});
		ctx.get().setPacketHandled(true);
	}
	
	/**
	 * Reports that the duration of a status effect was modified to a client.
	 * @param player The player to tell.
	 * @param effect The effect that was changed.
	 * @param addedDuration The change in duration.
	 */
	@ServerUseOnly
	public static void tellClientDurationModified(ServerPlayer player, Class<? extends MobEffect> effect, int addedDuration) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new EffectReplicationPacket(PotionRegistry.getId(effect), addedDuration));
	}
	
}
