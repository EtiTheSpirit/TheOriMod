package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.common.tile.light.helpers.EnergyReservoir;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LightToRFTile extends LightEnergyHandlingTile implements IEnergyStorage, ILightEnergyConsumer, IServerUpdatingTile {
	
	public static final ResourceLocation STORAGE_ID = OriMod.rsrc("rf_storage");
	
	private static final float MAX_CONVERSION_RATE_LUXEN = 1f;
	private final EnergyReservoir consumerHelper = new EnergyReservoir(MAX_CONVERSION_RATE_LUXEN);
	private int rfExtractedLastTick = 0;
	private List<BlockPos> lastKnownValidNeighbors = new ArrayList<>(6);
	
	public LightToRFTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_TO_RF_TILE.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	public void updateVisualPoweredAppearance() {
		BlockState currentState = getBlockState();
		boolean currentPower = currentState.getValue(ForlornAppearanceMarshaller.POWERED);
		boolean desiredPower = rfExtractedLastTick > 0;
		if (currentPower != desiredPower) {
			utilSetPoweredStateTo(desiredPower);
		}
	}
	
	@Override
	public void firstTick(Level inWorld, BlockPos at, BlockState state) {
		for (Direction cardinal : Direction.values()) {
			BlockPos changedAt = at.relative(cardinal);
			BlockEntity neighbor = inWorld.getBlockEntity(changedAt);
			if (neighbor != null) {
				Optional<IEnergyStorage> energyStorageCtr = neighbor.getCapability(ForgeCapabilities.ENERGY).resolve();
				if (energyStorageCtr.isPresent()) {
					lastKnownValidNeighbors.add(changedAt.immutable());
					return;
				}
			}
		}
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		float lux = ILightEnergyStorage.redstoneFluxToLuxen(maxExtract);
		if (simulate) {
			float available = consumerHelper.getStashedEnergy();
			return ILightEnergyStorage.luxenToRedstoneFlux(Math.min(available, lux));
		}
		
		lux = consumerHelper.consumeUpTo(lux, false);
		int extractedRf = ILightEnergyStorage.luxenToRedstoneFlux(lux);
		return extractedRf;
	}
	
	@Override
	public void updateServer(Level inLevel, BlockPos at, BlockState current) {
		rfExtractedLastTick = 0;
		for (BlockPos neighborPos : lastKnownValidNeighbors) {
			BlockEntity neighbor = inLevel.getBlockEntity(neighborPos);
			if (neighbor != null) {
				Optional<IEnergyStorage> energyStorageCtr = neighbor.getCapability(ForgeCapabilities.ENERGY).resolve();
				if (energyStorageCtr.isPresent()) {
					sendToStorage(energyStorageCtr.get());
					return;
				}
			}
		}
	}
	
	private void sendToStorage(IEnergyStorage storage) {
		if (storage.canReceive()) {
			int maxReceive = storage.receiveEnergy(Integer.MAX_VALUE, true);
			if (maxReceive == 0) return;
			int actuallyExtracted = extractEnergy(maxReceive, false);
			storage.receiveEnergy(actuallyExtracted, false);
			rfExtractedLastTick += actuallyExtracted;
		}
	}
	
	public void neighborChanged(BlockState thisState, Level world, BlockPos thisLocation, Block replacedBlock, BlockPos changedAt, boolean isMoving) {
		BlockEntity neighbor = world.getBlockEntity(changedAt);
		if (neighbor != null) {
			Optional<IEnergyStorage> energyStorageCtr = neighbor.getCapability(ForgeCapabilities.ENERGY).resolve();
			if (energyStorageCtr.isPresent()) {
				lastKnownValidNeighbors.add(changedAt.immutable());
				return;
			}
		}
		lastKnownValidNeighbors.remove(changedAt);
	}
	
	@Override
	public int getEnergyStored() {
		return ILightEnergyStorage.luxenToRedstoneFlux(consumerHelper.getStashedEnergy());
	}
	
	@Override
	public int getMaxEnergyStored() {
		return ILightEnergyStorage.luxenToRedstoneFlux(consumerHelper.getMaxStashedEnergy());
	}
	
	@Override
	public boolean canExtract() {
		return true;
	}
	
	@Override
	public boolean canReceive() {
		return false;
	}
	
	/**
	 * Attempts to consume the given amount of energy, returning the amount of power that was actually consumed.
	 *
	 * @param desiredAmount The amount of energy that is free for consumption. This can be {@link Float#POSITIVE_INFINITY} to query the amount of power the device wants.
	 * @param simulate      If true, the consumption will only be simulated for the sake of querying the desired value, and will not affect the device.
	 * @return The amount of energy that was actually used by this device.
	 */
	@Override
	public float consumeEnergy(float desiredAmount, boolean simulate) {
		float realAmount = desiredAmount / 2f;
		if (realAmount > MAX_CONVERSION_RATE_LUXEN) {
			realAmount = MAX_CONVERSION_RATE_LUXEN;
		}
		return consumerHelper.stash(realAmount, simulate);
	}
	
	/**
	 * For informational purposes, this returns the absolute maximum amount of power that this consumer could theoretically ever consume on a single tick.
	 *
	 * @return The absolute maximum amount of power that this consumer could theoretically ever consume on a single tick.
	 */
	@Override
	public float getMaximumDrawnAmountForDisplay() {
		return ILightEnergyStorage.redstoneFluxToLuxen(rfExtractedLastTick);
	}
	
	@Override
	public boolean hadTooLittlePowerLastForDisplay() {
		return false;
	}
}
