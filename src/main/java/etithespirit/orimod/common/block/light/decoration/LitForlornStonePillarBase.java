package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import static etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller.IS_BLUE;
import static etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller.POWERED;

public abstract class LitForlornStonePillarBase extends RotatedPillarBlock implements IForlornBlueOrangeBlock, IBlockTagProvider, IBlockItemPropertiesProvider {
	
	// TODO: Six sided stuff? There is no way to automate the creation of the block models for that, so I better be ready to make 64 variants manually
	
	private static final BlockBehaviour.Properties DEFAULT_PROPERTIES = BlockBehaviour.Properties.copy(Blocks.STONE)
		.lightLevel(state -> state.getValue(ForlornAppearanceMarshaller.POWERED) ? ForlornAppearanceMarshaller.LIGHT_LEVEL : 0)
		.strength(0.8f, 80f)
		.requiresCorrectToolForDrops()
		.isRedstoneConductor(($1, $2, $3) -> true);
	
	public LitForlornStonePillarBase() {
		super(DEFAULT_PROPERTIES);
		registerDefaultState(getStateDefinition().any().setValue(POWERED, false).setValue(IS_BLUE, false).setValue(AXIS, Direction.Axis.Y));
	}
	
	/*
	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
		if (!pLevel.isClientSide) {
			boolean isCurrentlyPowered = pState.getValue(POWERED);
			if (isCurrentlyPowered != pLevel.hasNeighborSignal(pPos)) {
				if (isCurrentlyPowered) {
					pLevel.scheduleTick(pPos, this, 4);
				} else {
					pLevel.setBlock(pPos, pState.cycle(POWERED), StaticData.REPLICATE_CHANGE);
				}
			}
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pState.getValue(POWERED) && !pLevel.hasNeighborSignal(pPos)) {
			pLevel.setBlock(pPos, pState.cycle(POWERED), StaticData.REPLICATE_CHANGE);
		}
	}
	*/
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		ForlornAppearanceMarshaller.autoCreateBlockStateDefinition(pBuilder).add(AXIS);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(AXIS, pContext.getClickedFace().getAxis()).setValue(POWERED, false).setValue(ForlornAppearanceMarshaller.IS_BLUE, true);
	}
	
	
	@Override
	public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
	
	@Override
	public Item.Properties getPropertiesOfItem() {
		return new Item.Properties().tab(OriModCreativeModeTabs.SPIRIT_DECORATION);
	}
	
	@Override
	public MutableComponent getName() {
		return SpiritItemCustomizations.getNameAsLight(super.getName());
	}
	
}
