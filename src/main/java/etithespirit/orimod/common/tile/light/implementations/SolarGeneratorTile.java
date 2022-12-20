package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.common.tile.light.helpers.EnergyReservoir;
import etithespirit.orimod.energy.ILightEnergyGenerator;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class SolarGeneratorTile extends LightEnergyHandlingTile implements IServerUpdatingTile, ILightEnergyGenerator {
	
	public static final float PEAK_GENERATION_RATE = 1/64f;
	private final EnergyReservoir generatorHelper = new EnergyReservoir(PEAK_GENERATION_RATE);
	
	public SolarGeneratorTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.SOLAR_GENERATOR.get(), pWorldPosition, pBlockState);
	}
	
	private float lastGenerated = 0;
	private boolean wasOverloaded = false;
	
	@Override
	public void updateVisualPoweredAppearance() {
		BlockState state = getBlockState();
		boolean isPoweredNow = state.getValue(ForlornAppearanceMarshaller.POWERED);
		boolean shouldPowerNow = lastGenerated > 0;
		if (isPoweredNow != shouldPowerNow) {
			super.utilSetPoweredStateTo(shouldPowerNow);
		}
	}
	
	private int getBrightness(Level inLevel, BlockPos at) {
		int adjustedBrightness = inLevel.getBrightness(LightLayer.SKY, at) - inLevel.getSkyDarken();
		float sunAngleRads = inLevel.getSunAngle(1);
		float nearestPole = sunAngleRads < (float)Math.PI ? 0 : (float)Math.PI * 2;
		sunAngleRads += (nearestPole - sunAngleRads) * 0.2f;
		adjustedBrightness = Math.round((float)adjustedBrightness * Mth.cos(sunAngleRads));
		
		adjustedBrightness = Mth.clamp(adjustedBrightness, 0, 15);
		return adjustedBrightness;
	}
	
	@Override
	public void updateServer(Level inLevel, BlockPos at, BlockState current) {
		float generated = 0;
		if (inLevel.dimensionType().hasSkyLight()) {
			int brightness = getBrightness(inLevel, at.above());
			generated = (brightness / 15f) * PEAK_GENERATION_RATE;
		}
		lastGenerated = generated;
		generatorHelper.stash(generated);
		
		boolean isBlockPowered = current.getValue(ForlornAppearanceMarshaller.POWERED);
		boolean shouldBePowered = lastGenerated > 0;
		if (isBlockPowered != shouldBePowered) {
			inLevel.setBlock(at, current.setValue(ForlornAppearanceMarshaller.POWERED, shouldBePowered), StaticData.REPLICATE_CHANGE);
		}
	}
	
	@Override
	public float takeGeneratedEnergy(float desiredAmount, boolean simulate) {
		if (desiredAmount <= 0) return 0;
		if (simulate) return Math.min(desiredAmount, generatorHelper.getStashedEnergy());
		float actualAmount = generatorHelper.consumeUpTo(desiredAmount, false);
		wasOverloaded = actualAmount != desiredAmount;
		return actualAmount;
	}
	
	@Override
	public float getMaximumGeneratedAmountForDisplay() {
		return lastGenerated;
	}
	
	@Override
	public boolean hadTooMuchDrawLastForDisplay() {
		return wasOverloaded;
	}
}
