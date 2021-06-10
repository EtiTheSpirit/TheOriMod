package etithespirit.etimod.world.dimension.config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Represents the settings that can be configured on the Light Forest dimension type.
 * @author Eti
 *
 */
public final class LightForestSettings {
	
	public final SkyShardSettings skyShards;
	public final GladesSettings gladesSettings;
	public final WaterLevelSettings waterLevelSettings;
	
	public LightForestSettings(SkyShardSettings skyShards, GladesSettings gladesSettings, WaterLevelSettings waterLevelSettings) {
		this.skyShards = skyShards;
		this.gladesSettings = gladesSettings;
		this.waterLevelSettings = waterLevelSettings;
	}
	
	public final SkyShardSettings getSkyShardSettings() {
		return skyShards;
	}
	
	public final GladesSettings getGladesSettings() {
		return gladesSettings;
	}
	
	public final WaterLevelSettings getWaterLevelSettings() {
		return waterLevelSettings;
	}
	
	/**
	 * The base class for all settings elements within LightForestSettings.
	 * @author Eti
	 *
	 */
	public static abstract class GeneralSettings {
		
		public final boolean enabled;
		public final String block;
		public final BlockState blockState;
		
		public final double noiseStretchX;
		public final double noiseStretchZ;
		
		public GeneralSettings(boolean enabled, String block, double nsx, double nsz) {
			this.enabled = enabled;
			this.block = block;
			this.noiseStretchX = nsx;
			this.noiseStretchZ = nsz;
			
			ResourceLocation rsrc = new ResourceLocation(block);
			RegistryObject<Block> targetBlock = RegistryObject.of(rsrc, ForgeRegistries.BLOCKS);
			
			@SuppressWarnings("deprecation")
			Block blockObj = targetBlock.orElseGet(() -> Registry.BLOCK.getOptional(rsrc).get());
			this.blockState = blockObj.defaultBlockState();
		}
		
		/**
		 * Returns whether or not this generation type should be used.
		 * @return
		 */
		public final boolean getEnabled() {
			return enabled;
		}
		
		/**
		 * Returns the raw resource location of the block to generate with in debug mode as a string.
		 * @return
		 */
		public final String getBlockString() {
			return block;
		}
		
		/**
		 * Using getBlockString(), this will attempt to find the given block in the registry, and will return that actual block instance's default state.
		 * @return
		 */
		public final BlockState getBlockState() {
			return blockState;
		}
		
		/**
		 * Returns the noise stretch on the X dimension.
		 * @return
		 */
		public final double getNoiseStretchX() {
			return noiseStretchX;
		}

		/**
		 * Returns the noise stretch on the Z dimension.
		 * @return
		 */
		public final double getNoiseStretchZ() {
			return noiseStretchZ;
		}
		
	}
	
	/**
	 * An implementation of GeneralSettings with nothing extra added to it.
	 * @author Eti
	 *
	 */
	public static final class EmptySettings extends GeneralSettings {
		
		public EmptySettings(boolean enabled, String block, double nsx, double nsz) {
			super(enabled, block, nsx, nsz);
		}
		
	}
	
	/**
	 * Skyshard Generator Settings
	 * @author Eti
	 *
	 */
	public static final class SkyShardSettings extends GeneralSettings {
		
		public final double upperHeightLimit;
		public final double lowerHeightLimit;
		
		public final double upperNoiseCompressionX;
		public final double upperNoiseCompressionZ;
		
		public final double lowerNoiseCompressionX;
		public final double lowerNoiseCompressionZ;
		
		public SkyShardSettings(boolean enabled, String block, double nsx, double nsz,
				double upperHeightLimit, double lowerHeightLimit,
				double upperNoiseCompressionX, double upperNoiseCompressionZ,
				double lowerNoiseCompressionX, double lowerNoiseCompressionZ) {
			
			super(enabled, block, nsx, nsz);
			this.upperHeightLimit = upperHeightLimit;
			this.lowerHeightLimit = lowerHeightLimit;
			
			this.upperNoiseCompressionX = upperNoiseCompressionX;
			this.upperNoiseCompressionZ = upperNoiseCompressionZ;
			
			this.lowerNoiseCompressionX = lowerNoiseCompressionX;
			this.lowerNoiseCompressionZ = lowerNoiseCompressionZ;
		}
		
		public final double getUpperHeightLimit() {
			return upperHeightLimit;
		}
		
		public final double getLowerHeightLimit() {
			return lowerHeightLimit;
		}
		
		public final double getUpperNoiseCompressionX() {
			return upperNoiseCompressionX;
		}
		
		public final double getUpperNoiseCompressionZ() {
			return upperNoiseCompressionZ;
		}
		
		public final double getLowerNoiseCompressionX() {
			return lowerNoiseCompressionX;
		}
		
		public final double getLowerNoiseCompressionZ() {
			return lowerNoiseCompressionZ;
		}
		
	}
	
	/**
	 * Glades generator settings.
	 * @author Eti
	 *
	 */
	public static final class GladesSettings extends GeneralSettings {
		
		public final double terrainHeight;
		public final double terrainMagnitude;
		public final double terrainHeightStretchXZ;
		public final double terrainHeightVariance;

		public GladesSettings(boolean enabled, String block, double nsx, double nsz,
				double terrainHeight, double terrainMagnitude, double terrainHeightStretchXZ, double terrainHeightVariance) {
			super(enabled, block, nsx, nsz);
			
			this.terrainHeight = terrainHeight;
			this.terrainMagnitude = terrainMagnitude;
			this.terrainHeightStretchXZ = terrainHeightStretchXZ;
			this.terrainHeightVariance = terrainHeightVariance;
		}
		
		public double getTerrainHeight() {
			return terrainHeight;
		}
		
		public double getTerrainMagnitude() {
			return terrainMagnitude;
		}
		
		public double getTerrainHeightStretchXZ() {
			return terrainHeightStretchXZ;
		}
		
		public double getTerrainHeightVariance() {
			return terrainHeightVariance;
		}
		
	}
	
	
	/**
	 * Settings that determine the water level in the dimension.
	 * @author Eti
	 */
	public static final class WaterLevelSettings extends GeneralSettings {
		
		public final double waterLevel;

		public WaterLevelSettings(boolean enabled, String block, double nsx, double nsz, double waterLevel) {
			super(enabled, block, nsx, nsz);
			this.waterLevel = waterLevel;
		}
		
		public double getWaterLevel() {
			return waterLevel;
		}
		
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	
	public static abstract class BaseFeatureContainer {
		
		public final boolean enabled;
		
		public BaseFeatureContainer(boolean enabled) {
			this.enabled = enabled;
		}
		
		/**
		 * Returns whether or not this feature should be used.
		 * @return
		 */
		public final boolean getEnabled() {
			return enabled;
		}
		
	}

	public static final class TreeFeatureContainer extends BaseFeatureContainer {
		
		public TreeFeatureContainer(boolean enabled) {
			super(enabled);
		}
		
	}
}
