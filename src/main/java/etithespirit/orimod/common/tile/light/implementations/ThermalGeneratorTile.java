package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.aos.ConnectionHelper;
import etithespirit.orimod.client.audio.LightTechLooper;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IAmbientSoundEmitter;
import etithespirit.orimod.common.tile.IClientUpdatingTile;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.common.tile.light.helpers.EnergyReservoir;
import etithespirit.orimod.common.tile.light.helpers.SoundSmearer;
import etithespirit.orimod.energy.ILightEnergyGenerator;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

import java.util.Optional;

public class ThermalGeneratorTile extends LightEnergyHandlingTile implements IAmbientSoundEmitter, IServerUpdatingTile, IClientUpdatingTile, ILightEnergyGenerator {
	
	public static final float PEAK_GENERATION_RATE = 1/4f;
	private @ClientUseOnly Optional<LightTechLooper> looper = Optional.empty();
	private final EnergyReservoir generatorHelper = new EnergyReservoir(PEAK_GENERATION_RATE * (7/5f));
	private final BlockPos[] neighborPositions = new BlockPos[6];
	private float lastHeat = 0;
	private boolean wasOverloaded = false;
	private float lastGeneratedEnergy = 0;
	
	private final SoundSmearer smearer = new SoundSmearer(SoundSmearer.SmearDirection.DELAY_BOTH, 10);
	
	public ThermalGeneratorTile(BlockPos at, BlockState state) {
		super(TileEntityRegistry.THERMAL_GENERATOR.get(), at, state);
		int index = 0;
		for (Direction dir : Direction.values()) {
			neighborPositions[index++] = at.relative(dir).immutable();
		}
	}
	
	@Override
	public void setLevel(Level pLevel) {
		super.setLevel(pLevel);
		if (pLevel.isClientSide) {
			createLoopedSound();
		}
	}
	
	private void createLoopedSound() {
		looper = Optional.of(new LightTechLooper(
			this,
			null,
			SoundRegistry.get("tile.light_tech.thermal.loop"),
			null
		));
		looper.get().setBaseVolume(0.4f);
	}
	
	/**
	 * Updates the state of this block based on whether or not it is powered.
	 */
	@Override
	public void updateVisualPoweredAppearance() {
		if (level == null) return;
		BlockState state = level.getBlockState(getBlockPos());
		boolean isPowered = state.getValue(ForlornAppearanceMarshaller.POWERED);
		boolean shouldPower = lastHeat > 0;
		if (isPowered != shouldPower) {
			super.utilSetPoweredStateTo(shouldPower);
		}
	}
	
	@Override
	public void updateServer(Level inLevel, BlockPos at, BlockState current) {
		lastHeat = getHeat();
		lastGeneratedEnergy = (lastHeat / 5f) * PEAK_GENERATION_RATE;
		generatorHelper.stash(lastGeneratedEnergy, false);
	}
	
	@Override
	public void updateClient(Level inLevel, BlockPos at, BlockState current) {
		BlockState state = getBlockState();
		smearer.tick(state.getValue(ForlornAppearanceMarshaller.POWERED));
	}
	
	/**
	 * Returns a measure of how much "heat" surrounds this block. A heat of 5 is the best level in the overworld (one lava block on all five sides).
	 * @return The amount of "heat" that this block has. 5 represents the best normal case, anything larger and the machine is overcharged and can make more power.
	 */
	private float getHeat() {
		if (level == null) return 0;
		boolean isHot = level.dimensionType().ultraWarm();
		float heat = 0;
		float boost = (isHot ? 1.25f : 1f);
		
		for (BlockPos position : neighborPositions) {
			BlockState state = level.getBlockState(position);
			FluidState fluid = level.getFluidState(position);
			if (state.getMaterial() == Material.FIRE || state.is(Blocks.CAMPFIRE)) {
				heat += 0.5f * boost;
			} else if (state.is(Blocks.MAGMA_BLOCK)) {
				heat += 0.75f * boost;
			} else if (fluid.is(FluidTags.LAVA)) {
				heat += boost;
			}
		}
		if (level.dimensionType().ultraWarm()) {
			heat += 1f;
		}
		return heat;
	}
	
	/**
	 * Returns a reference to the sound(s) that this emits. This should be cached on creation of the Block Entity,
	 * and then returned here. Do not create a new instance when this is called.
	 *
	 * @return A reference to the sound that this emits.
	 */
	@Override
	public Optional<LightTechLooper> getSoundInstance() {
		return looper;
	}
	
	/**
	 * Whether or not the system believes the sound should be playing right now.
	 *
	 * @return Whether or not the sound should be playing right now.
	 */
	@Override
	public boolean soundShouldBePlaying() {
		return smearer.shouldSoundPlay();
	}
	
	/**
	 * Attempts to generate the desired amount of energy, returning the actual amount that was generated.
	 *
	 * @param desiredAmount The amount of energy that the caller wants to consume (that this generator should generate). This can be {@link Float#POSITIVE_INFINITY} to query the amount of power the device wants.
	 * @param simulate      If true, the generation will only be simulated for the sake of querying the desired value, and will not affect the device.
	 * @return The amount of energy that was actually used by this device.
	 */
	@Override
	public float takeGeneratedEnergy(float desiredAmount, boolean simulate) {
		if (desiredAmount <= 0) return 0;
		if (simulate) return Math.min(desiredAmount, generatorHelper.getStashedEnergy());
		float actualAmount = generatorHelper.consumeUpTo(desiredAmount, false);
		wasOverloaded = actualAmount != desiredAmount;
		return actualAmount;
	}
	
	/**
	 * For informational purposes, this returns the absolute maximum amount of power that this generator could theoretically ever put out on a single tick.
	 *
	 * @return The absolute maximum amount of power that this generator could theoretically ever put out on a single tick.
	 */
	@Override
	public float getMaximumGeneratedAmountForDisplay() {
		return lastGeneratedEnergy;
	}
	
	/**
	 * Returns true if the last call to {@link #takeGeneratedEnergy(float, boolean)} (simulated or not) resulted in less power than desired being generated.
	 * This is intended for use by external tools (or, in the case of this mod specifically, by Jade) to determine when the device is overdrawn without
	 * actually having access to the energy consumer(s).
	 *
	 * @return True if the last call to {@link #takeGeneratedEnergy(float, boolean)} did not have enough power to satisfy the needs of the consumer(s).
	 */
	@Override
	public boolean hadTooMuchDrawLastForDisplay() {
		return wasOverloaded;
	}
}
