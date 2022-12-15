package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tags.OriModBlockTags;
import etithespirit.orimod.common.tile.light.implementations.LightCapacitorTile;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a Light Capacitor, used to store Light energy.
 */
public class LightCapacitorBlock extends ConnectableLightTechBlock implements IToolRequirementProvider, IForlornBlueOrangeBlock, EntityBlock {
	/***/
	public LightCapacitorBlock() {
		this(
			Properties.of(Material.STONE)
			.strength(1f, 100f)
			.lightLevel(state -> state.getValue(ForlornAppearanceMarshaller.POWERED) ? ForlornAppearanceMarshaller.LIGHT_LEVEL : 0)
			.requiresCorrectToolForDrops()
		);
	}
	
	private LightCapacitorBlock(Properties properties) {
		super(properties);
		ConnectableLightTechBlock.autoRegisterDefaultState(
			this::registerDefaultState,
			this.stateDefinition,
			state -> state.setValue(ForlornAppearanceMarshaller.POWERED, false)
		);
	}
	
	
	@Override
	public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
		pTooltip.add(Component.translatable("block.orimod.light_capacitor.tip"));
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LightCapacitorTile(pos, state);
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_LIGHT;
	}
	
}
