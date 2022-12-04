package etithespirit.orimod.api.environment;

import com.google.common.collect.ImmutableMap;
import etithespirit.orimod.api.APIProvider;
import etithespirit.orimod.api.util.valuetypes.MutableNumberRange;
import etithespirit.orimod.api.util.valuetypes.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * This class stores data about a biome's environmental affinity.
 * You can create instances for your mod as needed.
 */
public abstract class EnvironmentalAffinity {
	
	/** The ID of the biome that this affinity object applies to. */
	public final ResourceLocation biome;
	
	/**
	 * An object describing the efficiency of Light-based devices.
	 * This should return a value centered around 1 (where 1 means "no change"). It allows randomized interactions.
	 * This will be null if {@link #efficiencySingular} is in use instead.
	 */
	public final @Nullable NumberRange efficiency;
	
	/**
	 * A double representing the constant efficiency of Light-based devices.
	 * This can be used if there is no randomization per tick, in order to save computational power when ticking Light tech.
	 * This will be {@link Double#NaN} if this value is not in use.
	 */
	public final double efficiencySingular;
	
	/**
	 * Create a new affinity object for the given biome using the given flux and static efficiency percentage.
	 * @param biome The biome that this exists for.
	 * @param efficiencyPercentage The efficiency boost (or reduction) for all devices as a range of possible values. <strong>Note: If this is an immutable number range and it is singular (min == max), then {@link #efficiencySingular} will be set to that value and {@link #efficiency} will be null.</strong>
	 */
	public EnvironmentalAffinity(ResourceLocation biome, NumberRange efficiencyPercentage) {
		this.biome = biome;
		if (!(efficiencyPercentage instanceof MutableNumberRange) && efficiencyPercentage.isSingular()) {
			this.efficiency = null;
			this.efficiencySingular = efficiencyPercentage.getMin();
		} else {
			// Mutable ranges should still do this because they may be edited by the programmer on the fly
			// (whereas the immutable form cannot be edited, and will always be the same value)
			this.efficiency = efficiencyPercentage;
			this.efficiencySingular = Double.NaN;
		}
	}
	
	/**
	 * Create a new affinity object for the given biome using the given flux and static efficiency percentage.
	 * @param biome The biome that this exists for.
	 * @param efficiencyPercentage The efficiency boost (or reduction) for all devices as a singular value.
	 */
	public EnvironmentalAffinity(ResourceLocation biome, double efficiencyPercentage) {
		this.biome = biome;
		this.efficiency = null;
		this.efficiencySingular = efficiencyPercentage;
	}
	
	/**
	 * This method executes every forge Player tick (post phase, both sides) where the player is in the biome associated with this affinity.
	 * @param player The player to tick for.
	 */
	public abstract void onPlayerTick(Player player);
	
	/**
	 * This method executes every forge World tick (post phase, both sides).
	 * @param world The level to tick in.
	 */
	public abstract void onWorldTick(Level world);
	
	/**
	 * Returns the next randomized efficiency multiplier for all light tech in the area.
	 * @return The next randomized efficiency multiplier for all light tech in the area.
	 */
	public double getNextEfficiencyMultiplier() {
		if (efficiency == null) return efficiencySingular;
		return efficiency.random();
	}
	
	/**
	 * Sends a tick to all instances of {@link EnvironmentalAffinity} for the given player.
	 * @param player The player to apply to.
	 */
	public static void sendPlayerTickToAll(Player player) {
		ResourceLocation current = null;
		Class<?> latest = null;
		ImmutableMap<ResourceLocation, EnvironmentalAffinity> all = APIProvider.getEnvironmentalAffinityAPI().getAllBindings();
		try {
			Set<ResourceLocation> keys = all.keySet();
			for (ResourceLocation rsrc : keys) {
				current = rsrc;
				if (player.level.getBiome(player.getOnPos()).is(rsrc)) {
					EnvironmentalAffinity env = all.get(rsrc);
					latest = env.getClass();
					env.onPlayerTick(player);
				}
			}
		} catch (Exception exc) {
			//LOG.error("An error occurred whilst trying to execute player tick code for biome [{}]: {}", current, exc.getMessage());
			//throw exc;
			throw (RuntimeException)new RuntimeException(String.format("An error occurred whilst trying to execute the player tick portion of a Decay/Light Environment Affinity system registered for biome [%s]: %s\nThe offending class is located at: %s", current.toString(), exc.getMessage(), latest.getName())).initCause(exc);
		}
	}
	
	/**
	 * Sends a tick to all instances of {@link EnvironmentalAffinity} in the given level.
	 * @param world The level to send in.
	 */
	public static void sendWorldTickToAll(Level world) {
		ResourceLocation current = null;
		Class<?> latest = null;
		ImmutableMap<ResourceLocation, EnvironmentalAffinity> all = APIProvider.getEnvironmentalAffinityAPI().getAllBindings();
		try {
			Set<ResourceLocation> keys = all.keySet();
			for (ResourceLocation rsrc : keys) {
				current = rsrc;
				EnvironmentalAffinity env = all.get(rsrc);
				latest = env.getClass();
				env.onWorldTick(world);
			}
		} catch (Exception exc) {
			//LOG.error("An error occurred whilst trying to execute player tick code for biome [{}]: {}", current, exc.getMessage());
			//throw exc;
			throw (RuntimeException)new RuntimeException(String.format("An error occurred whilst trying to execute the world tick portion of a Decay/Light Environment Affinity system registered for biome [%s]: %s\nThe offending class is located at: %s", current.toString(), exc.getMessage(), latest.getName())).initCause(exc);
		}
	}

}