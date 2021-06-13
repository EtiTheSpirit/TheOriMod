package etithespirit.etimod.common.tile.light;

import com.google.common.collect.ImmutableSet;
import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.common.tile.AbstractLightEnergyStorageTileEntity;
import etithespirit.etimod.common.tile.ILightEnergyConduit;
import etithespirit.etimod.common.tile.IWorldUpdateListener;
import etithespirit.etimod.registry.TileEntityRegistry;
import etithespirit.etimod.util.collection.CachedImmutableSetProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A {@link TileEntity} associated with {@link LightConduitBlock} that is able to store a reference to all connected storage points.
 */
public class TileEntityLightEnergyConduit extends TileEntity implements IWorldUpdateListener, ILightEnergyConduit {
	
	private final CachedImmutableSetProvider<AbstractLightEnergyStorageTileEntity> anchors = new CachedImmutableSetProvider<>(true);
	
	public TileEntityLightEnergyConduit() {
		super(TileEntityRegistry.LIGHT_CONDUIT.get());
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos at, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
		TileEntity tile = world.getBlockEntity(changedAt);
		
		if (tile instanceof ILightEnergyConduit) {
			// The neighbor is a conduit.
			// We need to add it as a neighbor to all anchors connected to THIS conduit, and tell that
			// conduit that it has a new anchor.
			
			ILightEnergyConduit conduit = (ILightEnergyConduit)tile;
			for (AbstractLightEnergyStorageTileEntity anchor : anchors) {
				
				if (!conduit.isAnchor(anchor)) {
					conduit.registerAnchor(anchor);
				}
				if (!anchor.isConduitConnected(conduit)) {
					anchor.registerConnectedElement(conduit);
				}
				
			}
		}
		// Note to future self: Don't need to do anything if the tile is AbstractLightEnergyStorageTileEntity
		// This is because that already handles being placed / moved by a piston.
	}
	
	@Override
	public void setRemoved() {
		super.setRemoved();
		
		// Clean up the stored data. Unregister it from all storage points, then clear the known storage points.
		// Don't be the "that guy" that causes a memory leak.
		for (AbstractLightEnergyStorageTileEntity anchor : anchors) {
			// Don't unregister the anchor here unless you love ConcurrentModificationExceptions
			anchor.unregisterConnectedElement(this);
		}
		anchors.clear();
	}
	
	@Override
	public void registerAnchor(AbstractLightEnergyStorageTileEntity anchor) throws IllegalArgumentException {
		anchors.add(anchor);
	}
	
	@Override
	public void unregisterAnchor(AbstractLightEnergyStorageTileEntity anchor) throws IllegalArgumentException {
		anchors.remove(anchor);
	}
	
	@Override
	public boolean isAnchor(AbstractLightEnergyStorageTileEntity anchor) {
		return anchors.contains(anchor);
	}
	
	@Override
	public ImmutableSet<AbstractLightEnergyStorageTileEntity> getAnchors() {
		return anchors.immutable();
	}
	
	@Override
	public boolean isEnergized() {
		for (AbstractLightEnergyStorageTileEntity anchor : anchors) {
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
