package etithespirit.etimod.world.dimension.generation.style;

import java.util.Random;

import etithespirit.etimod.world.dimension.LightForestChunkGenerator;
import etithespirit.etimod.world.dimension.config.LightForestSettings;
import etithespirit.etimod.world.dimension.generation.IGeneratorRoutine;
import net.minecraft.world.gen.SimplexNoiseGenerator;

public abstract class GenerationStyle<TGenSettings extends LightForestSettings.GeneralSettings> implements IGeneratorRoutine {
	
	/**
	 * The generator that runs this routine.
	 */
	public final LightForestChunkGenerator generator;
	
	/**
	 * A noise generator that could be used in this routine.
	 */
	protected SimplexNoiseGenerator noise;
	
	/**
	 * The current seed that the noise generator is using.
	 */
	protected long currentSeed = 0;
	
	/**
	 * The container for settings of every routine. Use {@code getSettings()} to return settings specific to this {@link etithespirit.etimod.world.dimension.generation.style.GenerationStyle}
	 */
	public final LightForestSettings settings;
	
	/**
	 * Returns the settings specific to this generator.
	 */
	public abstract TGenSettings getSettings();
	
	/**
	 * Returns a noise percentage from -0.5 to +0.5 for the given world X/Z coordinates using the simplex noise generator.<br/>
	 * <strong>This automatically factors in the current generator's settings noise stretch x/z factors.</strong> Do not apply these yourself beforehand.
	 * @param worldX
	 * @param worldZ
	 * @return
	 */
	protected double getNoise(double worldX, double worldZ) {
		double x = worldX / getSettings().getNoiseStretchX();
		double z = worldZ / getSettings().getNoiseStretchZ();
		
		return noise.getValue(x, z);
	}
	
	/**
	 * Returns a noise percentage from -0.5 to +0.5 for the given world X/Z coordinates using the simplex noise generator.<br/>
	 * @param worldX
	 * @param worldZ
	 * @return
	 */
	protected double getNoise(double worldX, double worldZ, double stretchX, double stretchZ) {
		double x = worldX / stretchX;
		double z = worldZ / stretchZ;
		
		return noise.getValue(x, z);
	}
	
	/**
	 * Calls {@code getNoise(worldX, worldZ)} and scales the return value to an integer (via Math.floor) based on the world height limit. Note that this is based on a range of -0.5 to +0.5.
	 * @param worldX
	 * @param worldZ
	 * @param maxWorldHeight
	 * @return
	 */
	protected long getNoiseBlock(double worldX, double worldZ, long maxWorldHeight) {
		return (long)Math.floor(getNoise(worldX, worldZ) * maxWorldHeight);
	}
	
	
	@Override
	public void setNoiseToSeed(long seed) {
		if (currentSeed == seed) return;
		currentSeed = seed;
		noise = new SimplexNoiseGenerator(new Random(currentSeed));
	}
	
	public GenerationStyle(LightForestChunkGenerator generator) {
		this.generator = generator;
		settings = generator.getMySettings();
	}

}
