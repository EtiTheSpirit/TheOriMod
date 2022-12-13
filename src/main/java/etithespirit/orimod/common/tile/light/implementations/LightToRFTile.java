package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;

public class LightToRFTile extends LightEnergyStorageTile implements IEnergyStorage {
	
	
	public LightToRFTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_TO_RF_TILE.get(), pWorldPosition, pBlockState, new PersistentLightEnergyStorage(null, 50, 10, 5));
	}
	
	
	
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		float luxExtracted = extractLightFrom(ILightEnergyStorage.redstoneFluxToLuxen(maxExtract), simulate);
		return ILightEnergyStorage.luxenToRedstoneFlux(luxExtracted);
	}
	
	@Override
	public int getEnergyStored() {
		return ILightEnergyStorage.luxenToRedstoneFlux(getLightStored());
	}
	
	@Override
	public int getMaxEnergyStored() {
		return ILightEnergyStorage.luxenToRedstoneFlux(getMaxLightStored());
	}
	
	@Override
	public boolean canExtract() {
		return true;
	}
	
	@Override
	public boolean canReceive() {
		return false;
	}
}
