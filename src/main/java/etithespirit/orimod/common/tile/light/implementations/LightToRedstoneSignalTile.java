package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LightToRedstoneSignalTile extends LightEnergyStorageTile implements IServerUpdatingTile, LightEnergyStorageTile.ILuxenConsumer {
	
	public static final float CONSUMPTION_RATE = ILightEnergyStorage.LUM_UNIT_SIZE * 4;
	
	private boolean wasPoweredAtLastCheck = false;
	
	public LightToRedstoneSignalTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_TO_REDSTONE_SIGNAL_TILE.get(), pWorldPosition, pBlockState, new PersistentLightEnergyStorage(null, CONSUMPTION_RATE, CONSUMPTION_RATE, 0));
	}
	
	@Override
	public void updateServer(Level inWorld, BlockPos at, BlockState current) {
		boolean isPowered = current.getValue(ForlornAppearanceMarshaller.POWERED);
		boolean spentEnergy = trySpendEnergy(CONSUMPTION_RATE, false);
		
		// NOTE: The Powered BlockState is set automatically by another updater!
		// This means all we have to do is update neighbors to reflect upon the change.
		
		if (spentEnergy) {
			// We spent the energy successfully! It should be powered.
			if (!wasPoweredAtLastCheck) {
				inWorld.updateNeighborsAt(at, current.getBlock());
			}
		} else {
			// Could not spend the energy. Update!
			if (wasPoweredAtLastCheck) {
				inWorld.updateNeighborsAt(at, current.getBlock());
			}
		}
		
		wasPoweredAtLastCheck = isPowered;
	}
	
	@Override
	public float getLuxConsumedPerTick() {
		if (wasPoweredAtLastCheck) {
			return CONSUMPTION_RATE;
		}
		return 0;
	}
	
	@Override
	public boolean isOverdrawn() {
		return getLightStored() > 0 && getLightStored() < CONSUMPTION_RATE;
	}
}
