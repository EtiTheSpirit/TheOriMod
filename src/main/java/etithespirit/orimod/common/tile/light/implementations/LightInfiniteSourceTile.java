package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LightInfiniteSourceTile extends LightEnergyStorageTile implements LightEnergyStorageTile.ILuxenGenerator {
	public LightInfiniteSourceTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_INFINITE_SOURCE_TILE.get(), pWorldPosition, pBlockState,
		      new PersistentLightEnergyStorage(
			      null,
			      Float.POSITIVE_INFINITY,
			      Float.POSITIVE_INFINITY,
			      Float.POSITIVE_INFINITY,
			      Float.POSITIVE_INFINITY
		      )
		);
	}
	
	@Override
	public float receiveLight(float maxReceive, boolean simulate) {
		return 0;
	}
	
	@Override
	public float extractLightFrom(float maxExtract, boolean simulate) {
		return maxExtract;
	}
	
	@Override
	public float getLightStored() {
		return Float.POSITIVE_INFINITY;
	}
	
	@Override
	public float getMaxLightStored() {
		return Float.POSITIVE_INFINITY;
	}
	
	@Override
	public boolean canReceiveLight() {
		return false;
	}
	
	@Override
	public boolean canExtractLightFrom() {
		return true;
	}
	
	@Override
	public float getLuxGeneratedPerTick() {
		return Float.POSITIVE_INFINITY;
	}
}
