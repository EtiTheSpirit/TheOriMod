package etithespirit.orimod.datagen.features.implementations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record GorlekCrystalConfiguration(GorlekCrystalBlockConfiguration blockConfiguration, IntProvider crystalWidth, IntProvider crystalLength, double maxTiltAngle) implements FeatureConfiguration {
	
	public static final Codec<GorlekCrystalConfiguration> CONFIG_CODEC = RecordCodecBuilder.create(crystalCfg ->
       crystalCfg.group(
           GorlekCrystalBlockConfiguration.BLOCK_CONFIG_CODEC.fieldOf("blockConfiguration").forGetter(GorlekCrystalConfiguration::blockConfiguration),
           IntProvider.POSITIVE_CODEC.fieldOf("crystalWidth").forGetter(GorlekCrystalConfiguration::crystalWidth),
           IntProvider.POSITIVE_CODEC.fieldOf("crystalLength").forGetter(GorlekCrystalConfiguration::crystalLength),
           Codec.doubleRange(0, 90).fieldOf("maxTiltAngle").forGetter(GorlekCrystalConfiguration::maxTiltAngle)
       ).apply(crystalCfg, GorlekCrystalConfiguration::new)
	);
	
}
