package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.common.tile.IWorldUpdateListener;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.connection.ConnectionHelper;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import etithespirit.etimod.connection.Line;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * A counterpart to {@link AbstractLightEnergyHub} which represents a {@link TileEntity} that links two
 * or more {@link AbstractLightEnergyHub}s together. These form the fundamental components of a {@link Line}.
 *
 * @author Eti
 */
public abstract class AbstractLightEnergyLink extends TileEntity implements IWorldUpdateListener {
	
	public AbstractLightEnergyLink(TileEntityType<?> type) { super(type); }
	
	/** The {@link Assembly} that this is a part of. */
	protected Assembly assembly = null;
	
	/**
	 * @return The {@link Assembly} that this {@link AbstractLightEnergyLink} is a part of.
	 */
	public Assembly getAssembly() {
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
	public Line getAssemblyLine() {
		if (assembly == null) return null;
		return assembly.getLineContaining(this);
	}
	
	@Override
	public void neighborAddedOrRemoved(BlockState state, World world, BlockPos at, BlockPos changedAt, TileEntity replacedTile, boolean isMoving) {
		TileEntity newTile = world.getBlockEntity(changedAt);
		if (newTile instanceof AbstractLightEnergyLink) {
			if (ConnectionHelper.hasMutualConnectionToOther(world, at, changedAt, true)) {
				AbstractLightEnergyLink link = (AbstractLightEnergyLink) newTile;
				Line line = link.getAssemblyLine();
				if (line == null) return;
				
				if (ConnectionHelper.getDirectionsWithMutualConnections(world, at, true).length > 2) {
					line.spliceAndRebranch(this);
				}
			}
		} else if (replacedTile instanceof AbstractLightEnergyLink) {
			AbstractLightEnergyLink link = (AbstractLightEnergyLink)replacedTile;
			
		}
		// Don't care about anything else.
	}
	
	@Override
	public void changed(IWorld world, BlockPos at) {
	
	}
	
	@Override
	public void setRemoved() {
		super.setRemoved();
	}
}
