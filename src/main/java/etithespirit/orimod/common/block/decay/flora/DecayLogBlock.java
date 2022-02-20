package etithespirit.orimod.common.block.decay.flora;

import etithespirit.orimod.common.block.decay.IDecayBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DecayLogBlock extends DecayLogBase {
	
	private static final Properties DEFAULT_PROPERTIES = Properties.copy(Blocks.OAK_LOG).sound(SoundType.STONE);
	
	public DecayLogBlock() {
		super(DEFAULT_PROPERTIES, true);
	}
	
	@Override
	public void registerReplacements(List<BlockState> blocksToReplaceWithSelf) {
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.OAK_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.BIRCH_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.SPRUCE_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.JUNGLE_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.DARK_OAK_LOG);
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.ACACIA_LOG);
	}
}
