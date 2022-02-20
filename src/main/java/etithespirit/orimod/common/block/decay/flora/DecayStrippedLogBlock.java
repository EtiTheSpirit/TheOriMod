package etithespirit.orimod.common.block.decay.flora;

import etithespirit.orimod.common.block.decay.IDecayBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DecayStrippedLogBlock extends DecayLogBase {
	private static final Properties DEFAULT_PROPERTIES = Properties.copy(Blocks.STRIPPED_OAK_LOG).sound(SoundType.STONE);
	
	public DecayStrippedLogBlock() {
		super(DEFAULT_PROPERTIES, true);
	}
	
	@Override
	public void registerReplacements(List<BlockState> blocksToReplaceWithSelf) {
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_OAK_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_BIRCH_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_SPRUCE_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_JUNGLE_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_DARK_OAK_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.STRIPPED_ACACIA_LOG);
	}
}
