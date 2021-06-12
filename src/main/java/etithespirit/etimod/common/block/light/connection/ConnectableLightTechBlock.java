package etithespirit.etimod.common.block.light.connection;

import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.util.EtiUtils;
import etithespirit.etimod.util.blockstates.SixSidedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.function.Consumer;

import static etithespirit.etimod.info.coordinate.Cardinals.ADJACENTS_IN_ORDER;
import static net.minecraft.state.properties.BlockStateProperties.*;

/**
 * Defines a block
 */
public abstract class ConnectableLightTechBlock extends Block {
	
	public static final BooleanProperty AUTO = BooleanProperty.create("autoconnect");
	
	protected ConnectableLightTechBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}
	
	/**
	 * Should be called in the block's constructor {@code registerDefaultState(this::registerDefaultState, this.stateDefinition)}. This will automatically populate the default states.
	 * @param initializer
	 * @param stateContainer
	 */
	public static void registerDefaultState(Consumer<BlockState> initializer, StateContainer<Block, BlockState> stateContainer) {
		initializer.accept(stateContainer.any()
           .setValue(EAST, false)
           .setValue(WEST, false)
           .setValue(UP, false)
           .setValue(DOWN, false)
           .setValue(NORTH, false)
           .setValue(SOUTH, false)
           .setValue(AUTO, true)
		);
	}
	
	/**
	 * Returns whether or not the given {@link BlockState} is an instance of {@link ConnectableLightTechBlock}
	 * @param state The state of a block to test.
	 * @return
	 */
	public static boolean isInstance(BlockState state) {
		return isInstance(state.getBlock());
	}
	
	/**
	 * Returns whether or not the given {@link Block} is an instance of {@link ConnectableLightTechBlock}
	 * @param block The block to test.
	 * @return
	 */
	public static boolean isInstance(Block block) {
		return block instanceof ConnectableLightTechBlock;
	}
	
	/**
	 * <strong>When overriding, call super FIRST, then run your own code.</strong>
	 * @param builder
	 */
	@Override
	public void createBlockStateDefinition(StateContainer.Builder builder) {
		builder.add(EAST);
		builder.add(WEST);
		builder.add(UP);
		builder.add(DOWN);
		builder.add(NORTH);
		builder.add(SOUTH);
		builder.add(AUTO);
	}
	
	
	/**
	 * The key word of this method is <strong>connectable</strong>.
	 * This does <strong>NOT</strong> mean that it is necessarily connected to something!
	 * This reads the given {@link BlockState} and returns its cardinal direction flags.<br/>
	 * <br/>
	 * To check for actual connections, use {@link #getConnectedSurfaceFlags(World, BlockPos)}
	 * @param state The state of this block.
	 * @return
	 * @throws IllegalArgumentException If the input {@link BlockState} does not correspond to an instance of {@link ConnectableLightTechBlock}.
	 */
	public int getConnectableSurfaceFlags(BlockState state) throws IllegalArgumentException {
		return SixSidedUtils.getNumberFromSurfaces(state);
	}
	
	/**
	 * The key word of this method is <strong>connected</strong>.
	 * This reads all six adjacent blocks in the world and checks if they are an instance of {@link ConnectableLightTechBlock},
	 * and then checks if they are connected to this.<br/>
	 * <br/>
	 * To check this {@link BlockState} only, which may not be representative of actual live connections, use {@link #getConnectableSurfaceFlags(BlockState)}
	 * @param world The world that this block exists in.
	 * @param thisPos The position of this block.
	 * @return Cardinal flags that are usable in {@link SixSidedUtils}
	 * @throws IllegalArgumentException If the input {@link BlockPos} does not correspond to an instance of {@link ConnectableLightTechBlock}.
	 */
	public int getConnectedSurfaceFlags(World world, BlockPos thisPos) throws IllegalArgumentException {
		BlockState myState = world.getBlockState(thisPos);
		if (!isInstance(myState)) throw new IllegalArgumentException("The given position does not have an instance of " + getClass().getSimpleName() + " in the given world!");
		int retnFlags = 0;
		for (int idx = 0; idx < ADJACENTS_IN_ORDER.length; idx++) {
			Vector3i cardinal = ADJACENTS_IN_ORDER[idx];
			BlockState neighbor = world.getBlockState(thisPos.offset(cardinal));
			if (isInstance(neighbor)) {
				// Okay, step one complete. Neighbor is connectable.
				int bit = 1 << idx;
				BooleanProperty myConnection = SixSidedUtils.BITWISE_ASSOCIATIONS.get(bit);
				BooleanProperty otherConnection = SixSidedUtils.oppositeState(myConnection);
				
				boolean connectionFromMe = myState.getValue(myConnection);
				boolean connectionToMe = neighbor.getValue(otherConnection);
				
				if (connectionFromMe && connectionToMe) {
					retnFlags |= bit;
				}
			}
		}
		return retnFlags;
	}
	
	/**
	 * Returns whether or not the given flags are <em>all</em> connected to their associated neighbors.
	 * The connection flags can be acquired via {@link SixSidedUtils}.<br/>
	 * <br/>
	 * <strong>Edge Cases:</strong>
	 * <ul>
	 *      <li>Inputting a {@code connectionFlags} value of 0 will always return {@code false}</li>
	 *      <li>If connectionFlags attempts to test at least one neighbor that is not an instance of {@link ConnectableLightTechBlock}, this will return false</li>
	 * </ul>
	 *
	 * @param world The world that this block exists in.
	 * @param thisPos The position of this block.
	 * @param connectionFlags A six-bit value representing cardinal directions on a block. This is assembled in {@link SixSidedUtils}
	 * @return True if all of the given flags represent a valid, live connection to this block, and false if they do not.
	 * @throws IllegalArgumentException If thisPos does not represent the position of an instance of {@link ConnectableLightTechBlock} in the given world.
	 */
	public boolean isConnectedInDirections(World world, BlockPos thisPos, int connectionFlags) throws IllegalArgumentException {
		BlockState myState = world.getBlockState(thisPos);
		if (!isInstance(myState)) throw new IllegalArgumentException("The given position does not have an instance of " + getClass().getSimpleName() + " in the given world!");
		
		if (connectionFlags == 0) return false;
		for (int shl = 0; shl < 6; shl++) {
			int bit = 1 << shl;
			if (EtiUtils.hasFlag(connectionFlags, bit)) {
				// Now here I could have just used getConnectedSurfaceFlags and then used hasFlag for connectionFlags, but that'd
				// be very wasteful and check blocks that have nothing to do with what the caller wants.
				// So don't do that.
				BlockState neighbor = world.getBlockState(thisPos.offset(ADJACENTS_IN_ORDER[shl]));
				if (!isInstance(neighbor)) {
					// Well that's easy.
					return false;
				}
				BooleanProperty myConnection = SixSidedUtils.BITWISE_ASSOCIATIONS.get(bit);
				BooleanProperty otherConnection = SixSidedUtils.oppositeState(myConnection);
				boolean connectionFromMe = myState.getValue(myConnection);
				boolean connectionToMe = neighbor.getValue(otherConnection);
				
				if (!(connectionFromMe && connectionToMe)) {
					// No connection here, that's one that failed. Not connected in all directions,
					// so stop here and don't bother testing anything else.
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos at, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
		if (isInstance(state)) {
			// ^ This is connectable
			BlockState other = world.getBlockState(changedAt);
			if (isInstance(other)) {
				// ^ The changed block is connectable
				// Something replaced with connectable block.
				
				boolean isOtherAnyConnect = connectsFromAnySideAlways(other);
				boolean isSelfAnyConnect = this.connectsFromAnySideAlways();
				if (isOtherAnyConnect) {
					if (!state.getValue(AUTO)) return; // Unless we aren't automatic.
					int flag = ~SixSidedUtils.neighborFlagForBlockDirection(at, changedAt);
					world.setBlockAndUpdate(at, SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), SixSidedUtils.getNumberFromSurfaces(state) & flag));
				} else {
					BooleanProperty prop = SixSidedUtils.BITWISE_ASSOCIATIONS.get(SixSidedUtils.neighborFlagForBlockDirection(at, changedAt));
					BooleanProperty othersProp = SixSidedUtils.oppositeState(prop);
					// prop is my property connecting to other.
					// othersprop is the other block connecting to this.
					
					// Connect to other if other wants to connect to us.
					boolean isOtherConnected = other.getValue(othersProp) | isOtherAnyConnect;
					boolean isConnected = state.getValue(prop) | isSelfAnyConnect;
					if (isConnected == isOtherConnected) return;
					
					if (state.getValue(AUTO)) {
						world.setBlockAndUpdate(at, state.setValue(prop, isOtherConnected));
					}
				}
			} else if (!ConnectableLightTechBlock.isInstance(other)) {
				// Something replaced the connectable that isn't connectable.
				if (!state.getValue(AUTO)) return;
				if (isInstance(replacedBlock)) {
					// Something destroyed the conduit
					int inverseFlag = ~SixSidedUtils.neighborFlagForBlockDirection(at, changedAt);
					// ^ Get the opposite of the flag if we *were* going to connect to this block
					int newFlags = SixSidedUtils.getNumberFromSurfaces(state) & inverseFlag;
					// ^ And take that away from the current flags to disable that side.
					world.setBlockAndUpdate(at, SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), newFlags));
				}
			}
		}
	}
	
	/**
	 * Whether or not this block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values this block has.
	 * @return
	 */
	public boolean connectsFromAnySideAlways() {
		return false;
	}
	
	/**
	 * Whether or not the given block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values it has.
	 * @return
	 */
	public static boolean connectsFromAnySideAlways(Block block) {
		if (block instanceof ConnectableLightTechBlock) {
			return ((ConnectableLightTechBlock)block).connectsFromAnySideAlways();
		}
		return false;
	}
	
	/**
	 * Whether or not the given block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values it has.
	 * @return
	 */
	public static boolean connectsFromAnySideAlways(BlockState state) {
		return connectsFromAnySideAlways(state.getBlock());
	}
	
}
