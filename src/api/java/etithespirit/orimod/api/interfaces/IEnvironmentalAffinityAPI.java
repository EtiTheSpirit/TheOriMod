package etithespirit.orimod.api.interfaces;

import com.google.common.collect.ImmutableMap;
import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.api.environment.EnvironmentalAffinity;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * An API providing a means of interacting with the environment and registering how affine those environments are to Light or Decay.
 */
public interface IEnvironmentalAffinityAPI {
	
	
	/**
	 * <strong>MUST BE CHECKED BEFORE OTHER METHODS ARE USED.</strong>
	 * @return Whether or not the API is installed.
	 */
	boolean isInstalled();
	
	/**
	 * Returns the environmental affinity of this biome with respect to Decay vs Light.
	 * @param biome The biome's registry name.
	 * @return The environmental affinity of this biome, or null if no affinity was registered.
	 * @throws ArgumentNullException If the biome parameter is null.
	 * @throws IllegalStateException If the mod is not installed.
	 */
	@Nullable EnvironmentalAffinity getBiomeEnvEffects(ResourceLocation biome) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * Sets the environmental affinity of the given biome. If this biome has already been registered, it will overwrite the previous registered setting.
	 * @param biome The biome to associate with, by registry name.
	 * @param affinity The affinity to use for this biome. This can be null to remove the affinity.
	 * @throws ArgumentNullException If the biome parameter is null.
	 * @throws IllegalStateException If the mod is not installed, or if this is called after the Forge load cycle has completed.
	 */
	void setBiomeEnvEffects(ResourceLocation biome, @Nullable EnvironmentalAffinity affinity) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * Returns all bindings from biome to their environmental affinity.
	 * @return All bindings from biome to their environmental affinity.
	 */
	ImmutableMap<ResourceLocation, EnvironmentalAffinity> getAllBindings() throws IllegalStateException;

}
