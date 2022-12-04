package etithespirit.orimod.common.block.decay.flora;

import etithespirit.orimod.common.block.decay.IDecayBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;

import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/** A petrified log. */
public class DecayLogBlock extends DecayLogBase {
	
	private static final Properties DEFAULT_PROPERTIES = Properties.copy(Blocks.OAK_LOG).sound(SoundType.STONE);
	/** */
	public DecayLogBlock() {
		super(DEFAULT_PROPERTIES, true);
	}
	
	@Override
	public void registerReplacements(List<StateHolder<?, ?>> blocksToReplaceWithSelf) {
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.OAK_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.BIRCH_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.SPRUCE_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.JUNGLE_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.DARK_OAK_LOG);
		IDecayBlock.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.ACACIA_LOG);
	}
}
