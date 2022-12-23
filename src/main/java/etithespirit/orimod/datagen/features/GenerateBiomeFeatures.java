package etithespirit.orimod.datagen.features;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalBlockConfiguration;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalConfiguration;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalFeature;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalBlockSelectionConfiguration;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.world.FeatureRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GenerateBiomeFeatures {
	
	private final DataGenerator dataGenerator;
	private final ExistingFileHelper existingFileHelper;
	private final RegistryOps<JsonElement> registryOps;
	private static final String FEATURE_NAME = "gorlek_crystal";
	private static final ResourceLocation FEATURE_ID = OriMod.rsrc(FEATURE_NAME);
	
	
	public GenerateBiomeFeatures(DataGenerator generator, ExistingFileHelper helper) {
		dataGenerator = generator;
		existingFileHelper = helper;
		registryOps = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
	}
	
	private void generateGorlekOrePlacedFeatureUsing(GatherDataEvent evt, Holder<ConfiguredFeature<?, ?>> gorlekCrystal) {
		PlacedFeature feature = new PlacedFeature(
			gorlekCrystal,
			List.of(
				RarityFilter.onAverageOnceEvery(80),
				RandomOffsetPlacement.of(
					UniformInt.of(-8, 8),
					UniformInt.of(8, 16)
				),
				BiomeFilter.biome()
			)
		);
		Map<ResourceLocation, PlacedFeature> features = Map.of(
			FEATURE_ID, feature
		);
		
		JsonCodecProvider<PlacedFeature> provider = JsonCodecProvider.forDatapackRegistry(
			dataGenerator,
			existingFileHelper,
			OriMod.MODID,
			registryOps,
			Registry.PLACED_FEATURE_REGISTRY,
			features
		);
		dataGenerator.addProvider(evt.includeServer(), provider);
	}
	
	private void generateGorlekOreConfiguredFeatureUsing(GatherDataEvent evt) {
		ConfiguredFeature<GorlekCrystalConfiguration, GorlekCrystalFeature> cfgToDatagen = new ConfiguredFeature<>(
			(GorlekCrystalFeature)FeatureRegistry.GORLEK_CRYSTAL_FEATURE.get(),
			new GorlekCrystalConfiguration(
				new GorlekCrystalBlockConfiguration(
					BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
					BlockStateProvider.simple(Blocks.AMETHYST_BLOCK),
					BlockStateProvider.simple(BlockRegistry.GORLEK_ORE.get()),
					BlockStateProvider.simple(BlockRegistry.RAW_GORLEK_ORE_BLOCK.get()),
					new GorlekCrystalBlockSelectionConfiguration(
						0.45,
						0.02,
						0.125,
						0.001
					)
				),
				UniformInt.of(3, 5),
				UniformInt.of(8, 24),
				60
			)
		);
		
		Map<ResourceLocation, ConfiguredFeature<?, ?>> features = Map.of(
			FEATURE_ID, cfgToDatagen
		);
		
		JsonCodecProvider<ConfiguredFeature<?, ?>> provider = JsonCodecProvider.forDatapackRegistry(
			dataGenerator,
			existingFileHelper,
			OriMod.MODID,
			registryOps,
			Registry.CONFIGURED_FEATURE_REGISTRY,
			features
		);
		dataGenerator.addProvider(evt.includeServer(), provider);
		
		ResourceKey<Registry<ConfiguredFeature<?, ?>>> configuredFeatureRegistry = Registry.CONFIGURED_FEATURE_REGISTRY;
		Optional<? extends Registry<ConfiguredFeature<?, ?>>> registryInstanceCtr = registryOps.registry(configuredFeatureRegistry);
		if (registryInstanceCtr.isPresent()) {
			Registry<ConfiguredFeature<?, ?>> registryInstance = registryInstanceCtr.get();
			Holder<ConfiguredFeature<?, ?>> featureHolder = registryInstance.getOrCreateHolderOrThrow(ResourceKey.create(configuredFeatureRegistry, FEATURE_ID));
			generateGorlekOrePlacedFeatureUsing(evt, featureHolder);
		}
	}
	
	public void generateGorlekOreFeaturesUsing(GatherDataEvent evt) {
		generateGorlekOreConfiguredFeatureUsing(evt);
	}
	
}
