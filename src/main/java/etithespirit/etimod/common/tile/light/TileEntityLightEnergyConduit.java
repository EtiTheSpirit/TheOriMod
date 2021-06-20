package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.common.tile.AbstractLightEnergyAnchor;
import etithespirit.etimod.common.tile.IWorldUpdateListener;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.registry.TileEntityRegistry;
import etithespirit.etimod.util.collection.CachedImmutableSetWrapper;
import etithespirit.etimod.util.collection.IReadOnlyList;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A {@link TileEntity} associated with {@link LightConduitBlock} that is able to store a reference to all connected storage points.
 */
public class TileEntityLightEnergyConduit extends TileEntity implements IWorldUpdateListener, ILightEnergyConduit {
	
	private static final Logger LOG = LogManager.getLogger(EtiMod.MODID + "::" + TileEntityLightEnergyConduit.class.getSimpleName());
	
	private final CachedImmutableSetWrapper<AbstractLightEnergyAnchor> anchors = new CachedImmutableSetWrapper<>(true);
	
	public TileEntityLightEnergyConduit() {
		super(TileEntityRegistry.LIGHT_CONDUIT.get());
	}
	
	@Override
	public void neighborAddedOrRemoved(BlockState state, World world, BlockPos at, BlockPos changedAt, TileEntity replacedTile, boolean isMoving) {
	
	}
	
	@Override
	public void changed(IWorld world, BlockPos at) {
		ILightEnergyConduit[] allNeighbors = getNeighboringConduits(false);
		ILightEnergyConduit[] connectedNeighbors = getNeighboringConduits(true);
		for (AbstractLightEnergyAnchor anchor : anchors) {
			anchor.getConnectedConduits(true);
			
			for (ILightEnergyConduit neighbor : allNeighbors) {
				if (anchor.isConduitPartOfAnchor(neighbor, true)) {
					if (!neighbor.isConnectedTo(this, true)) {
						// Neighbor disconnection occurred via a BlockState change.
						Assembly asm = getAssembly();
						if (asm != null) {
							LOG.trace("A neighbor ILightEnergyConduit connected to an ILightEnergyConduit instance with an assembly.");
							asm.handleRemoval(neighbor);
						}
					}
				} else {
					if (neighbor.isConnectedTo(this, true)) {
						// Neighbor connection occurred via a BlockState change.
						Assembly asm = getAssembly();
						if (asm != null) {
							LOG.trace("A neighbor ILightEnergyConduit disconnected to an ILightEnergyConduit instance with an assembly.");
							asm.handleAddition(neighbor);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void setRemoved() {
		TileEntity replacement = level.getBlockEntity(worldPosition);
		this.tellAllNeighborsAddedOrRemoved(getBlockState(), level, worldPosition, replacement, false);
		
		// Clean up the stored data. Unregister it from all storage points, then clear the known storage points.
		// Don't be the "that guy" that causes a memory leak.
		
		// UPDATE: Anchors now recognize a hierarchy change and demand a rebranch so this should strictly be used to find stragglers
		for (AbstractLightEnergyAnchor anchor : anchors) {
			// Don't unregister the anchor here unless you love ConcurrentModificationExceptions
			if (anchor.isConduitPartOfAnchor(this, true)) {
				LOG.warn("WARNING: Anchor at {} did not dispose of conduit at {} when repopulating neighbors!", anchor.getBlockPos(), getBlockPos());
				anchor.unregisterConnectedElement(this);
			}
		}
		anchors.clear();
		
		super.setRemoved();
	}
	
	@Override
	public void registerAnchor(AbstractLightEnergyAnchor anchor) throws IllegalArgumentException {
		anchors.add(anchor);
	}
	
	@Override
	public void unregisterAnchor(AbstractLightEnergyAnchor anchor) throws IllegalArgumentException {
		anchors.remove(anchor);
	}
	
	@Override
	public boolean connectedToAnchor(AbstractLightEnergyAnchor anchor) {
		return anchors.contains(anchor);
	}
	
	@Override
	public IReadOnlyList<AbstractLightEnergyAnchor> getAnchors() {
		return anchors.asReadOnly();
	}
	
	@Override
	public Assembly getAssembly() {
		if (anchors.size() == 0) {
			return null;
		}
		return Assembly.getAssemblyFor(anchors.get(0));
	}
	
	@Override
	public boolean isEnergized() {
		for (AbstractLightEnergyAnchor anchor : anchors) {
			if (anchor.canExtractLight() && anchor.getLightStored() > 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void refresh() {
		if (!hasLevel()) return;
		BlockState currentState = level.getBlockState(getBlockPos());
		if (!ConnectableLightTechBlock.isInstance(currentState)) return;
		
		boolean energized = isEnergized();
		if (currentState.getValue(ConnectableLightTechBlock.ENERGIZED) != energized) {
			level.setBlockAndUpdate(getBlockPos(), currentState.setValue(ConnectableLightTechBlock.ENERGIZED, energized));
		}
	}
}
