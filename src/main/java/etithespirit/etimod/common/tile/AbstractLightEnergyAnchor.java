package etithespirit.etimod.common.tile;

import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.connection.ConnectionHelper;
import etithespirit.etimod.common.tile.light.ILightEnergyConduit;
import etithespirit.etimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.etimod.energy.ILightEnergyStorage;
import etithespirit.etimod.info.coordinate.Cardinals;
import etithespirit.etimod.util.collection.CachedImmutableSetWrapper;
import etithespirit.etimod.util.collection.IReadOnlyList;
import etithespirit.etimod.util.profiling.UniProfiler;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * A superclass representing all Light-based energy blocks' TEs (granted they directly handle energy numbers, so conduits don't count).<br/>
 * Alternatively referred to as an "anchor" for conduits.
 *  @author Eti
 */
@SuppressWarnings("unused")
public abstract class AbstractLightEnergyAnchor extends TileEntity implements IWorldUpdateListener, ILightEnergyStorage, ITickableTileEntity {
	
	/**
	 * The maximum amount of successful neighbors that will be scanned. Failing neighbors do not count to this limit.<br/>
	 * <br/>
	 * A successful neighbor counts as a neighbor that is an instance of {@link ILightEnergyConduit}
	 */
	public static final int MAX_SUCCESSFUL_TILE_COUNT = 16384;
	
	/**
	 * A container used to store energy.
	 */
	protected PersistentLightEnergyStorage storage;
	
	/**
	 * The assembly associated with this instance (a collection of conduits and instances of {@link AbstractLightEnergyAnchor})
	 */
	private Assembly assembly;
	
	/** Every connected instance of {@link ILightEnergyConduit}. This is ordered in the pattern that recursion follows. */
	protected final CachedImmutableSetWrapper<ILightEnergyConduit> connected = new CachedImmutableSetWrapper<>(true);
	private final CachedImmutableSetWrapper<AbstractLightEnergyAnchor> connectedAnchors = new CachedImmutableSetWrapper<>(true);
	private boolean hasPopulatedAnchorArrayFromRecursion = false;
	
	private final ArrayList<BlockPos> skipPos = new ArrayList<>();
	private boolean hasPopulatedConnectedArray = false;
	
	/** True if something has been manually added to {@link #connected} which may break the order of the connection array. */
	private boolean arrayMayBeOutOfOrder = false;
	
	/** Used for limiting the size of assemblies in conjunction with {@link #MAX_SUCCESSFUL_TILE_COUNT}. */
	private int numTestedSuccessfully = 0;
	
	public AbstractLightEnergyAnchor(TileEntityType<?> tileEntityTypeIn) {
		this(tileEntityTypeIn, null);
	}
	
	public AbstractLightEnergyAnchor(TileEntityType<?> tileEntityTypeIn, PersistentLightEnergyStorage storage) {
		super(tileEntityTypeIn);
		this.storage = storage;
	}
	
	@Override
	public void tick() {
		// TODO: Figure out how to avoid this.
		// ^ Assuming it even SHOULD be avoided.
		// Note: onLoad does not work, and setLevelAndPosition does not work.
		// Both of these cause deadlocks if I try to exec this.
		if (!hasPopulatedConnectedArray) {
			repopulateConnectedArray();
			assembly = Assembly.getAssemblyFor(this);
		}
	}
	
	@Override
	public void setRemoved() {
		TileEntity replacement = level.getBlockEntity(worldPosition);
		this.tellAllNeighborsAddedOrRemoved(getBlockState(), level, worldPosition, replacement, false);
		
		for (ILightEnergyConduit conduit : getConnectedConduits(true)) {
			if (conduit.connectedToAnchor(this)) {
				conduit.unregisterAnchor(this);
			}
		}
		for (ILightEnergyConduit conduit : getConnectedConduits(true)) {
			conduit.refresh();
		}
		assembly.tileEntityRemoved(this);
		
		super.setRemoved();
	}
	
	@Override
	public void setLevelAndPosition(World world, BlockPos pos) {
		super.setLevelAndPosition(world, pos);
		hasPopulatedConnectedArray = false;
	}
	
	@Override
	public void setPosition(BlockPos pos) {
		super.setPosition(pos);
		hasPopulatedConnectedArray = false;
	}
	
	@Override
	public void neighborAddedOrRemoved(BlockState state, World world, BlockPos at, BlockPos changedAt, TileEntity replacedTile, boolean isMoving) {
		// Since the capacitor is a storage block, it will also store all of the connected wires and other elements.
		
	}
	
