package etithespirit.orimod.common.block.decay;

import etithespirit.orimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.orimod.registry.world.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

import java.util.ArrayList;
import java.util.List;

import static etithespirit.orimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.orimod.common.block.decay.DecayCommon.DECAY_REPLACEMENT_TARGETS;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.orimod.info.coordinate.Cardinals.ADJACENTS_IN_ORDER;

/**
 * This base class represents a block associated with The Decay. It provides a crude implementation of spreading and "infecting"
 * (replacing) other blocks as defined by {@link IDecayBlockCommon}.
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public abstract class DecayBlockBase extends Block implements IDecayBlockCommon {
	
	/**
	 * Create a new Decay block with the given properties that doesn't spread.
	 * @param properties The properties of this block.
	 */
	public DecayBlockBase(Properties properties) {
		this(properties, false);
	}
	
	/**
	 * Create a new Decay block with the given properties.
	 * @param properties The properties of this block.
	 * @param spreads Whether or not this decay block spreads (replaces certain adjacent blocks with a decay equivalent). Setting this to true will set the {@code ticksRandomly} field on the input properties. This requires that at least one replaceable block is registered in {@code registerReplacements}, otherwise this ctor will raise a {@link java.lang.IllegalStateException}.
	 */
	public DecayBlockBase(Properties properties, boolean spreads) {
		super(spreads ? properties.randomTicks() : properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE).setValue(EDGE_DETECTION_RARITY, 1)); // Set this to 1 so that it's a half chance.
		if (spreads) {
			List<StateHolder<?, ?>> thisBlockReplacements = new ArrayList<>();
			registerReplacements(thisBlockReplacements);
			if (thisBlockReplacements.size() == 0) {
				throw new IllegalStateException("New Decay block had SPREADS=TRUE but did not register any blocks to spread to! Offender: " + this.getClass().getName());
			} else {
				for (StateHolder<?, ?> repl : thisBlockReplacements) {
					DECAY_REPLACEMENT_TARGETS.put(repl, this.defaultBlockState());
				}
				properties.randomTicks();
			}
		}
	}
	
	@Override
	@SuppressWarnings({ "unchecked" })
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		builder.add(ALL_ADJACENT_ARE_DECAY);
		builder.add(EDGE_DETECTION_RARITY);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		defaultRandomTick(state, worldIn, pos, random);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		defaultNeighborChanged(state, worldIn, pos, blockIn, worldIn.getBlockState(fromPos), fromPos, isMoving);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		// When the block is added, automatically set all neighbors to mycelium where applicable
		for (int i = 0; i < 6; i++) {
			Vec3i adj = ADJACENTS_IN_ORDER[i];
			BlockPos myceliumSpreadPos = pos.offset(adj);
			if (DecaySurfaceMyceliumBlock.canSpreadSurfaceMyceliumTo(worldIn, myceliumSpreadPos, worldIn.getBlockState(myceliumSpreadPos))) {
				worldIn.setBlockAndUpdate(myceliumSpreadPos, BlockRegistry.DECAY_SURFACE_MYCELIUM.get().defaultBlockState()); // It'll update its own state.
			}
		}
	}
	
	@Override
	public void stepOn(Level world, BlockPos at, BlockState state, Entity ent) {
		if (world.isClientSide) return;
		IDecayBlockCommon.super.defaultOnEntityWalked(world, at, ent);
	}
}
