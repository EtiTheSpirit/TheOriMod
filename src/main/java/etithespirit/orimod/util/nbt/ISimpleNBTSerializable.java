package etithespirit.orimod.util.nbt;

import net.minecraft.nbt.CompoundTag;

/**
 * Allows the implementor to read to and write from a {@link CompoundTag} instance.
 *
 * @author Eti
 */
public interface ISimpleNBTSerializable {
	
	/**
	 * Writes the data of this class to the given nbt tag.
	 * @param tag The compound tag to write to.
	 * @return The modified tag.
	 */
	CompoundTag writeToNBT(CompoundTag tag);
	
	/**
	 * Reads the data from the nbt tag and sets the values in this class from it.
	 * @param tag The tag to read from.
	 */
	void readFromNBT(CompoundTag tag);
	
}
