package etithespirit.orimod.apiimpl;

import com.google.common.collect.ImmutableMap;
import etithespirit.exception.ArgumentNullException;
import etithespirit.exception.ConstantErrorMessages;
import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.api.interfaces.IEnvironmentalAffinityAPI;
import etithespirit.orimod.api.environment.EnvironmentalAffinity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

/**
 * An implementation of the environmental affinity API.
 */
public class EnvironmentalAffinityAPI implements IEnvironmentalAffinityAPI {
	
	private static ImmutableMap<ResourceLocation, EnvironmentalAffinity> BINDINGS = null;
	private static final Map<ResourceLocation, EnvironmentalAffinity> AFFINITY_BINDINGS = new HashMap<>();
	
	/** This is declared so that the API can be constructed via reflection. */
	public EnvironmentalAffinityAPI() {} // This must be explicitly declared in order for the API to work.

	@Override
	public boolean isInstalled() {
		return true;
	}
	
	
	@Nullable
	@Override
	public EnvironmentalAffinity getBiomeEnvEffects(ResourceLocation biome) throws ArgumentNullException, IllegalStateException {
		return null;
	}
	
	@Override
	public void setBiomeEnvEffects(ResourceLocation biome, @Nullable EnvironmentalAffinity affinity) throws ArgumentNullException, IllegalStateException {
		if (OriMod.forgeLoadingComplete()) throw new IllegalStateException(ConstantErrorMessages.FORGE_LOADING_COMPLETED);
		if (biome == null) throw new ArgumentNullException("biome");
		AFFINITY_BINDINGS.put(biome, affinity);
	}
	
	@Override
	public ImmutableMap<ResourceLocation, EnvironmentalAffinity> getAllBindings() throws IllegalStateException {
		if (BINDINGS == null) {
			BINDINGS = ImmutableMap.copyOf(AFFINITY_BINDINGS);
		}
		return BINDINGS;
	}
	
	/** Iterates over all affinity bindings and ensures that the biome they were bound to is identical to the biome they were constructed with. */
	public static void validate() {
		AFFINITY_BINDINGS.forEach((rsrc, env) -> {
			if (!rsrc.equals(env.biome)) throw new InputMismatchException("A mod attempted to associate [" + rsrc.toString() + "] with an instance that was instantiated for [" + env.biome.toString() + "]!");
		});
	}
	
	/**
	 * Called when Forge's player tick event executes.
	 * @param evt The tick event.
	 */
	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent evt) {
		if (evt.phase == TickEvent.Phase.END) {
			EnvironmentalAffinity.sendPlayerTickToAll(evt.player);
		}
	}
	
	/**
	 * Called when Forge's world tick event executes.
	 * @param evt The tick event.
	 */
	public static void onWorldTickEvent(TickEvent.LevelTickEvent evt) {
		if (evt.phase == TickEvent.Phase.END) {
			EnvironmentalAffinity.sendWorldTickToAll(evt.level);
		}
	}
}
