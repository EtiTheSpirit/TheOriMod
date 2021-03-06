package etithespirit.orimod.lighttechlgc;


import com.google.common.collect.ImmutableList;
import etithespirit.orimod.annotation.NotNetworkReplicated;
import etithespirit.exception.NotImplementedException;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.aos.ConnectionHelper;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.orimod.util.collection.SidedListProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an arbitrary, scalable layout of connected objects that may have loops and/or forks. It is composed of
 * any number of {@link Line}s.
 *
 * Assemblies are not replicated over the network due to their innate complexity. They are instead calculated independently.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
@NotNetworkReplicated
@Deprecated(forRemoval = true)
public final class Assembly {
	
	private static int currentDebugID = 0;
	
	private static final Logger LOG = LogManager.getLogger(OriMod.MODID + "::Assembly");
	
	/** Every instantiated assembly on this side of the game by ID. This is sided due to the list being static. */
	private static final SidedListProvider<Assembly> ALL_ASM_CACHE = new SidedListProvider<>();
	
	/** All lines that form this assembly. */
	private final List<Line> lines = new ArrayList<>();
	
	/** The chunks occupied by this assembly. */
	private final List<ChunkPos> occupiedChunks = new ArrayList<>();
	// TODO: Should this be something like a multimap? Is tracking the amount of elements in a chunk helpful in any way?
	
	/** The {@link AssemblyHelper} that figures out what all connected bits of this assembly exist. */
	final AssemblyHelper helper;
	
	/** The {@link Level} this {@link Assembly} exists in. */
	public final Level world;
	
