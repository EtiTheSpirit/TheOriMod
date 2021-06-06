package etithespirit.etimod.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import etithespirit.etimod.world.dimension.config.LightForestSettings;
import etithespirit.etimod.world.dimension.generation.GeneratorController;
import etithespirit.etimod.world.dimension.generation.IGeneratorRoutine;
import etithespirit.etimod.world.dimension.generation.style.DimensionWaterLevel;
import etithespirit.etimod.world.dimension.generation.style.Glades;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

/**
 * Based off of McJty's tutorial. Modified for personal needs.
 * @author Eti
 *
 */
public class LightForestChunkGenerator extends ChunkGenerator {
	
	/*
	 * Settings for features present in the world.
	 */
	// public static final Codec<>
	
	/**
	 * Settings for the "Skyshard" generation type.
	 */
	public static final Codec<LightForestSettings.SkyShardSettings> GEN_SKYSHARD_CODEC = RecordCodecBuilder.create(instance -> 
		instance.group(
			// Common Settings across all generators (as dictated by the generation settings class hierarchy)
			Codec.BOOL.fieldOf("enabled").forGetter(LightForestSettings.SkyShardSettings::getEnabled),
			Codec.STRING.optionalFieldOf("_block", "minecraft:stone").forGetter(LightForestSettings.SkyShardSettings::getBlockString),
			Codec.DOUBLE.optionalFieldOf("noiseStretchX", 128D).forGetter(LightForestSettings.SkyShardSettings::getNoiseStretchX),
			Codec.DOUBLE.optionalFieldOf("noiseStretchZ", 128D).forGetter(LightForestSettings.SkyShardSettings::getNoiseStretchZ),
			
			// Skyshard-Specific Settings
			Codec.DOUBLE.fieldOf("upperHeightLimit").forGetter(LightForestSettings.SkyShardSettings::getUpperHeightLimit),
			Codec.DOUBLE.fieldOf("lowerHeightLimit").forGetter(LightForestSettings.SkyShardSettings::getLowerHeightLimit),
			
			Codec.DOUBLE.fieldOf("upperNoiseCompressionX").forGetter(LightForestSettings.SkyShardSettings::getUpperNoiseCompressionX),
			Codec.DOUBLE.fieldOf("upperNoiseCompressionZ").forGetter(LightForestSettings.SkyShardSettings::getUpperNoiseCompressionZ),
			
			Codec.DOUBLE.fieldOf("lowerNoiseCompressionX").forGetter(LightForestSettings.SkyShardSettings::getLowerNoiseCompressionX),
			Codec.DOUBLE.fieldOf("lowerNoiseCompressionZ").forGetter(LightForestSettings.SkyShardSettings::getLowerNoiseCompressionZ)
		).apply(instance, LightForestSettings.SkyShardSettings::new)
	);
	
	/**
	 * Settings for the "Glades" generation type.
	 */
	public static final Codec<LightForestSettings.GladesSettings> GEN_GLADES_CODEC = RecordCodecBuilder.create(instance -> 
		instance.group(
			// Common Settings across all generators (as dictated by the generation settings class hierarchy)
			Codec.BOOL.fieldOf("enabled").forGetter(LightForestSettings.GladesSettings::getEnabled),
			Codec.STRING.optionalFieldOf("_block", "minecraft:stone").forGetter(LightForestSettings.GladesSettings::getBlockString),
			Codec.DOUBLE.optionalFieldOf("noiseStretchX", 128D).forGetter(LightForestSettings.GladesSettings::getNoiseStretchX),
			Codec.DOUBLE.optionalFieldOf("noiseStretchZ", 128D).forGetter(LightForestSettings.GladesSettings::getNoiseStretchZ),
			
			
			// Glades-Specific Settings
			Codec.DOUBLE.fieldOf("terrainHeight").forGetter(LightForestSettings.GladesSettings::getTerrainHeight),
			Codec.DOUBLE.fieldOf("terrainMagnitude").forGetter(LightForestSettings.GladesSettings::getTerrainMagnitude),
			Codec.DOUBLE.fieldOf("terrainHeightStretchXZ").forGetter(LightForestSettings.GladesSettings::getTerrainHeightStretchXZ),
			Codec.DOUBLE.fieldOf("terrainHeightVariance").forGetter(LightForestSettings.GladesSettings::getTerrainHeightVariance)
			
		).apply(instance, LightForestSettings.GladesSettings::new)
	);
	
