package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.tile.light.TileEntityLightCapacitor;
import etithespirit.orimod.common.tile.light.TileEntityLightEnergyDebugger;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightDebuggerBlock extends ConnectableLightTechBlock implements ILightBlockIdentifier, EntityBlock {
	
	public static final BooleanProperty IS_SINK = BooleanProperty.create("is_sink");
	
	/***/
	public LightDebuggerBlock() {
		this(Properties.of(Material.STONE)
			     // No tool requirement
			     .sound(SoundType.STONE)
		);
	}
	
	private LightDebuggerBlock(Properties properties) {
		super(properties);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition, stateDef -> {
			stateDef.setValue(IS_SINK, false);
		});
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(IS_SINK);
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
		return new TileEntityLightEnergyDebugger(at, state);
	}
	
	
}
