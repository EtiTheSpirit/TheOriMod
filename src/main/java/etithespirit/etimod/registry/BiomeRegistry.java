package etithespirit.etimod.registry;

import etithespirit.etimod.EtiMod;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
//import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeRegistry {
	
	//////////////////////////////////////////////////////////////////////////////
	/// BIOME KEYS
	public static final RegistryKey<Biome> GLADES_KEY = makeKey("glades");
	public static final RegistryKey<Biome> DECAY_BADLANDS_KEY = makeKey("decay_badlands");
	
	
	//////////////////////////////////////////////////////////////////////////////
	/// BIOME OBJECTS & REGISTRY
	private static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, EtiMod.MODID);
	
	
	public static final RegistryObject<Biome> GLADES = BIOMES.register("glades", () -> {
		// 118, 123
				BiomeAmbience effects = new BiomeAmbience.Builder()
						.setWaterColor(0x99FFEC)
						.setWaterFogColor(0x99FFEC)
						.withGrassColor(0x5FC774)
						.withFoliageColor(0x5FC774)
						.withSkyColor(0x99FFFF)
						.setFogColor(0xEEEEEE)
						.build();
				
				return new Biome.Builder()
						.downfall(1)
						.temperature(0.5f)
						.depth(1)
						.scale(1)
						.withGenerationSettings(BiomeGenerationSettings.DEFAULT_SETTINGS)
						.withMobSpawnSettings(MobSpawnInfo.EMPTY)
						.setEffects(effects)
						.precipitation(RainType.RAIN)
						.category(Category.PLAINS)
						.build();
	});
	
	public static final RegistryObject<Biome> DECAY_BADLANDS = BIOMES.register("decay_badlands", () -> {
		// 118, 123
		BiomeAmbience effects = new BiomeAmbience.Builder()
				.setWaterColor(0x222222)
				.setWaterFogColor(0x000000)
				.withGrassColor(0x808080)
				.withFoliageColor(0x808080)
				.withSkyColor(0xBBBBAF)
				.setFogColor(0x666666)
				.build();
		
		return new Biome.Builder()
				.downfall(1)
				.temperature(0.5f)
				.depth(1)
				.scale(1)
				.withGenerationSettings(BiomeGenerationSettings.DEFAULT_SETTINGS)
				.withMobSpawnSettings(MobSpawnInfo.EMPTY)
				.setEffects(effects)
				.precipitation(RainType.SNOW)
				.category(Category.PLAINS)
				.build();
	});
	
	public static void registerAll() {
		BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private static RegistryKey<Biome> makeKey(String name) {
		return RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(EtiMod.MODID, name));
	}
}