	@Override
	public void changed(IWorld world, BlockPos at) {
	
	}
	
	/**
	 * Intended to be called by an instance of {@link ILightEnergyConduit}, it registers that instance as connected to this storage entity.<br/>
	 * <strong>This should be called AFTER the conduit registers this instance as an anchor, otherwise a desynchronization can occur.</strong>
	 * @param conduit The {@link ILightEnergyConduit} to register as connected.
	 * @throws IllegalArgumentException If the given {@link ILightEnergyConduit} is already connected.
	 */
	public final void registerConnectedElement(ILightEnergyConduit conduit) throws IllegalArgumentException {
		arrayMayBeOutOfOrder = true;
		connected.add(conduit);
		
		if (hasPopulatedAnchorArrayFromRecursion) {
			// Only add it to the array if we've done the big build first.
			for (AbstractLightEnergyAnchor anchor : conduit.getAnchors()) {
				if (!connectedAnchors.contains(anchor)) {
					connectedAnchors.add(anchor);
				}
			}
		}
	}
	
	/**
	 * Intended to be called by an instance of {@link ILightEnergyConduit}, it registers that instance as no longer connected to this storage entity.<br/>
	 * <strong>This should be called AFTER the conduit registers this instance as an anchor, otherwise a desynchronization can occur.</strong>
	 * @param conduit The {@link ILightEnergyConduit} to register as no longer connected.
	 * @throws IllegalArgumentException If the given {@link ILightEnergyConduit} is not connected.
	 */
	public final void unregisterConnectedElement(ILightEnergyConduit conduit) throws IllegalArgumentException {
		arrayMayBeOutOfOrder = true;
		connected.remove(conduit);
		
		if (hasPopulatedAnchorArrayFromRecursion) {
			// Only remove it from the array if we've done the big build first.
			for (AbstractLightEnergyAnchor anchor : conduit.getAnchors()) {
				if (connectedAnchors.contains(anchor)) {
					connectedAnchors.remove(anchor);
				}
			}
		}
	}
	
	/**
	 * Returns a read-only set of all connected {@link ILightEnergyConduit} instances recursively..
	 * Use {@link #registerConnectedElement(ILightEnergyConduit)} and {@link #unregisterConnectedElement(ILightEnergyConduit)}
	 * to modify this set.
	 * @param updateIfNeeded If true, the connected conduits will be acquired if necessary.
	 * @return A read-only set of the connected {@link ILightEnergyConduit} instances recursively..
	 */
	public final IReadOnlyList<ILightEnergyConduit> getConnectedConduits(boolean updateIfNeeded) {
		if (updateIfNeeded && (!hasPopulatedConnectedArray || arrayMayBeOutOfOrder)) {
			repopulateConnectedArray();
		}
		return connected.asReadOnly();
	}
	
	/**
	 * @param conduit The {@link ILightEnergyConduit} to check.
	 * @param updateIfNeeded If true, then the connected conduit list is updated if it needs an update.
	 * @return Whether or not the given {@link ILightEnergyConduit} is connected to this at the time of calling.
	 */
	public final boolean isConduitPartOfAnchor(ILightEnergyConduit conduit, boolean updateIfNeeded) {
		return connected.contains(conduit);
	}
	
	/**
	 * @return a read-only set of all connected instances of {@link AbstractLightEnergyAnchor} that are connected
	 * to this in some way via {@link ILightEnergyConduit} instances.
	 */
	public final IReadOnlyList<AbstractLightEnergyAnchor> getAllConnectedStorage() {
		if (hasPopulatedAnchorArrayFromRecursion) {
			return connectedAnchors.asReadOnly();
		}
		
		// TODO: Cluster all conduits into up to six clusters representing branches off of a given surface of this block.
		// TODO: ^ This can help because then I can just check the FIRST conduit of each branch for all anchors, and that's all I need.
		// TODO: ^ With this change, there is no need to iterate through every single conduit.
		// ^^^ For now, just cache this result. It'll help but it's not perfect.
		
		for (ILightEnergyConduit conduit : connected) {
			for (AbstractLightEnergyAnchor anchor : conduit.getAnchors()) {
				if (!connectedAnchors.contains(anchor)) {
					connectedAnchors.add(anchor);
				}
			}
		}
		
		hasPopulatedAnchorArrayFromRecursion = true;
		return connectedAnchors.asReadOnly();
	}
	
