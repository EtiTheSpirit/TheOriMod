package etithespirit.orimod.api.delegate;

import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

/**
 * A function used to acquire a SpiritMaterial.
 * @author Eti
 *
 */
@FunctionalInterface
public interface ISpiritMaterialAcquisitionFunction {
	
	/**
	 * Given an entity, the block it is above, and the block it is inside of, this is expected to return an appropriate {@link SpiritMaterial} based on an implementor-defined context.
	 * @param entity The entity that is being tested, such as a player that is walking.
	 * @param standingOnTopOf The position of the block that the entity is standing on top of. This is not guaranteed to be the block that this function was registered with.
	 * @param standingInsideOf The position of the block occupying the same position as that entity. This is not guaranteed to be the block that this function was registered with.
	 * @param isStandingIn True if this function executed because the player is standing <em>inside of</em> an associated block, false if this function executed because the player is standing <em>on top of</em> an associated block.
	 * @return An {@link SpiritMaterial} best suited for the context of the two blocks.
	 * @throws ArgumentNullException If any of the input parameters are null.
	 */
	@Nonnull
	SpiritMaterial getSpiritMaterial(@Nonnull Entity entity, @Nonnull BlockPos standingOnTopOf, @Nonnull BlockPos standingInsideOf, boolean isStandingIn) throws ArgumentNullException;
	
}
