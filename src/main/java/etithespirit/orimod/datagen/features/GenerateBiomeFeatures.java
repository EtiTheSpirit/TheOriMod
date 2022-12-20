package etithespirit.orimod.datagen.features;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.datagen.features.implementations.AddGorlekOreCrystalFeature;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalBlockConfiguration;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalConfiguration;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalFeature;
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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GenerateBiomeFeatures {
	
	private final DataGenerator dataGenerator;
	private final ExistingFileHelper existingFileHelper;
	private final RegistryOps<JsonElement> registryOps;
	private static final String FEATURE_NAME = "gorlek_crystal";
	private static final ResourceLocation FEATURE_ID = OriMod.rsrc(FEATURE_NAME);
	
	private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, OriMod.MODID);
	private static final DeferredRegister<BiomeModifier> BIOME_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIERS, OriMod.MODID);
	
	public static final RegistryObject<Codec<AddGorlekOreCrystalFeature>> GORLEK_CRYSTAL_CODEC = BIOME_MODIFIER_SERIALIZERS.register(
		FEATURE_NAME,
		() -> RecordCodecBuilder.create(builder -> builder.group(
			Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddGorlekOreCrystalFeature::biomes),
			PlacedFeature.CODEC.fieldOf("feature").forGetter(AddGorlekOreCrystalFeature::feature)
		).apply(builder, AddGorlekOreCrystalFeature::new))
	);
	
	
	
	public GenerateBiomeFeatures(DataGenerator generator, ExistingFileHelper helper) {
		dataGenerator = generator;
		existingFileHelper = helper;
		registryOps = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
	}
	
	public static void registerAll() {
		BIOME_MODIFIER_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private void generateGorlekOrePlacedFeatureUsing(GatherDataEvent evt, Holder<ConfiguredFeature<?, ?>> gorlekCrystal) {
		PlacedFeature feature = new PlacedFeature(
			gorlekCrystal,
			List.of(
				RarityFilter.onAverageOnceEvery(1),
				RandomOffsetPlacement.of(
					UniformInt.of(0, 15),
					UniformInt.of(-16, 16)
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
					BlockStateProvider.simple(Blocks.AMETHYST_BLOCK),
					BlockStateProvider.simple(BlockRegistry.GORLEK_ORE.get())
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
