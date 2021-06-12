package etithespirit.etimod.routine;

import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.info.coordinate.Cardinals;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorldReader;

import java.util.ArrayList;

/**
 * A utility class designed to go through all connections in a circuit and find things that are connected.
 */
public final class LightConduitRecursor {
	
	/** The world this will execute in. */
	public final IWorldReader world;
	
	/** The origin of the block to start recursion at. */
	public final BlockPos origin;
	
	/** The amount of elements to find. If the amount of tested elements exceeds this value, recursion will forcefully terminate. */
	public final int elementsToFind;
	
	/** The amount of blocks currently tested. */
	private int amountSuccessfullyTested = 0;
	
	/** Whether or not this recursor has already executed. */
	private boolean executed = false;
	
	/** Every block that has been tested, to prevent duplicates. */
	private final ArrayList<BlockPos> tested;
	
	/**
	 * The positions of the blocks that were found. Note that there will be occasional null entries, this is used to break lines apart
	 * in debug rendering.
	 */
	public final ArrayList<BlockPos> results = new ArrayList<>();
	
	public LightConduitRecursor(IWorldReader world, BlockPos startLocation, int desiredFoundElements) {
		this.world = world;
		this.origin = startLocation;
		this.elementsToFind = desiredFoundElements;
		this.tested = new ArrayList<>(desiredFoundElements);
	}
	
	/**
	 * Checks if the world is loaded around the origin of the test point by sqrt({@link #elementsToFind}). Naturally this doesn't cover
	 * all cases, and balances between sensible and at least having <em>some</em> form of a check.
	 * @return Whether or not the world is reasonably loaded around the given position.
	 */
	public boolean canProbablyExecute() {
		return world.isAreaLoaded(origin, (int)Math.sqrt(elementsToFind));
	}
	
	/**
	 * Begin the recursion process with the given style. Be sure to check {@link #canProbablyExecute()} first.
	 */
	public void execute() {
		if (executed) return;
		executeSequential(origin, getAllNeighbors(origin), true);
		executed = true;
	}
	
	/**
	 * Performs sequential recursion. Returns whether or not recursion should continue (if false, it should abort).
	 * @param neighbors An array of all six neighbor positions in world space. Entries may be null to represent skipped entries.
	 * @return True if recursion can continue, false if it can not.
	 */
	
	// TODO: Add a way that not only goes through all recursive connections, but something that
	// TODO: enables these connections to each receive neighbor updates so that I can update on demand.
	
	// TODO: Is there a way to listen to block updates in the entire world? Is that even a good idea?
	// TODO: How do I store listeners to block updates in any capacity?
	private boolean executeSequential(BlockPos at, BlockPos[] neighbors, boolean isFirst) {
		BlockState current = world.getBlockState(at);
		for (int idx = 0; idx < neighbors.length; idx++) {
			BlockPos neighbor = neighbors[idx];
			if (neighbor == null) continue;
			BlockState state = world.getBlockState(neighbor);
			
			boolean keep = false;
			if (ConnectableLightTechBlock.isInstance(state) && ConnectableLightTechBlock.isInstance(current)) {
				// They are connectable.
				// Test if they are actually connected now.
				boolean isConnected = ConnectableLightTechBlock.isConnected(world, at, neighbor);
				if (isFirst) {
					if (isConnected || ConnectableLightTechBlock.willConnect(world, at, neighbor)) {
						results.add(neighbor);
						keep = true;
						amountSuccessfullyTested++;
					}
				} else {
					if (isConnected) {
						results.add(neighbor);
						keep = true;
						amountSuccessfullyTested++;
					}
				}
			}
			tested.add(neighbor);
			if (!keep) neighbors[idx] = null;
			// If the neighbor isn't valid, remove it from the neighbor array for recursion so that
			// it trims away garbage data.
			
			if (amountSuccessfullyTested >= elementsToFind) return false;
		}
		
		for (BlockPos neighbor : neighbors) {
			if (neighbor == null) continue;
			if (!executeSequential(neighbor, getAllNeighbors(neighbor), false)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns all six neighbors around the given BlockPos.<br/>
	 * <strong>SOME INDICES MAY BE NULL.</strong> Any instances of {@link BlockPos} that have already been tested will be skipped.
	 * @param around The block to get all adjacent blocks to.
	 * @return An array of six {@link BlockPos} instances that are situated around the given position.
	 */
	private BlockPos[] getAllNeighbors(BlockPos around) {
		BlockPos[] states = new BlockPos[6];
		for (int idx = 0; idx < Cardinals.ADJACENTS_IN_ORDER.length; idx++) {
			Vector3i adj = Cardinals.ADJACENTS_IN_ORDER[idx];
			BlockPos toTest = around.offset(adj);
			if (tested.contains(toTest)) {
				states[idx] = null;
			} else {
				states[idx] = toTest;
			}
		}
		return states;
	}
}
