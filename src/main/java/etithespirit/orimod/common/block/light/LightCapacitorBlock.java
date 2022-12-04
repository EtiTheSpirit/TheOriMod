package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * Represents a Light Capacitor, used to store Light energy.
 */
public class LightCapacitorBlock extends ConnectableLightTechBlock implements ILightBlockIdentifier, IToolRequirementProvider, EntityBlock {
	/***/
	public LightCapacitorBlock() {
		this(Properties.of(Material.STONE).sound(SoundType.STONE));
	}
	
	private LightCapacitorBlock(Properties properties) {
		super(properties);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition);
	}
	
	@Override
	public boolean alwaysConnectsWhenPossible() {
		return true;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onPlace(BlockState state, Level world, BlockPos at, BlockState old, boolean isMoving) {
	
	}
	
	@Override
	public void connectionStateChanged(BlockState originalState, BlockState newState) { }
	
	
	//@Nullable
	//@Override
	//public BlockEntity newBlockEntity(BlockPos at, BlockState state) {
	//	return new TileEntityLightCapacitor(at, state);
	//}
	
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
