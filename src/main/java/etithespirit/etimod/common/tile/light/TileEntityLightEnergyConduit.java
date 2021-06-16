package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.common.tile.AbstractLightEnergyAnchor;
import etithespirit.etimod.common.tile.IWorldUpdateListener;
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
		/*
		TileEntity newTile = world.getBlockEntity(changedAt);
		
		if (newTile instanceof ILightEnergyConduit) {
			// The neighbor is a conduit.
			// We need to add it as a neighbor to all anchors connected to THIS conduit, and tell that
			// conduit that it has a new anchor.
			
			ILightEnergyConduit conduit = (ILightEnergyConduit)newTile;
			for (AbstractLightEnergyAnchor anchor : anchors) {
				
				if (!anchor.isConduitConnected(conduit)) {
					anchor.registerConnectedElement(conduit, true);
				}
				
				
			}
		}
		// Note to future self: Don't need to do anything if the tile is AbstractLightEnergyStorageTileEntity
		// because that already handles being placed / moved by a piston.
		*/
	}
	
	@Override
	public void changed(IWorld world, BlockPos at) {
		ILightEnergyConduit[] allNeighbors = getNeighboringConduits(false);
		for (AbstractLightEnergyAnchor anchor : anchors) {
			for (ILightEnergyConduit neighbor : allNeighbors) {
				if (anchor.isConduitPartOfAnchor(neighbor)) {
					if (!neighbor.isConnectedTo(this, true)) {
						// Disconnection occurred via a BlockState change.
						
					}
				} else {
					if (neighbor.isConnectedTo(this, true)) {
						// Connection occurred via a BlockState change.
						
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
			if (anchor.isConduitPartOfAnchor(this)) {
				LOG.warn("WARNING: Anchor at {} did not dispose of conduit at {} when repopulating neighbors!", anchor.getBlockPos(), getBlockPos());
				anchor.unregisterConnectedElement(this, false);
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
