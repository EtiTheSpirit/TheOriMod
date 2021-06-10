package etithespirit.etimod.util;

import javax.annotation.Nonnull;

import etithespirit.etimod.exception.ArgumentNullException;
import net.minecraft.entity.player.PlayerEntity;

public final class PlayerDataUtils {
	
	
	/**
	 * Non-intrusively removes experience points from the player, leaving other stats like their score unmodified.
	 * @param fromPlayer The player to remove experience from.
	 * @exception ArgumentNullException if any arguments denoted as @Nonnull are null.
	 */
	public static final void removeExperiencePoints(@Nonnull PlayerEntity fromPlayer, int amount) {
		if (fromPlayer == null) throw new ArgumentNullException("fromPlayer");
		
		fromPlayer.giveExperiencePoints(-amount);
		fromPlayer.increaseScore(amount);
	}

}
