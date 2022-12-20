package etithespirit.orimod.datagen.features;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.datagen.features.implementations.AddGorlekOreCrystalFeature;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalBlockConfiguration;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalConfiguration;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.world.FeatureRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;

public class GenerateBiomeFeatures {
	
	private final DataGenerator dataGenerator;
	private final ExistingFileHelper existingFileHelper;
	private final RegistryOps<JsonElement> registryOps;
	
	private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, OriMod.MODID);
	
	public static final RegistryObject<Codec<AddGorlekOreCrystalFeature>> GORLEK_CRYSTAL_CODEC = BIOME_MODIFIER_SERIALIZERS.register(
		"gorlek_crystal",
		() -> RecordCodecBuilder.create(builder -> builder.group(
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
	
	public void generateGorlekOreFeaturesUsing(GatherDataEvent evt) {
		Holder<ConfiguredFeature<?, ?>> gorlekCrystal = Holder.direct(new ConfiguredFeature<>(
			FeatureRegistry.GORLEK_CRYSTAL_FEATURE.get(),
			new GorlekCrystalConfiguration(
				new GorlekCrystalBlockConfiguration(
					BlockStateProvider.simple(Blocks.AMETHYST_BLOCK),
					BlockStateProvider.simple(BlockRegistry.GORLEK_ORE.get())
				),
				UniformInt.of(8, 24),
				UniformInt.of(3, 5),
				60
			)
		));
		
		ResourceLocation gorlekCrystalFeatureCodec = GORLEK_CRYSTAL_CODEC.getId();
		PlacedFeature feature = new PlacedFeature(
			gorlekCrystal,
			List.of(
				RarityFilter.onAverageOnceEvery(1),
				RandomOffsetPlacement.of(
					UniformInt.of(0, 15),
					UniformInt.of(-16, 16)
				)
			)
		);
		Map<ResourceLocation, PlacedFeature> features = Map.of(
			gorlekCrystalFeatureCodec, feature
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
	
}