	/**
	 * An ID for debugging this assembly. This is for use in clientside rendering.
	 */
	public final int _id;
	
	
	/**
	 * Returns a new assembly for the given {@link AbstractLightEnergyHub}, or if another assembly already
	 * has this as one of its roots, returns that other assembly.
	 * @param hub The {@link AbstractLightEnergyHub} to create or get an assembly for.
	 * @return A new instance of {@link Assembly}, or an existing instance of one already knows about this {@link AbstractLightEnergyHub}.
	 */
	public static @Nonnull Assembly getAssemblyFor(AbstractLightEnergyHub hub) {
		List<Assembly> assemblies = ALL_ASM_CACHE.getListForSide(hub.getLevel().isClientSide);
		
		for (Assembly assembly : assemblies) {
			List<AbstractLightEnergyHub> hubs = assembly.getHubs();
			if (hubs.contains(hub)) {
				return assembly;
			}
		}
		
		// Note to self: anticipateWithAuto should be true in this one, because if it makes it to this point in the code,
		// then the TE will have been freshly placed (or moved) and thus would have new automatic connections being made.
		BlockGetter reader = hub.getLevel();
		BlockPos origin = hub.getBlockPos();
		for (BlockPos validNeighborPos : ConnectionHelper.getDirectionsWithMutualConnections(reader, origin, true)) {
			BlockEntity neighborTE = reader.getBlockEntity(validNeighborPos);
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
	
	/**
	 * Similar to {@link #getAssemblyFor(AbstractLightEnergyHub)}, but this returns the assembly containing the given node. This will usually
	 * be more expensive, as assemblies have more nodes than hubs.
	 * @param link The node to search for.
	 * @return An {@link Assembly} containing the given {@link AbstractLightEnergyLink}, or null if no such assembly exists.
	 */
	public static @Nullable Assembly findAssemblyContainingNode(AbstractLightEnergyLink link) {
		List<Assembly> assemblies = ALL_ASM_CACHE.getListForSide(link.getLevel().isClientSide);
		
		for (Assembly assembly : assemblies) {
			if (assembly.getLineContaining(link) != null) {
				return assembly;
			}
		}
		return null;
	}
	
	private Assembly(AbstractLightEnergyHub hub) {
		world = hub.getLevel(); // Important that this is done BEFORE the AssemblyHelper is instantiated. It uses this.
		helper = new AssemblyHelper(this, hub);
		
		_id = currentDebugID++;
		// Wondering why this is going up by two? It's because the value is static and this might exist on both the client and integrated server.
		
		getAllInstances().add(this);
		connectHub(hub);
		
		lines.addAll(Line.constructFrom(this));
	}
	
	/**
	 * This sets {@link #currentDebugID} to 0 (to reset debug IDs) and clears the list of every instantiated assembly for this side from memory. Should <strong>only</strong> be executed when a world is unloading.
	 * @param client If true, the client list should be cleared. If false, the server (both dedicated and integrated apply) should be.
	 */
	public static void clearAllKnownAssemblies(boolean client) {
		currentDebugID = 0;
		ALL_ASM_CACHE.getListForSide(client).clear();
	}
	
	/**
	 * @return The static list of every instantiated {@link Assembly} for this side (client/server). <strong>Note that this relies on {@link #world} being set.</strong>
	 */
	private List<Assembly> getAllInstances() {
		return ALL_ASM_CACHE.getListForSide(world.isClientSide);
	}
	
	/**
	 * Assuming this assembly has just connected with the given other assembly, this merges the other assembly into this.
	 * @param other The other assembly that will be merged into this.
	 */
	public void mergeWith(Assembly other) {
		other.moveAllLinesAndHubsInto(this);
	}
	
	/**
	 * Assuming this assembly has just been broken into more than one disconnected part, this will figure out where the break
	 * occurred, resize this assembly to what parts remain, and create a new assembly out of the parts that were disconnected.
	 * @return This assembly in index #0, and the new assembly from the disconnected parts in the remaining indices.
	 */
	public Assembly[] fragment(Line breakOccurredIn) {
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
	 * Returns whether or not this assembly has fully loaded, or, all of its occupied chunks are loaded.
	 * @return Whether or not this assembly has fully loaded, or, all of its occupied chunks are loaded.
	 */
	public boolean isFullyLoaded() {
		for (ChunkPos pos : occupiedChunks) {
			if (!world.isLoaded(pos.getWorldPosition())) {
				return false;
			}
		}
		return true;
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
		} else {
			ChunkPos location = new ChunkPos(hub.getBlockPos());
			if (occupiedChunks.contains(location)) {
				occupiedChunks.remove(location);
			}
		}
	}
	
	/**
	 * Connects the given {@link AbstractLightEnergyHub hub} to this {@link Assembly}.
	 * @param hub The hub to add.
	 */
	public void connectHub(AbstractLightEnergyHub hub) {
		helper.manuallyRegisterHub(hub);
		ChunkPos location = new ChunkPos(hub.getBlockPos());
		if (!occupiedChunks.contains(location)) {
			occupiedChunks.add(location);
		}
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
	 * @return All anchor points in this assembly. This is a snapshot list and is readonly.
	 */
	public List<AbstractLightEnergyHub> getHubs() {
		return helper.getHubs();
	}
	
	/**
	 * @return All lines that this assembly is composed of. This is a snapshot list and is readonly.
	 */
	public List<Line> getLines() {
		return ImmutableList.copyOf(lines);
	}
	
	/** Dispose of the data associated with this assembly. This is used when the assembly is being fully deleted. */
	public void dispose() {
		for (AbstractLightEnergyLink link : helper.getLinks()) {
			link.setAssembly(null);
		}
		
		occupiedChunks.clear();
		lines.clear();
		helper.dispose();
		getAllInstances().remove(this);
	}
	
	/**
	 * For internal use. Removes the given line from the registry of lines in this assembly.
	 * @param line The line to remove.
	 */
	void removeLine(Line line) {
		this.lines.remove(line);
	}
	
	/**
	 * For internal use. Adds the given line to the registry of lines in this assembly.
	 * @param line The line to add.
	 */
	void addLine(Line line) {
		this.lines.add(line);
	}
	
	/**
	 * For internal use only. Translates all data of this assembly into the given other assembly, then disposes of this assembly.
	 * @param other The assembly to move into.
	 */
	void moveAllLinesAndHubsInto(Assembly other) {
		other.lines.addAll(this.lines);
		other.helper.mergeWith(this.helper);
		this.helper.updateAllComponents(other);
		this.helper.dispose();
		this.dispose();
	}
	
}
