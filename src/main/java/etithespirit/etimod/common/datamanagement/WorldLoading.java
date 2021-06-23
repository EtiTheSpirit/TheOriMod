package etithespirit.etimod.common.datamanagement;

import etithespirit.etimod.client.audio.SpiritSoundPlayer;
import etithespirit.etimod.common.morph.PlayerToSpiritBinding;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import etithespirit.etimod.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

public final class WorldLoading {
	
	public static void onLoggedInServer(PlayerLoggedInEvent evt) {
		PlayerEntity player = evt.getPlayer();
		boolean isSpirit = player.getPersistentData().getBoolean("isSpirit") || PlayerToSpiritBinding.get(player);
		ReplicateMorphStatus.tellEveryonePlayerSpiritStatus(player.getUUID(), isSpirit);
		player.refreshDimensions(); // This one is crazy in mojmap: refreshDimensions
	}
	
	public static void onLoggedInClient(PlayerLoggedInEvent evt) {
		PlayerEntity player = evt.getPlayer();
		if (Minecraft.getInstance().hasSingleplayerServer()) {		
			boolean isSpirit = player.getPersistentData().getBoolean("isSpirit");
			PlayerToSpiritBinding.put(player.getUUID(), isSpirit);
			player.refreshDimensions();
		} else {
			// NEW BEHAVIOR: We need to ask the server who is a spirit so that we see it once we join.
			ReplicateMorphStatus.askWhoIsASpiritAsync(); // The server will reply to this on its own accord.
		}
	}
	
	public static void onLoggedOutServer(PlayerLoggedOutEvent evt) {
		PlayerEntity player = evt.getPlayer();
		player.getPersistentData().putBoolean("isSpirit", PlayerToSpiritBinding.get(player));
		Assembly.clearAllKnownAssemblies(false);
	}
	
	public static void onLoggedOutClient(PlayerLoggedOutEvent evt) {
		Assembly.clearAllKnownAssemblies(true);
	}
	
	public static void onRespawnedClient(PlayerRespawnEvent evt) {
		PlayerEntity player = evt.getPlayer();
		player.refreshDimensions();
		SpiritSoundPlayer.playSoundAtPlayer(player, SoundRegistry.get("entity.spirit.respawn"), SoundCategory.PLAYERS, 0.2f, 1f);
	}
	
	public static void onRespawnedServer(PlayerRespawnEvent evt) {
		PlayerEntity player = evt.getPlayer();
		player.refreshDimensions();
	}
	
}
