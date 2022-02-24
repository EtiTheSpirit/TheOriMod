package etithespirit.orimod.energy;

import etithespirit.orimod.api.energy.FluxBehavior;
import etithespirit.orimod.util.valuetypes.LightEnergyAdapter;

import javax.annotation.Nonnull;

/**
 * An energy system similar to (but purposely not directly compatible with) {@link net.minecraftforge.energy.IEnergyStorage IEnergyStorage}.
 * It has a number of unique quirks to it, such as the ability to be affected by the environment.<br/><br/>
 *
 * Additionally, the mechanics of this storage medium are based off of my custom written lore for Spirit Light.<br/>
 * <br/>
 * <strong>For other modders, please note:</strong>
 * <ul>
 *     <li>The value of the Luxen (a Light unit) is extremely high. It is not like RF where units in the millions are needed. The default conversion ratio is 5000RF = 1 Luxen for a reason!</li>
 *     <li>You are advised to work with RF if possible. The only time you should use this class is if you are adding new Light technology. It is strongly recommended to <strong>NOT</strong> make a device that implements both energy interfaces as to ensure the energy types are meaningfully isolated.</li>
 *     <li>Respect configs! There is one adapter block included with the mod by default. You should not need to make any more, but if, for some reason, you must? Leverage the user's or server's configs to get the proper conversion ratios.</li>
 * </ul>
 *
 * @author Eti
 */
public interface ILightEnergyStorage {
	
	/**
	 * Adds energy to the storage. Returns quantity of energy that was accepted.
	 *
	 * @param maxReceive
	 *            Maximum amount of energy to be inserted.
	 * @param simulate
	 *            If TRUE, the insertion will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
	 */
	double receiveLight(double maxReceive, boolean simulate);
	
	/**
	 * Removes energy from the storage. Returns quantity of energy that was removed.
	 *
	 * @param maxExtract
	 *            Maximum amount of energy to be extracted.
	 * @param simulate
	 *            If TRUE, the extraction will only be simulated.
	 * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
	 */
	double extractLight(double maxExtract, boolean simulate);
	
	/**
	 * @return The amount of energy currently stored.
	 */
	double getLightStored();
	
	/**
	 * @return The maximum amount of energy that can be stored.
	 */
	double getMaxLightStored();
	
	/**
	 * Returns if this storage can have energy extracted.
	 * If this is false, then any calls to extractEnergy will return 0.
	 * @return Whether or not this storage can have energy extracted from it.
	 */
	boolean canExtractLight();
	
	/**
	 * Used to determine if this storage can receive energy.
	 * If this is false, then any calls to receiveEnergy will return 0.
	 * @return Whether or not this storage can have energy added to it.
	 */
	boolean canReceiveLight();
	
	/**
	 * Whether or not this storage is a valid candidate for use in the {@link LightEnergyAdapter Light Energy Adapter} (for conversion to RF).
	 * @return whether or not this can interact with RF (as opposed to Light), which determines its usability in {@link LightEnergyAdapter LightEnergyAdapter}.
	 */
	boolean acceptsConversion();
	
	/**
	 * Stores or takes an arbitrary amount of energy from no particular source or sink.
	 * Intended to represent gains or losses from environmental noise.<br/><br/>
	 *
	 * Implementors should make use of {@link #getFluxBehavior()} to determine the returned value.
	 *
	 * @param simulate
	 *            If TRUE, the fluctuations in power will only be simulated.
	 *
	 * @return Amount of energy that was (or would have been, if simulated) generated (or sapped) from this device and placed into (or taken from) its own storage.
	 * @deprecated Flux Behavior is being removed in favor of environmental efficiency.
	 */
	@Deprecated(forRemoval = true)
	default double applyEnvFlux(boolean simulate) { return 0; }
	
	/**
	 * The FluxBehavior responsible for returning unfiltered environmental flux values.<br/>
	 * <strong>NULL IS NOT ACCEPTABLE.</strong> This should always return {@link FluxBehavior#DISABLED FluxBehavior.DISABLED} if there is no flux.
	 * @return A FluxBehavior instance describing how flux is applied.
	 * @deprecated Flux Behavior is being removed in favor of environmental efficiency.
	 */
	@Deprecated(forRemoval = true)
	default @Nonnull FluxBehavior getFluxBehavior() {
		return FluxBehavior.DISABLED;
	}
}
