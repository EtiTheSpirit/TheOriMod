package etithespirit.etimod.world.dimension.generation;

import etithespirit.etimod.world.dimension.generation.style.GenerationStyle;
import net.minecraft.world.chunk.IChunk;

/**
 * Represents a piece of code that runs for every single block in a chunk.
 * @author Eti
 */
public interface IGeneratorRoutine {
	
	/**
	 * Generate a block in the chunk at the given location.<br/>
	 * Return true if the block was set to something by this routine, so no other routine should be allowed to modify it.
	 * @param source The {@link etithespirit.etimod.world.dimension.generation.GeneratorController} that called this routine.
	 * @param chunk The chunk to generate in.
	 * @param seed The world seed.
	 * @param worldX The current X coordinate in the world. It's safe to set a chunk's block to this coordinate as it wraps around 16.
	 * @param worldY The current Y coordinate in the world. It's safe to set a chunk's block to this coordinate as it wraps around 16.
	 * @param worldZ The current Z coordinate in the world. It's safe to set a chunk's block to this coordinate as it wraps around 16.
	 * @param maxHeight The height limit on the Y axis.
	 */
	boolean generateBlock(GeneratorController source, IChunk chunk, long seed, long worldX, long worldY, long worldZ, long maxHeight);

	/**
	 * Updates the noise generator to the given seed if necessary, or does nothing if it's already set.
	 * @param seed The new seed to use.
	 */
	void setNoiseToSeed(long seed);
	
	/**
	 * @return Whether or not this element is enabled.
	 */
	@SuppressWarnings("rawtypes")
	default boolean isEnabled() {
		if (this instanceof GenerationStyle) {
			return ((GenerationStyle)this).getSettings().getEnabled();
		}
		return true;
	}
}
