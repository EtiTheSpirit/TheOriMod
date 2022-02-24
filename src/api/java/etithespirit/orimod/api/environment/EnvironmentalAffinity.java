package etithespirit.orimod.api.environment;

import com.google.common.collect.ImmutableMap;
import etithespirit.orimod.api.APIProvider;
import etithespirit.orimod.api.energy.FluxBehavior;
import etithespirit.orimod.api.util.valuetypes.NumberRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * This class stores data about a biome's environmental affinity.
 * You can create instances for your mod as needed.
 */
public abstract class EnvironmentalAffinity {
	
	/** The ID of the biome that this affinity object applies to. */
	public final ResourceLocation biome;
	
	/** If the player or server has flux enabled, this is the behavior of this environment on their devices. */
	public final FluxBehavior flux;
	
	/** An object describing the efficiency of Light-based devices. This value is centered around 100% and thus its result should be multiplied directly with the cost of a device. */
	public final NumberRange efficiency;
	
	/** An object describing the efficiency of Light-based devices. This value is centered around 100% and thus its result should be multiplied directly with the cost of a device. */
	public final double efficiencySingular;
	
	/**
	 * Create a new affinity object for the given biome using the given flux and static efficiency percentage.
	 * @param biome The biome that this exists for.
	 * @param flux The flux the environment applies to Light storage devices.
	 * @param efficiencyPercentage The efficiency boost (or reduction) for all devices as a range of possible values.
	 */
	public EnvironmentalAffinity(ResourceLocation biome, FluxBehavior flux, NumberRange efficiencyPercentage) {
		this.biome = biome;
		this.flux = flux;
		this.efficiency = efficiencyPercentage;
		this.efficiencySingular = Double.NaN;
	}
	
	/**
	 * Create a new affinity object for the given biome using the given flux and static efficiency percentage.
	 * @param biome The biome that this exists for.
	 * @param flux The flux the environment applies to Light storage devices.
	 * @param efficiencyPercentage The efficiency boost (or reduction) for all devices as a singular value.
	 */
	public EnvironmentalAffinity(ResourceLocation biome, FluxBehavior flux, double efficiencyPercentage) {
		this.biome = biome;
		this.flux = flux;
		this.efficiency = null;
		this.efficiencySingular = efficiencyPercentage;
	}
	
	/**
	 * This method executes every forge Player tick (pre phase, both sides) where the player is in the biome associated with this affinity.
	 * @param player The player to tick for.
	 */
	public abstract void onPlayerTick(Player player);
	
	/**
	 * This method executes every forge World tick (pre phase, both sides).
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
				if (player.level.getBiome(player.getOnPos()).getRegistryName().equals(rsrc)) {
					EnvironmentalAffinity env = all.get(rsrc);
					latest = env.getClass();
					env.onPlayerTick(player);
				}
			}
		} catch (Exception exc) {
			//LOG.error("An error occurred whilst trying to execute player tick code for biome [{}]: {}", current, exc.getMessage());
			//throw exc;
			throw (RuntimeException)new RuntimeException(String.format("An error occurred whilst trying to execute the player tick portion of a Decay/Light Environment Affinity system registered for biome [%s]: %s\n\nThe offending class is located at: %s", current.toString(), exc.getMessage(), latest.getName())).initCause(exc);
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
			throw (RuntimeException)new RuntimeException(String.format("An error occurred whilst trying to execute the world tick portion of a Decay/Light Environment Affinity system registered for biome [%s]: %s\n\nThe offending class is located at: %s", current.toString(), exc.getMessage(), latest.getName())).initCause(exc);
		}
	}

}