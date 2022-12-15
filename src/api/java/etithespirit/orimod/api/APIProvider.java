package etithespirit.orimod.api;


import com.google.common.collect.ImmutableMap;
import etithespirit.exception.ArgumentNullException;
import etithespirit.exception.NotImplementedException;
import etithespirit.orimod.api.delegate.ISpiritMaterialAcquisitionFunction;
import etithespirit.orimod.api.environment.EnvironmentalAffinity;
import etithespirit.orimod.api.interfaces.ISpiritSoundAPI;
import etithespirit.orimod.api.interfaces.IEnvironmentalAffinityAPI;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * Provides access to a copy of the APIs offered by this mod.
 * @author Eti
 */
public final class APIProvider {
	
	private APIProvider() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static ISpiritSoundAPI spiritSoundAPI = null;
	private static IEnvironmentalAffinityAPI envAffinityAPI = null;
	
	/**
	 * Attempts to construct the given API class from the class name, sending a friendly reminder error if I forgot to create the public parameterless ctor.
	 * @param classPath The path to the implementation class.
	 * @param <T> The API type.
	 * @return The API implementation, or null if no such API exists due to the mod not being installed.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getAPIInstanceFrom(String classPath) {
		try {
			Class<?> apiClass = Class.forName(classPath);
			Object instance = apiClass.getDeclaredConstructor().newInstance();
			return (T) instance;
		} catch (NoSuchMethodException ctorMissing) {
			throw new NotImplementedException("Eti, you forgot to define the default parameterless ctor of " + classPath);
		} catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | InstantiationException ignored) { }
		return null;
	}
	
	/**
	 * Returns whether or not the Ori mod is installed.
	 * @return Whether or not the Ori mod is installed.
	 */
	public static boolean isModInstalled() {
		return ModList.get().isLoaded("orimod");
	}
	
	/**
	 * Returns an API that allows for custom block/material sound overrides to be registered to Spirits.
	 * Use this if you are interested in any of your mod blocks having different sounds when they are stepped on
	 * by spirits. This is, of course, purely cosmetic and exists for the sake of immersion.<br/>
	 * @return The instance of the sound API. If the mod is not installed (and only the API is), then a dummy API will be returned. Use {@link ISpiritSoundAPI#isInstalled()} to determine whether or not the returned API is usable, as the dummy API will raise an exception upon calling any of its members.
	 */
	public static ISpiritSoundAPI getSpiritSoundAPI() {
		if (spiritSoundAPI == null) {
			spiritSoundAPI = getAPIInstanceFrom("etithespirit.orimod.apiimpl.SpiritSoundAPI");
			if (spiritSoundAPI == null) {
				spiritSoundAPI = new ISpiritSoundAPI() {
					
					@Override
					public boolean isInstalled() {
						return false;
					}
					
					@Override
					public void registerSpiritStepSound(Block entireBlockType, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void registerSpiritStepSound(BlockState specificState, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void registerSpiritStepSound(TagKey<Block> blockTag, SpiritMaterial material) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setUseIfIn(Block entireBlockType) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setUseIfIn(BlockState specificState) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void associateMaterialWith(Material material, SpiritMaterial spiritMaterial) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setSpecialMaterialPredicate(Block entireBlockType, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setSpecialMaterialPredicate(Material entireBlockType, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
				};
			}
		}
		
		return spiritSoundAPI;
	}
	
	/**
	 * Returns an instance of the Environmental Affinity API if it exists, or a dummy instance otherwise.<br/>
	 * This API is not ready for use.
	 * @return An instance of the Environmental Affinity API if it exists, or a dummy instance otherwise.
	 */
	public static IEnvironmentalAffinityAPI getEnvironmentalAffinityAPI() {
		if (envAffinityAPI == null) {
			envAffinityAPI = getAPIInstanceFrom("etithespirit.orimod.apiimpl.EnvironmentalAffinityAPI");
			if (envAffinityAPI == null) {
				envAffinityAPI = new IEnvironmentalAffinityAPI() {
					@Override
					public boolean isInstalled() {
						return false;
					}
					
					@Nullable
					@Override
					public EnvironmentalAffinity getBiomeEnvEffects(ResourceLocation biome) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setBiomeEnvEffects(ResourceLocation biome, @Nullable EnvironmentalAffinity affinity) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public ImmutableMap<ResourceLocation, EnvironmentalAffinity> getAllBindings() throws IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
				};
			}
		}
		return envAffinityAPI;
	}
	
}
