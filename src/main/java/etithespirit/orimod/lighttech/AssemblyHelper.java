package etithespirit.orimod.lighttech;


import com.google.common.collect.ImmutableList;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.orimod.info.coordinate.Cardinals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A close cousin to {@link ConnectionHelper} that specializes in {@link Assembly Assemblies} rather than direct neighbor connections.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class AssemblyHelper {
	
	/** A logger used to track this helper's behavior. */
	private static final Logger LOG = LogManager.getLogger(OriMod.MODID + "::" + AssemblyHelper.class.getSimpleName());
	
	/**
	 * The maximum amount of successful neighbors that will be scanned. Failing neighbors do not count to this limit.<br/>
	 * <br/>
	 * A successful neighbor counts as a neighbor that is an instance of {@link AbstractLightEnergyLink}
	 */
	public static final int MAX_SUCCESSFUL_TILE_COUNT = 4096;
	
	/** The assembly associated with this instance (a collection of conduits and instances of {@link AbstractLightEnergyHub}) */
	private final Assembly assembly;
	
	/** Every connected instance of {@link AbstractLightEnergyLink}. This is ordered in the pattern that recursion follows. */
	private final List<AbstractLightEnergyLink> connectedLinks = new ArrayList<>();
	
	/** Every {@link AbstractLightEnergyHub} that is connected to this by any number of {@link AbstractLightEnergyLink}s (if any) */
	private final List<AbstractLightEnergyHub> connectedHubs = new ArrayList<>();
	
	/** Block positions that have already been scanned and should be skipped. */
	private final List<BlockPos> skipPos = new ArrayList<>();
	
	/** Used for limiting the size of assemblies in conjunction with {@link #MAX_SUCCESSFUL_TILE_COUNT}. */
	private int numTestedSuccessfully = 0;
	
	/**
	 * Creates a new helper for the given assembly and the first hub that is responsible for the creation of that assembly.
	 * @param forAsm The assembly to create for.
	 * @param initialHub The hub that this assembly was created by.
	 */
	public AssemblyHelper(Assembly forAsm, AbstractLightEnergyHub initialHub) {
		assembly = forAsm;
		boolean _shouldEnd = AssemblyCodeProfiler.tryProfileBeginAndPush("recurse");
		recurse(initialHub.getBlockPos(), true);
		AssemblyCodeProfiler.popAndEndIfNeeded(_shouldEnd);
	}
	
	/**
	 * Merges {@code other} into this instance of {@link AssemblyHelper}.
	 * @param other The other helper to merge into this.
	 */
	public void mergeWith(AssemblyHelper other) {
		connectedLinks.addAll(other.connectedLinks);
		connectedHubs.addAll(other.connectedHubs);
		skipPos.addAll(other.skipPos);
	}
	
	/** @return All hubs that are part of the assembly this works alongside. */
	public List<AbstractLightEnergyHub> getHubs() {
		return ImmutableList.copyOf(connectedHubs);
	}
	
	/** @return All links that are part of the assembly this works alongside. */
	public List<AbstractLightEnergyLink> getLinks() {
		return ImmutableList.copyOf(connectedLinks);
	}
	
	/**
	 * <strong>Note: This method may be expensive to call.</strong><br/>
	 * Forcefully recalculate all connected instances of {@link AbstractLightEnergyLink} and {@link AbstractLightEnergyHub}.
	 */
	public void forceRecalculateConnections() {
		boolean _shouldEnd = AssemblyCodeProfiler.tryProfileBeginAndPush("forceRecalculateConnections");
		
		AbstractLightEnergyHub mainTile = assembly.getCore(); // Get this beforehand as getCore() looks at connectedHubs.
		connectedHubs.clear();
		connectedLinks.clear();
		skipPos.clear();
		
		AssemblyCodeProfiler.push("recurse");
		recurse(mainTile.getBlockPos(), true);
		AssemblyCodeProfiler.pop();
		
		AssemblyCodeProfiler.popAndEndIfNeeded(_shouldEnd);
	}
	
	/**
	 * Manually adds the given {@link AbstractLightEnergyLink}.
	 * @param link The link to add.
	 * @throws IllegalArgumentException If the link is already registered.
	 */
	public void manuallyRegisterLink(AbstractLightEnergyLink link) {
		connectedLinks.add(link);
	}
	
	/**
	 * Manually removes the given {@link AbstractLightEnergyLink}.
	 * @param link The link to remove.
	 * @throws IllegalArgumentException If the link is not registered.
	 */
	public void manuallyUnregisterLink(AbstractLightEnergyLink link) {
		connectedLinks.remove(link);
	}
	
	/**
	 * Manually adds the given {@link AbstractLightEnergyHub}.
	 * @param hub The hub to add.
	 * @throws IllegalArgumentException If the hub is already registered.
	 */
	public void manuallyRegisterHub(AbstractLightEnergyHub hub) {
		connectedHubs.add(hub);
	}
	
	/**
	 * Manually removes the given {@link AbstractLightEnergyHub}.
	 * @param hub The hub to remove.
	 * @throws IllegalArgumentException If the hub is not registered.
	 */
	public void manuallyUnregisterHub(AbstractLightEnergyHub hub) {
		connectedHubs.remove(hub);
	}
	
	/**
	 * Recurses through all of the neighbors of the given origin block to find connections.
	 * @param origin The block with the neighbors surrounding it.
	 * @param isDepth1 Whether or not this is the first level of recursion, which decides whether or not to anticipate connections as well as whether or not to push a profiler stack.
	 * @return True if <strong>all</strong> recursion should continue, false if it should not.
	 */
	private boolean recurse(BlockPos origin, boolean isDepth1) {
		BlockPos[] neighbors = getAllNeighbors(origin);
		for (int idx = 0; idx < neighbors.length; idx++) {
			BlockPos validNeighbor = neighbors[idx];
			if (validNeighbor == null) continue;
			
			if (ConnectionHelper.hasMutualConnectionToOther(assembly.world, origin, validNeighbor, isDepth1)) {
				BlockEntity tile = assembly.world.getBlockEntity(validNeighbor);
				if (tile instanceof AbstractLightEnergyLink) {
					AbstractLightEnergyLink link = (AbstractLightEnergyLink) tile;
					if (!connectedLinks.contains(link)) {
						connectedLinks.add(link);
						numTestedSuccessfully++;
					} else {
						neighbors[idx] = null; // ahhnn yes lööp (If this is not removed, we will introduce an infinite loop)
						validNeighbor = null;
					}
				} else if (tile instanceof AbstractLightEnergyHub) {
					AbstractLightEnergyHub hub = (AbstractLightEnergyHub) tile;
					if (!connectedHubs.contains(hub)) {
						connectedHubs.add(hub);
						numTestedSuccessfully++;
					} else {
						neighbors[idx] = null; // Loop, same case as above.
						validNeighbor = null;
					}
				} else {
					neighbors[idx] = null; // This is an unrecognized TE. Ignore it.
					validNeighbor = null;
				}
			} else {
				neighbors[idx] = null;
				validNeighbor = null;
				// This neighbor is not valid because it is not actually connected to this TE.
				// Skip it.
			}
			
			if (numTestedSuccessfully >= MAX_SUCCESSFUL_TILE_COUNT) {
				// Check if we've added too many conduits to this chain.
				return false;
			}
			
			if (validNeighbor != null) {
				// Assuming some sanity check up above hasn't invalidated the neighbor, recurse through it too.
				if (!recurse(validNeighbor, false)) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns all six neighbors around the given BlockPos.<br/>
	 * <strong>SOME INDICES MAY BE NULL.</strong> Any instances of {@link BlockPos} that have already been tested or are not {@link AbstractLightEnergyLink} instances will be skipped.
	 * @param around The block to get all adjacent blocks to.
	 * @return An array of six {@link BlockPos} instances that are situated around the given position.
	 */
	private BlockPos[] getAllNeighbors(BlockPos around) {
		BlockPos[] validAdjacent = new BlockPos[6];
		if (!skipPos.contains(around)) skipPos.add(around); // Adding the local block is sufficient.
		
		for (int idx = 0; idx < Cardinals.ADJACENTS_IN_ORDER.length; idx++) {
			Vec3i adj = Cardinals.ADJACENTS_IN_ORDER[idx];
			
			BlockPos toTest = around.offset(adj);
			if (skipPos.contains(toTest)) {
				validAdjacent[idx] = null;
				continue;
			}
			
			BlockState state = assembly.world.getBlockState(toTest);
			if (!state.hasBlockEntity()) {
				validAdjacent[idx] = null;
				skipPos.add(toTest);
				continue;
			}
			
			// For the sake of compatibility, look for a block, not a TE. The block should be ConnectableLightTechBlock
			// because this allows us to pick up anything.
			if (state.getBlock() instanceof ConnectableLightTechBlock) {
				validAdjacent[idx] = toTest;
			} else {
				validAdjacent[idx] = null;
			}
		}
		return validAdjacent;
	}
	
	/**
	 * Unregisters all data from this helper, intended for use when the assembly object is being destroyed.
	 */
	public void dispose() {
		connectedLinks.clear();
		connectedHubs.clear();
		skipPos.clear();
	}
	
}
