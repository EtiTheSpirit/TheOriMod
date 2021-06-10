package etithespirit.etimod.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * A shitty representation of a Forge event sort of thing. Intended to be used in tandem with mixins to manually implement <a href="https://github.com/MinecraftForge/MinecraftForge/pull/7491">PR #7941</a>
 * @author Eti
 *
 */
public class EntityEmittedSoundEventProvider {
	
	private static final List<Consumer<EntityEmittedSoundEvent>> CONSUMERS = new ArrayList<Consumer<EntityEmittedSoundEvent>>(); 
	
	/**
	 * Passes the given sound through the EntityEmittedSoundEvent handler, which can systematically modify the sound based on the event it is sourced from. Returns the event packet containing all modifications.
	 * @param source The entity that is emitting this sound.
	 * @param player The player that this sound should NOT play for, or null to play for only this client. (see entity.playSound)
	 * @param x The X position of the sound emission.
	 * @param y The Y position of the sound emission.
	 * @param z The Z position of the sound emission.
	 * @param sound The sound that is being played.
	 * @param category The category of the sound that is being played.
	 * @param volume The volume of the sound that is being played.
	 * @param pitch The pitch of the sound that is being played.
	 * @return The sound event containing all modifications, or null if the entity's world is null.
	 */
	public static @Nullable EntityEmittedSoundEvent getSound(@Nonnull Entity source, @Nullable PlayerEntity player, double x, double y, double z, @Nonnull SoundEvent sound, SoundCategory category, float volume, float pitch) {
		EntityEmittedSoundEvent evt = new EntityEmittedSoundEvent(source, new Vector3d(x, y, z), sound, category, volume, pitch);
		World world = source.level;
		if (world == null) {
			return evt;
		}
		for (Consumer<EntityEmittedSoundEvent> consumer : CONSUMERS) {
			consumer.accept(evt);
			if (evt.isCanceled()) {
				return evt;
			}
		}
		return evt;
	}
	
	/**
	 * Plays this sound on the client.
	 * @param source
	 * @param evt
	 */
	@Deprecated
	public static void playSoundClient(@Nullable PlayerEntity player, EntityEmittedSoundEvent evt) {
		World world = evt.getEntity().level;
		if (world == null) return;
		if (evt.isCanceled()) return;
		Vector3d pos = evt.getPosition();
		world.playSound(player, pos.x, pos.y, pos.z, evt.getSound(), evt.getCategory(), evt.getVolume(), evt.getPitch());
	}
	
	/**
	 * Plays this sound on the server.
	 * @param source
	 * @param evt
	 */
	@Deprecated
	public static void playSoundServer(@Nonnull ServerPlayerEntity player, EntityEmittedSoundEvent evt) {
		World world = evt.getEntity().level;
		if (world == null) return;
		if (evt.isCanceled()) return;
		Vector3d pos = evt.getPosition();
		player.connection.send(new SPlaySoundEffectPacket(evt.getSound(), evt.getCategory(), pos.x, pos.y, pos.z, evt.getVolume(), evt.getPitch()));
	}
	
	/**
	 * Plays a sound after passing it through the event handler that allows customizing the sound.
	 * @param source The entity that is emitting this sound.
	 * @param player The player that this sound should NOT play for, or null to play for only this client. (see entity.playSound)
	 * @param x The X position of the sound emission.
	 * @param y The Y position of the sound emission.
	 * @param z The Z position of the sound emission.
	 * @param sound The sound that is being played.
	 * @param category The category of the sound that is being played.
	 * @param volume The volume of the sound that is being played.
	 * @param pitch The pitch of the sound that is being played.
	 * @return The sound event containing all modifications, or null if the entity's world is null.
	 */
	@Deprecated // Intrusive! This needs to be removed immediately in favor of injectors and variable modifications.
	public static void _playSound(@Nonnull Entity source, @Nullable PlayerEntity player, double x, double y, double z, @Nonnull SoundEvent sound, SoundCategory category, float volume, float pitch) {
		World world = source.level;
		if (world == null) return;
		
		EntityEmittedSoundEvent evt = getSound(source, player, x, y, z, sound, category, volume, pitch);
		if (evt.isCanceled()) return;
		
		Vector3d pos = evt.getPosition();
		world.playSound(player, pos.x, pos.y, pos.z, evt.getSound(), evt.getCategory(), evt.getVolume(), evt.getPitch());
	}
	
	/**
	 * For use in ServerPlayerEntity only, this should be used in its playSound override. This sends a network packet to everyone.
	 * @param player The player that this sound is coming from.
	 * @param x The X position of the sound emission.
	 * @param y The Y position of the sound emission.
	 * @param z The Z position of the sound emission.
	 * @param sound The sound that is being played.
	 * @param category The category of the sound that is being played.
	 * @param volume The volume of the sound that is being played.
	 * @param pitch The pitch of the sound that is being played.
	 * @return The sound event containing all modifications, or null if the entity's world is null.
	 */
	@Deprecated // Intrusive! This needs to be removed immediately in favor of injectors and variable modifications.
	public static void _serverPlaySound(@Nonnull ServerPlayerEntity player, double x, double y, double z, @Nonnull SoundEvent sound, SoundCategory category, float volume, float pitch) {
		World world = player.level;
		if (world == null) return;
		
		EntityEmittedSoundEvent evt = getSound(player, player, x, y, z, sound, category, volume, pitch);
		if (evt.isCanceled()) return;
		
		Vector3d pos = evt.getPosition();
		
		player.connection.send(new SPlaySoundEffectPacket(evt.getSound(), evt.getCategory(), pos.x, pos.y, pos.z, evt.getVolume(), evt.getPitch()));
	}
	
	public static void registerHandler(Consumer<EntityEmittedSoundEvent> handler) {
		if (!CONSUMERS.contains(handler)) {
			CONSUMERS.add(handler);
		}
	}
	
	public static void unregisterHandler(Consumer<EntityEmittedSoundEvent> handler) {
		if (CONSUMERS.contains(handler)) {
			CONSUMERS.remove(handler);
		}
	}
	
}
