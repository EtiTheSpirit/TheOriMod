package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class SolarGeneratorTile extends LightEnergyStorageTile implements IServerUpdatingTile, LightEnergyStorageTile.ILuxenGenerator {
	
	public static final float PEAK_GENERATION_RATE = 1/64f;
	
	public SolarGeneratorTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.SOLAR_GENERATOR.get(), pWorldPosition, pBlockState, new PersistentLightEnergyStorage(null, PEAK_GENERATION_RATE, 0, PEAK_GENERATION_RATE));
	}
	
	private float lastGenerated = 0;
	
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
		int brightness = getBrightness(inLevel, at.above());
		
		float generated = (brightness / 15f) * PEAK_GENERATION_RATE;
		lastGenerated = generated;
		generateEnergy(generated, false);
		
		boolean isBlockPowered = current.getValue(ForlornAppearanceMarshaller.POWERED);
		boolean shouldBePowered = lastGenerated > 0;
		if (isBlockPowered != shouldBePowered) {
			inLevel.setBlock(at, current.setValue(ForlornAppearanceMarshaller.POWERED, shouldBePowered), StaticData.REPLICATE_CHANGE);
		}
	}
	
	@Override
	public float getLuxGeneratedPerTick() {
		return lastGenerated;
	}
	
	
	@Override
	public boolean skipAutomaticPoweredBlockstate() {
		return true;
	}
}
