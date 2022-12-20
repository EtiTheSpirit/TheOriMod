package etithespirit.orimod.registry.world;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalConfiguration;
import etithespirit.orimod.datagen.features.implementations.GorlekCrystalFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class FeatureRegistry {
	
	private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, OriMod.MODID);
	public static final RegistryObject<Feature<GorlekCrystalConfiguration>> GORLEK_CRYSTAL_FEATURE = FEATURES.register("gorlek_crystal", GorlekCrystalFeature::new);
	
	public static void registerAll() {
		FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}

