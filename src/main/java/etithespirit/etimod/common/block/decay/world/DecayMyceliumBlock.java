package etithespirit.etimod.common.block.decay.world;

import java.util.List;

import etithespirit.etimod.common.block.decay.DecayBlockBase;
import etithespirit.etimod.common.block.decay.IDecayBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

/**
 * Decay mycelium, which replaces dirt and grass.
 * @author Eti
 *
 */
public class DecayMyceliumBlock extends DecayBlockBase {

	private static final Properties DEFAULT_PROPERTIES = Properties.of(Material.GRASS).sound(SoundType.SLIME_BLOCK).harvestLevel(0).harvestTool(ToolType.SHOVEL).strength(1.2f).friction(0.9995f);
	
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
