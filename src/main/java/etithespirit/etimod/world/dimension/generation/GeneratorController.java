package etithespirit.etimod.world.dimension.generation;

import java.util.ArrayList;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.IChunk;

public final class GeneratorController {

	private final ArrayList<IGeneratorRoutine> GENERATORS = new ArrayList<IGeneratorRoutine>();
	
	public void registerGenerator(IGeneratorRoutine generator) {
		GENERATORS.add(generator);
	}
	
	/**
	 * Iterates through all registered {@link etithespirit.etimod.world.dimension.generation.IGeneratorRoutine}s and runs them for every block.
	 * @param seed The world seed
	 * @param chunk The chunk that is being generated
	 */
	public void processForChunk(long seed, IChunk chunk) {
		final long maxHeight = 256;//chunk.getWorldForge().getHeight();
		boolean abort = false;
		for (int y = 0; y < maxHeight; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					ChunkPos currentPos = chunk.getPos();
					long newX = x + (currentPos.x * 16);
					long newZ = z + (currentPos.z * 16);
					for (IGeneratorRoutine gen : GENERATORS) {
						if (gen.isEnabled()) {
							gen.setNoiseToSeed(seed);
							abort = gen.generateBlock(this, chunk, seed, newX, y, newZ, maxHeight);
							if (abort) break;
						}
					}
				}
			}
		}
	}
	
	public GeneratorController() {
		// Bedrock floor generator
		registerGenerator(new IGeneratorRoutine() {
			@Override public boolean generateBlock(GeneratorController source, IChunk chunk, long seed, long worldX, long worldY, long worldZ, long maxHeight) {
				if (worldY > 0) return false;
				chunk.setBlockState(new BlockPos(worldX, worldY, worldZ), Blocks.BEDROCK.defaultBlockState(), false);
				return true;
			}

			@Override
			public void setNoiseToSeed(long seed) { }
		});
	}
}
