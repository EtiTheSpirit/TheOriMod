package etithespirit.orimod.common.tile.light;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Light Tile class represents any and all tile entities that handle Light energy in some way.
 *
 * Unlike previous iterations of the mod, there is no longer a (static) distinction between hubs and conduits. Any block can now switch between being a hub
 * and a conduit at any time.
 */
public abstract class AbstractLightTile extends BlockEntity {
	
	public AbstractLightTile(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
	}
	
	
	public void populateConnections() {
	
	}
	
}
