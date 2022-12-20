package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tags.PresetBlockTags;
import etithespirit.orimod.common.tile.light.implementations.LightToRedstoneSignalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightToRedstoneSignalBlock extends ConnectableLightTechBlock implements IToolRequirementProvider, IForlornBlueOrangeBlock {
	
	public LightToRedstoneSignalBlock() {
		this(
			Properties.of(Material.STONE)
				.strength(0.8f, 80f)
				.requiresCorrectToolForDrops()
				.isRedstoneConductor(StaticData.ALWAYS_TRUE)
		);
	}
	
	protected LightToRedstoneSignalBlock(Block.Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(
			this::registerDefaultState,
			this.stateDefinition,
			state -> state.setValue(ForlornAppearanceMarshaller.POWERED, false)
		);
	}
	
	/**
	 * <strong>When overriding, call super FIRST, then run your own code.</strong>
	 *
	 * @param builder The builder that assembles the valid {@link BlockState}s
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ForlornAppearanceMarshaller.POWERED);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext).setValue(ForlornAppearanceMarshaller.POWERED, false).setValue(ForlornAppearanceMarshaller.IS_BLUE, false);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isSignalSource(BlockState pState) {
		return true;
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
		if (pState.getValue(ForlornAppearanceMarshaller.POWERED)) {
			return 15;
		}
		return 0;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new LightToRedstoneSignalTile(pPos, pState);
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
		pTooltip.add(Component.translatable("block.orimod.light_to_redstone_signal.tip"));
	}
	
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
}
