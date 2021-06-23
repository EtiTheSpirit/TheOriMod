package etithespirit.etimod.registry;

import etithespirit.etimod.EtiMod;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
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

/**
 * Registers all biomes to the game.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public final class BiomeRegistry {
	
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
						.waterColor(0x99FFEC)
						.waterFogColor(0x99FFEC)
						.grassColorOverride(0x5FC774)
						.foliageColorOverride(0x5FC774)
						.skyColor(0x99FFFF)
						.fogColor(0xEEEEEE)
						.build();
				
				return new Biome.Builder()
						.downfall(1)
						.temperature(0.5f)
						.depth(1)
						.scale(1)
						.generationSettings(BiomeGenerationSettings.EMPTY)
						.mobSpawnSettings(MobSpawnInfo.EMPTY)
						.specialEffects(effects)
						.precipitation(RainType.RAIN)
						.biomeCategory(Category.PLAINS)
						.build();
	});
	
	public static final RegistryObject<Biome> DECAY_BADLANDS = BIOMES.register("decay_badlands", () -> {
		// 118, 123
		BiomeAmbience effects = new BiomeAmbience.Builder()
				.waterColor(0x222222)
				.waterFogColor(0x000000)
				.grassColorOverride(0x808080)
				.foliageColorOverride(0x808080)
				.skyColor(0xBBBBAF)
				.fogColor(0x666666)
				.build();
		
		return new Biome.Builder()
				.downfall(1)
				.temperature(0.5f)
				.depth(1)
				.scale(1)
				.generationSettings(BiomeGenerationSettings.EMPTY)
				.mobSpawnSettings(MobSpawnInfo.EMPTY)
				.specialEffects(effects)
				.precipitation(RainType.SNOW)
				.biomeCategory(Category.PLAINS)
				.build();
	});
	
	public static void registerAll() {
		BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private static RegistryKey<Biome> makeKey(String name) {
		return RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(EtiMod.MODID, name));
	}
}
