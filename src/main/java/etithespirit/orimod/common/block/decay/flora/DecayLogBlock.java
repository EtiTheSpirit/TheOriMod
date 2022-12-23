package etithespirit.orimod.common.block.decay.flora;

import etithespirit.orimod.common.block.decay.IDecayBlockCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.StateHolder;

import java.util.List;

/** A petrified log. */
public class DecayLogBlock extends DecayLogBase {
	
	private static final Properties DEFAULT_PROPERTIES = Properties.copy(Blocks.OAK_LOG).sound(SoundType.STONE);
	/** */
	public DecayLogBlock() {
		super(DEFAULT_PROPERTIES, true);
	}
	
	@Override
	public void registerReplacements(List<StateHolder<?, ?>> blocksToReplaceWithSelf) {
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.OAK_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.BIRCH_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.SPRUCE_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.JUNGLE_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.DARK_OAK_LOG);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.ACACIA_LOG);
	}
	
}
