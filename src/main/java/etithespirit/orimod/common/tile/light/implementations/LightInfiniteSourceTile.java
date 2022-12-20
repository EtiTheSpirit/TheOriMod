package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.energy.ILightEnergyGenerator;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LightInfiniteSourceTile extends LightEnergyHandlingTile implements ILightEnergyGenerator {
	public LightInfiniteSourceTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_INFINITE_SOURCE_TILE.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	public float takeGeneratedEnergy(float desiredAmount, boolean simulate) {
		return desiredAmount;
	}
	
	@Override
	public float getMaximumGeneratedAmountForDisplay() {
		return Float.POSITIVE_INFINITY;
	}
	
	@Override
	public boolean hadTooMuchDrawLastForDisplay() {
		return false;
	}
}
