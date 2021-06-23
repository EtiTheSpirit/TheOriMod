package etithespirit.etimod.world.dimension.generation.style;

import etithespirit.etimod.world.dimension.LightForestChunkGenerator;
import etithespirit.etimod.world.dimension.config.LightForestSettings;
import etithespirit.etimod.world.dimension.generation.GeneratorController;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

/**
 * A generation style used to generate tall shards of rock and ice in the sky.
 *
 * @author Eti
 */
@Deprecated
public class SkyShard extends GenerationStyle<LightForestSettings.SkyShardSettings> {
	
	public SkyShard(LightForestChunkGenerator generator) {
		super(generator);
	}
	
	@Override
	public LightForestSettings.SkyShardSettings getSettings() {
		return settings.getSkyShardSettings();
	}
	
	/*
	private double getNoise(int x, int z) {
		return noise.getValue(x / 64D, z / 64D) + 0.5D;
	}
	*/
	
	
	/*
function Noise(x, z)
	local n = NoiseAt(x, z) * 256
	local n2 = (NoiseAt(x / 2, z / 2, bit32.bnot(SEED)) * 128) + 96
	if n2 > 32 and n2 > n then
		return math.floor(n / 16)
	end
	return math.floor(n)
end

local height = (Noise(x * 6, z * 6) / 8) + 32
local floor = ((256 - Noise(x * 2, z * 2)) + 48) / 2
	 */
	
	private double specialNoiseAt(double x, double z, long maxHeight) {
		double n = getNoiseBlock(x, z, maxHeight);
		double n2 = getNoiseBlock(x / 2, z / 2, maxHeight);
		if (n2 > maxHeight / 8f && n2 > n) {
			return Math.floor(n / (maxHeight / 16f));
		}
		return Math.floor(n);
	}
	
	@Override
	public boolean generateBlock(GeneratorController source, IChunk chunk, long seed, long worldX, long worldY, long worldZ, long maxHeight) {
		final double upperHeightLimitBlocks = getSettings().getUpperHeightLimit() * maxHeight;
		final double lowerHeightLimitBlocks = getSettings().getLowerHeightLimit() * maxHeight;
		
		final double upperCompressX = getSettings().getUpperNoiseCompressionX();
		final double upperCompressZ = getSettings().getUpperNoiseCompressionZ();
		
		final double lowerCompressX = getSettings().getLowerNoiseCompressionX();
		final double lowerCompressZ = getSettings().getLowerNoiseCompressionZ();
		
		double height = specialNoiseAt(worldX * upperCompressX, worldZ * upperCompressZ, (long)Math.floor(maxHeight - upperHeightLimitBlocks)) + (maxHeight - upperHeightLimitBlocks);
		double floor = maxHeight - (specialNoiseAt(worldX * lowerCompressX, worldZ * lowerCompressZ, (long)Math.floor(maxHeight - lowerHeightLimitBlocks)) + lowerHeightLimitBlocks);
		
		height += upperHeightLimitBlocks;
		floor += lowerHeightLimitBlocks;
		
		if (worldY <= height && worldY >= floor) {
			chunk.setBlockState(new BlockPos(worldX, worldY, worldZ), getSettings().getBlockState(), false);
			return true;
		}
		return false;
	}	
}
