package etithespirit.etimod.energy;

import net.minecraftforge.energy.IEnergyStorage;

/** 
 * A static class that provides a means of adapting power between {@link etithespirit.etimod.energy.ILightEnergyStorage ILightEnergyStorage} and {@link net.minecraftforge.energy.IEnergyStorage IEnergyStorage} 
 * 
 * @author Eti
 */
public final class LightEnergyAdapter {
	// Prevent instances from being created. Shame Java has no static classes.
	private LightEnergyAdapter() { }
	
	
	/**
	 * When converting Light -&gt; RF, the value of Light is multiplied by this much when being translated into RF.
	 */
	public static final double DEFAULT_RATIO_FROM_LIGHT_TO_RF = 50;
	
	/**
	 * Transfer energy between a container of Light and a container of RF at the {@link #DEFAULT_RATIO_FROM_LIGHT_TO_RF}
	 * @param lightContainer The container for Light energy.
	 * @param rfContainer The container for RF energy.
	 * @param direction The direction in which the conversion is occurring (Light -&gt; RF or RF -&gt; Light)
	 * @param amount The amount of energy to transfer. The unit this represents depends on the "from" component of the direction, so if transferring from light to RF, the unit is light ("from light"), and if transferring from RF to light, the unit is RF ("from RF"). For cases where RF is the "from" component, it will be cast into int32.
	 * @param simulate If true, the transaction will only be simulated and not actually affect the containers.
	 * @return An {@link LightEnergyAdapter.EnergyTransactionResult EnergyTransactionResult} describing the amount of energy transferred in both units, the direction in which the energy was transferred, at what ratio, and whether or not it was simulated.
	 */
	public static final EnergyTransactionResult performTransfer(ILightEnergyStorage lightContainer, IEnergyStorage rfContainer, EnergyTransactionDirection direction, double amount, boolean simulate) {
		return performTransfer(lightContainer, rfContainer, direction, amount, DEFAULT_RATIO_FROM_LIGHT_TO_RF, simulate);
	}

