package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.List;

public class SolidLightConduitBlock extends ConnectableLightTechBlock implements IToolRequirementProvider {
	
	/***/
	public SolidLightConduitBlock() {
		this(BlockBehaviour.Properties.of(Material.STONE));
	}
	
	private SolidLightConduitBlock(Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition);
	}
	
	/**
	 * Executes when the connection state of this block changes, like when connecting to or disconnecting from a neighboring {@link ConnectableLightTechBlock}.
	 *
	 * @param originalState The original state of this block prior to the connection changing.
	 * @param newState      The new state of this block after the connection changed.
	 */
	@Override
	public void connectionStateChanged(BlockState originalState, BlockState newState) {
	
	}
	
	@Override
	public boolean alwaysConnectsWhenPossible() {
		return true;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return null;
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return List.of(BlockTags.MINEABLE_WITH_PICKAXE);
	}
}
