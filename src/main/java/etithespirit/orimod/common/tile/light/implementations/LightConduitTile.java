package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.tile.light.LightEnergyTile;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LightConduitTile extends LightEnergyTile {
	public LightConduitTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_ENERGY_TILE.get(), pWorldPosition, pBlockState);
	}
}
