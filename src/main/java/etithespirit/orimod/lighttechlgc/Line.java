package etithespirit.orimod.lighttechlgc;


import com.google.common.collect.ImmutableList;
import etithespirit.orimod.aos.ConnectionHelper;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.orimod.util.collection.Breaker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A segment of connectable blocks each with at most two neighbors, resulting in a line with no forks or breaks.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
@Deprecated(forRemoval = true)
public final class Line {
	
	/** The {@link Assembly} that contains this {@link Line}. */
	public final Assembly parent;
	
	/** The {@link AssemblyHelper} that constructs all the recursive pathways and figures out what's connected. */
	final AssemblyHelper helper;
	
	/** Every conduit that makes up this line. */
	private final List<AbstractLightEnergyLink> segments = new ArrayList<>();
	
	/** The other lines in the assembly that are connected to this line. */
	private final List<Line> connectedTo = new ArrayList<>();
	
	private AABB bounds = null;
	
	/**
	 * Construct a new line in the given assembly.
	 * @param parent The assembly this line is a part of.
	 */
	public Line(Assembly parent) {
		this.parent = parent;
		this.helper = parent.helper;
	}
	
	/**
	 * Given a {@link AbstractLightEnergyLink} that was modified to have more than two neighbor connections, this will split
	 * the line that the instance was on into three or more lines that are all reflective of independent paths.
	 * <strong>This automatically registers all lines.</strong>
	 * @param multiConnectedConduit The conduit that has a branch of three or more possible directions.
	 * @throws IllegalArgumentException If the given conduit does not have more than two connections associated with it.
	 */
	public Line[] spliceAndRebranch(AbstractLightEnergyLink multiConnectedConduit) {
		// Say there are two parallel lines || and they are merged into an H shape
		// This means that the cut needs to occur where the T joints were created.
		
		List<AbstractLightEnergyLink> alreadyCovered = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		List<Line> allowed = new ArrayList<>();
		
		allowed.add(this);
		AbstractLightEnergyLink[] connected = multiConnectedConduit.getNeighboringLinks(true);
		for (AbstractLightEnergyLink abstractLightEnergyLink : connected) {
			Line ofNeighbor = abstractLightEnergyLink.getAssemblyLine();
			if (ofNeighbor == null) continue;
			
			if (ofNeighbor.parent != parent) {
				// Different assembly! Need to merge assemblies.
				parent.mergeWith(ofNeighbor.parent);
			}
			if (!allowed.contains(ofNeighbor)) {
				allowed.add(ofNeighbor);
			}
		}
		
		
		Line dummyReplacesThis = new Line(parent);
		lines.add(dummyReplacesThis);
		populateAmong(lines, allowed, dummyReplacesThis, alreadyCovered, null, this.segments.stream().findFirst().get(), parent);
		
		this.segments.clear();
		this.segments.addAll(dummyReplacesThis.segments);
		List<Line> existingLines = parent.getLines();
		for (Line line : lines) {
			if (!existingLines.contains(line)) parent.addLine(line);
		}
		return lines.toArray(new Line[lines.size()]);
	}
	
	/**
	 * Breaks this line in half at the given connection. Lines[0] is this, and Lines[1] is the other line.
	 * If the block 'at' was removed, it will be neither part of the first or the second half. If it was simply changed
	 * to lose a connection, it will be a part of the line it is still connected to, unless it is connected to neither.<br/>
	 * <br/>
	 * <strong>This will automatically register the two lines into the parent assembly.</strong>
	 * @return Two lines, one or none containing the given link where appropriate.
	 * @throws IllegalStateException If the given link is still connected both ahead and behind itself
	 */
	public Line[] breakInHalf(AbstractLightEnergyLink at) throws IllegalStateException {
		AbstractLightEnergyLink previous = null;
		AbstractLightEnergyLink next = null;
		int idx = this.segments.indexOf(at);
		if (idx > 0) previous = this.segments.get(idx - 1);
		if (idx < this.segments.size() - 1) next = this.segments.get(idx + 1);
		
		boolean isPrevConnected = (previous != null && at.isConnectedTo(previous, true));
		boolean isNextConnected = (next != null && at.isConnectedTo(next, true));
		
		if (isPrevConnected && isNextConnected) {
			throw new IllegalStateException("The given connection is not actually separated, thus making an ambiguous situation! This cannot continue.");
		}
		int value = -1;
		if (isNextConnected) {
			value = 1;
		} else if (isPrevConnected) {
			value = 0;
		}
		
		List<AbstractLightEnergyLink>[] fragmented = Breaker.breakApart(this.segments, at, value);
		this.segments.clear();
		this.segments.addAll(fragmented[0]);
		Line other = new Line(parent);
		other.segments.addAll(fragmented[1]);
		
		parent.addLine(other);
		return new Line[] { this, other };
	}
	
	/**
	 * Returns the center of this line via its bounding box.
	 * @return The center of this line via its bounding box.
	 */
	public Vec3 getCenter() {
		return getBounds().getCenter();
	}
	
