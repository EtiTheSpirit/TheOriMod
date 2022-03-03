package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.common.block.light.LightDebuggerBlock;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityLightEnergyDebugger extends AbstractLightEnergyHub {
	
	public TileEntityLightEnergyDebugger(BlockPos at, BlockState state) {
		super(
			TileEntityRegistry.LIGHT_DEBUGGER.get(),
			at,
			state,
			new PersistentLightEnergyStorage(
				null,
				Double.POSITIVE_INFINITY,
				5,
				5,
				false,
				1000000
			)
		);
	}
	
	/**
	 * Returns true if this debugger takes energy, false if it gives energy.
	 * @return true if this debugger takes energy, false if it gives energy.
	 */
	public boolean isTakingEnergy() {
		if (this.hasLevel()) {
			Level world = this.getLevel();
			BlockState associatedBlock = world.getBlockState(getBlockPos());
			if (associatedBlock.getBlock() instanceof LightDebuggerBlock) {
				return associatedBlock.getValue(LightDebuggerBlock.IS_SINK);
			}
		}
		return false;
	}
	
	@Override
	public boolean canReceiveLight() {
		return isTakingEnergy();
	}
	
	@Override
	public boolean canExtractLight() {
		return !isTakingEnergy();
	}
	
}
