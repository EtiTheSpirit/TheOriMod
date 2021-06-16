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
	
	public final int id;
	
	/**
	 * Returns a new assembly for the given {@link AbstractLightEnergyAnchor}, or if another assembly already
	 * has this as one of its roots, returns that other assembly.
	 * @param tile The {@link AbstractLightEnergyAnchor} to create or get an assembly for.
	 * @return A new instance of {@link Assembly}, or an existing instance of one already knows about this {@link AbstractLightEnergyAnchor}.
	 */
	public static Assembly getAssemblyFor(AbstractLightEnergyAnchor tile) {
		for (Assembly assembly : ALL_ASSEMBLIES) {
			if (assembly.roots.contains(tile)) {
				return assembly;
			}
		}
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
		id = currentid++;
	}
	
	
	/**
	 * Returns the {@link Line} that contains the given conduit, or null if no such line exists.
	 * @param conduit The conduit to find.
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
	
	}
	
	/**
	 * Handles an existing conduit being removed from this {@link Assembly}.
	 * @param conduit The conduit that was just removed.
	 */
	public void handleRemoval(ILightEnergyConduit conduit) {
	
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
			LOG.debug("Disposed of Assembly as all roots have been destroyed.");
			this.dispose();
		}
	}
	
	/**
	 * Returns the first anchor point for this assembly. It is identical to the first entry in {@link #roots}.
	 * @return The first anchor point in this assembly, or null if this assembly does not have any roots.
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
