package etithespirit.orimod.common.datamanagement;

import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import etithespirit.orimod.common.capabilities.SpiritCapabilities;
import etithespirit.orimod.networking.player.ReplicateKnownAbilities;
import etithespirit.orimod.networking.spirit.ReplicateSpiritStatus;
import etithespirit.orimod.player.EffectEnforcement;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Optional;

public final class WorldLoading {
	
	public static void onLoggedInServer(PlayerEvent.PlayerLoggedInEvent evt) {
		Player player = evt.getEntity();
		ReplicateSpiritStatus.Server.tellEveryonePlayerSpiritStatus(player, SpiritIdentifier.isSpirit(player));
		player.refreshDimensions();
		EffectEnforcement.updatePlayerAttrs(player);
		
		ServerPlayer srvPlr = (ServerPlayer)player;
		ReplicateKnownAbilities.Server.tellAllCapsTo(srvPlr);
	}
	
	public static void onLoggedInClient(PlayerEvent.PlayerLoggedInEvent evt) {
		if (!Minecraft.getInstance().hasSingleplayerServer()) {
			ReplicateSpiritStatus.Client.askWhoIsASpiritAsync(); // The server will reply to this on its own accord.
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
		ReplicateSpiritStatus.Server.tellEveryonePlayerSpiritStatus(player, SpiritIdentifier.isSpirit(player));
		player.refreshDimensions();
		EffectEnforcement.updatePlayerAttrs(player);
	}
	
	public static void onChangeDimensionServer(PlayerEvent.PlayerChangedDimensionEvent evt) {
		ReplicateKnownAbilities.Server.tellAllCapsTo((ServerPlayer)evt.getEntity());
	}
	
	public static void onPlayerClone(PlayerEvent.Clone cloneEvt) {
		Player orgPlr = cloneEvt.getOriginal();
		Player newPlr = cloneEvt.getEntity();
		orgPlr.reviveCaps();
		Optional<SpiritCapabilities> caps = SpiritCapabilities.getCaps(orgPlr);
		if (caps.isPresent()) {
			Optional<SpiritCapabilities> newCaps = SpiritCapabilities.getCaps(newPlr);
			newCaps.ifPresent(spiritCapabilities -> spiritCapabilities.deserializeNBT(caps.get().serializeNBT()));
		}
	}
	
}
