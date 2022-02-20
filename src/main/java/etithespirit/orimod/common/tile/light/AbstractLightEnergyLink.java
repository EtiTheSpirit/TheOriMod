package etithespirit.orimod.common.tile.light;


import etithespirit.orimod.common.tile.IWorldUpdateListener;
import etithespirit.orimod.lighttech.Assembly;
import etithespirit.orimod.lighttech.ConnectionHelper;
import etithespirit.orimod.lighttech.Line;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * A counterpart to {@link AbstractLightEnergyHub} which represents a {@link BlockEntity} that links two
 * or more {@link AbstractLightEnergyHub}s together. These form the fundamental components of a {@link Line}.
 *
 * @author Eti
 */
public abstract class AbstractLightEnergyLink extends BlockEntity implements IWorldUpdateListener {
	
	/**
	 * Create a new link using the default BlockEntity ctor.
	 * @param type The block entity type.
	 * @param at The location to create it at.
	 * @param state The BlockState to create it for.
	 */
	public AbstractLightEnergyLink(BlockEntityType<?> type, BlockPos at, BlockState state) { super(type, at, state); }
	
	/** The {@link Assembly} that this is a part of. */
	protected Assembly assembly = null;
	
	/**
	 * @return The {@link Assembly} that this {@link AbstractLightEnergyLink} is a part of.
	 */
	public @Nullable
	Assembly getAssembly() {
		return assembly;
	}
	
	/**
	 * Intended for internal use only. Sets the {@link Assembly} associated with this {@link AbstractLightEnergyLink}
	 * @param assembly The assembly associated with this line.
	 */
	public void setAssembly(Assembly assembly) {
		this.assembly = assembly;
	}
	
	/**
	 * @return The specific line that this {@link AbstractLightEnergyLink} is a part of in its {@link Assembly}. If this has no assembly, this will return null;
	 */
	public @Nullable
	Line getAssemblyLine() {
		if (assembly == null) return null;
		return assembly.getLineContaining(this);
	}
	
	@Override
	public void neighborAddedOrRemoved(BlockState state, Level world, BlockPos at, BlockPos changedAt, BlockEntity replacedTile, boolean isMoving) {
		BlockEntity newTile = world.getBlockEntity(changedAt);
		if (newTile instanceof AbstractLightEnergyLink) {
			if (ConnectionHelper.hasMutualConnectionToOther(world, at, changedAt, true)) {
				AbstractLightEnergyLink link = (AbstractLightEnergyLink) newTile;
				
				Line line = link.getAssemblyLine();
				if (line != null) {
					// This may have merged two assemblies.
					if (assembly == line.parent) {
						// Nope, just interconnected an existing assembly.
						// TODO: Runtime splicing of lines in this case, because a loop will have been made.
					} else {
						// Yes it did.
						assembly.mergeWith(line.parent, link);
					}
				} else {
					// The new line isn't part of an assembly (yet).
					if (assembly != null) {
						// But this one is. Now check if this is the ONLY connection.
						// If this is, add it, but if it isn't, see if its other neighbors are part of this assembly or another.
						
					}
				}
				
				
				if (ConnectionHelper.getDirectionsWithMutualConnections(world, at, true).length > 2) {
					// line.spliceAndRebranch(this);
				}
			}
		} else if (replacedTile instanceof AbstractLightEnergyLink) {
			AbstractLightEnergyLink link = (AbstractLightEnergyLink)replacedTile;
			
		}
		// Don't care about anything else.
	}
	
}
