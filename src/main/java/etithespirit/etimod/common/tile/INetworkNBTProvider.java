package etithespirit.etimod.common.tile;

import net.minecraft.nbt.CompoundNBT;

/**
 * An interface that requires {@link net.minecraft.tileentity.TileEntity TileEntities} to package the data they send over
 * the network into an NBT tag, allowing inherited NBT tag behavior in a manner akin to worlds or entities.
 */
public interface INetworkNBTProvider {
	
	/**
	 * Receives a {@link CompoundNBT} to populate with data to send over the network. Lowest level implementors should call this method to
	 * acquire the NBT tag to pack into the replication packet.
	 * @param existingTag The tag in its current state.
	 * @return The tag with its modifications.
	 */
	CompoundNBT getNBTForUpdatePacket(CompoundNBT existingTag);
	
}
