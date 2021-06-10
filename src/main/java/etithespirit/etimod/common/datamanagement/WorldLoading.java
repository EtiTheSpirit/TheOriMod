package etithespirit.etimod.common.datamanagement;

import etithespirit.etimod.client.audio.SpiritSoundPlayer;
import etithespirit.etimod.common.morph.PlayerToSpiritBinding;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import etithespirit.etimod.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class WorldLoading {
	
	@SubscribeEvent
	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void onLoggedInServer(PlayerLoggedInEvent evt) {
		PlayerEntity player = evt.getPlayer();
		boolean isSpirit = player.getPersistentData().getBoolean("isSpirit") || PlayerToSpiritBinding.get(player);
		ReplicateMorphStatus.tellEveryonePlayerSpiritStatus(player.getUniqueID(), isSpirit);
		player.recalculateSize(); // This one is crazy in mojmap: refreshDimensions
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT) 
	public static void onLoggedInClient(PlayerLoggedInEvent evt) {
		PlayerEntity player = evt.getPlayer();
		if (Minecraft.getInstance().isSingleplayer()) {		
			boolean isSpirit = player.getPersistentData().getBoolean("isSpirit");
			PlayerToSpiritBinding.put(player, isSpirit);
			player.recalculateSize();
		} else {
			// NEW BEHAVIOR: We need to ask the server who is a spirit so that we see it once we join.
			ReplicateMorphStatus.askWhoIsASpiritAsync(); // The server will reply to this on its own accord.
		}
	}
	
	
	@SubscribeEvent
	public static void onLoggedOut(PlayerLoggedOutEvent evt) {
		PlayerEntity player = evt.getPlayer();
		player.getPersistentData().putBoolean("isSpirit", PlayerToSpiritBinding.get(player));
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRespawnedClient(PlayerRespawnEvent evt) {
		PlayerEntity player = evt.getPlayer();
		player.recalculateSize();
		SpiritSoundPlayer.playSoundAtPlayer(player, SoundRegistry.get("entity.spirit.respawn"), SoundCategory.PLAYERS, 0.2f, 1f);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void onRespawnedServer(PlayerRespawnEvent evt) {
		PlayerEntity player = evt.getPlayer();
		player.recalculateSize();
	}
	
}
