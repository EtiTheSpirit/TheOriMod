package etithespirit.orimod.datagen.features.implementations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record GorlekCrystalBlockConfiguration(BlockStateProvider shellBlock, BlockStateProvider alternateShellBlock, BlockStateProvider internalOreBlock, BlockStateProvider internalFullOreBlock, GorlekCrystalBlockSelectionConfiguration oreGenerationChances) {
	
	public static final Codec<GorlekCrystalBlockConfiguration> BLOCK_CONFIG_CODEC = RecordCodecBuilder.create(blockCfg ->
		blockCfg.group(
			// primaryCrystalBlock: This field represents the block responsible for making up the "shell" of the structure. The shell surrounds a thin seam
			// of ore going through the center of the long, line-like structure.
			BlockStateProvider.CODEC.fieldOf("shellBlock").forGetter(GorlekCrystalBlockConfiguration::shellBlock),
			
			// alternateShellBlock: This field represents the alternative block that can make up the shell. Shell blocks have a chance of being replaced by this.
			BlockStateProvider.CODEC.fieldOf("alternateShellBlock").forGetter(GorlekCrystalBlockConfiguration::alternateShellBlock),
			
			// internalOreBlock: This field represents the block responsible for representing the ore within the center of the shell.
			BlockStateProvider.CODEC.fieldOf("internalOreBlock").forGetter(GorlekCrystalBlockConfiguration::internalOreBlock),
			
			// internalFullOreBlock: This field represents the alternative ore block, which is the "Raw Ore" block. It should be rather rare.
			BlockStateProvider.CODEC.fieldOf("internalFullOreBlock").forGetter(GorlekCrystalBlockConfiguration::internalFullOreBlock),
			
			// oreGenerationChances: This contains the information used to determine *when* the blocks above get selected in generation.
			GorlekCrystalBlockSelectionConfiguration.ORE_CHANCE_CODEC.fieldOf("oreGenerationChances").forGetter(GorlekCrystalBlockConfiguration::oreGenerationChances)
		).apply(blockCfg, GorlekCrystalBlockConfiguration::new)
	);


}
