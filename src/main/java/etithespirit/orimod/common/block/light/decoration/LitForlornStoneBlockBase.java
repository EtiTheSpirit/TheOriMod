package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public abstract class LitForlornStoneBlockBase extends Block implements IForlornBlueOrangeBlock, IToolRequirementProvider, IBlockItemPropertiesProvider {
	
	// TODO: Six sided stuff? There is no way to automate the creation of the block models for that, so I better be ready to make 64 variants manually
	
	private static final Properties DEFAULT_PROPERTIES = Properties.copy(Blocks.STONE)
		.lightLevel(state -> state.getValue(ForlornAppearanceMarshaller.POWERED) ? ForlornAppearanceMarshaller.LIGHT_LEVEL : 0)
		.strength(0.8f, 80f)
		.requiresCorrectToolForDrops()
		.isRedstoneConductor(($1, $2, $3) -> true);
	
	public LitForlornStoneBlockBase() {
		super(DEFAULT_PROPERTIES);
		ForlornAppearanceMarshaller.autoRegisterDefaultState(this::registerDefaultState, stateDefinition);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		
		return this.defaultBlockState().setValue(POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos())).setValue(ForlornAppearanceMarshaller.IS_BLUE, true);
	}
	
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
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		ForlornAppearanceMarshaller.autoCreateBlockStateDefinition(pBuilder);
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
	
	@Override
	public Item.Properties getPropertiesOfItem() {
		return new Item.Properties().tab(OriModCreativeModeTabs.SPIRIT_DECORATION);
	}
	
	
	@Override
	public MutableComponent getName() {
		return StaticData.getNameAsLight(super.getName());
	}
}