	/**
	 * @return Whether or not the array of connected neighbors (recursively) has been populated.
	 */
	public final boolean hasAcquiredConnectedNeighbors() {
		return hasPopulatedConnectedArray;
	}
	
	/**
	 * <strong>Note: This method may be expensive to call.</strong><br/>
	 * Forcefully recalculate all connected instances of {@link ILightEnergyConduit}.
	 */
	public final void repopulateConnectedArray() {
		if (!hasLevel()) throw new NullPointerException("This TileEntity does not have an associated world!");
		
		UniProfiler.push(this.getClass(), "repopulateConnectedArray", "recurse");
		
		connected.clear();
		skipPos.clear();
		recurse(getBlockPos(), getAllNeighbors(getBlockPos()), true);
		
		UniProfiler.push(this.getClass(), "repopulateConnectedArray", "registerToConduits");
		for (ILightEnergyConduit conduit : connected) {
			if (!conduit.connectedToAnchor(this)) {
				conduit.registerAnchor(this);
			}
		}
		
		UniProfiler.pop();
		hasPopulatedConnectedArray = true;
	}
	
	/**
	 * Recurses through all of the neighbors of the given origin block.
	 * @param origin The block with the neighbors surrounding it.
	 * @param neighbors The neighbors of this block, which may be null if they are invalid.
	 * @param isDepth1 Whether or not this is the first level of recursion, which decides whether or not to anticipate connections.
	 * @return True if <strong>all</strong> recursion should continue, false if it should not.
	 */
	private boolean recurse(BlockPos origin, BlockPos[] neighbors, boolean isDepth1) {
		for (int idx = 0; idx < neighbors.length; idx++) {
			BlockPos validNeighbor = neighbors[idx];
			if (validNeighbor == null) continue;
			
			BlockState state = level.getBlockState(validNeighbor);
			
			Direction dir = ConnectionHelper.fromBlockPos(origin, validNeighbor, true);
			if (ConnectionHelper.hasMutualConnectionInDirection(level, origin, dir, isDepth1)) {
				TileEntity tile = level.getBlockEntity(validNeighbor);
				if (tile instanceof ILightEnergyConduit) {
					ILightEnergyConduit conduit = (ILightEnergyConduit) tile;
					if (!connected.contains(conduit)) {
						connected.add(conduit);
						numTestedSuccessfully++;
					} else {
						neighbors[idx] = null; // ahhnn yes lööp
						validNeighbor = null;
					}
				} else {
					neighbors[idx] = null; // This is a storage point, not a conduit. Stop this one.
					validNeighbor = null;
				}
			} else {
				neighbors[idx] = null;
				validNeighbor = null;
				// This neighbor is not valid because it is not actually connected to this.
				// Skip it.
			}
			
			if (numTestedSuccessfully >= MAX_SUCCESSFUL_TILE_COUNT) {
				return false;
			}
			
			if (validNeighbor != null) {
				if (!recurse(validNeighbor, getAllNeighbors(validNeighbor), false)) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns all six neighbors around the given BlockPos.<br/>
	 * <strong>SOME INDICES MAY BE NULL.</strong> Any instances of {@link BlockPos} that have already been tested or are not {@link ILightEnergyConduit} instances will be skipped.
	 * @param around The block to get all adjacent blocks to.
	 * @return An array of six {@link BlockPos} instances that are situated around the given position.
	 */
	private BlockPos[] getAllNeighbors(BlockPos around) {
		BlockPos[] validAdjacent = new BlockPos[6];
		if (!skipPos.contains(around)) skipPos.add(around); // Adding the local block is sufficient.
		
		for (int idx = 0; idx < Cardinals.ADJACENTS_IN_ORDER.length; idx++) {
			Vector3i adj = Cardinals.ADJACENTS_IN_ORDER[idx];
			
			BlockPos toTest = around.offset(adj);
			if (skipPos.contains(toTest)) {
				validAdjacent[idx] = null;
				continue;
			}
			
			BlockState state = level.getBlockState(toTest);
			if (!state.hasTileEntity()) {
				validAdjacent[idx] = null;
				skipPos.add(toTest);
				continue;
			}
			
			// Just test the block itself, don't bother with TEs quite yet.
			if (state.getBlock() instanceof ConnectableLightTechBlock) {
				validAdjacent[idx] = toTest;
			} else {
				validAdjacent[idx] = null;
			}
		}
		return validAdjacent;
	}
	
	@Override
	public double getViewDistance() {
		return 32D;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
}
