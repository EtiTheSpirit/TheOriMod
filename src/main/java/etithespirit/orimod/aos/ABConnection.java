package etithespirit.orimod.aos;

import com.google.common.base.Splitter;
import com.google.j2objc.annotations.Weak;
import etithespirit.orimod.common.tile.light.AbstractLightTile;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.util.collection.Breaker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * An AB Connection represents a connection between two Light energy "hub" blocks. Hubs are blocks where more than one connection
 * to that block is made, or where that block is functional on its own.
 *
 * It is possible for an AB Connection to be missing a from or to value, if this has occurred, then a line was created but with no endpoint.
 * This can occur to optimize larger assemblies by pre-creating lines such that they can be prepared to add new elements on the fly.
 */
public final class ABConnection {
	
	private static boolean USE_GREEDY_MEMORY;
	
	// TODO: Seal this
	public static boolean _greedySet = false;
	
	private AbstractLightTile from;
	private AbstractLightTile to;
	private final List<AbstractLightTile> links;
	
	
	private ABConnection(@Nullable AbstractLightTile alpha, @Nullable AbstractLightTile bravo, List<AbstractLightTile> links) {
		this.from = alpha;
		this.to = bravo;
		this.links = List.copyOf(links);
		if (!_greedySet) {
			_greedySet = true;
			USE_GREEDY_MEMORY = OriModConfigs.GREEDY_ASSEMBLY_OPTIMIZATION.get();
		}
	}
	
	/**
	 * Splits this line into one or more new lines based on the addition of a segment (forming a T/+ joint) or removal of a segment (splitting the line into pieces).
	 * Depending on the context and location in which this split occurs, this may return one line ({@code this}) or more. For example, calling split on the end segment
	 * will just extend or shrink this line, and cause this method to return itself. Calling split in the middle of a line with removal will return two lines, one will be
	 * this up to the point of the split, and the other will be a new line continuing after the gap.
	 * @param around
	 * @param isRemoving
	 * @return
	 */
	public ABConnection[] splitAround(AbstractLightTile around, boolean isRemoving) {
		List<AbstractLightTile>[] result = Breaker.breakApart(this.links, around, -1);
		return null;
	}
	
	public ABConnection[] splitAround(BlockGetter inWorld, BlockPos around, boolean isRemoving) {
		BlockEntity ent = inWorld.getBlockEntity(around);
		if (ent instanceof AbstractLightTile tile) {
			return splitAround(tile, isRemoving);
		}
		throw new IllegalArgumentException(String.format("The given position [%s] does not correspond to a Light Tile!", around.toShortString()));
	}
	
	/**
	 * Returns a reference to the first tile of the two in this line.
	 * @return a reference to the first tile of the two in this line.
	 */
	public @Nullable AbstractLightTile getTileA() {
		if (from == null) return null;
		if (from.isRemoved()) {
			from = null;
		}
		return from;
	}
	
	/**
	 * Returns a reference to the second tile of the two in this line.
	 * @return a reference to the second tile of the two in this line.
	 */
	public @Nullable AbstractLightTile getTileB() {
		if (to == null) return null;
		if (to.isRemoved()) {
			to = null;
		}
		return to;
	}
	
}
