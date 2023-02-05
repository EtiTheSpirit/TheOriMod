package etithespirit.orimod.common.block.light.interaction;

import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tile.light.implementations.LightRepairBoxTile;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightRepairBoxBlock extends ConnectableLightTechBlock implements IForlornBlueOrangeBlock, IBlockTagProvider {
	
	public LightRepairBoxBlock() {
		super(Block.Properties.of(Material.STONE).strength(0.8f, 80f));
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition, state -> state.setValue(ForlornAppearanceMarshaller.POWERED, false));
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
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new LightRepairBoxTile(pPos, pState);
	}
	
	@Override
	public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (!pState.is(pNewState.getBlock())) {
			BlockEntity blockentity = pLevel.getBlockEntity(pPos);
			if (blockentity instanceof LightRepairBoxTile box) {
				Containers.dropContents(pLevel, pPos, box);
				pLevel.updateNeighbourForOutputSignal(pPos, this);
			}
		}
		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pLevel.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockentity = pLevel.getBlockEntity(pPos);
			if (blockentity instanceof LightRepairBoxTile repairBox) {
				pPlayer.openMenu(repairBox);
			}
			
			return InteractionResult.CONSUME;
		}
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		pTooltip.add(Component.translatable("block.orimod.light_repair_box.tip").withStyle(ChatFormatting.DARK_AQUA));
	}
	
	
}
