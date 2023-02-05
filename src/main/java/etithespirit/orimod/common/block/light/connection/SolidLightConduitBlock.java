package etithespirit.orimod.common.block.light.connection;

import com.google.common.collect.ImmutableMap;
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SolidLightConduitBlock extends ConnectableLightTechBlock implements IBlockTagProvider {
	
	/***/
	public SolidLightConduitBlock() {
		this(
			BlockBehaviour.Properties.of(Material.GLASS)
				.strength(0.5f, 20f)
				.requiresCorrectToolForDrops()
				.dynamicShape()
				.noOcclusion()
				.sound(SoundType.GLASS)
		);
	}
	
	private SolidLightConduitBlock(Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition);
	}
	@Override
	public boolean alwaysConnectsWhenPossible() {
		return false;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
		return pAdjacentBlockState.is(this) || super.skipRendering(pState, pAdjacentBlockState, pSide);
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
		
		BlockState ret = SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), neighborFlags).setValue(ForlornAppearanceMarshaller.IS_BLUE, true);
		return ret;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pState.getBlock() == this && pPlayer.getItemInHand(pHand).is(Tags.Items.TOOLS_PICKAXES)) {
			if (!pLevel.isClientSide) {
				BlockState newState = SixSidedUtils.copyDirs(BlockRegistry.LIGHT_CONDUIT.get().defaultBlockState(), pState); // Copy all six directional booleans.
				newState = SixSidedUtils.copySingleProperty(newState, pState, IS_BLUE);
				
				// Quick thing: Is it surrounded by water on at least 2 sides?
				Direction[] dirs = new Direction[] {
					Direction.EAST,
					Direction.NORTH,
					Direction.WEST,
					Direction.SOUTH
				};
				int adjWater = 0;
				for (Direction dir : dirs) {
					if (pLevel.getFluidState(pPos.offset(dir.getNormal())).is(Fluids.WATER)) {
						adjWater++;
						if (adjWater >= 2) break;
					}
				}
				
				// Waterlog the block if at least 2 of the 4 adjacent ones are water.
				newState = newState.setValue(WATERLOGGED, adjWater >= 2);
				pLevel.setBlock(pPos, newState, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_ALL);
				if (adjWater >= 2) pLevel.scheduleTick(pPos, newState.getFluidState().getType(), 5);
				
				ItemEntity item = new ItemEntity(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), new ItemStack(Items.GLASS));
				pLevel.addFreshEntity(item);
			}
			pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4f, 0.5f + (pLevel.getRandom().nextFloat() * 0.2f));
			return InteractionResult.SUCCESS;
		}
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
		return true;
	}
	
	@Nullable
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
		pTooltip.add(Component.translatable("block.orimod.solid_light_conduit.tip").withStyle(ChatFormatting.DARK_AQUA));
	}
}
