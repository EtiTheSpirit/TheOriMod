package etithespirit.etimod.common.tile;

import com.google.common.collect.ImmutableSet;
import etithespirit.etimod.energy.ILightEnergyStorage;

import java.util.Set;

/**
 * An identifier that signfies any attached instances of {@link net.minecraft.tileentity.TileEntity TileEntity} as a means of transferring power.
 */
public interface ILightEnergyConduit {
	
	/**
	 * Adds an "anchor" to this assembly. An assembly describes a circuit, and an anchor represents something that manages
	 * iterating through all parts of a circuit.
	 * @param anchor The instance of {@link AbstractLightEnergyStorageTileEntity} that was responsible for locating this conduit through recursion.
	 * @throws IllegalArgumentException If the given {@link AbstractLightEnergyStorageTileEntity} is already registered.
	 */
	void registerAnchor(AbstractLightEnergyStorageTileEntity anchor) throws IllegalArgumentException;
	
	/**
	 * Removes an "anchor" from this assembly. An assembly describes a circuit, and an anchor represents something that manages
	 * iterating through all parts of a circuit.
	 * @param anchor The instance of {@link AbstractLightEnergyStorageTileEntity} that was responsible for locating this conduit through recursion.
	 * @throws IllegalArgumentException If the given {@link AbstractLightEnergyStorageTileEntity} is not already registered.
	 */
	void unregisterAnchor(AbstractLightEnergyStorageTileEntity anchor) throws IllegalArgumentException;
	
	/**
	 * Returns whether or not the given "anchor" is registered to this assembly.
	 * An assembly describes a circuit, and an anchor represents something that manages iterating through all parts of a circuit.
	 * @param anchor The instance of {@link AbstractLightEnergyStorageTileEntity} to test for.
	 * @return Whether or not the given {@link AbstractLightEnergyStorageTileEntity} is registered as an anchor to this conduit.
	 */
	boolean isAnchor(AbstractLightEnergyStorageTileEntity anchor);
	
	/**
	 * @return An array of all registered anchors.
	 */
	ImmutableSet<AbstractLightEnergyStorageTileEntity> getAnchors();
	
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
	
}
