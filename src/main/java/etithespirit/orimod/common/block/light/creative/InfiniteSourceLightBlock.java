package etithespirit.orimod.common.block.light.creative;

import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tile.light.implementations.LightInfiniteSourceTile;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class InfiniteSourceLightBlock extends ConnectableLightTechBlock implements IBlockTagProvider, IForlornBlueOrangeBlock {
	
	public InfiniteSourceLightBlock() {
		this(Properties.copy(Blocks.BEDROCK));
	}
	
	protected InfiniteSourceLightBlock(Block.Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition, state -> state.setValue(ForlornAppearanceMarshaller.POWERED, false));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ForlornAppearanceMarshaller.POWERED);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new LightInfiniteSourceTile(pPos, pState);
	}
	
	
	@Override
	public Iterable<TagKey<Block>> getAdditionalTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
}
