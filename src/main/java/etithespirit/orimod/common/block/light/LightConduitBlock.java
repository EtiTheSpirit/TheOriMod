package etithespirit.orimod.common.block.light;


import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.tile.light.TileEntityLightEnergyConduit;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import etithespirit.orimod.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

import static etithespirit.orimod.util.Bit32.hasFlag;

public class LightConduitBlock extends ConnectableLightTechBlock implements IToolRequirementProvider {
	
	private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
	
	private static final VoxelShape V_UP = Block.box(4, 12, 4, 12, 16, 12);
	private static final VoxelShape V_DOWN = Block.box(4, 0, 4, 12, 4, 12);
	private static final VoxelShape V_EAST = Block.box(12, 4, 4, 16, 12, 12);
	private static final VoxelShape V_WEST = Block.box(0, 4, 4, 4, 12, 12);
	private static final VoxelShape V_NORTH = Block.box(4, 4, 0, 12, 12, 4);
	private static final VoxelShape V_SOUTH = Block.box(4, 4, 12, 12, 12, 16);
	
	protected static final VoxelShape[] COLLISION_SHAPES = SixSidedUtils.getBitwiseColliderArrayFor(LightConduitBlock::getShapeFor);
	
	public LightConduitBlock() {
		this(BlockBehaviour.Properties.of(Material.STONE));
	}
	
	public LightConduitBlock(Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition);
	}
	
	/**
	 * Given a numeric value with any of the right-most six bits set (0b00XXXXXX), this will return a collision box with a panel over the corresponding face.<br/>
	 * In order from left to right, the bits are: back, front, bottom, top, left, right<br/>
	 * For example, inputting {@code 0b100001} will return a collision box with a panel on the front and right surfaces of <em>this</em> block (so, that is, on the neighboring blocks it will be on the left face of the block to the right of this one, and on the back face of the block in front of this one.)
	 * @param flags The flags representing which surfaces are enabled.
	 * @return A {@link VoxelShape} constructed from these flags.
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
		if (right) def = Shapes.or(def, V_EAST);
		if (left) def = Shapes.or(def, V_WEST);
		if (top) def = Shapes.or(def, V_UP);
		if (bottom) def = Shapes.or(def, V_DOWN);
		if (front) def = Shapes.or(def, V_NORTH);
		if (back) def = Shapes.or(def, V_SOUTH);
		
		return def;
	}
	
	/**
	 * Returns the shape of the collision box.
	 */
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return COLLISION_SHAPES[SixSidedUtils.getNumberFromSurfaces(state)];
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		
		BlockPos pos = ctx.getClickedPos();
		int neighborFlags = SixSidedUtils.getFlagsForNeighborsWhere(ctx.getLevel(), pos, state -> {
			boolean isConnectable = state.getBlock() instanceof ConnectableLightTechBlock;
			if (isConnectable) return state.getValue(AUTO);
			return false;
		});
		if (neighborFlags != 0) {
			// Auto-connection was made.
			// TODO: Only play this if the wires are live when connected, otherwise it just sounds really annoying.
			ctx.getLevel().playSound(null, ctx.getClickedPos(), SoundRegistry.get("item.lumo_wand.swapconduitauto"), SoundSource.BLOCKS, 0.1f, 1f);
		}
		return SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), neighborFlags);
		
	}
	
	@Override
	public void connectionStateChanged(BlockState originalState, BlockState newState) {
	
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos state, BlockState world) {
		return new TileEntityLightEnergyConduit(state, world);
	}
	
	@Override
	public Iterable<Tag.Named<Block>> getTagsForBlock() {
		return List.of(BlockTags.MINEABLE_WITH_PICKAXE);
	}
}
