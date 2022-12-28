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
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Provides access to a copy of the APIs offered by this mod.
 * @author Eti
 */
public final class APIProvider {
	
	private APIProvider() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	//private static ISpiritSoundAPI spiritSoundAPI = null;
	//private static IEnvironmentalAffinityAPI envAffinityAPI = null;
	
	private static final Map<String, ISpiritSoundAPI> SOUND_APIS_FOR_MODS = new HashMap<>();
	private static final Map<String, IEnvironmentalAffinityAPI> ENV_APIS_FOR_MODS = new HashMap<>();
	private static final Logger LOG = LogManager.getLogger("OriMod API");
	
	private static ImmutableMap<String, ISpiritSoundAPI> IMMUTABLE_SOUNDS_APIS;
	private static ImmutableMap<String, IEnvironmentalAffinityAPI> IMMUTABLE_ENV_APIS;
	private static boolean isLoadComplete = false;
	private static String calledLoadCompleted = "<no mod>";
	
	public static void _internalMarkLoadCycleCompleted() {
		if (isLoadComplete) throw new IllegalStateException(
			"Mod [" + ModLoadingContext.get().getActiveNamespace() + "] is trying to lock the Ori Mod API system, but can't, because Mod [" + calledLoadCompleted + "] has already locked it. " +
				"If you are seeing this error message, someone did something foolish and has had 15 social credit deducted from their account for doing things they are not supposed to be doing. " +
				"If both mods are 'orimod', something has gone terribly wrong and you should submit this as a bug."
			);
		isLoadComplete = true;
		calledLoadCompleted = ModLoadingContext.get().getActiveNamespace();
		IMMUTABLE_SOUNDS_APIS = ImmutableMap.copyOf(SOUND_APIS_FOR_MODS);
		IMMUTABLE_ENV_APIS = ImmutableMap.copyOf(ENV_APIS_FOR_MODS);
	}
	
	public static ImmutableMap<String, ISpiritSoundAPI> getAllSpiritSoundAPIs() {
		if (!isLoadComplete) throw new IllegalStateException("Cannot call this method until Mod loading has completed for all mods.");
		return IMMUTABLE_SOUNDS_APIS;
		
	}
	
	public static ImmutableMap<String, IEnvironmentalAffinityAPI> getAllEnvAffinityAPIs() {
		if (!isLoadComplete) throw new IllegalStateException("Cannot call this method until Mod loading has completed for all mods.");
		return IMMUTABLE_ENV_APIS;
	}
	
