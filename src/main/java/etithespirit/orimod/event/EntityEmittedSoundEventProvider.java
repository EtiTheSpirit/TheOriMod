package etithespirit.orimod.event;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A shitty representation of a Forge event sort of thing. Intended to be used in tandem with mixins to manually implement <a href="https://github.com/MinecraftForge/MinecraftForge/pull/7491">PR #7941</a>
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public class EntityEmittedSoundEventProvider {
	
	private static final List<Consumer<EntityEmittedSoundEvent>> CONSUMERS = new ArrayList<>();
	
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
	public static @Nonnull EntityEmittedSoundEvent getSound(@Nonnull Entity source, @Nullable Player player, double x, double y, double z, @Nonnull SoundEvent sound, SoundSource category, float volume, float pitch) {
		EntityEmittedSoundEvent evt = new EntityEmittedSoundEvent(source, new Vec3(x, y, z), sound, category, volume, pitch);
		Level world = source.level;
		for (Consumer<EntityEmittedSoundEvent> consumer : CONSUMERS) {
			consumer.accept(evt);
			if (evt.isCanceled()) {
				return evt;
			}
		}
		return evt;
	}
	
	/**
	 * Register an event handler for this event.
	 * @param handler The handler to register.
	 */
	public static void registerHandler(Consumer<EntityEmittedSoundEvent> handler) {
		if (!CONSUMERS.contains(handler)) {
			CONSUMERS.add(handler);
		}
	}
	
	/**
	 * Unregister an event handler for this event.
	 * @param handler The handler to unregister.
	 */
	public static void unregisterHandler(Consumer<EntityEmittedSoundEvent> handler) {
		CONSUMERS.remove(handler);
	}
	
}
