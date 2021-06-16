package etithespirit.etimod.connection;

import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.exception.ArgumentNullException;
import etithespirit.etimod.util.blockstates.SixSidedUtils;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;

import static net.minecraft.state.properties.BlockStateProperties.*;
import static etithespirit.etimod.exception.ArgumentNullException.assertNotNull;
import static etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock.connectsAutomatically;

/**
 * Mostly intended for use in {@link ConnectableLightTechBlock}, this provides methods associated with testing connections.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class ConnectionHelper {
	
	private ConnectionHelper() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * Ensures that the given {@link BooleanProperty} represents one of the six directions (UP/DOWN/NORTH/SOUTH/EAST/WEST).
	 * @param state The {@link BooleanProperty} to test.
	 * @throws IllegalArgumentException if the given {@link BooleanProperty} is not one of the six directional properties.
	 */
	private static void assertCardinalBoolean(BooleanProperty state) throws IllegalArgumentException {
		if (state != UP && state != DOWN && state != NORTH && state != SOUTH && state != EAST && state != WEST) {
			throw new IllegalArgumentException("Cannot execute with a non-cardinal BooleanProperty! Expected UP/DOWN/NORTH/SOUTH/EAST/WEST");
		}
	}
	
	/**
	 * Returns whether or not the block {@code at} is able to establish a connection in the direction of {@code dir}.
	 * This ignores the state or block type of the neighbor in that direction.<br/>
	 * Note that if this is called when the block is placed or before all neighbor updates have completed, it may return false
	 * despite auto-connecting. This can be partly remedied by setting {@code anticipateAuto} to true if this method is used
	 * when the block is placed.
	 * @param reader Something to access the blocks in the world.
	 * @param at The location of the block to test.
	 * @param dir The face to test.
	 * @param anticipateAuto If true, then the automatic state of the block at {@code at} is tested as well (assuming that a block update will occur immediately after this method is called, connecting the two)
	 * @return Whether or not {@code at} is able to establish a connection in the direction of {@code dir}
	 * @throws ArgumentNullException If any of the three parameters are null.
	 * @throws IllegalArgumentException if the given {@link BlockPos} does not correspond to an instance of {@link ConnectableLightTechBlock}.
	 */
	public static boolean hasOutgoingConnectionInDirection(IBlockReader reader, BlockPos at, Direction dir, boolean anticipateAuto) throws ArgumentNullException, IllegalArgumentException {
		assertNotNull(reader, "reader");
		assertNotNull(at, "at");
		assertNotNull(dir, "dir");
		
		BlockState target = reader.getBlockState(at);
		ConnectableLightTechBlock connectable = ConnectableLightTechBlock.from(target);
		if (connectable == null) throw new IllegalArgumentException("The given BlockPos does not correspond to an instance of " + ConnectableLightTechBlock.class.getSimpleName() + " in the given world.");
		
		if (connectable.connectsFromAnySideAlways()) {
			return true;
		}
		
		BooleanProperty state = SixSidedUtils.getBlockStateFromDirection(dir);
		boolean isAlready = target.getValue(state);
		if (anticipateAuto && !isAlready) {
			return connectsAutomatically(target);
		}
		return isAlready;
	}
	
	/**
	 * Similar to {@link #hasOutgoingConnectionInDirection(IBlockReader, BlockPos, Direction, boolean)} but this also checks the neighbor to this block.
	 * @param reader Something to access the blocks in the world.
	 * @param at The location of the block to test.
	 * @param dir The face to test.
	 * @param anticipateWithAuto If true, then the automatic state of the block at {@code at} is tested as well (assuming that a block update will occur immediately after this method is called, connecting the two)
	 * @return Whether or not {@code at} is able to establish a connection in the direction of {@code dir}, and the block adjacent in that direction can establish a connection to this block, causing a proper connection.
	 * @throws ArgumentNullException If any of the three parameters are null.
	 * @throws IllegalArgumentException if the given {@link BlockPos} or its neighbor in the direction of {@code dir} does not correspond to an instance of {@link ConnectableLightTechBlock}.
	 */
	public static boolean hasMutualConnectionInDirection(IBlockReader reader, BlockPos at, Direction dir, boolean anticipateWithAuto) throws ArgumentNullException, IllegalArgumentException {
		boolean fromAt = hasOutgoingConnectionInDirection(reader, at, dir, anticipateWithAuto);
		boolean toAt = hasOutgoingConnectionInDirection(reader, at.offset(dir.getNormal()), dir.getOpposite(), anticipateWithAuto);
		return fromAt && toAt;
	}
	
	/**
	 * Similar to {@link #hasOutgoingConnectionInDirection(IBlockReader, BlockPos, Direction, boolean)} but this also checks the neighbor to this block.
	 * @param reader Something to access the blocks in the world.
	 * @param at The location of the block to test.
	 * @param other The location of the other block to test.
	 * @param anticipateWithAuto If true, then the automatic state of the block at {@code at} is tested as well (assuming that a block update will occur immediately after this method is called, connecting the two)
	 * @return Whether or not {@code at} is able to establish a connection in the direction of {@code dir}, and the block adjacent in that direction can establish a connection to this block, causing a proper connection.
	 * @throws ArgumentNullException If any of the three parameters are null.
	 * @throws IllegalArgumentException if the given {@link BlockPos} or its neighbor do not correspond to an instance of {@link ConnectableLightTechBlock}, or if the two {@link BlockPos} instances are not adjacent.
	 */
	public static boolean hasMutualConnectionInDirection(IBlockReader reader, BlockPos at, BlockPos other, boolean anticipateWithAuto) throws ArgumentNullException, IllegalArgumentException {
		Direction dir = fromBlockPos(at, other, true);
		boolean fromAt = hasOutgoingConnectionInDirection(reader, at, dir, anticipateWithAuto);
		boolean toAt = hasOutgoingConnectionInDirection(reader, at.offset(dir.getNormal()), dir.getOpposite(), anticipateWithAuto);
		return fromAt && toAt;
	}
	
	/**
	 * Given two instances of {@link BlockPos}, this will return a {@link Direction} from {@code from} -&gt; {@code to}.
	 * @param from The origin.
	 * @param to The destination.
	 * @param strictAdjacentOnly If true, this will throw {@link IllegalArgumentException} if the given {@link BlockPos} instances are not neighbors.
	 * @return A {@link Direction} representing the way to get from origin to destination.
	 * @throws IllegalArgumentException If {@code strictAdjacentOnly} is true and the two {@link BlockPos} instances are not neighbors.
	 */
	public static Direction fromBlockPos(BlockPos from, BlockPos to, boolean strictAdjacentOnly) throws IllegalArgumentException {
		Vector3i difference = to.subtract(from);
		
		if (strictAdjacentOnly) {
			if (Math.abs(difference.getX()) + Math.abs(difference.getY()) + Math.abs(difference.getZ()) != 1) {
				throw new IllegalArgumentException("The given BlockPos instances are not direct neighbors.");
			}
		}
		
		return Direction.fromNormal(difference.getX(), difference.getY(), difference.getZ());
	}
	
	/**
	 * Returns whether or not the given {@link BlockPos} instances are direct neighbors.
	 * @param alpha The first of the two to check.
	 * @param bravo The second of the two to check.
	 * @return True if the distance between these two positions is equal to 1.
	 */
	public static boolean areNeighbors(BlockPos alpha, BlockPos bravo) {
		Vector3i diff = alpha.subtract(bravo);
		return (Math.abs(diff.getX()) + Math.abs(diff.getY()) + Math.abs(diff.getZ())) == 1;
	}
	
}
