package etithespirit.orimod.common.block.light.connection;


import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.light.implementations.LightConduitTile;
import etithespirit.orimod.energy.ILightEnergyGenerator;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.util.Bit32;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static etithespirit.orimod.util.Bit32.hasAnyFlag;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

/**
 * Represents a Light Conduit.
 */
public class LightConduitBlock extends ConnectableLightTechBlock implements IBlockTagProvider, SimpleWaterloggedBlock {
	
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
		this(
			BlockBehaviour.Properties.of(Material.STONE)
			.strength(0.5f, 20f)
			.dynamicShape()
			.requiresCorrectToolForDrops()
		);
	}
	
	private LightConduitBlock(Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition, state -> state.setValue(WATERLOGGED, false));
	}
	
	@Override
	@SuppressWarnings({"unchecked"})
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
		return !pState.getValue(WATERLOGGED);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		ItemStack inHand = pPlayer.getItemInHand(pHand);
		if (pState.getBlock() == this && inHand.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == Blocks.GLASS) {
			inHand.setCount(inHand.getCount() - 1);
			if (!pLevel.isClientSide) {
				BlockState newState = SixSidedUtils.copyDirs(BlockRegistry.SOLID_LIGHT_CONDUIT.get().defaultBlockState(), pState); // Copy all six directional booleans.
				newState = SixSidedUtils.copySingleProperty(newState, pState, IS_BLUE);
				
				pLevel.setBlock(pPos, newState, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_ALL);
			}
			
			SoundType glassSFX = Blocks.GLASS.getSoundType(Blocks.GLASS.defaultBlockState(), pLevel, pPos, pPlayer);
			pLevel.playSound(pPlayer, pPos, glassSFX.getPlaceSound(), SoundSource.BLOCKS, glassSFX.getVolume(), glassSFX.getPitch());
			return InteractionResult.SUCCESS;
		}
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
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
					if (be instanceof ILightEnergyStorage storageTile) {
						if (storageTile.getLightStored() > 0) foundNeighboringEnergizedTechBlock.set(true);
					} else if (be instanceof ILightEnergyGenerator generatorTile) {
						if (generatorTile.getMaximumGeneratedAmountForDisplay() > 0) foundNeighboringEnergizedTechBlock.set(true);
					}
					return true;
				}
				int neighborOutgoing = SixSidedUtils.getNumberFromSurfaces(neighborState);
				if (Bit32.hasAnyFlag(neighborOutgoing, SixSidedUtils.neighborFlagForBlockDirection(neighborPos, currentPos))) {
					BlockEntity be = ctx.getLevel().getBlockEntity(neighborPos);
					if (be instanceof ILightEnergyStorage storageTile) {
						if (storageTile.getLightStored() > 0) foundNeighboringEnergizedTechBlock.set(true);
					} else if (be instanceof ILightEnergyGenerator generatorTile) {
						if (generatorTile.getMaximumGeneratedAmountForDisplay() > 0) foundNeighboringEnergizedTechBlock.set(true);
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
		
		FluidState fluidstate = ctx.getLevel().getFluidState(ctx.getClickedPos());
		BlockState ret = SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), neighborFlags).setValue(ForlornAppearanceMarshaller.IS_BLUE, true);
		ret = ret.setValue(WATERLOGGED, fluidstate.is(Fluids.WATER) && !fluidstate.isEmpty());
		return ret;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		if (pState.getValue(WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LightConduitTile(pos, state);
	}
	
	@Override
	public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
		pTooltip.add(Component.translatable("block.orimod.light_conduit.tip").withStyle(ChatFormatting.DARK_AQUA));
	}
	
}
