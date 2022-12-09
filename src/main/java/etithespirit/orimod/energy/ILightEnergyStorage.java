package etithespirit.orimod.energy;

import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.util.valuetypes.LightEnergyAdapter;

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
 *     <li>Respect configs! There is one adapter block included with the mod by default. You should not need to make any more, but if for some reason you must, please: Leverage the user's or server's configs to get the proper conversion ratios.</li>
 * </ul>
 *
 * @author Eti
 */
public interface ILightEnergyStorage {
	
	/**
	 * A default helper method to perform a transfer of energy. If the amount is negative, the sender and receiver are swapped.
	 * @param sender The object sending energy.
	 * @param receiver The object receiving energy.
	 * @param amount The amount to transfer. A value of {@link Double#POSITIVE_INFINITY} can be used to perform the largest transfer possible.
	 * @param simulate If TRUE, the transfer will only be simulated.
	 * @return The amount that was transferred (or that would be transferred, if simulated).
	 */
	static double transferLight(ILightEnergyStorage sender, ILightEnergyStorage receiver, double amount, boolean simulate) {
		if (amount < 0) return transferLight(receiver, sender, -amount, simulate);
		if (amount == 0) return 0;
		
		if (sender.canExtractLightFrom() && receiver.canReceiveLight()) {
			double realAmountTaken = sender.extractLightFrom(amount, true);
			double realAmountReceived = receiver.receiveLight(realAmountTaken, true);
			
			// In a sane scenario, and for a scenario where only losses occur, amount will be larger than realAmountTaken, which will be larger than realAmountReceived
			// thus, realAmountReceived is the grand representation of the actual transfer.
			if (simulate) return realAmountReceived;
			
			sender.extractLightFrom(realAmountReceived, false);
			receiver.receiveLight(realAmountReceived, false);
			return realAmountReceived;
		}
		return 0;
	}
	
	/**
	 * Converts an amount of Luxen to RF.
	 * @param luxen The amount of Luxen to convert.
	 * @return The equivalent amount of RF.
	 */
	static double luxenToRedstoneFlux(double luxen) {
		return luxen * OriModConfigs.LUX_TO_RF_RATIO.get();
	}
	
	/**
	 * Converts an amount of RF to Luxen.
	 * @param rf The amount of RF to convert.
	 * @return The equivalent amount of Luxen.
	 */
	static double redstoneFluxToLuxen(double rf) {
		if (rf == 0) return 0;
		return rf / OriModConfigs.LUX_TO_RF_RATIO.get();
	}
	
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
	double extractLightFrom(double maxExtract, boolean simulate);
	
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
	boolean canExtractLightFrom();
	
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
}
