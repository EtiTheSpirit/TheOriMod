package etithespirit.orimod.server.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/**
 * A utility class to manage the state of chunks being kept alive or not.
 */
public final class ChunkKeepAlive {
	
	private ChunkKeepAlive() { }
	
	/**
	 * Checks if the chunk containing the given {@link BlockPos} is forced to be alive.
	 * @param world The world to check in.
	 * @param pos The position to check in.
	 * @return True if the chunk is being kept alive, false if not.
	 */
	public static boolean isChunkKeptAlive(ServerLevel world, BlockPos pos) {
		return isChunkKeptAlive(world, new ChunkPos(pos));
	}
	
	/**
	 * Checks if the chunk at the given {@link ChunkPos} is forced to be alive.
	 * @param world The world to check in.
	 * @param pos The position to check in.
	 * @return True if the chunk is being kept alive, false if not.
	 */
	public static boolean isChunkKeptAlive(ServerLevel world, ChunkPos pos) {
		LongSet forcedChunks = world.getForcedChunks();
		return forcedChunks.contains(pos.toLong());
	}
	
	/**
	 * Tells the server level that the chunk containing the given {@link BlockPos} should be kept alive.
	 * @param world The world to modify.
	 * @param pos The position to modify.
	 * @param alive Whether or not to keep the chunk alive.
	 */
	public static void setChunkKeptAlive(ServerLevel world, BlockPos pos, boolean alive) {
		setChunkKeptAlive(world, new ChunkPos(pos), alive);
	}
	
	/**
	 * Tells the server level that the chunk at given {@link ChunkPos} should be kept alive.
	 * @param world The world to modify.
	 * @param pos The position to modify.
	 * @param alive Whether or not to keep the chunk alive.
	 */
	public static void setChunkKeptAlive(ServerLevel world, ChunkPos pos, boolean alive) {
		world.setChunkForced(pos.x, pos.z, alive);
	}
	
}
