package etithespirit.orimod.common.block.light.connection;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.implementations.LightConduitTile;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.util.Bit32;
import etithespirit.orimod.util.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class SolidLightConduitBlock extends ConnectableLightTechBlock implements IToolRequirementProvider {
	
	/***/
	public SolidLightConduitBlock() {
		this(
			BlockBehaviour.Properties.of(Material.GLASS)
				.strength(0.5f, 20f)
				.requiresCorrectToolForDrops()
				.dynamicShape()
				.noOcclusion()
				.sound(SoundType.GLASS)
		);
	}
	
	private SolidLightConduitBlock(Properties props) {
		super(props);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition);
	}
	@Override
	public boolean alwaysConnectsWhenPossible() {
		return false;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
		return pAdjacentBlockState.is(this) || super.skipRendering(pState, pAdjacentBlockState, pSide);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos pos = ctx.getClickedPos();
		AtomicBoolean foundNeighboringEnergizedTechBlock = new AtomicBoolean(false);
		int neighborFlags = SixSidedUtils.getFlagsForNeighborsWhere(ctx.getLevel(), pos, (neighborState, currentPos, neighborPos) -> {
			boolean isConnectable = neighborState.getBlock() instanceof ConnectableLightTechBlock;
			if (isConnectable) {
				if (neighborState.getValue(AUTO)) {
					BlockEntity be = ctx.getLevel().getBlockEntity(neighborPos);
					if (be instanceof LightEnergyStorageTile storageTile) {
						if (storageTile.getLightStored() > 0) foundNeighboringEnergizedTechBlock.set(true);
					}
					return true;
				}
				int neighborOutgoing = SixSidedUtils.getNumberFromSurfaces(neighborState);
				if (Bit32.hasAnyFlag(neighborOutgoing, SixSidedUtils.neighborFlagForBlockDirection(neighborPos, currentPos))) {
					BlockEntity be = ctx.getLevel().getBlockEntity(neighborPos);
					if (be instanceof LightEnergyStorageTile storageTile) {
						if (storageTile.getLightStored() > 0) foundNeighboringEnergizedTechBlock.set(true);
					}
					return true;
				}
			}
			return false;
		});
		if (neighborFlags != 0) {
			// Auto-connection was made.
			// TODO: Only play this if the wires are live when connected, otherwise it just sounds really annoying.
			// ctx.getLevel().playSound(null, ctx.getClickedPos(), SoundRegistry.get("item.lumo_wand.swapconduitauto"), SoundSource.BLOCKS, 0.1f, 1f);
			if (foundNeighboringEnergizedTechBlock.get()) {
				ctx.getLevel().playSound(null, ctx.getClickedPos(), SoundRegistry.get("tile.light_tech.energize"), SoundSource.BLOCKS, 0.3f, 1f);
			}
		}
		
		BlockState ret = SixSidedUtils.whereSurfaceFlagsAre(this.defaultBlockState(), neighborFlags).setValue(ForlornAppearanceMarshaller.IS_BLUE, true);
		return ret;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
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
