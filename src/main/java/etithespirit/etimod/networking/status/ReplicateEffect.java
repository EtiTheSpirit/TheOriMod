package etithespirit.etimod.networking.status;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.networking.ReplicationData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ReplicateEffect {
	// Common
	private static final Function<PacketBuffer, StatusEffectPacket> BUFFER_TO_PACKET = StatusEffectPacket::BufferToPacket;
	private static final BiConsumer<StatusEffectPacket, PacketBuffer> PACKET_TO_BUFFER = StatusEffectPacket::PacketToBuffer;
	
	private static BiConsumer<StatusEffectPacket, Supplier<NetworkEvent.Context>> ON_CLIENT_EVENT;
	
	private static BiConsumer<StatusEffectPacket, Supplier<NetworkEvent.Context>> ON_SERVER_EVENT;
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(EtiMod.MODID, "replicate_effect"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);
	
	public static void registerPackets(Dist side) {
		if (side.isClient()) {
			ON_CLIENT_EVENT = new BiConsumer<StatusEffectPacket, Supplier<NetworkEvent.Context>>() {
				@Override
				public void accept(StatusEffectPacket t, Supplier<Context> u) {
					onClientEvent(t, u);
				}
			};
			
			// INSTANCE.registerMessage(index, messageType, encoder, decoder, messageConsumer, networkDirection);
			INSTANCE.registerMessage(ReplicationData.nextID(), StatusEffectPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ON_CLIENT_EVENT);
		} else {
			ON_SERVER_EVENT = new BiConsumer<StatusEffectPacket, Supplier<NetworkEvent.Context>>() {
				@Override
				public void accept(StatusEffectPacket t, Supplier<Context> u) {
					onServerEvent(t, u);
				}
			};
			
			// INSTANCE.registerMessage(index, messageType, encoder, decoder, messageConsumer, networkDirection);
			INSTANCE.registerMessage(ReplicationData.nextID(), StatusEffectPacket.class, PACKET_TO_BUFFER, BUFFER_TO_PACKET, ON_SERVER_EVENT);
		}
	}
	
	protected static void onServerEvent(StatusEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (msg.shouldBeAdding) {
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity player = ctx.get().getSender();
				player.addEffect(msg.CreateNewEffectInstance());
			});
		} else {
			ctx.get().enqueueWork(() -> {
				ServerPlayerEntity player = ctx.get().getSender();
				player.removeEffect(msg.GetPotion());
			});
		}
		ctx.get().setPacketHandled(true);
	}
	
	
	protected static void onClientEvent(StatusEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
	}
	
	public static void applyPotionEffectOnServer(EffectInstance effect) {
		INSTANCE.sendToServer(new StatusEffectPacket(effect, true));
	}
	
	public static void removePotionEffectOnServer(Effect effect) {
		StatusEffectPacket packet = new StatusEffectPacket();
		packet.statusEffect = effect.getRegistryName();
		packet.shouldBeAdding = false;
		INSTANCE.sendToServer(packet);
	}
}
