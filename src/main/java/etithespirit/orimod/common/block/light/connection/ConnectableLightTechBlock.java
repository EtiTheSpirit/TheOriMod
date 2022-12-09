package etithespirit.orimod.common.block.light.connection;


import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.tile.light.LightEnergyTicker;
import etithespirit.orimod.common.tile.light.LightEnergyTile;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;


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
 * said tile extends {@link etithespirit.orimod.common.tile.light.LightEnergyTile}
 * @author Eti
 */
@SuppressWarnings("unused")
public abstract class ConnectableLightTechBlock extends Block implements EntityBlock, IBlockItemPropertiesProvider, IForlornBlueOrangeBlock {
	
	/** Whether or not this should automatically connect to neighboring instances of {@link ConnectableLightTechBlock} */
	public static final BooleanProperty AUTO = BooleanProperty.create("autoconnect");
	
	/**
	 * The desired user skin of this conduit, which can either be blue or orange.
	 */
	public static final BooleanProperty IS_BLUE = ForlornAppearanceMarshaller.IS_BLUE;
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
			                   .setValue(IS_BLUE, true)
		);
	}
	
	/**
	 * Should be called in the block's constructor {@code registerDefaultState(this::registerDefaultState, this.stateDefinition)}. This will automatically populate the default states.
	 * @param initializer The method that initializes the valid {@link BlockState}s
	 * @param stateContainer The {@link StateDefinition} that houses the possible {@link BlockState}s.
	 * @param addAdditionalStates A callback that can be used to add extra states on top of the defaults.
	 */
	public static void autoRegisterDefaultState(Consumer<BlockState> initializer, StateDefinition<Block, BlockState> stateContainer, Consumer<BlockState> addAdditionalStates) {
		BlockState state = stateContainer.any()
			.setValue(EAST, false)
			.setValue(WEST, false)
			.setValue(UP, false)
			.setValue(DOWN, false)
			.setValue(NORTH, false)
			.setValue(SOUTH, false)
			.setValue(AUTO, true)
			.setValue(IS_BLUE, true);
		addAdditionalStates.accept(state);
		initializer.accept(state);
	}
	
	/**
	 * @param state The state of a block to test.
	 * @return whether or not the given {@link BlockState} is an instance of {@link ConnectableLightTechBlock}
	 */
	public static boolean isConnectableBlock(BlockState state) {
		return isConnectableBlock(state.getBlock());
	}
	
	/**
	 * @param block The block to test.
	 * @return whether or not the given {@link Block} is an instance of {@link ConnectableLightTechBlock}
	 */
	public static boolean isConnectableBlock(Block block) {
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
		builder.add(IS_BLUE);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public PushReaction getPistonPushReaction(BlockState pState) {
		return PushReaction.BLOCK;
	}
	
	/*
	// TODO: Change my design so I can use this. Right now I depend on the reference to the previous block to have correct behavior.
	@Override
	public BlockState updateShape(BlockState thisState, Direction unusedDir, BlockState otherState, LevelAccessor world, BlockPos thisLocation, BlockPos changedAt) {
		// super.neighborChanged(thisState, world, thisLocation, replacedBlock, changedAt, isMoving);
		// BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor
		if (isConnectableBlock(thisState)) {
			// ^ This is connectable
			boolean isNewConnectable = isConnectableBlock(otherState);
			if (isNewConnectable) {
				ConnectableLightTechBlock other = from(otherState);
				// Next branch: Is this block or the neighboring block one that always connects in all directions no matter what?
				// Easy test: If both of them are, then that is no problem, in fact we just do nothing
				if (this.alwaysConnectsWhenPossible() && other.alwaysConnectsWhenPossible()) return thisState;
				if (isConnectedTo(world, thisLocation, changedAt)) return thisState;
				Direction dir = SixSidedUtils.getDirectionBetweenBlocks(thisLocation, changedAt);
				if (!shouldBeUpdatedToConnectTo(world, thisState, otherState, dir)) return thisState;
				
				BooleanProperty toSet = SixSidedUtils.getBlockStateFromDirection(dir);
				return thisState.setValue(toSet, true);
			} else if (!isNewConnectable) {
				Direction dir = SixSidedUtils.getDirectionBetweenBlocks(thisLocation, changedAt);
				BooleanProperty toSet = SixSidedUtils.getBlockStateFromDirection(dir);
				return thisState.setValue(toSet, false);
			}
		}
		return thisState;
	}
	*/
	
	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState thisState, Level world, BlockPos thisLocation, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
		super.neighborChanged(thisState, world, thisLocation, replacedBlock, changedAt, isMoving);
		// BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor
		if (isConnectableBlock(thisState)) {
			// ^ This is connectable
			BlockState otherState = world.getBlockState(changedAt);
			
			// The other state is currently connectable.
			boolean isOtherStateConnectable = isConnectableBlock(otherState);
			
			// The other state was previously not connectable, but it now is.
			boolean otherChangedIntoConnectable = !isConnectableBlock(replacedBlock) && isOtherStateConnectable;
			
			if (otherChangedIntoConnectable) {
				// The other state was previously not connectable, but it now is.
				ConnectableLightTechBlock other = from(otherState);
				if (this.alwaysConnectsWhenPossible() && other.alwaysConnectsWhenPossible()) {
					// Both are already autoconnect. A new connection has been created. Tell the systems that the block changed but in no direction.
					connectionStateChanged(thisState, thisState, thisLocation, world, null, true);
					return;
				}
				
				if (isConnectedTo(world, thisLocation, changedAt)) {
					// A connection has been made with the replacement block.
					connectionStateChanged(thisState, thisState, thisLocation, world, null, true);
					return;
				}
				
				Direction dir = SixSidedUtils.getDirectionBetweenBlocks(thisLocation, changedAt);
				if (!shouldBeUpdatedToConnectTo(world, thisState, otherState, dir)) {
					connectionStateChanged(thisState, thisState, thisLocation, world, null, true);
					return;
				}
				BooleanProperty toSet = SixSidedUtils.getBlockStateFromDirection(dir);
				BlockState thisNewState = thisState.setValue(toSet, true);
				world.setBlockAndUpdate(thisLocation, thisNewState);
				connectionStateChanged(thisState, thisNewState, thisLocation, world, toSet, false);
				
			} else if (isOtherStateConnectable) {
				// The other state is currently connectable, and given the build of this if statement, the change was connectable => connectable
				ConnectableLightTechBlock other = from(otherState);
				// Next branch: Is this block or the neighboring block one that always connects in all directions no matter what?
				// Easy test: If both of them are, then that is no problem, in fact we just do nothing
				if (this.alwaysConnectsWhenPossible() && other.alwaysConnectsWhenPossible()) {
					// Do NOT update cache here!
					return;
				}
				
				Direction dir = SixSidedUtils.getDirectionBetweenBlocks(thisLocation, changedAt);
				boolean thisShouldUpdateForOther = shouldBeUpdatedToConnectTo(world, thisState, otherState, dir);
				if (isConnectedTo(world, thisLocation, changedAt))  {
					// A connection has been made with the replacement block.
					connectionStateChanged(thisState, thisState, thisLocation, world, null, true);
					return;
				}
				
				//if (!thisShouldUpdateForOther) return;
				if (!thisShouldUpdateForOther) {
					connectionStateChanged(thisState, thisState, thisLocation, world, null, true);
					return;
				}
				if (otherState.getValue(SixSidedUtils.getBlockStateFromDirection(dir.getOpposite()))) {
					// Other wants to connect to this.
					BooleanProperty toSet = SixSidedUtils.getBlockStateFromDirection(dir);
					BlockState thisNewState = thisState.setValue(toSet, true);
					world.setBlockAndUpdate(thisLocation, thisNewState);
					connectionStateChanged(thisState, thisNewState, thisLocation, world, toSet, false);
				}
			} else {
				Direction dir = SixSidedUtils.getDirectionBetweenBlocks(thisLocation, changedAt);
				BooleanProperty toSet = SixSidedUtils.getBlockStateFromDirection(dir);
				BlockState thisNewState = thisState.setValue(toSet, false);
				world.setBlockAndUpdate(thisLocation, thisNewState);
				connectionStateChanged(thisState, thisNewState, thisLocation, world, toSet, false);
			}
		}
	}
	
	/**
	 * Executes when the connection state of this block changes, like when connecting to or disconnecting from a neighboring {@link ConnectableLightTechBlock}.
	 * @param originalState The original state of this block prior to the connection changing.
	 * @param newState The new state of this block after the connection changed.
	 * @param at The location of the change.
	 * @param inWorld The level the change occurred in.
	 * @param prop The property that was changed. This will be null if an existing connection was changed (see {@code existingConnectionChanged} parameter)
	 * @param existingConnectionChanged If true, a this occurred because one of the two connected blocks changed into a <em>different</em> connectable block - a connection was not newly created or destroyed, but rather changed.
	 */
	public abstract void connectionStateChanged(BlockState originalState, BlockState newState, BlockPos at, Level inWorld, @Nullable BooleanProperty prop, boolean existingConnectionChanged);
	
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
	 * @return A reference to this block's BlockEntity.
	 */
	public LightEnergyTile selfBE(BlockGetter world, BlockPos at) {
		return (LightEnergyTile)world.getBlockEntity(at);
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
	
	/**
	 * Given the two block positions, which must be adjacent, this returns whether or not the two blocks are connected.
	 * @param world The world to read from.
	 * @param from The origin block's position.
	 * @param to The destination block's position.
	 * @return True if the two blocks are both adjacent, and if both blocks either have the applicable cardinal states set and/or always connect in the given direction.
	 * @throws IllegalArgumentException If the two positions do not represent the positions of {@link ConnectableLightTechBlock}s.
	 */
	public static boolean isConnectedTo(BlockGetter world, BlockPos from, BlockPos to) throws IllegalArgumentException {
		BlockState fromState = world.getBlockState(from);
		BlockState toState = world.getBlockState(to);
		if (!isConnectableBlock(fromState) || !isConnectableBlock(toState)) throw new IllegalArgumentException("One or both of the given block positions did not correspond to an instance of " + ConnectableLightTechBlock.class.getSimpleName());
		
		Direction dir = SixSidedUtils.getDirectionBetweenBlocks(from, to);
		if (dir == null) return false;
		
		if (alwaysConnectsWhenPossible(fromState) && alwaysConnectsWhenPossible(toState)) return true;
		
		BooleanProperty fromProp = SixSidedUtils.getBlockStateFromDirection(dir);
		BooleanProperty toProp = SixSidedUtils.oppositeState(fromProp);
		boolean connectsFrom = alwaysConnectsWhenPossible(fromState) || fromState.getValue(fromProp);
		boolean connectsTo = alwaysConnectsWhenPossible(toState) || toState.getValue(toProp);
		return connectsFrom && connectsTo;
	}
	
	/**
	 * Assuming that {@code newState} was just placed in the world, and relative to {@code existingState} it is one block in the given direction,
	 * this method will observe the two states and determine if the existing state should be updated to connect to the new state.<br/>
	 * <strong>The following are important to note:</strong>
	 * <ol>
	 *     <li>This assumes that the newly placed state has, prior to being placed (i.e. in {@link #getStateForPlacement(BlockPlaceContext)}), set its state to connect to this existing block.</li>
	 *     <li>This returns false if the existing state is already connected in the given direction (and thus, no update is necessary).</li>
	 * </ol>
	 * @param world The world this change occurred in.
	 * @param existingState The connectable tech block that was already in the world.
	 * @param newState The connectable tech block that was just placed in the world (and resulted in this method being called)
	 * @param inDirection The new state exists in this direction one block away from the existing state.
	 * @return Whether or not the existing state should be updated to connect to the new state.
	 * @throws IllegalArgumentException If the either one or both of the two states are not instances of {@link ConnectableLightTechBlock}.
	 */
	public static boolean shouldBeUpdatedToConnectTo(BlockGetter world, BlockState existingState, BlockState newState, Direction inDirection) throws IllegalArgumentException {
		if (!isConnectableBlock(existingState) || !isConnectableBlock(newState)) throw new IllegalArgumentException("One or both of the given block states did not correspond to an instance of " + ConnectableLightTechBlock.class.getSimpleName());
		if (existingState.getValue(SixSidedUtils.getBlockStateFromDirection(inDirection))) return false;
		
		return connectsAutomatically(existingState) || alwaysConnectsWhenPossible(existingState);
	}
	
	
	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide) {
			return (BlockEntityTicker<T>)LightEnergyTicker.CLIENT;
		} else {
			return (BlockEntityTicker<T>)LightEnergyTicker.SERVER;
		}
	}
	
	@Override
	public Item.Properties getPropertiesOfItem() {
		return (new Item.Properties()).tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_COMPLETE);
	}
	
}
