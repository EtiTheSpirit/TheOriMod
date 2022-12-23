package etithespirit.orimod.common.block.decay.flora;

import etithespirit.orimod.common.block.decay.IDecayBlockCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.StateHolder;

import java.util.List;

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
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_OAK_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_BIRCH_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_SPRUCE_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_JUNGLE_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_DARK_OAK_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.STRIPPED_ACACIA_LOG);
	}
	
}