	public static final Codec<LightForestSettings.WaterLevelSettings> GEN_WATER_LEVEL_CODEC = RecordCodecBuilder.create(instance -> 
		instance.group(
			Codec.BOOL.fieldOf("enabled").forGetter(LightForestSettings.WaterLevelSettings::getEnabled),
			Codec.STRING.optionalFieldOf("_block", "minecraft:stone").forGetter(LightForestSettings.WaterLevelSettings::getBlockString),
			Codec.DOUBLE.optionalFieldOf("noiseStretchX", 128D).forGetter(LightForestSettings.WaterLevelSettings::getNoiseStretchX),
			Codec.DOUBLE.optionalFieldOf("noiseStretchZ", 128D).forGetter(LightForestSettings.WaterLevelSettings::getNoiseStretchZ),
			Codec.DOUBLE.fieldOf("waterLevel").forGetter(LightForestSettings.WaterLevelSettings::getWaterLevel)
		).apply(instance, LightForestSettings.WaterLevelSettings::new)
	);
	
	/**
	 * The root of the generation settings. This is the "settings" object in json.
	 */
	public static final Codec<LightForestSettings> GENERATION_SETTINGS_CODEC = RecordCodecBuilder.create(instance -> 
		instance.group(
			GEN_SKYSHARD_CODEC.fieldOf("skyShards").forGetter(LightForestSettings::getSkyShardSettings),
			GEN_GLADES_CODEC.fieldOf("glades").forGetter(LightForestSettings::getGladesSettings),
			GEN_WATER_LEVEL_CODEC.fieldOf("water").forGetter(LightForestSettings::getWaterLevelSettings)
		).apply(instance, LightForestSettings::new)
	);
	
	/**
	 * The main chunk generator codec.
	 */
	public static final Codec<LightForestChunkGenerator> CORE_CODEC = RecordCodecBuilder.create(instance ->
		instance.group(
			RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter(LightForestChunkGenerator::getBiomeRegistry),
			GENERATION_SETTINGS_CODEC.fieldOf("settings").forGetter(LightForestChunkGenerator::getSettings)
		).apply(instance, LightForestChunkGenerator::new)
	);

	
	public final LightForestSettings settings;
	
	public final GeneratorController controller = new GeneratorController();

	public final IGeneratorRoutine[] GENERATORS;
	
	public LightForestChunkGenerator(Registry<Biome> biomes, LightForestSettings settings) {
		super(new LightForestBiomeProvider(biomes), new DimensionStructuresSettings(false));
		this.settings = settings;
		
		GENERATORS = new IGeneratorRoutine[] {
			new Glades(this),
			// ^ This implements IGeneratorRoutine which simply offers a "generateBlock" method, which takes in an x, y, and z coordinate.
			// These coordinates are created via the chunk generation loop.
			
			new DimensionWaterLevel(this) // must be last (or among the last)
		};
		
		for (IGeneratorRoutine routine : GENERATORS) {
			controller.registerGenerator(routine);
		}
		// ^ The generator controller is responsible for iterating over all possible blocks in a chunk and running an IGeneratorRoutine on each.
	}
	
	public LightForestSettings getSettings() {
		return settings;
	}
	
	public Registry<Biome> getBiomeRegistry() {
		return ((LightForestBiomeProvider)biomeProvider).getBiomeRegistry();
	}

	@Override
	protected Codec<? extends ChunkGenerator> func_230347_a_() {
		return CORE_CODEC;
	}

	@Override
	public ChunkGenerator func_230349_a_(long seed) {
		return new LightForestChunkGenerator(getBiomeRegistry(), settings);
	}

	@Override
	public void generateSurface(WorldGenRegion worldGenRegion, IChunk chunk) {
		controller.processForChunk(worldGenRegion.getSeed(), chunk);
		// ^ GeneratorController performs its loop here through 16x256x16.
	}

	/**
	 * According to proposed mappings:
	 * "generateBaseChunk"
	 * Fills the Chunk with Blocks & updates the Heightmaps, no biome specific features or bedrock yet.
	 * 
	 * I don't exactly know how this couples with generateSurface, but I think I have a general idea.
	 */
	@Override
	public void func_230352_b_(IWorld world, StructureManager structMgr, IChunk chunk) {
		
	}

	@Override
	public int getHeight(int x, int z, Type heightmapType) {
		return 0;
	}

	@Override
	public IBlockReader func_230348_a_(int x, int z) {
		return new Blockreader(new BlockState[0]);
	}

}
