package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityLightEnergyConduit extends AbstractLightEnergyLink {
	
	public TileEntityLightEnergyConduit(BlockPos at, BlockState state) {
		super(TileEntityRegistry.LIGHT_CONDUIT.get(), at, state);
	}
	
	@Override
	public void changed(LevelAccessor world, BlockPos at) {
	
	}
	
	@Override
	public void tellAllNeighborsAddedOrRemoved(BlockState state, Level world, BlockPos changedAt, BlockEntity replacedTile, boolean isMoving) {
		super.tellAllNeighborsAddedOrRemoved(state, world, changedAt, replacedTile, isMoving);
	}
}
