package etithespirit.etimod.common.block.light.connection;

import etithespirit.etimod.util.EtiUtils;
import etithespirit.etimod.util.blockstates.SixSidedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import etithespirit.etimod.energy.ILightEnergyStorage;

import java.util.function.Consumer;

import static etithespirit.etimod.info.coordinate.Cardinals.ADJACENTS_IN_ORDER;
import static net.minecraft.state.properties.BlockStateProperties.*;

/**
 * Defines a block that serves as a non-complex means of handling Light energy. This should strictly be
 * used when energy numbers are not a part of this block's function. It should instead be used when
 * this block provides a means of allowing {@link ILightEnergyStorage} implementors
 * to communicate with one-another, for instance, by allowing two storage devices to transfer power through a pipeline.
 */
public abstract class ConnectableLightTechBlock extends Block {
	
	/** Whether or not this should automatically connect to neighboring instances of {@link ConnectableLightTechBlock} */
	public static final BooleanProperty AUTO = BooleanProperty.create("autoconnect");
	
	/**
	 * Whether or not this is energized, which is not valid for use on Tile Entity providers implementing {@link ILightEnergyStorage}.
	 * Instead, this represents whether or not a passive Light-based block is handling Energy indirectly in some way, for instance, if a conduit is permitting transfer between two power sources. */
	public static final BooleanProperty ENERGIZED = BooleanProperty.create("in_use");
	
	/**
	 * If the given {@link Block} is an instance of {@link ConnectableLightTechBlock}, this will return Block as that type. Otherwise, this returns null.
	 * This is comparable to the "as" keyword in C#.
	 * @param block The block to test.
	 * @return The given block as a {@link ConnectableLightTechBlock} if it is an instance, or null if it is not.
	 */
	public static ConnectableLightTechBlock from(Block block) {
		if (block instanceof ConnectableLightTechBlock) return (ConnectableLightTechBlock) block;
		return null;
	}
	
	/**
	 * If the given {@link BlockState}'s {@link Block} is an instance of {@link ConnectableLightTechBlock}, this will return Block as that type. Otherwise, this returns null.
	 * This is comparable to the "as" keyword in C#.
	 * @param state The block to test.
	 * @return The given block as a {@link ConnectableLightTechBlock} if it is an instance, or null if it is not.
	 */
	public static ConnectableLightTechBlock from(BlockState state) {
		if (state == null) return null;
		return ConnectableLightTechBlock.from(state.getBlock());
	}
	
