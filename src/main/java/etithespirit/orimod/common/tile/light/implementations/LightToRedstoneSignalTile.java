package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.common.tile.light.helpers.EnergyReservoir;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LightToRedstoneSignalTile extends LightEnergyHandlingTile implements IServerUpdatingTile, ILightEnergyConsumer {
	
	public static final float REDSTONE_SIGNAL_PER_LUX = ILightEnergyStorage.LUM_UNIT_SIZE;
	
	float lastAvailablePower = 0;
	int lastRSSignal = 0;
	
	public LightToRedstoneSignalTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_TO_REDSTONE_SIGNAL_TILE.get(), pWorldPosition, pBlockState);
	}
	
	// Do not handle the update here. Instead, do it in the server tick, because at the same time we need to update neighbors for redstone ticks.
	@Override
	public void updateVisualPoweredAppearance() { }
	
	@Override
	public void updateServer(Level inWorld, BlockPos at, BlockState current) {
		boolean isPoweredVisually = current.getValue(ForlornAppearanceMarshaller.POWERED);
		int currentRSSignal =  lastAvailablePower > 0 ? 15 : 0; // This is order-sensitive
		boolean isPoweredThisTick = trySpendEnergyForTick();
		if (isPoweredVisually != isPoweredThisTick || lastRSSignal != currentRSSignal) {
			lastRSSignal = currentRSSignal; // Yes, this needs to be here (to update getRedstonePowerLevel())
			utilSetPoweredStateTo(isPoweredThisTick);
			inWorld.updateNeighborsAt(at, current.getBlock());
		}
		lastRSSignal = currentRSSignal;
	}
	
	private boolean trySpendEnergyForTick() {
		boolean ok = lastAvailablePower >= REDSTONE_SIGNAL_PER_LUX;
		lastAvailablePower = 0;
		return ok;
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
		if (simulate) {
			lastAvailablePower += desiredAmount; // Do the sim check because on execute, it consumes the amount returned (always 0 here) which just resets this.
		}
		return 0;
	}
	
	public int getRedstonePowerLevel() {
		return lastRSSignal;
	}
	
	@Override
	public float getMaximumDrawnAmountForDisplay() {
		return 0;
	}
	
	@Override
	public boolean hadTooLittlePowerLastForDisplay() {
		return false;
	}
}
