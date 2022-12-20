package etithespirit.orimod.common.block.decay.world;

import etithespirit.orimod.common.block.decay.DecayBlockBase;
import etithespirit.orimod.common.block.decay.IDecayBlockCommon;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.Material;

import java.util.List;

public class DecayPlantMatterBlock extends DecayBlockBase {
	
	private static final BlockBehaviour.Properties DEFAULT_PROPERTIES = BlockBehaviour.Properties.of(Material.GRASS).sound(SoundType.SLIME_BLOCK).strength(1.2f).friction(0.9995f);
	
	public DecayPlantMatterBlock() {
		super(DEFAULT_PROPERTIES);
	}
	
	@Override
	public void registerReplacements(List<StateHolder<?, ?>> blocksToReplaceWithSelf) {
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.MOSS_BLOCK);
		IDecayBlockCommon.registerAllStatesFor(blocksToReplaceWithSelf, Blocks.HAY_BLOCK);
	}
	
	@Override
	public BlockState healsInto(BlockState thisState) {
		// TODO: What block here? There is not a solid podzol block, but if there was, I would use that.
		return Blocks.PODZOL.defaultBlockState();
	}
}
