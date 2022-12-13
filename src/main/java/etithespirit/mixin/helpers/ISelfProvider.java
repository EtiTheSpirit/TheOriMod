package etithespirit.mixin.helpers;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Utility for mixins. Offers a range of methods that provide {@code this} as a preset type.
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public interface ISelfProvider {
	
	/**
	 * Identical to "this", but treats the implementer like an Entity.
	 * @return This instance as an {@link Entity}.
	 */
	default Entity selfProvider$entity() {
		return (Entity)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a Player.
	 * @return This instance as a {@link Player}
	 */
	default Player selfProvider$player() {
		return (Player)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a ServerPlayer.
	 * @return This instance as a {@link ServerPlayer}
	 */
	default ServerPlayer selfProvider$serverPlayer() {
		return (ServerPlayer)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a LocalPlayer.
	 * @return This instance as a {@link LocalPlayer}
	 */
	default LocalPlayer selfProvider$clientPlayer() {
		return (LocalPlayer)this;
	}
	
	/**
	 * Identical to "this", but treats the implementer like a Level.
	 * @return This instance as a {@link Level}
	 */
	default Level selfProvider$world() { return (Level)this; }
	
	/**
	 * Identical to "this", but treats the implementer like an Item.
	 * @return This instance as an {@link Item}
	 */
	default Item selfProvider$item() { return (Item)this; }
	
	
	/**
	 * Identical to "this", but treats the implementer like the desired type.
	 * @return This instance as an {@link Item}
	 */
	@SuppressWarnings("unchecked")
	default <T> T selfProvider$any() { return (T)this; }
	
}
