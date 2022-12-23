package etithespirit.orimod.datagen.features.implementations;

import com.google.common.collect.AbstractIterator;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.StaticData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GorlekCrystalFeature extends Feature<GorlekCrystalConfiguration> {
	
	public GorlekCrystalFeature() {
		super(GorlekCrystalConfiguration.CONFIG_CODEC);
	}
	
	/**
	 * Derived from entity code, this takes in a pitch and yaw and returns a normal vector given the two rotations. The rotations are in degrees.
	 * "Always Up" refers to the fact that the Y axis is always positive.
	 * @param xRot The pitch in degrees.
	 * @param yRot The yaw in degrees.
	 * @return A normal rotated by the pitch and yaw. In which order? Good question!
	 */
	private static Vec3 getNormalAlwaysUp(double xRot, double yRot) {
		float xRotRad = (float)(xRot * Mth.DEG_TO_RAD);
		float yRotRad = (float)(-yRot * Mth.DEG_TO_RAD);
		double cosY = Mth.cos(yRotRad);
		double sinY = Mth.sin(yRotRad);
		double cosX = Mth.cos(xRotRad);
		double sinX = Mth.sin(xRotRad);
		return new Vec3(sinY * cosX, Math.abs(-sinX), cosY * cosX);
	}
	
	/**
	 * Explicitly set up to return the appropriate ore block (be it the standard ore block, or the raw ore block itself) based on RNG.
	 * This does not care about what the block would be in the structure (i.e. ore vs. shell), as this should have been calculated before calling this method.
	 * @param rng The randomizer.
	 * @param blockCfg The block data.
	 * @return Either the stock ore block, or the raw ore full block.
	 */
	private static BlockState getOre(RandomSource rng, GorlekCrystalBlockConfiguration blockCfg) {
		GorlekCrystalBlockSelectionConfiguration randomCfg = blockCfg.oreGenerationChances();
		if (rng.nextDouble() < randomCfg.chanceForAlternateOreInstead()) {
			return blockCfg.internalFullOreBlock().getState(rng, BlockPos.ZERO);
		}
		return blockCfg.internalOreBlock().getState(rng, BlockPos.ZERO);
	}
	
	/**
	 * Explicitly set up to return the appropriate shell block based on RNG.
	 * This does not care about what the block would be in the structure (i.e. ore vs. shell), as this should have been calculated before calling this method.
	 * @param rng The randomizer.
	 * @param blockCfg The block data.
	 * @return Either the default shell block, or its alternate counterpart.
	 */
	private static BlockState getShell(RandomSource rng, GorlekCrystalBlockConfiguration blockCfg) {
		GorlekCrystalBlockSelectionConfiguration randomCfg = blockCfg.oreGenerationChances();
		if (rng.nextDouble() < randomCfg.chanceForAlternateShellBlock()) {
			return blockCfg.alternateShellBlock().getState(rng, BlockPos.ZERO); // They use simple sources so no special position info is needed.
		}
		return blockCfg.shellBlock().getState(rng, BlockPos.ZERO);
	}
	
	/**
	 * The slightly-more-specific variation to {@link #getOre(RandomSource, GorlekCrystalBlockConfiguration)}, this method
	 * returns the intended block for the ore seam <em>in general</em> (including the chance to fail to generate ore and instead generate a shell block).
	 * It should only be called for blocks running through the seam.
	 * @param rng The randomizer.
	 * @param blockCfg The block data.
	 * @return Either a random ore block, or a random shell block.
	 */
	private static BlockState getBlockStateForOreSeam(RandomSource rng, GorlekCrystalBlockConfiguration blockCfg) {
		GorlekCrystalBlockSelectionConfiguration randomCfg = blockCfg.oreGenerationChances();
		if (rng.nextDouble() < randomCfg.chanceForOreBlock()) {
			return getOre(rng, blockCfg);
		}
		return getShell(rng, blockCfg);
	}
	
	/**
	 * The counterpart to {@link #getBlockStateForOreSeam(RandomSource, GorlekCrystalBlockConfiguration)}, this variation
	 * should be called for blocks in the shell.
	 * @param rng The randomizer.
	 * @param blockCfg The block data.
	 * @return Either a random ore block, or a random shell block.
	 */
	private static BlockState getBlockStateForShell(RandomSource rng, GorlekCrystalBlockConfiguration blockCfg) {
		GorlekCrystalBlockSelectionConfiguration randomCfg = blockCfg.oreGenerationChances();
		if (rng.nextDouble() < randomCfg.chanceForOreBlockInShell()) {
			// The chance for an ore block in place of a stock shell block occurred! Return an ore block.
			return getOre(rng, blockCfg);
		}
		return getShell(rng, blockCfg);
	}
	
	private static boolean generateCircleOfBlocks(Map<BlockPos, BlockState> desiredWrites, BlockPos center, WorldGenLevel level, int radius, Vec3 nrm, GorlekCrystalBlockConfiguration blockCfg) {
		if (radius < 3) radius = 3;
		if ((radius & 1) == 0) radius -= 1;
		AtomicBoolean ok = new AtomicBoolean(true);
		
		// Get the square radius so that distSqr is accurate (avoid sqrt!)
		// Then create an AABB of a flat plane (one block thick) using the normal to figure out the most appropriate orientation of the plate.
		int radiusSqr = radius * radius;
		AABB target;
		if (Math.abs(nrm.y) > 0.707) {
			target = AABB.ofSize(Vec3.atCenterOf(center), radius, 1, radius);
		} else if (Math.abs(nrm.x) > 0.707) {
			target = AABB.ofSize(Vec3.atCenterOf(center), 1, radius, radius);
		} else {
			target = AABB.ofSize(Vec3.atCenterOf(center), radius, radius, 1);
		}
		
		BlockPos.betweenClosedStream(target).forEach(pos -> {
			if (!ok.get()) return;
			
			double centerSqrDist = pos.distSqr(center);
			if (centerSqrDist <= radiusSqr) {
				if (!level.ensureCanWrite(pos)) {
					ok.set(false);
					desiredWrites.clear();
					return;
				}
				
				BlockState state = level.getBlockState(pos);
				if (state.is(Blocks.BEDROCK)) {
					return;
				}
				
				if (pos.equals(center)) {
					// This is along the seam itself.
					desiredWrites.putIfAbsent(pos.immutable(), getBlockStateForOreSeam(level.getRandom(), blockCfg));
				} else {
					// This is part of the shell.
					desiredWrites.putIfAbsent(pos.immutable(), getBlockStateForShell(level.getRandom(), blockCfg));
				}
			}
		});
		return ok.get();
	}
	
	/**
	 * Returns an iterable of BlockPos going along a line in the given direction for the given length.
	 * @param start The origin of the line.
	 * @param direction The direction the line travels.
	 * @param length How far the line travels.
	 * @return An iterable of every block in the line.
	 */
	private static Iterable<BlockPos> alongLine(BlockPos start, Vec3 direction, int length) {
		return () -> new AbstractIterator<>() {
			private BlockPos toBlockPos(Vec3 preciseLocation) {
				return new BlockPos(
					Math.floor(preciseLocation.x),
					Math.floor(preciseLocation.y),
					Math.floor(preciseLocation.z)
				);
			}
			
			private BlockPos lastRetn = start;
			private Vec3 last = Vec3.atCenterOf(start);
			private int iter = 0;
			
			@Nullable
			@Override
			protected BlockPos computeNext() {
				if (iter >= length) {
					return endOfData();
				}
				iter++;
				BlockPos nextRetn;
				do {
					Vec3 next = last.add(direction);
					nextRetn = toBlockPos(next);
					last = next;
				} while (nextRetn.equals(lastRetn));
				lastRetn = nextRetn;
				return nextRetn;
			}
		};
	}
	
	/**
	 * Places the given feature at the given location.
	 * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
	 * that they can safely generate into.
	 *
	 * @param pContext A context object with a reference to the level and the position the feature is being placed at
	 */
	@Override
	public boolean place(FeaturePlaceContext<GorlekCrystalConfiguration> pContext) {
		GorlekCrystalConfiguration config = pContext.config();
		
		WorldGenLevel level = pContext.level();
		RandomSource rng = level.getRandom();
		OriMod.logCustomTrace("Placed Gorlek Crystal feature at " + pContext.origin().toShortString());
		
		double tiltAngle = rng.nextDouble() * config.maxTiltAngle();
		double yaw = rng.nextDouble() * 360;
		Vec3 direction = getNormalAlwaysUp(tiltAngle, yaw);
		
		int thickness = config.crystalWidth().sample(rng);
		int length = config.crystalLength().sample(rng);
		
		Iterable<BlockPos> posAlongLine = alongLine(pContext.origin(), direction, length);
		GorlekCrystalBlockConfiguration blockCfg = config.blockConfiguration();
		
		Map<BlockPos, BlockState> fillStates = new HashMap<>(128);
		for (BlockPos position : posAlongLine) {
			if (!generateCircleOfBlocks(
				fillStates,
				position,
				level,
				thickness / 2,
				direction,
				blockCfg
			)) {
				return false;
			}
		}
		
		fillStates.forEach((pos, state) -> level.setBlock(pos, state, StaticData.REPLICATE_CHANGE));
		return true;
	}
}
