package etithespirit.etimod.connection;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.etimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.etimod.exception.NotImplementedException;
import etithespirit.etimod.util.collection.CachedImmutableSetWrapper;
import etithespirit.etimod.util.collection.SidedListProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

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
	
	/** Every instantiated assembly on this side of the game. */
	private static final SidedListProvider<Assembly> ALL_ASM_CACHE = new SidedListProvider<>(true);
	
	/** All lines that form this assembly. */
	private final CachedImmutableSetWrapper<Line> lines = new CachedImmutableSetWrapper<>(true);
	
	/** The {@link AssemblyHelper} that figures out what all connected bits of this assembly exist. */
	private final AssemblyHelper helper;
	
	/** The {@link World} this {@link Assembly} exists in. */
	public final World world;
	
	/**
	 * An ID <strong>EXCLUSIVELY</strong> used for debugging. This ID is not synchronized over the client/server boundary and should <strong>never</strong> be used for network references.
	 */
	public final int debugId;
	
	/**
	 * A synchronized unique identifier for this assembly that is safe for network replication.
	 */
	public final UUID assemblyId = UUID.randomUUID();
	
	/**
	 * Returns a new assembly for the given {@link AbstractLightEnergyHub}, or if another assembly already
	 * has this as one of its roots, returns that other assembly.
	 * @param hub The {@link AbstractLightEnergyHub} to create or get an assembly for.
	 * @return A new instance of {@link Assembly}, or an existing instance of one already knows about this {@link AbstractLightEnergyHub}.
	 */
	public static Assembly getAssemblyFor(AbstractLightEnergyHub hub) {
		CachedImmutableSetWrapper<Assembly> assemblies = ALL_ASM_CACHE.getListForSide(hub.getLevel().isClientSide);
		
		for (Assembly assembly : assemblies) {
			List<AbstractLightEnergyHub> hubs = assembly.getHubs();
			if (hubs.contains(hub)) {
				return assembly;
			}
		}
		
		// Note to self: anticipateWithAuto should be true in this one, because if it makes it to this point in the code,
		// then the TE will have been freshly placed (or moved) and thus would have new automatic connections being made.
		IBlockReader reader = hub.getLevel();
		BlockPos origin = hub.getBlockPos();
		for (BlockPos validNeighborPos : ConnectionHelper.getDirectionsWithMutualConnections(reader, origin, true)) {
			TileEntity neighborTE = reader.getBlockEntity(validNeighborPos);
			if (neighborTE instanceof AbstractLightEnergyLink) {
				AbstractLightEnergyLink link = (AbstractLightEnergyLink)neighborTE;
				Assembly other = link.getAssembly();
				if (other != null) {
					other.connectHub(hub);
					return other;
				}
			}
		}
		
		// At this point, the only remaining option is that there is no assembly for this hub, and so we need a new one.
		Assembly instance = new Assembly(hub);
		for (AbstractLightEnergyHub otherHub : instance.helper.getHubs()) {
			if (instance.isHubConnected(otherHub)) continue;
			instance.connectHub(otherHub);
		}
		for (AbstractLightEnergyLink link : instance.helper.getLinks()) {
			link.setAssembly(instance);
		}
		return instance;
	}
	
	private Assembly(AbstractLightEnergyHub hub) {
		world = hub.getLevel(); // Important that this is done BEFORE the AssemblyHelper is instantiated. It uses this.
		helper = new AssemblyHelper(this, hub);
		
		debugId = currentid++;
		// Wondering why this is going up by two? It's because the value is static and this might exist on both the client and server.
		
		getAllInstances().add(this);
		
		lines.addAll(Line.constructFrom(this, helper));
	}
	
	/**
	 * Should <strong>only</strong> be executed when a world is unloading. This sets {@link #currentid} to 0 (to reset debug IDs) and clears the list of every instantiated assembly for this side.
	 * @param client If true, the client list should be cleared. If false, the server (both dedicated and integrated apply) should be.
	 */
	public static void clearAllKnownAssemblies(boolean client) {
		currentid = 0;
		ALL_ASM_CACHE.getListForSide(client).clear();
	}
	
	/**
	 * @return The static list of every instantiated {@link Assembly} for this side (client/server). <strong>Note that this relies on {@link #world} being set.</strong>
	 */
	private CachedImmutableSetWrapper<Assembly> getAllInstances() {
		return ALL_ASM_CACHE.getListForSide(world.isClientSide);
	}
	
	/**
	 * Assuming this assembly has just connected with the given other assembly, this merges the other assembly into this.
	 * @param other The other assembly that will be merged into this.
	 * @param cause If this was caused by the addition of a link, this is the link that caused it.
	 */
	public void mergeWith(Assembly other, AbstractLightEnergyLink cause) {
		// In a merge, whatever conduit made the merge has three possibilities
		// #1: It was a new block added to the end of a line, extending its length (for at least one of both sides. the other side may have a T joint or it may be straight)
			// If this is the case, then that block goes to that line, and then every line is copied over because they are valid.
		// #2: It was a new block added to the side of a line, causing a T joint and a line fragment (importantly, for both sides at once)
			// That block should be added as a new line instance containing only that block.
		// #3: It was two lines that were next to eachother in some way, and their connection state was changed, causing a connection to form.
			// The lines should remain completely unchanged, with the exception of the lines that it is connected to.
		
		throw new NotImplementedException();
	}
	
	/**
	 * Assuming this assembly has just been broken into more than one disconnected part, this will figure out where the break
	 * occurred, resize this assembly to what parts remain, and create a new assembly out of the parts that were disconnected.
	 * @return This assembly in index #0, and the new assembly from the disconnected parts in the remaining indices.
	 */
	public Assembly[] fragment() {
		throw new NotImplementedException();
	}
	
	/**
	 * Returns the {@link Line} that contains the given conduit, or null if no such line exists.
	 * @param conduit The conduit that is presumably a part of one of the lines in this assembly.
	 * @return The {@link Line} containing the given conduit, or null if no line in this assembly contains it.
	 */
	public @Nullable Line getLineContaining(AbstractLightEnergyLink conduit) {
		for (Line line : lines) {
			if (line.getSegments().contains(conduit)) {
				return line;
			}
		}
		return null;
	}
	
	/**
	 * Handles a new conduit being added to this {@link Assembly}.
	 * @param link The conduit that was just added.
	 */
	public void handleLinkAddition(AbstractLightEnergyLink link) {
		throw new NotImplementedException();
	}
	
	/**
	 * Handles an existing conduit being removed from this {@link Assembly}.
	 * @param link The conduit that was just removed.
	 */
	public void handleLinkRemoval(AbstractLightEnergyLink link) {
		throw new NotImplementedException();
	}
	
	/**
	 * Handles whenever the outgoing connections associated with this conduit change.
	 * @param link The conduit whose connection state changed.
	 */
	public void handleLinkConnectionStateChanged(AbstractLightEnergyLink link) {
		throw new NotImplementedException();
	}
	
	/**
	 * Returns whether or not this assembly has no hubs. In intended cases, this should always return true, and if it is returning false, then
	 * the reference to this assembly should be dropped immediately as it needs to be scheduled for GC.
	 * @return Whether or not this assembly has no hubs.
	 */
	public boolean isEmpty() {
		return helper.getHubs().isEmpty();
	}
	
	/**
	 * Disconnects the given {@link AbstractLightEnergyHub hub} from this {@link Assembly}, and disposes of this assembly if it has no more hubs after this is called.
	 * @param hub The hub to remove.
	 */
	public void disconnectHub(AbstractLightEnergyHub hub) {
		helper.manuallyUnregisterHub(hub); // Let the exception go through if this is called incorrectly.
		if (isEmpty()) {
			LOG.debug("Disposed of Assembly on this thread as all roots have been destroyed.");
			this.dispose();
		}
	}
	
	/**
	 * Connects the given {@link AbstractLightEnergyHub hub} to this {@link Assembly}.
	 * @param hub The hub to add.
	 */
	public void connectHub(AbstractLightEnergyHub hub) {
		helper.manuallyRegisterHub(hub);
	}
	
	/**
	 * Returns whether or not the given {@link AbstractLightEnergyHub} is part of this {@link Assembly}.
	 * @param hub The hub to check.
	 * @return Whether or not the given hub is part of this assembly.
	 */
	public boolean isHubConnected(AbstractLightEnergyHub hub) {
		return helper.getHubs().contains(hub);
	}
	
	/**
	 * Returns the first anchor point for this assembly. This does not necessarily mean first sequentially, as it references the internal connected hub array and returns the first element.
	 * @return The first anchor point in this assembly, or null if this assembly does not have any roots (in which case this assembly should technically not even exist)
	 */
	public @Nullable AbstractLightEnergyHub getCore() {
		List<AbstractLightEnergyHub> hubs = getHubs();
		if (hubs.size() > 0) {
			return hubs.get(0);
		}
		return null;
	}
	
	/**
	 * @return All anchor points in this assembly. Directly references the internal helper to acquire this list.
	 */
	public List<AbstractLightEnergyHub> getHubs() {
		return helper.getHubs();
	}
	
	/**
	 * @return All lines that this assembly is composed of.
	 */
	public List<Line> getLines() {
		return lines.asReadOnly();
	}
	
	public void dispose() {
		for (AbstractLightEnergyLink link : helper.getLinks()) {
			link.setAssembly(null);
		}
		
		lines.clear();
		helper.dispose();
		getAllInstances().remove(this);
	}
}
