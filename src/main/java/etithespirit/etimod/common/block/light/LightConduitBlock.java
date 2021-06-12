package etithespirit.etimod.common.block.light;

import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.registry.SoundRegistry;
import etithespirit.etimod.util.blockstates.SixSidedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import static etithespirit.etimod.util.EtiUtils.hasFlag;

public class LightConduitBlock extends ConnectableLightTechBlock {
	
	private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
	
	private static final VoxelShape V_UP = Block.box(4, 12, 4, 12, 16, 12);
	private static final VoxelShape V_DOWN = Block.box(4, 0, 4, 12, 4, 12);
	private static final VoxelShape V_EAST = Block.box(12, 4, 4, 16, 12, 12);
	private static final VoxelShape V_WEST = Block.box(0, 4, 4, 4, 12, 12);
	private static final VoxelShape V_NORTH = Block.box(4, 4, 0, 12, 12, 4);
	private static final VoxelShape V_SOUTH = Block.box(4, 4, 12, 12, 12, 16);
	
	protected static final VoxelShape[] COLLISION_SHAPES = SixSidedUtils.getBitwiseColliderArrayFor(LightConduitBlock::getShapeFor);
	
	public LightConduitBlock() {
		this(Properties.of(Material.STONE));
	}
	
	public LightConduitBlock(Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition);
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
		return COLLISION_SHAPES[SixSidedUtils.getNumberFromSurfaces(state)];
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockPos pos = ctx.getClickedPos();
		int neighborFlags = SixSidedUtils.getFlagsForNeighborsWhere(ctx.getLevel(), pos, state -> {
			boolean isConnectable = state.getBlock() instanceof ConnectableLightTechBlock;
			if (isConnectable) return state.getValue(AUTO);
			return false;
		});
		if (neighborFlags != 0) {
			// Auto-connection was made.
			// TODO: Only play this if the wires are live when connected.
			ctx.getLevel().playSound(null, ctx.getClickedPos(), SoundRegistry.get("item.lumo_wand.swapconduitauto"), SoundCategory.BLOCKS, 0.2f, 1f);
		}
		return SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), neighborFlags);
		
	}
}
