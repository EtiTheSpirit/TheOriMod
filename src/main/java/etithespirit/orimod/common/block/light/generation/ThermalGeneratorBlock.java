package etithespirit.orimod.common.block.light.generation;

import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tags.PresetBlockTags;
import etithespirit.orimod.common.tile.light.implementations.ThermalGeneratorTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThermalGeneratorBlock extends ConnectableLightTechBlock implements IBlockTagProvider, IForlornBlueOrangeBlock {
	public ThermalGeneratorBlock() {
		super(BlockBehaviour.Properties.of(Material.STONE).strength(2f, 50f).requiresCorrectToolForDrops());
		autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition, state -> state.setValue(ForlornAppearanceMarshaller.POWERED, false));
	}
	
	@Override
	@SuppressWarnings({"unchecked"})
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ForlornAppearanceMarshaller.POWERED);
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		pTooltip.add(Component.translatable("block.orimod.thermal_generator.tip"));
	}
	
	/**
	 * Returns all tags that this block should be added to. The tags must come from {@link BlockTags}.
	 * Reminder to self: Because it keeps eluding you, this is not a MC method. This is your method.
	 *
	 * @return A list of tags that this block must use.
	 */
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ThermalGeneratorTile(pPos, pState);
	}
}
