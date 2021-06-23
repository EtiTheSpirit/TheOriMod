package etithespirit.etimod.info.spirit.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A basic implementation of spirit capabilities. See {@link SpiritCapabilities} for more information.
 *
 * @author Eti
 */
public interface ISpiritCapabilities extends INBTSerializable<CompoundNBT> {
	
	/**
	 * Set whether or not the associated entity is a spirit.
	 * @param isSpirit Whether or not the entity is a spirit.
	 */
	void setIsSpirit(boolean isSpirit);
	
	/**
	 * @return Whether or not the entity is classified as a spirit.
	 */
	boolean getIsSpirit();
	
	/**
	 * @return The spirit ability container, which can be mutated.
	 */
	SpiritGameAbilities getSpiritAbilities();
	
	/**
	 * Copies the data of this instance into the other instance.
	 * @param other The receiving capabilities instance.
	 */
	void copyTo(ISpiritCapabilities other);
	
}
