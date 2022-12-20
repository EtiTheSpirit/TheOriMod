package etithespirit.orimod.datagen.features.implementations;

import com.google.common.collect.AbstractIterator;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.StaticData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GorlekCrystalFeature extends Feature<GorlekCrystalConfiguration> {
	
	public GorlekCrystalFeature() {
		super(GorlekCrystalConfiguration.CONFIG_CODEC);
	}
	
	private static Vec3 getNormal(double xRot, double yRot) {
		float xRotRad = (float)(xRot * Mth.DEG_TO_RAD);
		float yRotRad = (float)(-yRot * Mth.DEG_TO_RAD);
		double cosY = Mth.cos(yRotRad);
		double sinY = Mth.sin(yRotRad);
		double cosX = Mth.cos(xRotRad);
		double sinX = Mth.sin(xRotRad);
		return new Vec3(sinY * cosX, -sinX, cosY * cosX);
	}
	
	private static void generateCircleOfBlocks(BlockPos center, WorldGenLevel level, BlockStateProvider shellProvider, BlockStateProvider oreProvider, int radius) {
		BlockPos.betweenClosedStream(AABB.ofSize(Vec3.atCenterOf(center), radius * 2, 1, radius * 2)).forEach(pos -> {
			if (Mth.sqrt((float)pos.distSqr(center)) <= radius) {
				if (level.ensureCanWrite(pos)) {
					level.setBlock(pos, shellProvider.getState(level.getRandom(), pos), StaticData.REPLICATE_CHANGE | StaticData.CAUSE_BLOCK_UPDATE);
				}
			}
		});
		if (level.ensureCanWrite(center)) {
			level.setBlock(center, oreProvider.getState(level.getRandom(), center), StaticData.REPLICATE_CHANGE | StaticData.CAUSE_BLOCK_UPDATE);
		}
	}
	
	private static Iterable<BlockPos> alongLine(BlockPos start, Vec3 direction, int length) {
		return () -> new AbstractIterator<>() {
			private BlockPos toBlockPos(Vec3 preciseLocation) {
				return new BlockPos(
					Math.floor(preciseLocation.x),
					Math.floor(preciseLocation.y),
					Math.floor(preciseLocation.z)
				);
			}
			
			private BlockPos last = start;
			private int iter = 0;
			
			@Nullable
			@Override
			protected BlockPos computeNext() {
				if (iter >= length) {
					return endOfData();
				}
				iter++;
				Vec3 nowPrecise = Vec3.atCenterOf(last);
				Vec3 next = nowPrecise.add(direction);
				BlockPos nextRetn = toBlockPos(next);
				last = nextRetn;
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
		RandomSource rng = pContext.level().getRandom();
		OriMod.LOG.debug("Generated crystal at " + pContext.origin().toShortString());
		
		double tiltAngle = rng.nextDouble() * config.maxTiltAngle();
		double yaw = rng.nextDouble() * 360;
		Vec3 direction = getNormal(tiltAngle, yaw);
		int thickness = config.crystalWidth().sample(rng);
		int length = config.crystalLength().sample(rng);
		Iterable<BlockPos> posAlongLine = alongLine(pContext.origin(), direction, length);
		for (BlockPos position : posAlongLine) {
			generateCircleOfBlocks(
				position,
				pContext.level(),
				config.blockConfiguration().primaryBlockProvider(),
				config.blockConfiguration().internalOreBlock(),
				thickness / 2
			);
		}
		return true;
	}
}
