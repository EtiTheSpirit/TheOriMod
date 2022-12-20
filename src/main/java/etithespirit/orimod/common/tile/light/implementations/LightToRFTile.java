package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.common.tile.light.helpers.EnergyReservoir;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;

public class LightToRFTile extends LightEnergyHandlingTile implements IEnergyStorage, ILightEnergyConsumer {
	
	private static final float MAX_CONVERSION_RATE_LUXEN = 1f;
	private final EnergyReservoir consumerHelper = new EnergyReservoir(MAX_CONVERSION_RATE_LUXEN);
	private int rfExtractedLastTick = 0;
	
	public LightToRFTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_TO_RF_TILE.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		float lux = ILightEnergyStorage.redstoneFluxToLuxen(maxExtract);
		if (simulate) {
			float available = consumerHelper.getStashedEnergy();
			return ILightEnergyStorage.luxenToRedstoneFlux(Math.min(available, lux));
		}
		
		lux = consumerHelper.consumeUpTo(lux, false);
		rfExtractedLastTick = ILightEnergyStorage.luxenToRedstoneFlux(lux);
		return rfExtractedLastTick;
	}
	
	@Override
	public int getEnergyStored() {
		return ILightEnergyStorage.luxenToRedstoneFlux(consumerHelper.getStashedEnergy());
	}
	
	@Override
	public int getMaxEnergyStored() {
		return ILightEnergyStorage.luxenToRedstoneFlux(consumerHelper.getMaxStashedEnergy());
	}
	
	@Override
	public boolean canExtract() {
		return true;
	}
	
	@Override
	public boolean canReceive() {
		return false;
	}
	
	/**
	 * Attempts to consume the given amount of energy, returning the amount of power that was actually consumed.
	 *
	 * @param desiredAmount The amount of energy that is free for consumption. This can be {@link Float#POSITIVE_INFINITY} to query the amount of power the device wants.
	 * @param simulate      If true, the consumption will only be simulated for the sake of querying the desired value, and will not affect the device.
	 * @return The amount of energy that was actually used by this device.
	 */
	@Override
	public float consumeEnergy(float desiredAmount, boolean simulate) {
		float realAmount = desiredAmount / 2f;
		if (realAmount > MAX_CONVERSION_RATE_LUXEN) {
			realAmount = MAX_CONVERSION_RATE_LUXEN;
		}
		return consumerHelper.stash(realAmount);
	}
	
	/**
	 * For informational purposes, this returns the absolute maximum amount of power that this consumer could theoretically ever consume on a single tick.
	 *
	 * @return The absolute maximum amount of power that this consumer could theoretically ever consume on a single tick.
	 */
	@Override
	public float getMaximumDrawnAmountForDisplay() {
		return ILightEnergyStorage.redstoneFluxToLuxen(rfExtractedLastTick);
	}
	
	@Override
	public boolean hadTooLittlePowerLastForDisplay() {
		return false;
	}
}
