package etithespirit.mixininterfaces;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Utility for mixins. Offers a self() method that simply casts this into entity.
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public interface ISelfProvider {
	
	/**
	 * Identical to "this", but treats the implementer like an Entity.
	 * @return This instance as an {@link Entity}.
	 */
	default Entity self() {
		return (Entity)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a PlayerEntity.
	 * @return This instance as a {@link PlayerEntity}
	 */
	default PlayerEntity player() {
		return (PlayerEntity)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a ServerPlayerEntity.
	 * @return This instance as a {@link ServerPlayerEntity}
	 */
	default ServerPlayerEntity serverPlayer() {
		return (ServerPlayerEntity)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a ClientPlayerEntity.
	 * @return This instance as a {@link ClientPlayerEntity}
	 */
	default ClientPlayerEntity clientPlayer() {
		return (ClientPlayerEntity)this;
	}

}
