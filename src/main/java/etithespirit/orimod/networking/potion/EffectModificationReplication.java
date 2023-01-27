package etithespirit.orimod.networking.potion;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ServerUseOnly;
import etithespirit.orimod.networking.ReplicationData;
import etithespirit.orimod.registry.gameplay.EffectRegistry;
import etithespirit.orimod.util.extension.MobEffectDataStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
public final class EffectModificationReplication {
	
	
	private static final Function<FriendlyByteBuf, Packet> BUFFER_TO_PACKET = EffectModificationReplication::bufferToPacket;
	private static final BiConsumer<Packet, FriendlyByteBuf> PACKET_TO_BUFFER = EffectModificationReplication::packetToBuffer;
	
	/**
	 * The networking channel for this packet.
	 */
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(OriMod.MODID, "replicate_effect"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	private static Packet bufferToPacket(FriendlyByteBuf buffer) {
		return new Packet(new ResourceLocation(OriMod.MODID, buffer.readUtf()), buffer.readInt());
	}
	
	private static void packetToBuffer(Packet packet, FriendlyByteBuf buffer) {
		buffer.writeUtf(packet.effect.getPath());
		buffer.writeInt(packet.addedDuration);
	}
	
	public static final class Server {
		
		public static void registerServerPackets() {
			INSTANCE.registerMessage(ReplicationData.nextID(false), Packet.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, EffectModificationReplication.Server::onServerEvent);
		}
		
		private static void onServerEvent(Packet msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().setPacketHandled(true);
		}
		
		/**
		 * Reports that the duration of a status effect was modified to a client.
		 *
		 * @param player        The player to tell.
		 * @param effect        The effect that was changed.
		 * @param addedDuration The change in duration.
		 */
		@ServerUseOnly
		public static void tellClientDurationModified(ServerPlayer player, Class<? extends MobEffect> effect, int addedDuration) {
			INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new Packet(EffectRegistry.getId(effect), addedDuration));
		}
	}
	
	public static final class Client {
		
		public static void registerClientPackets() {
			INSTANCE.registerMessage(ReplicationData.nextID(true), Packet.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, EffectModificationReplication.Client::onClientEvent);
		}
		
		private static void onClientEvent(Packet msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				LocalPlayer player = Minecraft.getInstance().player;
				MobEffect effect = EffectRegistry.fromId(msg.effect);
				MobEffectInstance fx = player.getEffect(effect);
				CompoundTag effectData = MobEffectDataStorage.accessData(fx);
				effectData.putInt("maxDuration", effectData.getInt("maxDuration") + msg.addedDuration);
				fx.duration += msg.addedDuration;
			});
			ctx.get().setPacketHandled(true);
		}
	}
	
	@SuppressWarnings("ClassCanBeRecord")
	private static final class Packet {
		
		/** The actual effect being modified. */
		public final ResourceLocation effect;
		
		/** The duration that was added to the effect. */
		public final int addedDuration;
		
		/**
		 * Constructs a new status effect replication packet.
		 * @param effect The effect that was modified.
		 * @param addedDuration The change in duration of this effect.
		 */
		public Packet(ResourceLocation effect, int addedDuration) {
			this.effect = effect;
			this.addedDuration = addedDuration;
		}
		
	}
}
