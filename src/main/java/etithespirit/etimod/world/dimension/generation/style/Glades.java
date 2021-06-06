package etithespirit.etimod.world.dimension.generation.style;

import etithespirit.etimod.world.dimension.LightForestChunkGenerator;
import etithespirit.etimod.world.dimension.config.LightForestSettings;
import etithespirit.etimod.world.dimension.config.LightForestSettings.GladesSettings;
import etithespirit.etimod.world.dimension.generation.GeneratorController;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

public class Glades extends GenerationStyle<LightForestSettings.GladesSettings> {

	public Glades(LightForestChunkGenerator generator) {
		super(generator);
	}
	
	@Override
	public GladesSettings getSettings() {
		return settings.getGladesSettings();
	}

	@Override
	public boolean generateBlock(GeneratorController source, IChunk chunk, long seed, long worldX, long worldY, long worldZ, long maxHeight) {
		//long blockHeight = getNoiseBlock(worldX, worldZ, maxHeight) / 7;
		double blockHeight = getNoise(worldX, worldZ);
		
		double stretchXZ = getSettings().getTerrainHeightStretchXZ();
		double variance = getNoise(worldX, worldZ, stretchXZ, stretchXZ);
		
		blockHeight *= getSettings().getTerrainMagnitude();
		blockHeight += getSettings().getTerrainHeight();
		blockHeight += variance * getSettings().getTerrainHeightVariance();
		
		// Round it to a full block so grass works.
		blockHeight = Math.ceil(blockHeight);
		
		if (worldY <= blockHeight) {
			if (worldY == blockHeight) {
				chunk.setBlockState(new BlockPos(worldX, worldY, worldZ), Blocks.GRASS_BLOCK.getDefaultState(), false);
			} else {
				chunk.setBlockState(new BlockPos(worldX, worldY, worldZ), Blocks.DIRT.getDefaultState(), false);
			}
			return true;
		}
		return false;
	}
}