	/**
	 * @return The bounding box of this line.
	 */
	public AABB getBounds() {
		if (bounds == null) {
			Vec3 min = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
			Vec3 max = new Vec3(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
			for (AbstractLightEnergyLink conduit : segments) {
				BlockPos pos = conduit.getBlockPos();
				if (pos.getX() < min.x) {
					min = new Vec3(pos.getX(), min.y, min.z);
				}
				if (pos.getY() < min.y) {
					min = new Vec3(min.x, pos.getY(), min.z);
				}
				if (pos.getZ() < min.z) {
					min = new Vec3(min.x, min.y, pos.getZ());
				}
				
				if (pos.getX() > max.x) {
					max = new Vec3(pos.getX(), max.y, max.z);
				}
				if (pos.getY() > max.y) {
					max = new Vec3(max.x, pos.getY(), max.z);
				}
				if (pos.getZ() > max.z) {
					max = new Vec3(max.x, max.y, pos.getZ());
				}
			}
			bounds = new AABB(min, max.add(1, 1, 1)); // Add 1 because it's a BlockPos
		}
		return bounds;
	}
	
	/**
	 * @return Every segment in this line in order such that index 0 will return the start of the line and index [length-1] will return the end.
	 */
	public List<AbstractLightEnergyLink> getSegments() {
		return ImmutableList.copyOf(segments);
	}
	
	/**
	 * Checks if the given link is part of this line.
	 * @param link The link to check.
	 * @return True if the link is in this line, false if not.
	 */
	public boolean contains(AbstractLightEnergyLink link) {
		return segments.contains(link);
	}
	
	/**
	 * Get all lines of the given {@link Assembly}.
	 * @param parent The assembly to build from.
	 * @return The lines constructed from the connections and their branches, or null if the given {@link AbstractLightEnergyHub} is not in a world.
	 */
	public static List<Line> constructFrom(Assembly parent) {
		//UniProfiler.push(Line.class, "constructFrom", "populate");
		//boolean _shouldEnd = AssemblyCodeProfiler.tryProfileBeginAndPush("constructFrom");
		
		List<AbstractLightEnergyLink> alreadyCovered = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		
		for (AbstractLightEnergyLink conduit : parent.helper.getLinks()) {
			if (alreadyCovered.contains(conduit)) continue;
			Line currentLine = new Line(parent);
			lines.add(currentLine);
			populate(lines, currentLine, alreadyCovered, null, conduit, parent);
		}
		
		//AssemblyCodeProfiler.popAndEndIfNeeded(_shouldEnd);
		return lines;
	}
	
	private static void populate(List<Line> allLines, Line currentLine, List<AbstractLightEnergyLink> alreadyCovered, AbstractLightEnergyLink previous, AbstractLightEnergyLink around, Assembly parent) {
		currentLine.segments.add(around);
		if (!alreadyCovered.contains(around)) alreadyCovered.add(around);
		
		BlockPos[] neighborBlockPos = ConnectionHelper.getDirectionsWithMutualConnections(parent.world, around.getBlockPos(), false);
		ArrayList<AbstractLightEnergyLink> neighbors = new ArrayList<>();
		for (BlockPos pos : neighborBlockPos) {
			BlockEntity neighborTE = parent.world.getBlockEntity(pos);
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
				populate(allLines, currentLine, alreadyCovered, neighbor, neighbor, parent);
			} else {
				// 2 neighbors, this is mid or end
				AbstractLightEnergyLink target = neighbors.get(0);
				if (previous == target || alreadyCovered.contains(target)) {
					target = neighbors.get(1);
				}
				
				if (alreadyCovered.contains(target)) {
					// allLines.add(currentLine);
					return;
				}
				
				populate(allLines, currentLine, alreadyCovered, target, target, parent);
			}
		} else {
			if (neighbors.size() != 0) {
				// more than 2, this is a joint. 0 neighbors means to just do nothing due to adding the directly registered conduit up above.
				for (AbstractLightEnergyLink neighbor : neighbors) {
					if (alreadyCovered.contains(neighbor)) continue;
					
					Line forBranch = new Line(parent);
					allLines.add(forBranch);
					populate(allLines, forBranch, alreadyCovered, neighbor, neighbor, parent);
				}
			}
		}
	}
	
	private static void populateAmong(List<Line> allLines, List<Line> allowedLines, Line currentLine, List<AbstractLightEnergyLink> alreadyCovered, AbstractLightEnergyLink previous, AbstractLightEnergyLink around, Assembly parent) {
		currentLine.segments.add(around);
		if (!alreadyCovered.contains(around)) alreadyCovered.add(around);
		
		BlockPos[] neighborBlockPos = ConnectionHelper.getDirectionsWithMutualConnections(parent.world, around.getBlockPos(), false);
		ArrayList<AbstractLightEnergyLink> neighbors = new ArrayList<>();
		for (BlockPos pos : neighborBlockPos) {
			BlockEntity neighborTE = parent.world.getBlockEntity(pos);
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
				if (isInAllowedList(allowedLines, neighbor)) {
					populateAmong(allLines, allowedLines, currentLine, alreadyCovered, neighbor, neighbor, parent);
				}
			} else {
				// 2 neighbors, this is mid or end
				AbstractLightEnergyLink target = neighbors.get(0);
				if (previous == target || alreadyCovered.contains(target)) {
					target = neighbors.get(1);
				}
				
				if (alreadyCovered.contains(target)) {
					// allLines.add(currentLine);
					return;
				}
				
				if (isInAllowedList(allowedLines, target)) {
					populateAmong(allLines, allowedLines, currentLine, alreadyCovered, target, target, parent);
				}
			}
		} else {
			if (neighbors.size() != 0) {
				// more than 2, this is a joint. 0 neighbors means to just do nothing due to adding the directly registered conduit up above.
				for (AbstractLightEnergyLink neighbor : neighbors) {
					if (alreadyCovered.contains(neighbor)) continue;
					if (!isInAllowedList(allowedLines, neighbor)) continue;
					
					Line forBranch = new Line(parent);
					allLines.add(forBranch);
					populateAmong(allLines, allowedLines, forBranch, alreadyCovered, neighbor, neighbor, parent);
				}
			}
		}
	}
	
	private static boolean isInAllowedList(List<Line> allowed, AbstractLightEnergyLink check) {
		return allowed.stream().anyMatch(line -> line.contains(check));
	}
	
}
