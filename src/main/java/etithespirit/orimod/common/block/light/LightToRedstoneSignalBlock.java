package etithespirit.orimod.common.block.light;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.LightEnergyTile;
import etithespirit.orimod.common.tile.light.implementations.LightToRedstoneSignalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class LightToRedstoneSignalBlock extends ConnectableLightTechBlock implements IForlornBlueOrangeBlock {
	
	public LightToRedstoneSignalBlock() {
		this(Properties.of(Material.STONE).strength(0.8f, 80f).requiresCorrectToolForDrops().isRedstoneConductor(StaticData.TRUE_POSITION_PREDICATE));
	}
	
	protected LightToRedstoneSignalBlock(Block.Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(
			this::registerDefaultState,
			this.stateDefinition,
			state -> state.setValue(ForlornAppearanceMarshaller.POWERED, false)
		);
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
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext).setValue(ForlornAppearanceMarshaller.POWERED, false).setValue(ForlornAppearanceMarshaller.IS_BLUE, false);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isSignalSource(BlockState pState) {
		return true;
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
		if (pState.getValue(ForlornAppearanceMarshaller.POWERED)) {
			return 15;
		}
		return 0;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new LightToRedstoneSignalTile(pPos, pState);
	}
}