	/**
	 * Transfer energy between a container of Light and a container of RF at a custom conversion ratio.
	 * @param lightContainer The container for Light energy.
	 * @param rfContainer The container for RF energy.
	 * @param direction The direction in which the conversion is occurring (Light -&gt; RF or RF -&gt; Light)
	 * @param amount The amount of energy to transfer. The unit this represents depends on the "from" component of the direction, so if transferring from light to RF, the unit is light ("from light"), and if transferring from RF to light, the unit is RF ("from RF"). For cases where RF is the "from" component, it will be cast into int32.
	 * @param lightToRFRatio The conversion ratio from Light to RF. Contrary to {@code amount}, this is <strong>ALWAYS</strong> in Light -&gt; RF.
	 * @param simulate If true, the transaction will only be simulated and not actually affect the containers.
	 * @return An {@link LightEnergyAdapter.EnergyTransactionResult EnergyTransactionResult} describing the amount of energy transferred in both units, the direction in which the energy was transferred, at what ratio, and whether or not it was simulated.
	 */
	public static final EnergyTransactionResult performTransfer(ILightEnergyStorage lightContainer, IEnergyStorage rfContainer, EnergyTransactionDirection direction, double amount, double lightToRFRatio, boolean simulate) {
		if (!lightContainer.acceptsConversion()) {
			return new EnergyTransactionResult(0, 0, lightToRFRatio, direction, simulate);
		}
		if (direction == EnergyTransactionDirection.FROM_LIGHT_TO_RF) {
			if (!lightContainer.canExtractLight() || !rfContainer.canReceive()) {
				return new EnergyTransactionResult(0, 0, lightToRFRatio, direction, simulate);
			}
			
			final double lightToTransfer = amount;
			
			// The amount of Light we initially extracted from our Light container. This might not necessarily be the amount we can use.
			// Because of this, we ALWAYS simulate this action.
			final double lightInitiallyExtracted = lightContainer.extractLight(lightToTransfer, true);
			
			// Now, let's try to see how much RF we can receive. There's a good chance that while we were able to get a given amount of Light, we were
			// NOT able to translate all of it into RF for this container (maybe this container is too full?)
			// Contrarily to above, we don't have to simulate this. We can actually use the return value to our advantage.
			final int rfReceived = rfContainer.receiveEnergy((int)(lightInitiallyExtracted * lightToRFRatio), simulate);
			
			// Now one thing is that since the above depends on the simulate parameter, if we *did* just so happen to simulate, then we need to have
			// some different behavior.
			
			// So now we need to figure out how much Light that equates to before anything else. RF device was able to store (r)
			// so what is (r) in (L)?
			final double lightReceivedByRF = rfReceived / lightToRFRatio; 
			final double lightActuallyExtracted = lightContainer.extractLight(lightReceivedByRF, simulate);
			
			return new EnergyTransactionResult(lightActuallyExtracted, rfReceived, lightToRFRatio, direction, simulate);
		} else {
			if (!lightContainer.canReceiveLight() || !rfContainer.canExtract()) {
				return new EnergyTransactionResult(0, 0, lightToRFRatio, direction, simulate);
			}
			
			final int rfToTransfer = (int)amount;
			
			// The amount of RF we initially extracted from our RF container. This might not necessarily be the amount we can use.
			// Because of this, we ALWAYS simulate this action.
			final int rfInitiallyExtracted = rfContainer.extractEnergy(rfToTransfer, true);
			
			// Now, let's try to see how much Light we can receive. There's a good chance that while we were able to get a given amount of RF, we were
			// NOT able to translate all of it into Light for this container (maybe this container is too full?)
			// Contrarily to above, we don't have to simulate this. We can actually use the return value to our advantage.
			final double lightReceived = lightContainer.receiveLight(rfInitiallyExtracted / lightToRFRatio, simulate);
			
			// Now one thing is that since the above depends on the simulate parameter, if we *did* just so happen to simulate, then we need to have
			// some different behavior.
			
			// So now we need to figure out how much RF that equates to before anything else. Light device was able to store (L)
			// so what is (L) in (r)?
			final int rfReceivedByLight = (int)(lightReceived * lightToRFRatio); 
			final int rfActuallyExtracted = rfContainer.extractEnergy(rfReceivedByLight, simulate);
			
			return new EnergyTransactionResult(lightReceived, rfActuallyExtracted, lightToRFRatio, direction, simulate);
		}
	}
	
	/**
	 * Represents the result of a transaction done between Light and RF equipment.
	 * @author Eti
	 *
	 */
	public static final class EnergyTransactionResult {	
		
		/**
		 * The amount of Light that was transferred.
		 */
		public final double lightTransferred;
		
		/**
		 * The amount of RF that was transferred.
		 */
		public final int rfTransferred;
		
		/**
		 * The conversion ratio of the energy from Light -&gt; RF
		 */
		public final double lightToRFRatio;
		
		/**
		 * The direction of this transfer, that is, Light -&gt; RF or RF -&gt; Light
		 */
		public final EnergyTransactionDirection direction;
		
		/**
		 * If true, this conversion was not actually performed, only simulated.
		 */
		public final boolean wasSimulated;
		
		protected EnergyTransactionResult(double lightTransferred, int rfTransferred, double lightToRFRatio, EnergyTransactionDirection direction, boolean wasSimulated) {
			this.lightTransferred = lightTransferred;
			this.rfTransferred = rfTransferred;
			this.lightToRFRatio = lightToRFRatio;
			this.direction = direction;
			this.wasSimulated = wasSimulated;
		}
	}
	
	/**
	 * Describes the manner in which energy is being moved.
	 * @author Eti
	 *
	 */
	public static enum EnergyTransactionDirection {
		
		/**
		 * This energy transfer is being done from a container of light to a container of RF.
		 */
		FROM_LIGHT_TO_RF,
		
		/**
		 * This energy transfer is being done from a container of RF to a container of Light.
		 */
		FROM_RF_TO_LIGHT;
		
	}
}
