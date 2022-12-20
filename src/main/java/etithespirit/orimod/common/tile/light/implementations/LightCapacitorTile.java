package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import etithespirit.orimod.util.nbt.NBTIOHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LightCapacitorTile extends LightEnergyHandlingTile implements ILightEnergyStorage {
	
	private float maxReceiveRate = 20;
	private float maxDischargeRate = 20;
	private float maxStorage = 100;
	private float storedPower = 0;
	
	public LightCapacitorTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_ENERGY_STORAGE_TILE.get(), pWorldPosition, pBlockState);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		maxReceiveRate = NBTIOHelper.getFloatOrDefault(pTag, "maxReceiveRate", maxReceiveRate);
		maxDischargeRate = NBTIOHelper.getFloatOrDefault(pTag, "maxDischargeRate", maxDischargeRate);
		maxStorage = NBTIOHelper.getFloatOrDefault(pTag, "maxStorage", maxStorage);
		storedPower = NBTIOHelper.getFloatOrDefault(pTag, "storedPower", storedPower);
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		pTag.putFloat("maxReceiveRate", maxReceiveRate);
		pTag.putFloat("maxDischargeRate", maxDischargeRate);
		pTag.putFloat("maxStorage", maxStorage);
		pTag.putFloat("storedPower", storedPower);
	}
	
	@Override
	public void saveToItem(ItemStack pStack) {
		super.saveToItem(pStack);
		CompoundTag pTag = pStack.getOrCreateTag();
		pTag.putFloat("maxReceiveRate", maxReceiveRate);
		pTag.putFloat("maxDischargeRate", maxDischargeRate);
		pTag.putFloat("maxStorage", maxStorage);
		pTag.putFloat("storedPower", storedPower);
		pStack.setTag(pTag);
	}
	
	@Override
	public float receiveLight(float maxReceive, boolean simulate) {
		maxReceive = Mth.clamp(maxReceive, 0, maxReceiveRate);
		if (maxReceive == 0) return 0;
		
		float available = maxStorage - storedPower;
		if (available < maxReceive) {
			maxReceive = available;
		}
		if (simulate) return maxReceive;
		if (maxReceive == 0) return 0;
		
		storedPower += maxReceive;
		this.setChanged();
		return maxReceive;
	}
	
	@Override
	public float extractLightFrom(float maxExtract, boolean simulate) {
		maxExtract = Mth.clamp(maxExtract, 0, maxDischargeRate);
		if (maxExtract == 0) return 0;
		
		float available = storedPower;
		if (available < maxExtract) {
			maxExtract = available;
		}
		if (simulate) return maxExtract;
		if (maxExtract == 0) return 0;
		
		storedPower -= maxExtract;
		this.setChanged();
		return maxExtract;
	}
	
	/**
	 * @return The amount of energy currently stored.
	 */
	@Override
	public float getLightStored() {
		return storedPower;
	}
	
	/**
	 * @return The maximum amount of energy that can be stored.
	 */
	@Override
	public float getMaxLightStored() {
		return maxStorage;
	}
	
	/**
	 * Returns if this storage can have energy extracted.
	 * If this is false, then any calls to extractEnergy will return 0.
	 *
	 * @return Whether or not this storage can have energy extracted from it.
	 */
	@Override
	public boolean canExtractLightFrom() {
		return maxDischargeRate > 0;
	}
	
	/**
	 * Used to determine if this storage can receive energy.
	 * If this is false, then any calls to receiveEnergy will return 0.
	 *
	 * @return Whether or not this storage can have energy added to it.
	 */
	@Override
	public boolean canReceiveLight() {
		return maxReceiveRate > 0;
	}
}
