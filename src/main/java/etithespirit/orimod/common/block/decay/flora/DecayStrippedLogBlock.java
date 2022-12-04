package etithespirit.orimod.common.block.decay.flora;

import etithespirit.orimod.common.block.decay.IDecayBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * A stripped decay log.
 */
public class DecayStrippedLogBlock extends DecayLogBase {
	private static final Properties DEFAULT_PROPERTIES = Properties.copy(Blocks.STRIPPED_OAK_LOG).sound(SoundType.STONE);
	
	/** */
	public DecayStrippedLogBlock() {
		super(DEFAULT_PROPERTIES, true);
	}
	
	@Override
	public void registerReplacements(List<StateHolder<?, ?>> blocksToReplaceWithSelf) {
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_OAK_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_BIRCH_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_SPRUCE_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_JUNGLE_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_DARK_OAK_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_ACACIA_LOG);
	}
}
