package etithespirit.etimod.connection;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.tile.AbstractLightEnergyAnchor;
import etithespirit.etimod.common.tile.light.ILightEnergyConduit;
import etithespirit.etimod.util.collection.CachedImmutableSetWrapper;
import etithespirit.etimod.util.collection.IReadOnlyList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * Represents an arbitrary, scalable layout of connected objects that may have loops and/or forks. It is composed of
 * any number of {@link Line}s. It's like a multiblock construction without any guidelines whatsoever, which is part of
 * why it is (ironically) so specialized.
 *
 * @author Eti
 */
public final class Assembly {
	
	private static int currentid = 0;
	
	private static final Logger LOG = LogManager.getLogger(EtiMod.MODID + "::Assembly");
	
	private static final CachedImmutableSetWrapper<Assembly> ALL_ASSEMBLIES = new CachedImmutableSetWrapper<>(true);
	
	/** All lines that form this assembly. */
	protected final CachedImmutableSetWrapper<Line> lines;
	
	/** All entry/end points of this assembly. */
	protected final CachedImmutableSetWrapper<AbstractLightEnergyAnchor> roots = new CachedImmutableSetWrapper<>(true);
	
	/**
	 * An ID <strong>EXCLUSIVELY</strong> used for debugging. This ID is not synchronized over the client/server boundary and should <strong>never</strong> be used for network references.
	 */
	public final int debugId;
	
	/**
	 * Returns a new assembly for the given {@link AbstractLightEnergyAnchor}, or if another assembly already
	 * has this as one of its roots, returns that other assembly.
	 * @param tile The {@link AbstractLightEnergyAnchor} to create or get an assembly for.
	 * @return A new instance of {@link Assembly}, or an existing instance of one already knows about this {@link AbstractLightEnergyAnchor}.
	 */
	public static Assembly getAssemblyFor(AbstractLightEnergyAnchor tile) {
		// Look for assemblies whose roots contain this tile.
		for (Assembly assembly : ALL_ASSEMBLIES) {
			if (assembly.roots.contains(tile)) {
				return assembly;
			}
		}
		
		// The above probably won't return.
		// The other thing is to try to see if this was placed next to a conduit.
		// And (more importantly) if that conduit was connected to an existing assembly.
		IReadOnlyList<ILightEnergyConduit> conduits = tile.getConnectedConduits(true);
		
		// SUPER IMPORTANT NOTE: Do NOT use the getAssembly method of the conduits in the loop below!
		// That will just cause an infinite recursion loop, because at this point in time, the given conduits won't have
		// realized that they are in this assembly. It's this block right here that actually tells them they are in
		// this assembly in the first place. So naturally, we can't rely on it quite yet.
		
		for (ILightEnergyConduit conduit : conduits) {
			for (AbstractLightEnergyAnchor anchor : conduit.getAnchors()) {
				if (anchor == tile) continue; // Note to self, yes use == because this is by reference.
				//if (anchor.getAllConnectedStorage().contains(tile)) {
				// This anchor that is not this tile is connected to this tile.
				// Get the assembly that this is a part of.
				Assembly result = getAssemblyFor(anchor);
				// We know roots won't contain the current tile (the first block up above didn't return)
				result.roots.add(tile);
				return result;
				//}
			}
		}
		
		// And now, if all else fails, return this.
		Assembly instance = new Assembly(tile);
		for (Line line : instance.lines) {
			IReadOnlyList<ILightEnergyConduit> segments = line.getSegments();
			ILightEnergyConduit last = segments.get(segments.size() - 1);
			for (AbstractLightEnergyAnchor anchor : last.getAnchors()) {
				if (!instance.roots.contains(anchor)) {
					instance.roots.add(anchor);
				}
			}
		}
		return instance;
	}
	
	private Assembly(AbstractLightEnergyAnchor origin) {
		ALL_ASSEMBLIES.add(this);
		roots.add(origin);
		lines = new CachedImmutableSetWrapper<>(Line.constructFrom(origin, this), true);
		debugId = currentid++;
		// Wondering why this is going up by two? It's because the value is static.
	}
	
	public void forceRefreshAllLines() {
		lines.clear();
		lines.addAll(Line.constructFrom(getCore(), this));
	}
	
	
	/**
	 * Returns the {@link Line} that contains the given conduit, or null if no such line exists.
	 * @param conduit The conduit that is presumably a part of one of the lines in this assembly.
	 * @return The {@link Line} containing the given conduit, or null if no line in this assembly contains it.
	 */
	public @Nullable Line getLineContaining(ILightEnergyConduit conduit) {
		for (Line line : lines) {
			if (line.getSegments().contains(conduit)) {
				return line;
			}
		}
		return null;
	}
	
	/**
	 * Handles a new conduit being added to this {@link Assembly}.
	 * @param conduit The conduit that was just added.
	 */
	public void handleAddition(ILightEnergyConduit conduit) {
		//getLineContaining(conduit).spliceAndRebranch(conduit);
		getCore().repopulateConnectedArray();
		forceRefreshAllLines(); // Lazy method.
	}
	
	/**
	 * Handles an existing conduit being removed from this {@link Assembly}.
	 * @param conduit The conduit that was just removed.
	 */
	public void handleRemoval(ILightEnergyConduit conduit) {
		getCore().repopulateConnectedArray();
		forceRefreshAllLines(); // Lazy method.
	}
	
	/**
	 * Handles whenever the outgoing connections associated with this conduit change.
	 * @param conduit
	 */
	public void handleConnectionStateChanged(ILightEnergyConduit conduit) {
	
	}
	
	public void tileEntityRemoved(AbstractLightEnergyAnchor tile) {
		roots.remove(tile); // Let the exception go through if this is called incorrectly.
		if (roots.size() == 0) {
			LOG.debug("Disposed of Assembly on this thread as all roots have been destroyed.");
			this.dispose();
		}
	}
	
	/**
	 * Returns the first anchor point for this assembly. It is identical to the first entry in {@link #roots}.
	 * @return The first anchor point in this assembly, or null if this assembly does not have any roots (in which case this assembly should technically not even exist)
	 */
	public AbstractLightEnergyAnchor getCore() {
		if (roots.size() > 0) {
			return roots.get(0);
		}
		return null;
	}
	
	/**
	 * @return All anchor points in this assembly.
	 */
	public IReadOnlyList<AbstractLightEnergyAnchor> getRoots() {
		return roots.asReadOnly();
	}
	
	/**
	 * @return All lines that this assembly is composed of.
	 */
	public IReadOnlyList<Line> getLines() {
		return lines.asReadOnly();
	}
	
	public void dispose() {
		lines.clear();
		roots.clear();
		ALL_ASSEMBLIES.remove(this);
	}
}
