package etithespirit.etimod.common.block.decay.flora;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.block.decay.IDecayBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import static etithespirit.etimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.etimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.etimod.common.block.decay.DecayCommon.BLOCK_REPLACEMENT_TARGETS;

/**
 * A decayed tree log.
 * @author Eti
 *
 */
public abstract class DecayLogBase extends RotatedPillarBlock implements IDecayBlock {
	
	/**
	 * Create a new Decay block that doesn't spread.
	 * @param properties
	 */
	public DecayLogBase(Properties properties) {
		this(properties, false);
	}
	
	/**
	 * Create a new Decay block.
	 * @param properties The properties of this block.
	 * @param spreads Whether or not this decay block spreads (replaces certain adjacent blocks with a decay equivalent). Setting this to true will set the {@code ticksRandomly} field on the input properties.
	 */
	public DecayLogBase(Properties properties, boolean spreads) {
		super(spreads ? properties.randomTicks() : properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE).setValue(EDGE_DETECTION_RARITY, 1)); // Set this to 1 so that it's a half chance.
		if (spreads) {
			List<BlockState> thisBlockReplacements = new ArrayList<BlockState>();
			registerReplacements(thisBlockReplacements);
			if (thisBlockReplacements.size() == 0) {
				EtiMod.LOG.warn("New Decay block had SPREADS=TRUE but did not register any blocks to spread to! Offender: " + this.getClass().getName());
				return;
			} else {
				for (BlockState repl : thisBlockReplacements) {
					BLOCK_REPLACEMENT_TARGETS.put(repl, this.defaultBlockState().setValue(AXIS, repl.getValue(AXIS)));
				}
				properties.randomTicks();
				return;
			}
		}
	}
		
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public void createBlockStateDefinition(StateContainer.Builder builder) {
		builder.add(AXIS);
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
	
}
