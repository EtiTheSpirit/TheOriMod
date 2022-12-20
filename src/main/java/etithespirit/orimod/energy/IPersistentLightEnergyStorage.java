package etithespirit.orimod.energy;

import net.minecraft.nbt.CompoundTag;

/**
 * A persistent energy storage. This extends {@link ILightEnergyStorage} but also provides a markDirty method as well as save and load methods.
 */
public interface IPersistentLightEnergyStorage extends ILightEnergyStorage {
	
	/**
	 * The default key used to save the energy stored in this instance to NBT.
	 */
	String ENERGY_KEY = "lightEnergy";
	
	void markDirty();
	
	default void saveEnergyInfo(CompoundTag tag) {
		tag.putFloat(ENERGY_KEY, getLightStored());
	}
	
	void loadEnergyInfo(CompoundTag tag);
	
}
