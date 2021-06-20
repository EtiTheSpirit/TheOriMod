package etithespirit.etimod.connection;

import etithespirit.etimod.client.render.debug.RenderUtil;
import etithespirit.etimod.common.tile.AbstractLightEnergyAnchor;
import etithespirit.etimod.common.tile.light.ILightEnergyConduit;
import etithespirit.etimod.util.collection.CachedImmutableSetWrapper;
import etithespirit.etimod.util.collection.IReadOnlyList;
import etithespirit.etimod.util.profiling.UniProfiler;
import javafx.scene.chart.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

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
	
	/** Every conduit that makes up this line. */
	private final CachedImmutableSetWrapper<ILightEnergyConduit> segments = new CachedImmutableSetWrapper<>();
	
	private AxisAlignedBB bounds = null;
	
	private Line(Assembly parent) {
		this.parent = parent;
	}
	
	/**
	 * Given a {@link ILightEnergyConduit} that was modified to have more than two neighbor connections, this will split
	 * the line that the instance was on into three or more lines that are all reflective of independent paths.
	 * @param multiConnectedConduit The conduit that has a branch of three or more possible directions.
	 * @throws IllegalArgumentException If the given conduit does not have more than two connections associated with it.
	 */
	@Deprecated // Not yet functional.
	public void spliceAndRebranch(ILightEnergyConduit multiConnectedConduit) {
		ILightEnergyConduit[] neighbors = multiConnectedConduit.getNeighboringConduits(true);
		if (neighbors.length <= 2) {
			throw new IllegalArgumentException("This conduit does not have more than two connections! Splitting is useless.");
		}
		
		CachedImmutableSetWrapper<Line> newNeighbors = new CachedImmutableSetWrapper<>();
		for (ILightEnergyConduit conduit : neighbors) {
			if (segments.contains(conduit)) continue; // Skip components of this line that we know about.
			
			// Now this is where things get complicated, the line must assume that there is potential for
			// a large connection being made, for instance, filling a gap and connecting to a larger line.
			
			newNeighbors.addAll(constructFrom(conduit, parent));
		}
		//return newNeighbors.asReadOnly();
		//parent.lines.remove(this);
		parent.lines.addAll(newNeighbors);
	}
	
	/**
	 * Splices this line into two pieces.
	 * @param at The {@link ILightEnergyConduit} to cut around.
	 */
	public void splice(ILightEnergyConduit at) {
	
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
			for (ILightEnergyConduit conduit : segments) {
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
	public IReadOnlyList<ILightEnergyConduit> getSegments() {
		return segments.asReadOnly();
	}
	
	/**
	 * Get all lines branching out from the given {@link AbstractLightEnergyAnchor}.
	 * @param root The {@link AbstractLightEnergyAnchor} with connections branching out of it.
	 * @return The lines constructed from the connections and their branches, or null if the given {@link AbstractLightEnergyAnchor} is not in a world.
	 */
	public static List<Line> constructFrom(AbstractLightEnergyAnchor root, Assembly parent) {
		if (!root.hasLevel()) throw new IllegalArgumentException("The given TileEntity does not have an associated world!");
		
		UniProfiler.push(Line.class, "constructFrom", "populate");
		
		List<ILightEnergyConduit> alreadyCovered = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		
		for (ILightEnergyConduit conduit : root.getConnectedConduits(true)) {
			if (alreadyCovered.contains(conduit)) continue;
			Line currentLine = new Line(parent);
			lines.add(currentLine);
			populate(lines, currentLine, alreadyCovered, null, conduit, parent);
		}
		
		UniProfiler.pop();
		
		return lines;
	}
	
	private static List<Line> constructFrom(ILightEnergyConduit conduit, Assembly parent) {
		if (!conduit.hasLevel()) throw new IllegalArgumentException("The given TileEntity does not have an associated world!");
		
		UniProfiler.push(Line.class, "constructFrom", "populateFromConduit");
		
		List<ILightEnergyConduit> alreadyCovered = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		
		for (Line line : parent.lines) {
			alreadyCovered.addAll(line.segments);
		}
		
		for (ILightEnergyConduit neighbor : conduit.getNeighboringConduits(true)) {
			if (alreadyCovered.contains(neighbor)) continue;
			Line currentLine = new Line(parent);
			lines.add(currentLine);
			populate(lines, currentLine, alreadyCovered, null, neighbor, parent);
		}
		
		UniProfiler.pop();
		
		return lines;
	}
	
	private static void populate(List<Line> allLines, Line currentLine, List<ILightEnergyConduit> alreadyCovered, ILightEnergyConduit previous, ILightEnergyConduit around, Assembly parent) {
		currentLine.segments.add(around);
		if (!alreadyCovered.contains(around)) alreadyCovered.add(around);
		
		ILightEnergyConduit[] neighbors = around.getNeighboringConduits(true);
		if (neighbors.length <= 2 && neighbors.length > 0) {
			if (neighbors.length == 1) {
				// 1 neighbor, only possible at start since the anchor (root) doesn't count as an instance of conduit
				if (alreadyCovered.contains(neighbors[0])) {
					// allLines.add(currentLine);
					return;
				}
				populate(allLines, currentLine, alreadyCovered, neighbors[0], neighbors[0], parent);
			} else {
				// 2 neighbors, mid or end
				ILightEnergyConduit target = neighbors[0];
				if (previous == target || alreadyCovered.contains(target)) {
					target = neighbors[1];
				}
				
				if (alreadyCovered.contains(target)) {
					// allLines.add(currentLine);
					return;
				}
				
				populate(allLines, currentLine, alreadyCovered, target, target, parent);
			}
		} else {
			if (neighbors.length != 0) {
				// more than 2, this is a joint. 0 neighbors means to just do nothing due to adding the directly registered conduit up above.
				for (int index = 0; index < neighbors.length; index++) {
					ILightEnergyConduit neighbor = neighbors[index];
					if (alreadyCovered.contains(neighbor)) continue;
					
					Line forBranch = new Line(parent);
					allLines.add(forBranch);
					populate(allLines, forBranch, alreadyCovered, neighbor, neighbor, parent);
				}
			}
		}
	}
	
}
