package etithespirit.orimod.common.block.light.connection;


import etithespirit.orimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;


import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WEST;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.EAST;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.UP;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.DOWN;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.NORTH;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SOUTH;

import java.util.function.Consumer;

/**
 * Defines a block that serves as a non-complex means of handling Light energy. This should strictly be
 * used when energy numbers are not a part of this block's function. It should instead be used when
 * this block provides a means of allowing {@link ILightEnergyStorage} implementors
 * to communicate with one-another, for instance, by allowing two storage devices to transfer power through a pipeline.<br/>
 * <br/>
 * Blocks extending this class should always have a {@link net.minecraft.world.level.block.entity.BlockEntity TileEntity} associated with them, where
 * said tile extends {@link AbstractLightEnergyLink} or {@link AbstractLightEnergyHub}
 * @author Eti
 */
@SuppressWarnings("unused")
public abstract class ConnectableLightTechBlock extends Block implements EntityBlock {
	
	/** Whether or not this should automatically connect to neighboring instances of {@link ConnectableLightTechBlock} */
	public static final BooleanProperty AUTO = BooleanProperty.create("autoconnect");
	
	/**
	 * Whether or not this is energized, which is not valid for use on Tile Entity providers implementing {@link ILightEnergyStorage}.
	 * Instead, this represents whether or not a passive Light-based block is handling Energy indirectly in some way, for instance, if a conduit is permitting transfer between two power sources.
	 */
	public static final BooleanProperty ENERGIZED = BooleanProperty.create("in_use");
	
	/**
	 * Redirects to Block's ctor.
	 * @param props The properties of this block.
	 */
	protected ConnectableLightTechBlock(Properties props) {
		super(props);
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
	 * @param stateContainer The {@link StateDefinition} that houses the possible {@link BlockState}s.
	 */
	public static void autoRegisterDefaultState(Consumer<BlockState> initializer, StateDefinition<Block, BlockState> stateContainer) {
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
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
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
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState thisState, Level world, BlockPos at, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
		// BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor
		if (isInstance(thisState)) {
			// ^ This is connectable
			BlockState otherState = world.getBlockState(changedAt);
			if (isInstance(otherState)) {
				// ^ The changed block is connectable
				// Read: Something in the world was replaced with connectable block.
				// (Doesn't matter if what was there before was or wasn't connectable)
				ConnectableLightTechBlock other = from(otherState.getBlock());
				
				boolean otherIsAlwaysConnected = other.alwaysConnectsWhenPossible();
				boolean thisIsAlwaysConnected = this.alwaysConnectsWhenPossible();
				boolean otherAutoConnects = connectsAutomatically(otherState);
				boolean thisAutoConnects = connectsAutomatically(thisState);
				// ^ Connects on any side, doesn't require the cardinal states to be set to true.
				
				if (otherIsAlwaysConnected) {
					// The other block that replaced will force connect. Do we connect?
					if (!thisAutoConnects) return; // Nope! We are not automatic.
					int newFlag = SixSidedUtils.neighborFlagForBlockDirection(at, changedAt);
					int existingFlags = SixSidedUtils.getNumberFromSurfaces(thisState);
					BlockState newState = SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), existingFlags | newFlag);
					
					world.setBlockAndUpdate(at, newState);
					connectionStateChanged(thisState, newState);
				} else {
					// The other block that replaced will NOT force connect.
					// The question then becomes whether or not we will try to do the same.
					BooleanProperty prop = SixSidedUtils.getBlockStateForSingleFlagValue(SixSidedUtils.neighborFlagForBlockDirection(at, changedAt));
					BooleanProperty othersProp = SixSidedUtils.oppositeState(prop);
					// prop is my property connecting to other.
					// othersProp is the other block connecting to this.
					
					// Connect to other if other wants to connect to us.
					boolean isOtherConnected = otherState.getValue(othersProp);
					boolean isConnected = thisState.getValue(prop) | (thisAutoConnects | thisIsAlwaysConnected);
					if (isConnected == isOtherConnected) return;
					
					if (thisAutoConnects) {
						BlockState newState = thisState.setValue(prop, isOtherConnected);
						world.setBlockAndUpdate(at, newState);
						connectionStateChanged(thisState, newState);
					}
				}
			} else {
				// Something replaced the connectable that isn't connectable.
				if (!connectsAutomatically(thisState)) return; // Do nothing if we don't auto update.
				if (isInstance(replacedBlock)) {
					// Something destroyed the conduit
					int inverseFlag = ~SixSidedUtils.neighborFlagForBlockDirection(at, changedAt);
					// ^ Get the opposite of the flag if we *were* going to connect to this block
					int newFlags = SixSidedUtils.getNumberFromSurfaces(thisState) & inverseFlag;
					// ^ And take that away from the current flags to disable that side.
					
					BlockState newState = SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), newFlags);
					world.setBlockAndUpdate(at, newState);
					connectionStateChanged(thisState, newState);
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
	 * Tests if this block is set to always connect to any neighbor that is accepting connections in the appropriate direction.
	 * This is used for blocks like the Light Capacitor, which cannot have any given face turned on or off - under all circumstances, if something is connected to the
	 * face of a capacitor, then that connection is live.<br/>
	 * <br/>
	 * To name an example, if a conduit and a capacitor are next to eachother, then whether or not they are connected
	 * is <em>only</em> dependent on the conduit - the capacitor always connects, so the conduit is the only remaining thing that would be conditional.<br/>
	 * <br/>
	 * <strong>Note:</strong> When this is true, all six cardinal states as well as the automatic connection state are not meaningful, that is, it should be treated
	 * as if all seven of those states are true no matter what.
	 * @return Whether or not this block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values / automatic state this block has.
	 */
	public boolean alwaysConnectsWhenPossible() {
		return false;
	}
	
	/**
	 * @return The result of {@link #alwaysConnectsWhenPossible()} when called on the given block.
	 * If the block is not an instance of {@link ConnectableLightTechBlock} then this will return false.
	 * @param block The {@link Block} to test.
	 */
	public static boolean alwaysConnectsWhenPossible(Block block) {
		if (block instanceof ConnectableLightTechBlock connectable) {
			return connectable.alwaysConnectsWhenPossible();
		}
		return false;
	}
	
	/**
	 * @return Whether or not the given block can auto-connect to neighbors regardless of the cardinal {@link BooleanProperty} values it has.
	 * @param state The {@link BlockState} containing the {@link Block} to test.
	 */
	public static boolean alwaysConnectsWhenPossible(BlockState state) {
		return alwaysConnectsWhenPossible(state.getBlock());
	}
	
	/**
	 * @param state The {@link BlockState} to test.
	 * @return Whether or not the given state has its value for {@link #AUTO} set.
	 */
	public static boolean connectsAutomatically(BlockState state) {
		return state.getValue(AUTO);
	}
	
}
