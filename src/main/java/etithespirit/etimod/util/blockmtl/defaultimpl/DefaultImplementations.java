package etithespirit.etimod.util.blockmtl.defaultimpl;

import javax.annotation.Nullable;

import etithespirit.etimod.util.blockmtl.SpiritMaterialModState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public final class DefaultImplementations {

	/**
	 * Fluid levels at or below this are considered shallow (puddle), and above is considered deep
	 */
	public static final int SHALLOW_WATER_LEVEL = 3;
	
	// Prevent instances
	private DefaultImplementations() { }
	
	/**
	 * The default method to determine the sound of a woody block. If your mod needs to have its own function, you can optionally call this too.
	 * @param entity
	 * @param on
	 * @param in
	 * @return
	 */
	public static final SpiritMaterialModState getModStateForWoodBlock(Entity entity, BlockPos on, @Nullable BlockPos in) {
		World world = entity.getEntityWorld();
		if (in != null) {
			BlockState inBlock = world.getBlockState(in);
			if (world.isRainingAt(in)) {
				return SpiritMaterialModState.WET;
			}
			if (inBlock.isAir(world, in)) {
				// ^ As per forge standards, the one with params (world, in) should be used.
				// Nothing is on top of this block.
				return SpiritMaterialModState.DRY;
			} else {
				if (inBlock.getBlock() == Blocks.VINE) {
					return SpiritMaterialModState.MOSSY;
				}
				if (inBlock.getBlock() == Blocks.SNOW) {
					return SpiritMaterialModState.SNOWY;
				}
				if (inBlock.getFluidState() != null) {
					// We are on a fluid.
					int level = inBlock.getFluidState().getLevel();
					if (level > 0 && level <= 2) {
						// New: Test if level is >0 as well, because waterlogged blocks DO have a FluidState, just with a 0 level.
						return SpiritMaterialModState.WET;
					}
				}
			}
		}
		return SpiritMaterialModState.DEFAULT;
	}
	
	public static final SpiritMaterialModState getModStateForWater(Entity entity, BlockPos on, @Nullable BlockPos in) {
		World world = entity.getEntityWorld();
		if (in != null) {
			BlockState inBlock = world.getBlockState(in);
			if (inBlock.isAir(world, in)) {
				// Should never happen.
				return SpiritMaterialModState.DEFAULT;
			} else {
				if (inBlock.getFluidState() != null) {
					int level = inBlock.getFluidState().getLevel();
					if (level <= SHALLOW_WATER_LEVEL && level > 0) {
						// We are on a fluid, but this fluid is super shallow. Return the wet state.
						return SpiritMaterialModState.SHALLOW;
					}
				}
				return SpiritMaterialModState.DEEP;
			}
		}
		return SpiritMaterialModState.DEFAULT;
	}
	
}
