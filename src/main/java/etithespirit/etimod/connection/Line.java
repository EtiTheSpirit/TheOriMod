package etithespirit.etimod.connection;

import etithespirit.etimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.etimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.etimod.util.collection.CachedImmutableSetWrapper;
import etithespirit.etimod.util.collection.IReadOnlyList;
import etithespirit.etimod.util.profiling.UniProfiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * A segment of connectable blocks each with at most two neighbors, resulting in a line with no forks or breaks.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class Line {
	
	/** The {@link Assembly} that contains this {@link Line}. */
	public final Assembly parent;
	
	/** The {@link AssemblyHelper} that constructs all the recursive pathways and figures out what's connected. */
	private final AssemblyHelper helper;
	
	/** Every conduit that makes up this line. */
	private final CachedImmutableSetWrapper<AbstractLightEnergyLink> segments = new CachedImmutableSetWrapper<>();
	
	/** The other lines in the assembly that are connected to this line. */
	private final CachedImmutableSetWrapper<Line> connectedTo = new CachedImmutableSetWrapper<>();
	
	private AxisAlignedBB bounds = null;
	
	public Line(Assembly parent, AssemblyHelper helper) {
		this.parent = parent;
		this.helper = helper;
	}
	
	/**
	 * Given a {@link AbstractLightEnergyLink} that was modified to have more than two neighbor connections, this will split
	 * the line that the instance was on into three or more lines that are all reflective of independent paths.
	 * @param multiConnectedConduit The conduit that has a branch of three or more possible directions.
	 * @throws IllegalArgumentException If the given conduit does not have more than two connections associated with it.
	 */
	
	public void spliceAndRebranch(AbstractLightEnergyLink multiConnectedConduit) {
		// Known fact, this line will remain in-tact.
		// The only change is that the new segment will be appended.
		// Now the thing is, that new segment could have its own segments and potentially even be part of another assembly.
		
	}
	
	/**
	 * Returns the center of this line via its bounding box.
	 * @return The center of this line via its bounding box.
	 */
	public Vector3d getCenter() {
		return getBounds().getCenter();
	}
	
	public AxisAlignedBB getBounds() {
		if (bounds == null) {
			Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
			Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
			for (AbstractLightEnergyLink conduit : segments) {
				BlockPos pos = conduit.getBlockPos();
				if (pos.getX() < min.x) {
					min = new Vector3d(pos.getX(), min.y, min.z);
				}
				if (pos.getY() < min.y) {
					min = new Vector3d(min.x, pos.getY(), min.z);
				}
				if (pos.getZ() < min.z) {
					min = new Vector3d(min.x, min.y, pos.getZ());
				}
				
				if (pos.getX() > max.x) {
					max = new Vector3d(pos.getX(), max.y, max.z);
				}
				if (pos.getY() > max.y) {
					max = new Vector3d(max.x, pos.getY(), max.z);
				}
				if (pos.getZ() > max.z) {
					max = new Vector3d(max.x, max.y, pos.getZ());
				}
			}
			bounds = new AxisAlignedBB(min, max.add(1, 1, 1)); // Add 1 because it's a BlockPos
		}
		return bounds;
	}
	
	/**
	 * @return Every segment in this line in order such that index 0 will return the start of the line and index [length-1] will return the end.
	 */
	public IReadOnlyList<AbstractLightEnergyLink> getSegments() {
		return segments.asReadOnly();
	}
	
	/**
	 * Get all lines of the given {@link Assembly}.
	 * @return The lines constructed from the connections and their branches, or null if the given {@link AbstractLightEnergyHub} is not in a world.
	 */
	public static List<Line> constructFrom(Assembly parent, AssemblyHelper helper) {
		UniProfiler.push(Line.class, "constructFrom", "populate");
		
		List<AbstractLightEnergyLink> alreadyCovered = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		
		for (AbstractLightEnergyLink conduit : helper.getLinks()) {
			if (alreadyCovered.contains(conduit)) continue;
			Line currentLine = new Line(parent, helper);
			lines.add(currentLine);
			populate(lines, currentLine, alreadyCovered, null, conduit, parent, helper);
		}
		
		UniProfiler.pop();
		
		return lines;
	}
	
	private static void populate(List<Line> allLines, Line currentLine, List<AbstractLightEnergyLink> alreadyCovered, AbstractLightEnergyLink previous, AbstractLightEnergyLink around, Assembly parent, AssemblyHelper helper) {
		currentLine.segments.add(around);
		if (!alreadyCovered.contains(around)) alreadyCovered.add(around);
		
		BlockPos[] neighborBlockPos = ConnectionHelper.getDirectionsWithMutualConnections(parent.world, around.getBlockPos(), false);
		ArrayList<AbstractLightEnergyLink> neighbors = new ArrayList<>();
		for (BlockPos pos : neighborBlockPos) {
			TileEntity neighborTE = parent.world.getBlockEntity(pos);
			if (neighborTE instanceof AbstractLightEnergyLink) {
				AbstractLightEnergyLink link = (AbstractLightEnergyLink)neighborTE;
				if (!alreadyCovered.contains(link)) {
					neighbors.add(link);
				}
			}
		}
		
		if (neighbors.size() <= 2 && neighbors.size() > 0) {
			if (neighbors.size() == 1) {
				// 1 neighbor, only possible at start since the anchor (root) doesn't count as an instance of conduit
				if (alreadyCovered.contains(neighbors.get(0))) {
					// allLines.add(currentLine);
					return;
				}
				AbstractLightEnergyLink neighbor = neighbors.get(0);
				populate(allLines, currentLine, alreadyCovered, neighbor, neighbor, parent, helper);
			} else {
				// 2 neighbors, mid or end
				AbstractLightEnergyLink target = neighbors.get(0);
				if (previous == target || alreadyCovered.contains(target)) {
					target = neighbors.get(1);
				}
				
				if (alreadyCovered.contains(target)) {
					// allLines.add(currentLine);
					return;
				}
				
				populate(allLines, currentLine, alreadyCovered, target, target, parent, helper);
			}
		} else {
			if (neighbors.size() != 0) {
				// more than 2, this is a joint. 0 neighbors means to just do nothing due to adding the directly registered conduit up above.
				for (AbstractLightEnergyLink neighbor : neighbors) {
					if (alreadyCovered.contains(neighbor)) continue;
					
					Line forBranch = new Line(parent, helper);
					allLines.add(forBranch);
					populate(allLines, forBranch, alreadyCovered, neighbor, neighbor, parent, helper);
				}
			}
		}
	}
	
}
