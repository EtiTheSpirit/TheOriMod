package etithespirit.etimod.world.dimension.generation.style;

import etithespirit.etimod.world.dimension.LightForestChunkGenerator;
import etithespirit.etimod.world.dimension.config.LightForestSettings;
import etithespirit.etimod.world.dimension.generation.GeneratorController;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

public class DimensionWaterLevel extends GenerationStyle<LightForestSettings.WaterLevelSettings> {

	public DimensionWaterLevel(LightForestChunkGenerator generator) {
		super(generator);
	}
	
	@Override
	public LightForestSettings.WaterLevelSettings getSettings() {
		return settings.getWaterLevelSettings();
	}

	@Override
	public boolean generateBlock(GeneratorController source, IChunk chunk, long seed, long worldX, long worldY, long worldZ, long maxHeight) {
		if (worldY <= getSettings().getWaterLevel()) {
			chunk.setBlockState(new BlockPos(worldX, worldY, worldZ), Blocks.WATER.defaultBlockState(), false);
			BlockPos below = new BlockPos(worldX, worldY - 1, worldZ);
			if (chunk.getBlockState(below).getBlock().equals(Blocks.GRASS_BLOCK)) {
				chunk.setBlockState(below, Blocks.DIRT.defaultBlockState(), false);
			}
			return true;
		}
		return false;
	}

	

}
