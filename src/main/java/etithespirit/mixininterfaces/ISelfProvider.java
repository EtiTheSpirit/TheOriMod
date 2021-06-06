package etithespirit.mixininterfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Utility for mixins. Offers a self() method that simply casts this into entity.
 * @author Eti
 *
 */
public interface ISelfProvider {
	
	/**
	 * Identical to "this", but treats the implementer like an Entity.
	 * @return
	 */
	public default Entity self() {
		return (Entity)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a PlayerEntity.
	 * @return
	 */
	public default PlayerEntity player() {
		return (PlayerEntity)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a ServerPlayerEntity.
	 * @return
	 */
	public default ServerPlayerEntity serverPlayer() {
		return (ServerPlayerEntity)this;
	}

}
