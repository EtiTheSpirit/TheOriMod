package etithespirit.etimod.common.block.light;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.registry.SoundRegistry;
import etithespirit.etimod.util.EtiUtils;
import etithespirit.etimod.util.blockstates.SixSidedCollider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.state.properties.BlockStateProperties.*;
import static etithespirit.etimod.util.EtiUtils.hasFlag;

public class LightConduitBlock extends Block {
	
	public static final BooleanProperty AUTO = BooleanProperty.create("autoconnect");
	
	private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
	
	private static final VoxelShape V_UP = Block.box(4, 12, 4, 12, 16, 12);
	private static final VoxelShape V_DOWN = Block.box(4, 0, 4, 12, 4, 12);
	private static final VoxelShape V_EAST = Block.box(12, 4, 4, 16, 12, 12);
	private static final VoxelShape V_WEST = Block.box(0, 4, 4, 4, 12, 12);
	private static final VoxelShape V_NORTH = Block.box(4, 4, 0, 12, 12, 4);
	private static final VoxelShape V_SOUTH = Block.box(4, 4, 12, 12, 12, 16);
	
	protected static final VoxelShape[] COLLISION_SHAPES = SixSidedCollider.getBitwiseColliderArrayFor(LightConduitBlock::getShapeFor);
	
	public LightConduitBlock() {
		this(Properties.of(Material.STONE));
	}
	
	public LightConduitBlock(Properties props) {
		super(props);
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(EAST, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(NORTH, false)
				.setValue(SOUTH, false)
				.setValue(AUTO, true)
		);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	 * Given a numeric value with any of the right-most six bits set (0b00XXXXXX), this will return a collision box with a panel over the corresponding face.<br/>
	 * In order from left to right, the bits are: back, front, bottom, top, left, right<br/>
	 * For example, inputting {@code 0b100001} will return a collision box with a panel on the front and right surfaces of <em>this</em> block (so, that is, on the neighboring blocks it will be on the left face of the block to the right of this one, and on the back face of the block in front of this one.)
	 * @param flags
	 * @return
	 */
	private static VoxelShape getShapeFor(int flags) {
		if (flags == 0) return CORE;
		
		boolean right = hasFlag(flags, 0b000001);
		boolean left = hasFlag(flags, 0b000010);
		boolean top = hasFlag(flags, 0b000100);
		boolean bottom = hasFlag(flags, 0b001000);
		boolean front = hasFlag(flags, 0b010000);
		boolean back = hasFlag(flags, 0b100000);
		
		VoxelShape def = CORE;
		if (right) def = VoxelShapes.or(def, V_EAST);
		if (left) def = VoxelShapes.or(def, V_WEST);
		if (top) def = VoxelShapes.or(def, V_UP);
		if (bottom) def = VoxelShapes.or(def, V_DOWN);
		if (front) def = VoxelShapes.or(def, V_NORTH);
		if (back) def = VoxelShapes.or(def, V_SOUTH);
		
		return def;
	}
	
	/**
	 * Returns the shape of the collision box.
	 */
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return COLLISION_SHAPES[SixSidedCollider.getNumberFromSurfaces(state)];
		
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockPos pos = ctx.getClickedPos();
		int neighborFlags = SixSidedCollider.getNeighborsWhere(ctx.getLevel(), pos, state -> {
			boolean isLightConduit = state.getBlock() instanceof LightConduitBlock;
			if (isLightConduit) return state.getValue(AUTO);
			return false;
		});
		if (neighborFlags != 0) {
			// Auto-connection was made.
			ctx.getLevel().playSound(null, ctx.getClickedPos(), SoundRegistry.get("item.lumo_wand.swapconduitauto"), SoundCategory.BLOCKS, 0.2f, 1f);
		}
		return SixSidedCollider.withSurfaceFlags(this.defaultBlockState(), neighborFlags);
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos at, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
		
		if (state.getBlock() instanceof LightConduitBlock) {
			// ^ This is a conduit
			BlockState other = world.getBlockState(changedAt);
			if (other.getBlock() instanceof LightConduitBlock) {
				// ^ The changed block is a conduit
				// Something replaced with conduit block.
				
				BooleanProperty prop = SixSidedCollider.BITWISE_ASSOCIATIONS.get(SixSidedCollider.neighborFlagForBlock(world, at, changedAt));
				BooleanProperty othersProp = SixSidedCollider.oppositeState(prop);
				// prop is my property connecting to other.
				// othersprop is the other block connecting to this.
				
				// Connect to other if other wants to connect to us.
				boolean isOtherConnected = other.getValue(othersProp);
				boolean isConnected = state.getValue(prop);
				if (isConnected == isOtherConnected) return;
				
				if (state.getValue(AUTO)) {
					world.setBlockAndUpdate(at, state.setValue(prop, isOtherConnected));
				}
			} else {
				if (!state.getValue(AUTO)) return;
				if (replacedBlock instanceof LightConduitBlock) {
					// Something destroyed the conduit
					int inverseFlag = ~SixSidedCollider.neighborFlagForBlock(world, at, changedAt);
					world.setBlockAndUpdate(at, SixSidedCollider.withSurfaceFlags(this.defaultBlockState(), SixSidedCollider.getNumberFromSurfaces(state) & inverseFlag));
				}
			}
		}
	}
	
	/*
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos at, PlayerEntity player, Hand hand, BlockRayTraceResult rt) {
		BooleanProperty blockFaceState = SixSidedCollider.getBlockStateFromEvidentFace(rt);
		world.setBlockAndUpdate(at, state.setValue(blockFaceState, !state.getValue(blockFaceState)));
		return ActionResultType.SUCCESS;
	}
	*/
}
