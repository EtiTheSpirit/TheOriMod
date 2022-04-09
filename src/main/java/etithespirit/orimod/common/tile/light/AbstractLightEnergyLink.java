package etithespirit.orimod.common.tile.light;


import etithespirit.orimod.common.tile.WorldUpdateListener;
import etithespirit.orimod.info.coordinate.Cardinals;
import etithespirit.orimod.lighttechlgc.Assembly;
import etithespirit.orimod.aos.ConnectionHelper;
import etithespirit.orimod.lighttechlgc.Line;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A counterpart to {@link AbstractLightEnergyHub} which represents a {@link BlockEntity} that links two
 * or more {@link AbstractLightEnergyHub}s together. These form the fundamental components of a {@link Line}.
 *
 * @author Eti
 */
public abstract class AbstractLightEnergyLink extends BlockEntity {
	
	/**
	 * Create a new link using the default BlockEntity ctor.
	 * @param type The block entity type.
	 * @param at The location to create it at.
	 * @param state The BlockState to create it for.
	 */
	public AbstractLightEnergyLink(BlockEntityType<?> type, BlockPos at, BlockState state) { super(type, at, state); }
	
	/** The {@link Assembly} that this is a part of. */
	protected Assembly assembly = null;
	
	/**
	 * @return The {@link Assembly} that this {@link AbstractLightEnergyLink} is a part of.
	 */
	public @Nullable Assembly getAssembly() {
		return assembly;
	}
	
	/**
	 * Intended for internal use only. Sets the {@link Assembly} associated with this {@link AbstractLightEnergyLink}
	 * @param assembly The assembly associated with this line.
	 */
	public void setAssembly(Assembly assembly) {
		this.assembly = assembly;
	}
	
	/**
	 * @return The specific line that this {@link AbstractLightEnergyLink} is a part of in its {@link Assembly}. If this has no assembly, this will return null;
	 */
	public @Nullable Line getAssemblyLine() {
		if (assembly == null) return null;
		return assembly.getLineContaining(this);
	}
	
	
	/**
	 * Returns all neighboring links, optionally only connected ones.
	 * @return All neighbors that are also links, optionally only connected links.
	 */
	public AbstractLightEnergyLink[] getNeighboringLinks(boolean connectedOnly) {
		List<AbstractLightEnergyLink> links = new ArrayList<>();
		Level world = this.getLevel();
		BlockPos at = this.getBlockPos();
		for (int i = 0; i < 6; i++) {
			Vec3i adj = Cardinals.ADJACENTS_IN_ORDER[i];
			BlockPos queryPos = at.offset(adj);
			BlockEntity ent = world.getBlockEntity(queryPos);
			if (ent instanceof AbstractLightEnergyLink link) {
				if (!connectedOnly || (connectedOnly && isConnectedTo(link, true))) {
					links.add(link);
				}
			}
		}
		return links.toArray(new AbstractLightEnergyLink[links.size()]);
	}
	
	/**
	 * Returns whether or not this link is connected to the given other link. If `directly` is true, then this does a neighbor check, otherwise this will check to see
	 * if they are a part of the same {@link Assembly}.
	 * @param other The other link to check.
	 * @param directly True if a neighbor check should be done, false if the entire assembly should be checked.
	 * @return Whether or not the two links are connected.
	 */
	public boolean isConnectedTo(AbstractLightEnergyLink other, boolean directly) {
		if (directly) {
			return ConnectionHelper.hasMutualConnectionToOther(this.getLevel(), this.getBlockPos(), other.getBlockPos(), false);
		} else {
			return this.getAssembly().getLineContaining(other) != null;
		}
	}
	
}
