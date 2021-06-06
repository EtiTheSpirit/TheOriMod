package etithespirit.etimod.energy;

/**
 * An energy system similar to (but purposely not directly compatible with) {@link net.minecraftforge.energy.IEnergyStorage}. It has a number of unique quirks to it, such as the ability to be affected by environmental flux.<br/> 
 * This is also based on custom lore regarding Spirit Light.
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
    * Returns the amount of energy currently stored.
    */
	double getLightStored();

    /**
    * Returns the maximum amount of energy that can be stored.
    */
	double getMaxLightStored();

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    boolean canExtractLight();

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    boolean canReceiveLight();
    
    /**
     * Used to determine if this storage is affected by light-flux in the air, which will randomly add or remove energy from this device.
     */
    boolean subjectToFlux();
    
    /**
     * Whether or not this can both receive and give RF (as opposed to Light), which determines its usability in {@link etithespirit.etimod.energy.LightEnergyAdapter LightEnergyAdapter}.
     */
    boolean acceptsConversion();
	
	/**
	 * Stores or takes an arbitrary amount of energy from no particular source or sink. 
	 * Intended to represent gains or losses from environmental noise.<br/><br/>
	 * 
	 * This requires not only hasEnvNoise to return true. Specifically, if at least one of min or max are less than zero, it requires canExtract to be true, 
	 * and if at least one of the two are greater than zero, it requires canReceive to be true. If these requirements are not met, 
	 * then the energy will be "clipped"; if the range is -0.5 to +0.5, and canExtract is false, then any values that generate less 
	 * than zero will be discarded (and 0 returned), rendering only half of the range usable.<br/><br/>
	 * 
	 * If a randomizer is implemented, the iteration number should be tracked so that if applyEnvFlux is called with a given range where simulate=true, 
	 * the behavior <em>must</em> be reflective of what will occur on the next call where simulate=false, granted no changes have occurred between the two
	 * calls. Calling with simulate=true will only provide one step of lookahead, and will not reveal results further than one iteration forward.
	 * 
	 * @param minGen
	 *            Minimum amount of energy that can be generated. This can be negative to cause flux to take energy out of the device.
	 * 
	 * @param maxGen
     *            Maximum amount of energy that can be generated. This can be negative to cause flux to take energy out of the device.
     *            
	 * @param simulate
     *            If TRUE, the generation will only be simulated.
     *            
	 * @return Amount of energy that was (or would have been, if simulated) generated from this device and placed into its own storage.
	 */
    double applyEnvFlux(double minGen, double maxGen, boolean simulate);

}
