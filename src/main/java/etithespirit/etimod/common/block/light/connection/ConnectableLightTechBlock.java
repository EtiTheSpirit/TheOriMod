package etithespirit.etimod.common.block.light.connection;

import etithespirit.etimod.util.blockstates.SixSidedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import etithespirit.etimod.energy.ILightEnergyStorage;

import java.util.function.Consumer;

import static net.minecraft.state.properties.BlockStateProperties.*;

/**
 * Defines a block that serves as a non-complex means of handling Light energy. This should strictly be
 * used when energy numbers are not a part of this block's function. It should instead be used when
 * this block provides a means of allowing {@link ILightEnergyStorage} implementors
 * to communicate with one-another, for instance, by allowing two storage devices to transfer power through a pipeline.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public abstract class ConnectableLightTechBlock extends Block {
	
	/** Whether or not this should automatically connect to neighboring instances of {@link ConnectableLightTechBlock} */
	public static final BooleanProperty AUTO = BooleanProperty.create("autoconnect");
	
	/**
	 * Whether or not this is energized, which is not valid for use on Tile Entity providers implementing {@link ILightEnergyStorage}.
	 * Instead, this represents whether or not a passive Light-based block is handling Energy indirectly in some way, for instance, if a conduit is permitting transfer between two power sources. */
	public static final BooleanProperty ENERGIZED = BooleanProperty.create("in_use");
	
	protected ConnectableLightTechBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}
	
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
	
	@Override
	@SuppressWarnings("deprecated")
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
					
					BlockState newState = SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), SixSidedUtils.getNumberFromSurfaces(state) | flag);
					world.setBlockAndUpdate(at, newState);
					connectionStateChanged(state, newState);
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
						BlockState newState = state.setValue(prop, isOtherConnected);
						world.setBlockAndUpdate(at, newState);
						connectionStateChanged(state, newState);
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
					
					BlockState newState = SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), newFlags);
					world.setBlockAndUpdate(at, newState);
					connectionStateChanged(state, newState);
				}
			}
		}
	}
	
	/**
	 * Executes when the connection state of this block changes, like when connecting to or disconnecting from a neighboring {@link ConnectableLightTechBlock}.
	 * @param originalState The original state of this block prior to the connection changing.
	 * @param newState The new state of this block after the connection changed.
	 */
	public abstract void connectionStateChanged(BlockState originalState, BlockState newState);
	
	/**
	 * Tests if this block is set to always connect to its neighbors. If this is true, then not only are the cardinal
	 * {@link BooleanProperty} instances meaningless, but {@link #AUTO} is also meaningless (as this returning true is
	 * identical to always having outgoing connections, so no automation is needed in the first place).
	 * @return Whether or not this block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values / automatic state this block has.
	 */
	public boolean connectsFromAnySideAlways() {
		return false;
	}
	
	/**
	 * @return Whether or not the given block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values it has.
	 * @param block The {@link Block} to test.
	 */
	public static boolean connectsFromAnySideAlways(Block block) {
		if (block instanceof ConnectableLightTechBlock) {
			return ((ConnectableLightTechBlock)block).connectsFromAnySideAlways();
		}
		return false;
	}
	
	/**
	 * @return Whether or not the given block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values it has.
	 * @param state The {@link BlockState} containing the {@link Block} to test.
	 */
	public static boolean connectsFromAnySideAlways(BlockState state) {
		return connectsFromAnySideAlways(state.getBlock());
	}
	
	/**
	 * @param state The {@link BlockState} to test.
	 * @return Whether or not the given state has its value for {@link #AUTO} set.
	 */
	public static boolean connectsAutomatically(BlockState state) {
		return state.getValue(AUTO);
	}
	
}