	protected ConnectableLightTechBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}
	
	/**
	 * Should be called in the block's constructor {@code registerDefaultState(this::registerDefaultState, this.stateDefinition)}. This will automatically populate the default states.
	 * @param initializer The method that initializes the valid {@link BlockState}s
	 * @param stateContainer The {@link StateContainer} that houses the possible {@link BlockState}s.
	 */
	public static void autoRegisterDefaultState(Consumer<BlockState> initializer, StateContainer<Block, BlockState> stateContainer) {
		initializer.accept(stateContainer.any()
           .setValue(EAST, false)
           .setValue(WEST, false)
           .setValue(UP, false)
           .setValue(DOWN, false)
           .setValue(NORTH, false)
           .setValue(SOUTH, false)
           .setValue(AUTO, true)
		   .setValue(ENERGIZED, false)
		);
	}
	
	/**
	 * @param state The state of a block to test.
	 * @return whether or not the given {@link BlockState} is an instance of {@link ConnectableLightTechBlock}
	 */
	public static boolean isInstance(BlockState state) {
		return isInstance(state.getBlock());
	}
	
	/**
	 * @param block The block to test.
	 * @return whether or not the given {@link Block} is an instance of {@link ConnectableLightTechBlock}
	 */
	public static boolean isInstance(Block block) {
		return block instanceof ConnectableLightTechBlock;
	}
	
	/**
	 * <strong>When overriding, call super FIRST, then run your own code.</strong>
	 * @param builder The builder that assembles the valid {@link BlockState}s
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void createBlockStateDefinition(StateContainer.Builder builder) {
		builder.add(EAST);
		builder.add(WEST);
		builder.add(UP);
		builder.add(DOWN);
		builder.add(NORTH);
		builder.add(SOUTH);
		builder.add(AUTO);
		builder.add(ENERGIZED);
	}
	
	
	/**
	 * The key word of this method is <strong>connectable</strong>.
	 * This does <strong>NOT</strong> mean that it is necessarily connected to something!
	 * This reads the given {@link BlockState} and returns its cardinal direction flags.<br/>
	 * <br/>
	 * To check for actual connections, use {@link #getConnectedSurfaceFlags(IWorldReader, BlockPos)}
	 * @param state The state of this block.
	 * @return A flags value representing the faces that can be connected to.
	 * @throws IllegalArgumentException If the input {@link BlockState} does not correspond to an instance of {@link ConnectableLightTechBlock}.
	 */
	public static int getConnectableSurfaceFlags(BlockState state) throws IllegalArgumentException {
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
	public static int getConnectedSurfaceFlags(IWorldReader world, BlockPos thisPos) throws IllegalArgumentException {
		BlockState myState = world.getBlockState(thisPos);
		if (!isInstance(myState)) throw new IllegalArgumentException("The given position does not have an instance of " + ConnectableLightTechBlock.class.getSimpleName() + " in the given world!");
		int retnFlags = 0;
		for (int idx = 0; idx < ADJACENTS_IN_ORDER.length; idx++) {
			Vector3i cardinal = ADJACENTS_IN_ORDER[idx];
			BlockState neighbor = world.getBlockState(thisPos.offset(cardinal));
			if (isInstance(neighbor)) {
				// Okay, step one complete. Neighbor is connectable.
				int bit = 1 << idx;
				BooleanProperty myConnection = SixSidedUtils.getBlockStateForSingleFlagValue(bit);
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
	public static boolean isConnectedInDirections(IWorldReader world, BlockPos thisPos, int connectionFlags) throws IllegalArgumentException {
		BlockState myState = world.getBlockState(thisPos);
		if (!isInstance(myState)) throw new IllegalArgumentException("The given position does not have an instance of " + ConnectableLightTechBlock.class.getSimpleName() + " in the given world!");
		
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
				BooleanProperty myConnection = SixSidedUtils.getBlockStateForSingleFlagValue(bit);
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
	
	/**
	 * Returns whether or not the two given {@link BlockPos} instances, which are expected to be adjacent to one-another, are
	 * connected to eachother via their state.
	 * @param world The world that the blocks exist in.
	 * @param alpha The first of the two blocks to test.
	 * @param bravo The second of the two blocks to test.
	 * @return True if both blocks have a directional state set to TRUE that face eachother, false if not.
	 * @throws IllegalArgumentException If the input {@link BlockPos} instances point to blocks that are not instances of {@link ConnectableLightTechBlock}
	 */
	public static boolean isConnected(IWorldReader world, BlockPos alpha, BlockPos bravo) throws IllegalArgumentException {
		BlockState a = world.getBlockState(alpha);
		BlockState b = world.getBlockState(bravo);
		if (!isInstance(a) || !isInstance(b)) throw new IllegalArgumentException("At least one given position does not have an instance of " + ConnectableLightTechBlock.class.getSimpleName() + " in the given world!");
		
		int flag = SixSidedUtils.neighborFlagForBlockDirection(alpha, bravo);
		BooleanProperty connectedA = SixSidedUtils.getBlockStateForSingleFlagValue(flag);
		BooleanProperty connectedB = SixSidedUtils.oppositeState(connectedA);
		
		boolean isAConnected = a.getValue(connectedA) || from(a).connectsFromAnySideAlways();
		boolean isBConnected = b.getValue(connectedB) || from(b).connectsFromAnySideAlways();
		
		return isAConnected && isBConnected;
	}
	
	/**
	 * Returns whether or not the two given {@link BlockPos} instances, which are expected to be adjacent to one-another, are
	 * going to connect as soon as they both receive a neighbor update signal. This is useful for cases of checking connection
	 * before they actually connect.<br/>
	 * <br/>
	 * <strong>NOTE:</strong> This does not return true if the two are actively connected. Use {@link #isConnected(IWorldReader, BlockPos, BlockPos)} to test that.
	 * Additionally, <strong>this should be used if alpha and/or bravo are subjects of block updates.</strong>
	 * Without ensuring this, this might falsely return true if they are both auto-connectors but were manually disconnected.
	 * @param world The world that the blocks exist in.
	 * @param alpha The first of the two blocks to test.
	 * @param bravo The second of the two blocks to test.
	 * @return True if these two blocks will initiate a connection to eachother once they both receive a block neighbor update signal.
	 * @throws IllegalArgumentException If the input {@link BlockPos} instances point to blocks that are not instances of {@link ConnectableLightTechBlock}
	 */
	public static boolean willConnect(IWorldReader world, BlockPos alpha, BlockPos bravo) throws IllegalArgumentException {
		BlockState a = world.getBlockState(alpha);
		BlockState b = world.getBlockState(bravo);
		if (!isInstance(a) || !isInstance(b)) throw new IllegalArgumentException("At least one given position does not have an instance of " + ConnectableLightTechBlock.class.getSimpleName() + " in the given world!");
		
		int flag = SixSidedUtils.neighborFlagForBlockDirection(alpha, bravo);
		BooleanProperty connectedA = SixSidedUtils.getBlockStateForSingleFlagValue(flag);
		BooleanProperty connectedB = SixSidedUtils.oppositeState(connectedA);
		
		boolean isAAuto = a.getValue(AUTO);
		boolean isBAuto = b.getValue(AUTO);
		
		ConnectableLightTechBlock aCon = from(a);
		ConnectableLightTechBlock bCon = from(b);
		
		if (isAAuto && isBAuto) return true; // Both have auto connection on, this will work.
		if (!isAAuto && isBAuto) return a.getValue(connectedA) || aCon.connectsFromAnySideAlways(); // B has auto on, A is trying to connect
		//noinspection ConstantConditions
		if (isAAuto && !isBAuto) return b.getValue(connectedB) || bCon.connectsFromAnySideAlways(); // Swap B and A above.
		
		return false; // This strictly returns if they *will* connect, not if they *are* connected.
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, World world, BlockPos at, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
		if (isInstance(state)) {
			// ^ This is connectable
			BlockState other = world.getBlockState(changedAt);
			if (isInstance(other)) {
				// ^ The changed block is connectable
				// Something replaced with connectable block.
				
				boolean isOtherAnyConnect = connectsFromAnySideAlways(other);
				boolean isSelfAnyConnect = this.connectsFromAnySideAlways();
				// ^ Connects on any side, doesn't require the cardinal states to be set to true.
				
				if (isOtherAnyConnect) {
					if (!state.getValue(AUTO)) return; // Unless we aren't automatic.
					int flag = SixSidedUtils.neighborFlagForBlockDirection(at, changedAt);
					world.setBlockAndUpdate(at, SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), SixSidedUtils.getNumberFromSurfaces(state) | flag));
					
				} else {
					BooleanProperty prop = SixSidedUtils.getBlockStateForSingleFlagValue(SixSidedUtils.neighborFlagForBlockDirection(at, changedAt));
					BooleanProperty othersProp = SixSidedUtils.oppositeState(prop);
					// prop is my property connecting to other.
					// othersprop is the other block connecting to this.
					
					// Connect to other if other wants to connect to us.
					boolean isOtherConnected = other.getValue(othersProp);
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
	 * @return Whether or not this block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values this block has.
	 */
	public boolean connectsFromAnySideAlways() {
		return false;
	}
	
	/**
	 * @return Whether or not the given block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values it has.
	 */
	public static boolean connectsFromAnySideAlways(Block block) {
		if (block instanceof ConnectableLightTechBlock) {
			return ((ConnectableLightTechBlock)block).connectsFromAnySideAlways();
		}
		return false;
	}
	
	/**
	 * @return Whether or not the given block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values it has.
	 */
	public static boolean connectsFromAnySideAlways(BlockState state) {
		return connectsFromAnySideAlways(state.getBlock());
	}
	
}
