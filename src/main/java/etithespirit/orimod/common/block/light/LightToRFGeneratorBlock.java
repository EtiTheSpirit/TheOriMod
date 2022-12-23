package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tile.light.implementations.LightToRFTile;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightToRFGeneratorBlock extends ConnectableLightTechBlock implements IToolRequirementProvider, IForlornBlueOrangeBlock {
	
	public LightToRFGeneratorBlock() {
		this(
			Properties.of(Material.STONE)
			.strength(1f, 100f)
			.lightLevel(state -> state.getValue(ForlornAppearanceMarshaller.POWERED) ? ForlornAppearanceMarshaller.LIGHT_LEVEL : 0)
			.requiresCorrectToolForDrops()
		);
	}
	
	protected LightToRFGeneratorBlock(Properties props) {
		super(props);
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
	
	@Override
	public boolean alwaysConnectsWhenPossible() {
		return true;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new LightToRFTile(pPos, pState);
	}
	
	@Override
	public void neighborChanged(BlockState thisState, Level world, BlockPos thisLocation, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
		super.neighborChanged(thisState, world, thisLocation, replacedBlock, changedAt, isMoving);
		BlockEntity bEnt = world.getBlockEntity(thisLocation);
		if (bEnt instanceof LightToRFTile converter) {
			converter.neighborChanged(thisState, world, thisLocation, replacedBlock, changedAt, isMoving);
		}
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
		pTooltip.add(Component.translatable("block.orimod.light_to_rf.tip"));
	}
}
