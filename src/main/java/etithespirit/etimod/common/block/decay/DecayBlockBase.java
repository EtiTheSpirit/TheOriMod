package etithespirit.etimod.common.block.decay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import etithespirit.etimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.etimod.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import static etithespirit.etimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.etimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.etimod.common.block.decay.DecayCommon.BLOCK_REPLACEMENT_TARGETS;

/**
 * This base class represents a block associated with The Decay. It provides a crude implementation of spreading and "infecting"
 * (replacing) other blocks as defined by {@link IDecayBlock}.
 * @author Eti
 *
 */
public abstract class DecayBlockBase extends Block implements IDecayBlock {
	
	/**
	 * Create a new Decay block with the given properties that doesn't spread.
	 * @param properties
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
			List<BlockState> thisBlockReplacements = new ArrayList<BlockState>();
			registerReplacements(thisBlockReplacements);
			if (thisBlockReplacements.size() == 0) {
				throw new IllegalStateException("New Decay block had SPREADS=TRUE but did not register any blocks to spread to! Offender: " + this.getClass().getName());
			} else {
				for (BlockState repl : thisBlockReplacements) {
					BLOCK_REPLACEMENT_TARGETS.put(repl, this.defaultBlockState());
				}
				properties.randomTicks();
				return;
			}
		}
	}
		
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public void createBlockStateDefinition(StateContainer.Builder builder) {
		builder.add(ALL_ADJACENT_ARE_DECAY);
		builder.add(EDGE_DETECTION_RARITY);
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		defaultRandomTick(state, worldIn, pos, random);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		defaultNeighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
	}
	
	@Override
	public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		// When the block is added, automatically set all neighbors to mycelium where applicable
		for (int i = 0; i < 6; i++) {		
			Vector3i adj = DecayCommon.ADJACENTS_IN_ORDER[i];
			BlockPos myceliumSpreadPos = pos.offset(adj);
			if (DecaySurfaceMyceliumBlock.canSpreadTo(worldIn, myceliumSpreadPos, worldIn.getBlockState(myceliumSpreadPos))) {
				worldIn.setBlockAndUpdate(myceliumSpreadPos, BlockRegistry.DECAY_SURFACE_MYCELIUM.get().defaultBlockState()); // It'll update its own state.
			}
		}
	}
}
