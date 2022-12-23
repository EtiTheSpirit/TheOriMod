package etithespirit.orimod.datagen.features.implementations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GorlekCrystalBlockSelectionConfiguration(double chanceForOreBlock, double chanceForOreBlockInShell, double chanceForAlternateShellBlock, double chanceForAlternateOreInstead) {
	
	private static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0, 1);
	
	public static final Codec<GorlekCrystalBlockSelectionConfiguration> ORE_CHANCE_CODEC = RecordCodecBuilder.create(cfg ->
		cfg.group(
			// chanceForOreBlock: This field represents the chance that an ore block will generate in its intended place.
			// The "intended place" is at the center of the generated structure, which generates like a long line with arbitrary orientation.
			CHANCE_RANGE.fieldOf("chanceForOreBlock").forGetter(GorlekCrystalBlockSelectionConfiguration::chanceForOreBlock),
			
			// chanceForOreBlockInShell: This field represents the chance that any block making up the outer shell of the structure gets
			// replaced with an ore block. This should generally be quite rare.
			CHANCE_RANGE.fieldOf("chanceForOreBlockInShell").forGetter(GorlekCrystalBlockSelectionConfiguration::chanceForOreBlockInShell),
			
			// chanceForAmethystInShell: This field represents the chance that any block making up the outer shell gets replaced with amethyst.
			// It is similar to the above, but should be more common. Amethyst is not the hardcoded block, it's just the intended one.
			CHANCE_RANGE.fieldOf("chanceForAlternateShellBlock").forGetter(GorlekCrystalBlockSelectionConfiguration::chanceForAlternateShellBlock),
			
			// chanceForAlternateOreInstead: This field represents the chance that a full ore block gets generated in place of any given
			// standard ore block. As the name implies, this chance is only rolled when the block is expected to be ore. It is skipped for other blocks.
			CHANCE_RANGE.fieldOf("chanceForAlternateOreInstead").forGetter(GorlekCrystalBlockSelectionConfiguration::chanceForAlternateOreInstead)
		).apply(cfg, GorlekCrystalBlockSelectionConfiguration::new)
	);
	
}