	/**
	 * Attempts to construct the given API class from the class name, sending a friendly reminder error if I forgot to create the public parameterless ctor.
	 * @param classPath The path to the implementation class.
	 * @param <T> The API type.
	 * @return The API implementation, or null if no such API exists due to the mod not being installed.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getAPIInstanceFrom(String classPath, String name) {
		try {
			Class<?> apiClass = Class.forName(classPath);
			Object instance = apiClass.getDeclaredConstructor(String.class).newInstance(name);
			return (T) instance;
		} catch (NoSuchMethodException ctorMissing) {
			throw new NotImplementedException("Eti, you forgot to define the ctor of " + classPath + " properly. It should be a constructor with a single string property, which receives the mod ID.");
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
	
	private static IllegalStateException illegalStateForNotInstalledMod(String modId) {
		return new IllegalStateException("OriMod is not installed. This method should not be called! Offending mod ID: " + modId);
	}
	
	/**
	 * Returns an API that allows for custom block/material sound overrides to be registered to Spirits. This API is not a singleton
	 * and is returned on a per-mod-id basis (that is, your mod has its own instance separate from other mods). This way proper error logging can be done.
	 * <strong>As such, the caller should cache the result of this method.</strong><br/>
	 * <br/>
	 * Use this if you are interested in any of your mod blocks having different sounds when they are stepped on
	 * by spirits. This is, of course, purely cosmetic and exists for the sake of immersion.<br/>
	 * @return The instance of the sound API. If the mod is not installed (and only the API is), then a dummy API will be returned.
	 *          Use {@link ISpiritSoundAPI#isInstalled()} to determine whether or not the returned API is usable, as the dummy API will raise an exception upon calling any of its members.
	 *
	 * @exception IllegalCallerException If the method is not called while a mod is actively loading (this uses mod loading contexts to figure out which mod owns this API).
	 */
	public static ISpiritSoundAPI getSpiritSoundAPI() throws IllegalCallerException {
		ModLoadingContext ctx = ModLoadingContext.get();
		String currentLoadingModID = ctx.getActiveNamespace();
		if (currentLoadingModID.equals("minecraft")) {
			throw new IllegalCallerException("The caller of this method did not call it during a mod's initialization sequence. This method should only be called in a mod-specific place, like its constructor.");
		}
		
		LOG.info("Mod[ID={}] has requested a Spirit Sound API.", currentLoadingModID);
		
		ISpiritSoundAPI spiritSoundAPI = SOUND_APIS_FOR_MODS.get(currentLoadingModID);
		if (spiritSoundAPI == null) {
			spiritSoundAPI = getAPIInstanceFrom("etithespirit.orimod.apiimpl.SpiritSoundAPI", currentLoadingModID);
			if (spiritSoundAPI == null) {
				spiritSoundAPI = new ISpiritSoundAPI() {
					
					@Override
					public boolean isInstalled() {
						return false;
					}
					
					@Override
					public void registerBlock(Supplier<Block> entireBlockType, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void registerBlockState(Supplier<BlockState> specificState, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void registerTag(TagKey<Block> blockTag, SpiritMaterial material) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void setUseIfInBlock(Supplier<Block> entireBlockType, boolean useIfIn) throws ArgumentNullException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void setUseIfInState(Supplier<BlockState> specificState, boolean useIfIn) throws ArgumentNullException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void setUseIfInBlock(TagKey<Block> blockTag, boolean useIfIn) throws ArgumentNullException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void associateMaterialWith(Material material, SpiritMaterial spiritMaterial) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void setSpecialMaterialPredicate(Supplier<Block> entireBlockType, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void setSpecialMaterialPredicate(Material entireBlockType, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void setSpecialMaterialPredicate(TagKey<Block> blockTag, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
				};
			}
			
			SOUND_APIS_FOR_MODS.put(currentLoadingModID, spiritSoundAPI);
		}
		
		return spiritSoundAPI;
	}
	
	/**
	 * Returns an instance of the Environmental Affinity API if it exists, or a dummy instance otherwise.<br/>
	 * This API is not ready for use.
	 * @return An instance of the Environmental Affinity API if it exists, or a dummy instance otherwise.
	 *
	 * @exception IllegalCallerException If the method is not called while a mod is actively loading (this uses mod loading contexts to figure out which mod owns this API).
	 */
	public static IEnvironmentalAffinityAPI getEnvironmentalAffinityAPI() {
		ModLoadingContext ctx = ModLoadingContext.get();
		String currentLoadingModID = ctx.getActiveNamespace();
		if (currentLoadingModID.equals("minecraft")) {
			throw new IllegalCallerException("The caller of this method did not call it during a mod's initialization sequence. This method should only be called in a mod-specific place, like its constructor.");
		}
		
		IEnvironmentalAffinityAPI envAffinityAPI = ENV_APIS_FOR_MODS.get(currentLoadingModID);
		if (envAffinityAPI == null) {
			envAffinityAPI = getAPIInstanceFrom("etithespirit.orimod.apiimpl.EnvironmentalAffinityAPI", currentLoadingModID);
			if (envAffinityAPI == null) {
				envAffinityAPI = new IEnvironmentalAffinityAPI() {
					@Override
					public boolean isInstalled() {
						return false;
					}
					
					@Nullable
					@Override
					public EnvironmentalAffinity getBiomeEnvEffects(ResourceLocation biome) throws ArgumentNullException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public void setBiomeEnvEffects(ResourceLocation biome, @Nullable EnvironmentalAffinity affinity) throws ArgumentNullException, IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
					
					@Override
					public ImmutableMap<ResourceLocation, EnvironmentalAffinity> getAllBindings() throws IllegalStateException {
						throw illegalStateForNotInstalledMod(currentLoadingModID);
					}
				};
			}
			ENV_APIS_FOR_MODS.put(currentLoadingModID, envAffinityAPI);
		}
		return envAffinityAPI;
	}
	
}
