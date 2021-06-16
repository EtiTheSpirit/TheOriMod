package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.common.tile.AbstractLightEnergyAnchor;
import etithespirit.etimod.connection.ConnectionHelper;
import etithespirit.etimod.info.coordinate.Cardinals;
import etithespirit.etimod.util.collection.IReadOnlyList;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * An identifier that signfies any attached instances of {@link net.minecraft.tileentity.TileEntity TileEntity} as a means of transferring power.
 */
public interface ILightEnergyConduit {
	
	/**
	 * Adds an "anchor" to this assembly. An assembly describes a circuit, and an anchor represents something that manages
	 * iterating through all parts of a circuit.
	 * @param anchor The instance of {@link AbstractLightEnergyAnchor} that was responsible for locating this conduit through recursion.
	 * @throws IllegalArgumentException If the given {@link AbstractLightEnergyAnchor} is already registered.
	 */
	void registerAnchor(AbstractLightEnergyAnchor anchor) throws IllegalArgumentException;
	
	/**
	 * Removes an "anchor" from this assembly. An assembly describes a circuit, and an anchor represents something that manages
	 * iterating through all parts of a circuit.
	 * @param anchor The instance of {@link AbstractLightEnergyAnchor} that was responsible for locating this conduit through recursion.
	 * @throws IllegalArgumentException If the given {@link AbstractLightEnergyAnchor} is not already registered.
	 */
	void unregisterAnchor(AbstractLightEnergyAnchor anchor) throws IllegalArgumentException;
	
	/**
	 * Returns whether or not the given "anchor" is registered to this assembly.
	 * An assembly describes a circuit, and an anchor represents something that manages iterating through all parts of a circuit.
	 * @param anchor The instance of {@link AbstractLightEnergyAnchor} to test for.
	 * @return Whether or not the given {@link AbstractLightEnergyAnchor} is registered as an anchor to this conduit.
	 */
	boolean connectedToAnchor(AbstractLightEnergyAnchor anchor);
	
	/**
	 * @return An array of all registered anchors.
	 */
	IReadOnlyList<AbstractLightEnergyAnchor> getAnchors();
	
	/**
	 * @return Whether or not at least one of the given connected storage points has energy in it, and can output energy.
	 */
	boolean isEnergized();
	
	/**
	 * Causes this conduit to refresh its energized state. Should only be called by storage points if there is some substantial change,
	 * for instance, the energy stored changes from nonzero to zero (or the other way around). Simple changes in energy should not
	 * execute this method.
	 */
	void refresh();
	
	/**
	 * Exposes {@link net.minecraft.tileentity.TileEntity#hasLevel()}.
	 * @return Whether or not this has an assigned world.
	 */
	boolean hasLevel();
	
	/**
	 * Exposes {@link net.minecraft.tileentity.TileEntity#getLevel()}.
	 * @return The world this exists in.
	 */
	World getLevel();
	
	/**
	 * Exposes {@link net.minecraft.tileentity.TileEntity#getBlockPos()}, which returns the block position of the tile.
	 * @return The position of this tile in the world.
	 */
	BlockPos getBlockPos();
	
	/**
	 * Exposes {@link net.minecraft.tileentity.TileEntity#getBlockState()}, which returns the {@link BlockState} at the location of this tile.
	 * @return The {@link BlockState} located at this tile.
	 */
	BlockState getBlockState();
	
	/**
	 * @param onlyConnected If true, only conduits with connections to this are returned. If false, all neighbors that are an instance of {@link ILightEnergyConduit} are returned.
	 * @param anticipateWithAuto If true, the automatic state of this and its neighbors is factored in, which will detect a connection that would be made after a block update completes.
	 * @return A list of every neighboring conduit
	 */
	default ILightEnergyConduit[] getNeighboringConduits(boolean onlyConnected, boolean anticipateWithAuto) {
		ILightEnergyConduit[] conduits = new ILightEnergyConduit[0];
		if (!hasLevel()) return conduits;
		
		ArrayList<ILightEnergyConduit> dyn = new ArrayList<>();
		for (Vector3i adj : Cardinals.ADJACENTS_IN_ORDER) {
			BlockPos testPos = getBlockPos().offset(adj);
			TileEntity neighbor = getLevel().getBlockEntity(testPos);
			if (neighbor instanceof ILightEnergyConduit) {
				ILightEnergyConduit conduit = (ILightEnergyConduit)neighbor;
				boolean canReturn = true;
				if (onlyConnected) {
					canReturn = ConnectionHelper.hasMutualConnectionInDirection(getLevel(), getBlockPos(), Direction.fromNormal(adj.getX(), adj.getY(), adj.getZ()), anticipateWithAuto);
				}
				if (canReturn) {
					dyn.add(conduit);
				}
			}
		}
		
		return dyn.toArray(conduits);
	}
	
	/**
	 * @param onlyConnected If true, only conduits with connections to this are returned. If false, all neighbors that are an instance of {@link ILightEnergyConduit} are returned.
	 * @return A list of every neighboring conduit
	 */
	default ILightEnergyConduit[] getNeighboringConduits(boolean onlyConnected) {
		return getNeighboringConduits(onlyConnected, false);
	}
	
	/**
	 * Returns whether or not this conduit is connected to the other conduit.
	 * @param other The other conduit to test.
	 * @param anticipateWithAuto Whether or not to use this and {@code other}'s {@link etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock#AUTO AUTO} property to determine if these two will connect after a block update occurs.
	 * @return Whether or not this conduit is connected to the given other conduit.
	 */
	default boolean isConnectedTo(ILightEnergyConduit other, boolean anticipateWithAuto) {
		if (!isAdjacentTo(other)) return false;
		return ConnectionHelper.hasMutualConnectionInDirection(getLevel(), getBlockPos(), other.getBlockPos(), anticipateWithAuto);
	}
	
	/**
	 * Returns whether or not this conduit is a direct neighbor of the given {@link ILightEnergyConduit}.
	 * @param other The {@link ILightEnergyConduit} to test.
	 * @return Whether or not this conduit is a direct neighbor of the given {@link ILightEnergyConduit}.
	 */
	default boolean isAdjacentTo(ILightEnergyConduit other) {
		return isAdjacentTo(other.getBlockPos());
	}
	
	/**
	 * Returns whether or not this conduit is a direct neighbor to the given {@link BlockPos}
	 * @param other The {@link BlockPos} to test.
	 * @return Whether or not this conduit is directly adjacent to the given {@link BlockPos}
	 */
	default boolean isAdjacentTo(BlockPos other) {
		return ConnectionHelper.areNeighbors(getBlockPos(), other);
	}
	
}
