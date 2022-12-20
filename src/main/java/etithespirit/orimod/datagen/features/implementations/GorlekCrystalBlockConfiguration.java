package etithespirit.orimod.datagen.features.implementations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record GorlekCrystalBlockConfiguration(BlockStateProvider primaryBlockProvider, BlockStateProvider internalOreBlock) {
	
	public static final Codec<GorlekCrystalBlockConfiguration> BLOCK_CONFIG_CODEC = RecordCodecBuilder.create(blockCfg ->
	      blockCfg.group(
	          BlockStateProvider.CODEC.fieldOf("primaryCrystalBlock").forGetter(GorlekCrystalBlockConfiguration::primaryBlockProvider),
	          BlockStateProvider.CODEC.fieldOf("internalOreBlock").forGetter(GorlekCrystalBlockConfiguration::internalOreBlock)
	      ).apply(blockCfg, GorlekCrystalBlockConfiguration::new)
	);
	
	
}
