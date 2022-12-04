package etithespirit.orimod.apiimpl.debug;

import etithespirit.orimod.api.environment.EnvironmentalAffinity;
import etithespirit.orimod.api.util.valuetypes.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PurposelyBrokenEnv extends EnvironmentalAffinity {
	/**
	 * Create a new affinity object for the given biome using the given flux and static efficiency percentage.
	 *
	 * @param biome                The biome that this exists for.
	 * @param efficiencyPercentage The efficiency boost (or reduction) for all devices as a range of possible values.
	 */
	public PurposelyBrokenEnv(ResourceLocation biome, NumberRange efficiencyPercentage) {
		super(biome, efficiencyPercentage);
	}
	
	/**
	 * This method executes every forge Player tick (pre phase, both sides) where the player is in the biome associated with this affinity.
	 *
	 * @param player
	 */
	@Override
	public void onPlayerTick(Player player) {
		throw new RuntimeException("Oops.");
	}
	
	/**
	 * This method executes every forge World tick (pre phase, both sides).
	 *
	 * @param world
	 */
	@Override
	public void onWorldTick(Level world) {
	
	}
}
