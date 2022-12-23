package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.common.tile.light.helpers.EnergyReservoir;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LightToRedstoneSignalTile extends LightEnergyHandlingTile implements IServerUpdatingTile, ILightEnergyConsumer {
	
	public static final float CONSUMPTION_RATE = ILightEnergyStorage.LUM_UNIT_SIZE * 4;
	
	private boolean isPoweredThisTick = false;
	private final EnergyReservoir consumerHelper = new EnergyReservoir(CONSUMPTION_RATE);
	
	public LightToRedstoneSignalTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_TO_REDSTONE_SIGNAL_TILE.get(), pWorldPosition, pBlockState);
	}
	
	// Do not handle the update here. Instead, do it in the server tick, because at the same time we need to update neighbors for redstone ticks.
	@Override
	public void updateVisualPoweredAppearance() { }
	
	@Override
	public void updateServer(Level inWorld, BlockPos at, BlockState current) {
		boolean isPoweredVisually = current.getValue(ForlornAppearanceMarshaller.POWERED);
		isPoweredThisTick = trySpendEnergyForTick();
		
		if (isPoweredVisually != isPoweredThisTick) {
			super.utilSetPoweredStateTo(isPoweredThisTick);
			inWorld.updateNeighborsAt(at, current.getBlock());
		}
	}
	
	private boolean trySpendEnergyForTick() {
		return consumerHelper.tryConsume(CONSUMPTION_RATE, false);
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
		return consumerHelper.stash(realAmount, simulate);
	}
	
	@Override
	public float getMaximumDrawnAmountForDisplay() {
		if (isPoweredThisTick) {
			return CONSUMPTION_RATE;
		}
		return 0;
	}
	
	@Override
	public boolean hadTooLittlePowerLastForDisplay() {
		return false;
	}
}
