package etithespirit.orimod.datagen.features.implementations;

import com.mojang.serialization.Codec;
import etithespirit.orimod.datagen.features.GenerateBiomeFeatures;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record AddGorlekOreCrystalFeature(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) implements BiomeModifier {
	
	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
		if (phase == Phase.ADD) {
			builder.getGenerationSettings().addFeature(
				GenerationStep.Decoration.UNDERGROUND_ORES,
				feature
			);
		}
	}
	
	@Override
	public Codec<? extends BiomeModifier> codec() {
		return GenerateBiomeFeatures.GORLEK_CRYSTAL_CODEC.get();
	}
}
