package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.tile.light.implementations.LightConduitTile;
import etithespirit.orimod.util.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
	
	@Override
	public void connectionStateChanged(BlockState originalState, BlockState newState, BlockPos at, Level inWorld, BooleanProperty prop, boolean existingConnectionChanged) {
		selfBE(inWorld, at).markLastKnownNeighborsDirty();
	}
	
	@Override
	public boolean alwaysConnectsWhenPossible() {
		return true;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LightConduitTile(pos, state);
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY;
	}
}
