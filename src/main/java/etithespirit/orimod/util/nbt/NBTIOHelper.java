package etithespirit.orimod.util.nbt;


import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;

/**
 * This class provides utilities that allow writing some more object types to NBT as well as reading that data back.
 */
public final class NBTIOHelper {
	
	private NBTIOHelper() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * Writes the given Vector to the given tag.
	 * @param to The tag to write to.
	 * @param name The name of the value to write.
	 * @param vector The actual value being written.
	 */
	public static void putVector3i(CompoundTag to, String name, Vec3i vector) {
		to.putIntArray(name, new int[] { vector.getX(), vector.getY(), vector.getZ() });
	}
	
	/**
	 * Reads a {@link Vec3i} from the given tag.
	 * @param from The tag to read from.
	 * @param name The name of the value to read.
	 * @return The acquired {@link Vec3i}.
	 * @throws IllegalArgumentException if the associated value is not an integer array with 3 elements in it, including if the value is missing.
	 */
	public static Vec3i getVector3i(CompoundTag from, String name) {
		int[] value = from.getIntArray(name);
		if (value.length != 3) throw new IllegalArgumentException("The acquired integer array does not have 3 elements.");
		return new Vec3i(value[0], value[1], value[2]);
	}
	
	/**
	 * Writes the given {@link ChunkPos} to the given tag.
	 * @param to The tag to write to.
	 * @param name The name of the value to write.
	 * @param location The actual value being written.
	 */
	public static void putChunkPos(CompoundTag to, String name, ChunkPos location) {
		to.putIntArray(name, new int[] { location.x, location.z });
	}
	
	/**
	 * Reads a {@link ChunkPos} from the given tag.
	 * @param from The tag to read from.
	 * @param name The name of the value to read.
	 * @return The acquired {@link ChunkPos}.
	 * @throws IllegalArgumentException if the associated value is not an integer array with 2 elements in it, including if the value is missing.
	 */
	public static ChunkPos getChunkPos(CompoundTag from, String name) {
		int[] value = from.getIntArray(name);
		if (value.length != 2) throw new IllegalArgumentException("The acquired integer array does not have 2 elements.");
		return new ChunkPos(value[0], value[1]);
	}
	
	/**
	 * Writes many {@link ChunkPos} instances to the given tag under the given name.
	 * @param to The tag to write to.
	 * @param name The name to associate the values with.
	 * @param locations The actual values being written.
	 * @throws NullPointerException If any location is null.
	 */
	public static void putManyChunkPos(CompoundTag to, String name, ChunkPos... locations) {
		int[] result = new int[locations.length * 2];
		for (int idx = 0; idx < locations.length; idx++) {
			ChunkPos current = locations[idx];
			if (current == null) throw new NullPointerException("Received a null ChunkPos in the input parameters.");
			
			int resultIndex = idx * 2;
			result[resultIndex] = current.x;
			result[resultIndex + 1] = current.z;
		}
		to.putIntArray(name, result);
	}
	
	/**
	 * Reads many {@link ChunkPos} instances from the given tag under the given name.
	 * @param from The tag to read from.
	 * @param name The name of the array storing the values.
	 * @return An array of the retrieved {@link ChunkPos} instances.
	 * @throws IllegalArgumentException if the associated value is not an integer array with an even number of elements in it (each {@link ChunkPos}) <em>must</em> write two integers). If there are 0 elements, this returns an empty array.
	 */
	public static ChunkPos[] getManyChunkPos(CompoundTag from, String name) {
		int[] values = from.getIntArray(name);
		if (values.length == 0) return new ChunkPos[0];
		if ((values.length & 1) != 0) throw new IllegalArgumentException("The acquired integer array does not have a number of elements that is a multiple of 2.");
		ChunkPos[] result = new ChunkPos[values.length / 2];
		for (int idx = 0; idx < values.length; idx += 2) {
			result[idx / 2] = new ChunkPos(values[idx], values[idx + 1]);
		}
		return result;
	}
	
	/**
	 * Returns the float stored at the given key iff the tag has the key registered and it is a float type. Returns def otherwise.
	 * @param tag The tag to read.
	 * @param key The key of the value.
	 * @param def The fallback value if the real value is missing or the wrong type.
	 * @return The float stored at the given key iff the tag has the key registered and it is a float type. Returns def otherwise.
	 */
	public static float getFloatOrDefault(CompoundTag tag, String key, float def) {
		if (tag.contains(key, CompoundTag.TAG_FLOAT)) {
			return tag.getFloat(key);
		}
		return def;
	}
}
