package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.energy.ILightEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class AbstractLightStorageTile extends AbstractLightTile implements ILightEnergyStorage {
	
	/** A container used to store energy. */
	protected final @Nonnull PersistentLightEnergyStorage storage;
	
	public AbstractLightStorageTile(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, Supplier<PersistentLightEnergyStorage> storageProvider) {
		super(pType, pWorldPosition, pBlockState);
		storage = storageProvider.get();
	}
	
	public AbstractLightStorageTile(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, @Nonnull PersistentLightEnergyStorage storageProvider) {
		super(pType, pWorldPosition, pBlockState);
		storage = storageProvider;
		storage.markDirty = this::setChanged;
	}
	
	
	
	@Override
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
	@Override
	public double receiveLight(double maxReceive, boolean simulate) {
		return storage.receiveLight(maxReceive, simulate);
	}
	
	@Override
	public double extractLight(double maxExtract, boolean simulate) {
		return storage.extractLight(maxExtract, simulate);
	}
	
	@Override
	public double getLightStored() {
		return storage.getLightStored();
	}
	
	@Override
	public double getMaxLightStored() {
		return storage.getMaxLightStored();
	}
	
	@Override
	public boolean canReceiveLight() {
		return storage.canReceiveLight();
	}
	
	@Override
	public boolean canExtractLight() {
		return storage.canExtractLight();
	}
	
	@Override
	public boolean acceptsConversion() {
		return storage.acceptsConversion();
	}
}
