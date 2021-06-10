package etithespirit.etimod.server.datapersistence;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.morph.PlayerToSpiritBinding;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

/**
 * Provides an interface to save and load any number of players' current models to file.
 * @author Eti
 *
 */
public class MorphDataPersistence extends MapData {
	
	/**
	 * The name of the data persistence entry.
	 */
	private static final String DATA_NAME = EtiMod.MODID + "_MorphData";

	protected MorphDataPersistence() {
		super(DATA_NAME);
	}

	@Override
	public void load(CompoundNBT nbt) {
		CompoundNBT data = nbt.getCompound("MorphBindings");
		Set<String> keys = data.getAllKeys();
		for (String key : keys) {
			boolean value = data.getBoolean(key);
			try {
				UUID keyID = UUID.fromString(key);
				PlayerToSpiritBinding.put(keyID, value);
			} catch (Exception exc) { }
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		Map<UUID, Boolean> binding = PlayerToSpiritBinding.BINDINGS;
		Set<UUID> uuids = binding.keySet();
		CompoundNBT container = new CompoundNBT();
		for (UUID id : uuids) {
			Boolean value = binding.get(id);
			if (value != null) {
				container.putBoolean(id.toString(), value);
			}
		}
		compound.put("MorphBindings", container);
		return compound;
	}
	
	/**
	 * Initializes this data persistence and loads its data. This only needs to be called once as it is global.
	 * @param world
	 * @return
	 */
	public static MorphDataPersistence get(World world) {
		MapData instance = world.getMapData(DATA_NAME);
		if (instance == null || !(instance instanceof MorphDataPersistence)) {
			MorphDataPersistence data = new MorphDataPersistence();
			world.setMapData(data);
			return data;
		}
		return (MorphDataPersistence)instance;
	}
	
}
