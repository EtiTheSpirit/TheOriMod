package etithespirit.etimod.util.blockmtl.defaultimpl;

import etithespirit.etimod.util.blockmtl.SpiritMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class DefaultImplementations {

	/**
	 * Fluid levels at or below this are considered shallow (puddle), and above is considered deep
	 */
	public static final int SHALLOW_WATER_LEVEL = 3;
	
	// Prevent instances
	private DefaultImplementations() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	@SuppressWarnings("deprecation")
	public static final SpiritMaterial getWoodMaterial(Entity entity, BlockPos on, BlockPos in, boolean isStandingIn) {
		World world = entity.getEntityWorld();
		if (isStandingIn) {
			BlockState inBlock = world.getBlockState(in);
			if (world.isRainingAt(in)) {
				return SpiritMaterial.WOOD_WET;
			}
			if (inBlock.isAir(world, in)) {
				// ^ As per forge standards, the one with params (world, in) should be used.
				// Nothing is on top of this block.
				return SpiritMaterial.WOOD_DRY;
			} else {
				if (inBlock.getBlock() == Blocks.VINE) {
					return SpiritMaterial.WOOD_MOSSY;
				}
				if (inBlock.getBlock() == Blocks.SNOW) {
					return SpiritMaterial.WOOD_SNOWY;
				}
				if (inBlock.getFluidState() != null) {
					// We are on a fluid.
					int level = inBlock.getFluidState().getLevel();
					if (level > 0 && level <= 2) {
						// New: Test if level is >0 as well, because waterlogged blocks DO have a FluidState, just with a 0 level.
						return SpiritMaterial.WOOD_WET;
					}
				}
			}
		}
		return SpiritMaterial.WOOD_DRY;
	}
	
	@SuppressWarnings("deprecation")
	public static final SpiritMaterial getWaterMaterial(Entity entity, BlockPos on, BlockPos in, boolean isStandingIn) {
		World world = entity.getEntityWorld();
		if (isStandingIn) {
			BlockState inBlock = world.getBlockState(in);
			if (!inBlock.isAir(world, in)) {
				if (inBlock.getFluidState() != null) {
					int level = inBlock.getFluidState().getLevel();
					if (level <= SHALLOW_WATER_LEVEL && level > 0) {
						// We are on a fluid, but this fluid is super shallow. Return the wet state.
						// Check for a level greater than zero to not play wet sounds on waterlogged blocks.
						return SpiritMaterial.WATER_SHALLOW;
					}
				}
				return SpiritMaterial.WATER_DEEP;
			}
		}
		return SpiritMaterial.WATER_SHALLOW;
	}
	
}
