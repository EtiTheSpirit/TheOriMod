package etithespirit.orimod.server.world;

import etithespirit.orimod.OriMod;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;

/**
 * A utility class to manage the state of chunks being kept alive or not.
 *
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
	 * @param reservedPos The position to modify, which should correspond to a block entity that needs to keep a chunk alive.
	 * @param alive Whether or not to keep the chunk alive.
	 */
	public static void setChunkKeptAlive(ServerLevel world, BlockPos reservedPos, boolean alive) {
		ForgeChunkManager.forceChunk(world, OriMod.MODID, reservedPos, toChunkCoord(reservedPos.getX()), toChunkCoord(reservedPos.getY()), alive, true);
	}
	
	private static int toChunkCoord(int coord) {
		return coord >> 4;
	}
	
}
