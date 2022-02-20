package etithespirit.orimod.common.tile;


import net.minecraft.nbt.CompoundTag;

/**
 * An interface that requires {@link net.minecraft.world.level.block.entity.BlockEntity Block Entities} to package the data they send over
 * the network into an NBT tag, allowing inherited NBT tag behavior in a manner akin to worlds or entities.
 */
public interface INetworkNBTProvider {
	
	/**
	 * Receives a {@link CompoundTag} to populate with data to send over the network. Lowest level implementors should call this method to
	 * acquire the NBT tag to pack into the replication packet.
	 * @param existingTag The tag in its current state.
	 * @return The tag with its modifications.
	 */
	CompoundTag getNBTForUpdatePacket(CompoundTag existingTag);
	
}
