package etithespirit.orimod.api.environment;

import com.google.common.collect.ImmutableMap;
import etithespirit.orimod.api.APIProvider;
import etithespirit.orimod.api.interfaces.IEnvironmentalAffinityAPI;
import etithespirit.orimod.api.util.valuetypes.MutableNumberRange;
import etithespirit.orimod.api.util.valuetypes.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;
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
	public EnvironmentalAffinity(ResourceLocation biome, @Nullable NumberRange efficiencyPercentage) {
		this.biome = biome;
		if (!(efficiencyPercentage instanceof MutableNumberRange) && (efficiencyPercentage != null) && efficiencyPercentage.isSingular()) {
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
		ImmutableMap<String, IEnvironmentalAffinityAPI> apiBindings = APIProvider.getAllEnvAffinityAPIs();
		Set<String> keys = apiBindings.keySet();
		for (String key : keys) {
			ResourceLocation lastBiomeID = null;
			try {
				ImmutableMap<ResourceLocation, EnvironmentalAffinity> implementations = Objects.requireNonNull(apiBindings.get(key)).getAllBindings();
				Set<ResourceLocation> rsrcs = implementations.keySet();
				for (ResourceLocation rsrc : rsrcs) {
					lastBiomeID = rsrc;
					EnvironmentalAffinity impl = Objects.requireNonNull(implementations.get(rsrc));
					impl.onPlayerTick(player);
				}
			} catch (Exception exc) {
				String message = String.format(
					"An error occurred whilst trying to do a player tick in the Environmental Affinity system. The ID of the mod that registered this implementation is '%s', and the biome that the failure occurred in is '%s'.",
					key,
					lastBiomeID == null ? "<no biome - this error occurred while trying to get the API instance for the mod in question>" : lastBiomeID.toString()
				);
				throw new RuntimeException(message, exc);
			}
		}
	}
	
	/**
	 * Sends a tick to all instances of {@link EnvironmentalAffinity} in the given level.
	 * @param world The level to send in.
	 */
	public static void sendWorldTickToAll(Level world) {
		ImmutableMap<String, IEnvironmentalAffinityAPI> apiBindings = APIProvider.getAllEnvAffinityAPIs();
		Set<String> keys = apiBindings.keySet();
		for (String key : keys) {
			ResourceLocation lastBiomeID = null;
			try {
				ImmutableMap<ResourceLocation, EnvironmentalAffinity> implementations = Objects.requireNonNull(apiBindings.get(key)).getAllBindings();
				Set<ResourceLocation> rsrcs = implementations.keySet();
				for (ResourceLocation rsrc : rsrcs) {
					lastBiomeID = rsrc;
					EnvironmentalAffinity impl = Objects.requireNonNull(implementations.get(rsrc));
					impl.onWorldTick(world);
				}
			} catch (Exception exc) {
				String message = String.format(
					"An error occurred whilst trying to do a world tick in the Environmental Affinity system. The ID of the mod that registered this implementation is '%s', and the biome that the failure occurred in is '%s'.",
					key,
					lastBiomeID == null ? "<no biome - this error occurred while trying to get the API instance for the mod in question>" : lastBiomeID.toString()
				);
				throw new RuntimeException(message, exc);
			}
		}
	}

}