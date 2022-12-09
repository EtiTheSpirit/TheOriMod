package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.LightEnergyTile;
import etithespirit.orimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.orimod.common.tile.light.implementations.LightCapacitorTile;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.util.Bit32;
import etithespirit.orimod.util.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a Light Capacitor, used to store Light energy.
 */
public class LightCapacitorBlock extends ConnectableLightTechBlock implements ILightBlockIdentifier, IToolRequirementProvider, IForlornBlueOrangeBlock, EntityBlock {
	/***/
	public LightCapacitorBlock() {
		this(Properties.of(Material.STONE).sound(SoundType.STONE).lightLevel(state -> state.getValue(ForlornAppearanceMarshaller.POWERED) ? ForlornAppearanceMarshaller.LIGHT_LEVEL : 0));
	}
	
	private LightCapacitorBlock(Properties properties) {
		super(properties);
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
	
	@Override
	public boolean alwaysConnectsWhenPossible() {
		return true;
	}
	
	@Override
	public void connectionStateChanged(BlockState originalState, BlockState newState, BlockPos at, Level inWorld, BooleanProperty prop, boolean existingConnectionChanged) {
		selfBE(inWorld, at).markLastKnownNeighborsDirty();
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LightCapacitorTile(pos, state);
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY;
	}
	
}
