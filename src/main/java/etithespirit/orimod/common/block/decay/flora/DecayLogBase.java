package etithespirit.orimod.common.block.decay.flora;

import etithespirit.orimod.OriMod;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.decay.IDecayBlockCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.ArrayList;
import java.util.List;

import static etithespirit.orimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.orimod.common.block.decay.DecayCommon.DECAY_REPLACEMENT_TARGETS;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;

/**
 * A decayed tree log.
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public abstract class DecayLogBase extends RotatedPillarBlock implements IDecayBlockCommon {
	
	
	public static final BooleanProperty IS_SAFE = BooleanProperty.create("is_safe");
	
	
	/**
	 * Create a new Decay block that doesn't spread.
	 * @param properties The properties of this block.
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
		this.registerDefaultState(this.stateDefinition.any().setValue(ALL_ADJACENT_ARE_DECAY, Boolean.FALSE).setValue(EDGE_DETECTION_RARITY, 1).setValue(IS_SAFE, false)); // Set this to 1 so that it's a half chance.
		if (spreads) {
			List<StateHolder<?, ?>> thisBlockReplacements = new ArrayList<>();
			registerReplacements(thisBlockReplacements);
			if (thisBlockReplacements.size() == 0) {
				OriMod.LOG.warn("New Decay block had SPREADS=TRUE but did not register any blocks to spread to! Offender: " + this.getClass().getName());
			} else {
				for (StateHolder<?, ?> repl : thisBlockReplacements) {
					DECAY_REPLACEMENT_TARGETS.put(repl, this.defaultBlockState().setValue(AXIS, repl.getValue(AXIS)));
				}
				properties.randomTicks();
			}
		}
	}
	
	@Override
	@SuppressWarnings({ "unchecked" })
	public void createBlockStateDefinition(StateDefinition.Builder builder) {
		builder.add(AXIS);
		builder.add(ALL_ADJACENT_ARE_DECAY);
		builder.add(EDGE_DETECTION_RARITY);
		builder.add(IS_SAFE);
	}
	
	
	
	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
		defaultRandomTick(state, worldIn, pos, random);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		defaultNeighborChanged(state, worldIn, pos, blockIn, worldIn.getBlockState(fromPos), fromPos, isMoving);
	}
	
	@Override
	public BlockState healsInto(BlockState thisState) {
		return thisState.setValue(IS_SAFE, true);
	}
	
	@Override
	public MutableComponent getName() {
		return StaticData.getNameAsDecay(super.getName());
	}
	
}