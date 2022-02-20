package etithespirit.orimod.common.block.decay.world;

import etithespirit.orimod.common.block.decay.DecayBlockBase;
import etithespirit.orimod.common.block.decay.IDecayBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.List;

/**
 * Decay mycelium, which replaces dirt and grass.
 * @author Eti
 *
 */
public class DecayMyceliumBlock extends DecayBlockBase {
	
	private static final Properties DEFAULT_PROPERTIES = Properties.of(Material.GRASS).sound(SoundType.SLIME_BLOCK).strength(1.2f).friction(0.9995f);
	
	public DecayMyceliumBlock() {
		super(DEFAULT_PROPERTIES, true);
	}
	
	@Override
	public void registerReplacements(List<BlockState> blocksToReplaceWithSelf) {
		IDecayBlock.registerAllStatesForBlock(blocksToReplaceWithSelf, Blocks.GRASS_BLOCK);
		blocksToReplaceWithSelf.add(Blocks.DIRT.defaultBlockState());
		blocksToReplaceWithSelf.add(Blocks.COARSE_DIRT.defaultBlockState());
		blocksToReplaceWithSelf.add(Blocks.PODZOL.defaultBlockState());
	}
}
