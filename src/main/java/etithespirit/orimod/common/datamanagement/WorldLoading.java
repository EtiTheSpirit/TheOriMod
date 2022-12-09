package etithespirit.orimod.common.datamanagement;

import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import etithespirit.orimod.networking.spirit.ReplicateSpiritStatus;
import etithespirit.orimod.player.EffectEnforcement;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class WorldLoading {
	
	public static void onLoggedInServer(PlayerEvent.PlayerLoggedInEvent evt) {
		Player player = evt.getEntity();
		ReplicateSpiritStatus.tellEveryonePlayerSpiritStatus(player, SpiritIdentifier.isSpirit(player));
		player.refreshDimensions();
		EffectEnforcement.updatePlayerAttrs(player);
	}
	
	public static void onLoggedInClient(PlayerEvent.PlayerLoggedInEvent evt) {
		if (!Minecraft.getInstance().hasSingleplayerServer()) {
			ReplicateSpiritStatus.askWhoIsASpiritAsync(); // The server will reply to this on its own accord.
		}
		EffectEnforcement.updatePlayerAttrs(evt.getEntity());
	}
	
	public static void onLoggedOutServer(PlayerEvent.PlayerLoggedOutEvent evt) {

	}
	
	public static void onLoggedOutClient(PlayerEvent.PlayerLoggedOutEvent evt) {
	
	}
	
	public static void onRespawnedClient(PlayerEvent.PlayerRespawnEvent evt) {
		Player player = evt.getEntity();
		player.refreshDimensions();
		SpiritSoundPlayer.playSoundAtPlayer(player, SoundRegistry.get("entity.spirit.respawn"), SoundSource.PLAYERS, 0.2f, 1f);
		EffectEnforcement.updatePlayerAttrs(player);
	}
	
	public static void onRespawnedServer(PlayerEvent.PlayerRespawnEvent evt) {
		Player player = evt.getEntity();
		ReplicateSpiritStatus.tellEveryonePlayerSpiritStatus(player, SpiritIdentifier.isSpirit(player));
		player.refreshDimensions();
		EffectEnforcement.updatePlayerAttrs(player);
	}
	
}
