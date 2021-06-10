package etithespirit.etimod.common.block.decay.flora;

import java.util.List;

import etithespirit.etimod.common.block.decay.IDecayBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import net.minecraft.block.AbstractBlock.Properties;

public class DecayLogBlock extends DecayLogBase {
	
	private static final Properties DEFAULT_PROPERTIES = Properties.copy(Blocks.OAK_LOG).sound(SoundType.STONE);
	
	public DecayLogBlock() {
		super(DEFAULT_PROPERTIES, true);
	}

	@Override
	public void registerReplacements(List<BlockState> blocksToReplaceWithSelf) {
		// TODO Auto-generated method stub
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.OAK_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.BIRCH_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.SPRUCE_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.JUNGLE_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.DARK_OAK_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.ACACIA_LOG);
	}
}
