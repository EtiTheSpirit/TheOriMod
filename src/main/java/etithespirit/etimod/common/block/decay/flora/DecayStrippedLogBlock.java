package etithespirit.etimod.common.block.decay.flora;

import java.util.List;

import etithespirit.etimod.common.block.decay.IDecayBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;

public class DecayStrippedLogBlock extends DecayLogBase {
	private static final Properties DEFAULT_PROPERTIES = Properties.from(Blocks.STRIPPED_OAK_LOG).sound(SoundType.STONE);
		
	public DecayStrippedLogBlock() {
		super(DEFAULT_PROPERTIES, true);
	}

	@Override
	public void registerReplacements(List<BlockState> blocksToReplaceWithSelf) {
		// TODO Auto-generated method stub
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_OAK_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_BIRCH_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_SPRUCE_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_JUNGLE_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_DARK_OAK_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_ACACIA_LOG);
	}
}
