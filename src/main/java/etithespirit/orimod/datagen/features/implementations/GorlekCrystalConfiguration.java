package etithespirit.orimod.datagen.features.implementations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record GorlekCrystalConfiguration(GorlekCrystalBlockConfiguration blockConfiguration, IntProvider crystalWidth, IntProvider crystalLength, double maxTiltAngle) implements FeatureConfiguration {

	public static final Codec<GorlekCrystalConfiguration> CONFIG_CODEC = RecordCodecBuilder.create(crystalCfg ->
		crystalCfg.group(
			// blockConfiguration: This field contains all configuration for the blocks used in the feature, as well as when those blocks get used.
			GorlekCrystalBlockConfiguration.BLOCK_CONFIG_CODEC.fieldOf("blockConfiguration").forGetter(GorlekCrystalConfiguration::blockConfiguration),
			
			// crystalWidth: The width of the feature when generated. This will always be rounded down to the nearest odd number.
			IntProvider.POSITIVE_CODEC.fieldOf("crystalWidth").forGetter(GorlekCrystalConfiguration::crystalWidth),
			
			// crystalLength: The length of the feature when generated (obviously).
			IntProvider.POSITIVE_CODEC.fieldOf("crystalLength").forGetter(GorlekCrystalConfiguration::crystalLength),
			
			// maxTiltAngle: The tilt angle of the crystal. The yaw is always randomized in a full 360. This determines its pitch. A value of 90 permits the crystal to be completely upright, and 0 perfectly horizontal.
			Codec.doubleRange(0, 90).fieldOf("maxTiltAngle").forGetter(GorlekCrystalConfiguration::maxTiltAngle)
		).apply(crystalCfg, GorlekCrystalConfiguration::new)
	);

}
