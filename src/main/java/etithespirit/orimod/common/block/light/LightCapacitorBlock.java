package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.tile.light.LightEnergyTicker;
import etithespirit.orimod.common.tile.light.TileEntityLightCapacitor;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a Light Capacitor, used to store Light energy.
 */
public class LightCapacitorBlock extends ConnectableLightTechBlock implements ILightBlockIdentifier, IToolRequirementProvider, EntityBlock {
	/***/
	public LightCapacitorBlock() {
		this(Properties.of(Material.STONE)
			// No tool requirement
			     .sound(SoundType.STONE)
		);
	}
	/***/
	public LightCapacitorBlock(Properties properties) {
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
	
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos at, BlockState state) {
		return new TileEntityLightCapacitor(at, state);
	}
	
	@Override
	public Iterable<Tag.Named<Block>> getTagsForBlock() {
		return List.of(BlockTags.MINEABLE_WITH_PICKAXE);
	}
	
}
