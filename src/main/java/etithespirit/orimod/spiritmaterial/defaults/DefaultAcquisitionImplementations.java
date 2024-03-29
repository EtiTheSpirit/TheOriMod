package etithespirit.orimod.spiritmaterial.defaults;


import etithespirit.orimod.api.delegate.ISpiritMaterialAcquisitionFunction;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default implementations for sound behaviors on blocks.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class DefaultAcquisitionImplementations {
	
	/**
	 * Fluid levels at or below this are considered shallow (puddle), and above is considered deep
	 */
	public static final int SHALLOW_WATER_LEVEL = 3;
	
	// Prevent instances
	private DefaultAcquisitionImplementations() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * A default implementation of {@link ISpiritMaterialAcquisitionFunction} that returns the appropriate material for wood blocks based on the weather and nearby objects.
	 * @param entity The entity stepping on this block.
	 * @param on The block being walked on top of.
	 * @param in The block being walked inside of.
	 * @param isStandingIn Whether or not the block that was associated with a custom material is the one that is being stood inside of (if false, the block associated is being walked on top of)
	 * @return A better suited Spirit Material given the context of the given wood block.
	 */
	public static @Nullable	SpiritMaterial getWoodMaterial(Entity entity, BlockPos on, BlockPos in, boolean isStandingIn) {
		Level world = entity.getCommandSenderWorld();
		if (isStandingIn) {
			BlockState inBlock = world.getBlockState(in);
			if (world.isRainingAt(in)) {
				return SpiritMaterial.WOOD_WET;
			}
			if (inBlock.isAir()) {
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
				if (!inBlock.getFluidState().isEmpty()) {
					// We are on a fluid.
					int level = inBlock.getFluidState().getAmount();
					if (level > 0 && level <= 2) {
						// New: Test if level is >0 as well, because waterlogged blocks DO have a FluidState, just with a 0 level.
						return SpiritMaterial.WOOD_WET;
					}
				}
			}
		} else {
			if (world.isRainingAt(on.above())) {
				return SpiritMaterial.WOOD_WET;
			} else {
				//return SpiritMaterial.WOOD_DRY;
				return null; // Reason: It is possible to register both a function (this) and a static binding. The static binding must hold the default value or else it wreaks havoc on special block
				// overrides (i.e. smithing table was set to metal but never played its metallic sound because of the non-null return value here).
			}
		}
		return null;
	}
	
	/**
	 * A default implementation of {@link ISpiritMaterialAcquisitionFunction} that returns the appropriate material for water blocks based on how full the block is.
	 * @param entity The entity stepping on this block.
	 * @param on The block being walked on top of.
	 * @param in The block being walked inside of.
	 * @param isStandingIn Whether or not the block that was associated with a custom material is the one that is being stood inside of (if false, the block associated is being walked on top of)
	 * @return A better suited Spirit Material given the context of the given water block.
	 */
	public static @Nullable SpiritMaterial getWaterMaterial(Entity entity, BlockPos on, BlockPos in, boolean isStandingIn) {
		Level world = entity.getCommandSenderWorld();
		if (isStandingIn) {
			BlockState inBlock = world.getBlockState(in);
			if (!inBlock.isAir()) {
				if (!inBlock.getFluidState().isEmpty()) {
					int level = inBlock.getFluidState().getAmount();
					if (level <= SHALLOW_WATER_LEVEL && level > 0) {
						// We are on a fluid, but this fluid is super shallow. Return the wet state.
						// Check for a level greater than zero to not play wet sounds on waterlogged blocks.
						return SpiritMaterial.WATER_SHALLOW;
					}
				}
				return SpiritMaterial.WATER_DEEP;
			}
		}
		return null;
	}
	
}
