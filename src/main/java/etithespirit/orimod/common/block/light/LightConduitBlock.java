package etithespirit.orimod.common.block.light;


import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.LightEnergyTile;
import etithespirit.orimod.common.tile.light.implementations.LightConduitTile;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.util.Bit32;
import etithespirit.orimod.util.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.concurrent.atomic.AtomicBoolean;

import static etithespirit.orimod.util.Bit32.hasAnyFlag;

/**
 * Represents a Light Conduit.
 */
public class LightConduitBlock extends ConnectableLightTechBlock implements IToolRequirementProvider {
	
	private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
	
	private static final VoxelShape V_UP = Block.box(4, 12, 4, 12, 16, 12);
	private static final VoxelShape V_DOWN = Block.box(4, 0, 4, 12, 4, 12);
	private static final VoxelShape V_EAST = Block.box(12, 4, 4, 16, 12, 12);
	private static final VoxelShape V_WEST = Block.box(0, 4, 4, 4, 12, 12);
	private static final VoxelShape V_NORTH = Block.box(4, 4, 0, 12, 12, 4);
	private static final VoxelShape V_SOUTH = Block.box(4, 4, 12, 12, 12, 16);
	
	/***/
	protected static final VoxelShape[] COLLISION_SHAPES = SixSidedUtils.getBitwiseColliderArrayFor(LightConduitBlock::getShapeFor);
	
	/***/
	public LightConduitBlock() {
		this(BlockBehaviour.Properties.of(Material.STONE));
	}
	
	private LightConduitBlock(Properties props) {
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
		
		boolean right = hasAnyFlag(flags, 0b000001);
		boolean left = hasAnyFlag(flags, 0b000010);
		boolean top = hasAnyFlag(flags, 0b000100);
		boolean bottom = hasAnyFlag(flags, 0b001000);
		boolean front = hasAnyFlag(flags, 0b010000);
		boolean back = hasAnyFlag(flags, 0b100000);
		
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
		AtomicBoolean foundNeighboringEnergizedTechBlock = new AtomicBoolean(false);
		int neighborFlags = SixSidedUtils.getFlagsForNeighborsWhere(ctx.getLevel(), pos, (neighborState, currentPos, neighborPos) -> {
			boolean isConnectable = neighborState.getBlock() instanceof ConnectableLightTechBlock;
			if (isConnectable) {
				if (neighborState.getValue(AUTO)) {
					BlockEntity be = ctx.getLevel().getBlockEntity(neighborPos);
					if (be instanceof LightEnergyStorageTile storageTile) {
						if (storageTile.getLightStored() > 0) foundNeighboringEnergizedTechBlock.set(true);
					}
					return true;
				}
				int neighborOutgoing = SixSidedUtils.getNumberFromSurfaces(neighborState);
				if (Bit32.hasAnyFlag(neighborOutgoing, SixSidedUtils.neighborFlagForBlockDirection(neighborPos, currentPos))) {
					BlockEntity be = ctx.getLevel().getBlockEntity(neighborPos);
					if (be instanceof LightEnergyStorageTile storageTile) {
						if (storageTile.getLightStored() > 0) foundNeighboringEnergizedTechBlock.set(true);
					}
					return true;
				}
			}
			return false;
		});
		if (neighborFlags != 0) {
			// Auto-connection was made.
			// TODO: Only play this if the wires are live when connected, otherwise it just sounds really annoying.
			// ctx.getLevel().playSound(null, ctx.getClickedPos(), SoundRegistry.get("item.lumo_wand.swapconduitauto"), SoundSource.BLOCKS, 0.1f, 1f);
			if (foundNeighboringEnergizedTechBlock.get()) {
				ctx.getLevel().playSound(null, ctx.getClickedPos(), SoundRegistry.get("tile.light_tech.energize"), SoundSource.BLOCKS, 0.3f, 1f);
			}
		}
		return SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), neighborFlags);
		
	}
	
	@Override
	public void connectionStateChanged(BlockState originalState, BlockState newState, BlockPos at, Level inWorld, BooleanProperty prop, boolean existingConnectionChanged) {
		selfBE(inWorld, at).markLastKnownNeighborsDirty();
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LightConduitTile(pos, state);
	}
	
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY;
	}
}
