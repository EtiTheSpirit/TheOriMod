package etithespirit.etimod.common.datamanagement;

import etithespirit.etimod.client.audio.SpiritSoundPlayer;
import etithespirit.etimod.common.player.EffectEnforcement;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.info.spirit.SpiritData;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import etithespirit.etimod.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;

@SuppressWarnings("unused")
public final class WorldLoading {
	
	public static void onLoggedInServer(PlayerLoggedInEvent evt) {
		PlayerEntity player = evt.getPlayer();
		ReplicateMorphStatus.tellEveryonePlayerSpiritStatus(player, SpiritData.isSpirit(player));
		player.refreshDimensions();
		EffectEnforcement.updatePlayerAttrs(player);
	}
	
	public static void onLoggedInClient(PlayerLoggedInEvent evt) {
		if (!Minecraft.getInstance().hasSingleplayerServer()) {
			ReplicateMorphStatus.askWhoIsASpiritAsync(); // The server will reply to this on its own accord.
		}
		EffectEnforcement.updatePlayerAttrs(evt.getPlayer());
	}
	
	public static void onLoggedOutServer(PlayerLoggedOutEvent evt) {
		Assembly.clearAllKnownAssemblies(false);
	}
	
	public static void onLoggedOutClient(PlayerLoggedOutEvent evt) {
		Assembly.clearAllKnownAssemblies(true);
	}
	
	public static void onRespawnedClient(PlayerRespawnEvent evt) {
		PlayerEntity player = evt.getPlayer();
		player.refreshDimensions();
		SpiritSoundPlayer.playSoundAtPlayer(player, SoundRegistry.get("entity.spirit.respawn"), SoundCategory.PLAYERS, 0.2f, 1f);
		EffectEnforcement.updatePlayerAttrs(player);
	}
	
	public static void onRespawnedServer(PlayerRespawnEvent evt) {
		PlayerEntity player = evt.getPlayer();
		ReplicateMorphStatus.tellEveryonePlayerSpiritStatus(player, SpiritData.isSpirit(player));
		player.refreshDimensions();
		EffectEnforcement.updatePlayerAttrs(player);
	}
	
}
