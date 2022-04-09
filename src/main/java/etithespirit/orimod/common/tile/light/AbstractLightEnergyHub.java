package etithespirit.orimod.common.tile.light;


import etithespirit.orimod.common.tile.WorldUpdateListener;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.lighttechlgc.Assembly;
import etithespirit.orimod.server.world.ChunkKeepAlive;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * A superclass representing all Light-based energy blocks that manage energy in some way, such as adding it, removing it, or both.<br/>
 * Hubs are a core component of {@link Assembly assemblies} which manage and optimize networks of Light equipment.
 * @author Eti
 */
@SuppressWarnings("unused")
public abstract class AbstractLightEnergyHub extends BlockEntity implements ILightEnergyStorage {
	
	/** The assembly associated with this instance (a collection of {@link AbstractLightEnergyLink} and {@link AbstractLightEnergyHub}) */
	// protected Assembly assembly = null;
	
	/** A container used to store energy. */
	protected final @Nonnull PersistentLightEnergyStorage storage;
	
	/**
	 * Create a new hub with the given storage.
	 * @param tileEntityTypeIn The type of BlockEntity to create.
	 * @param at The location to create it at.
	 * @param state The BlockState to create it for.
	 * @param storageProvider A provicder of the storage object to allow referencing some instance data.
	 */
	protected AbstractLightEnergyHub(BlockEntityType<?> tileEntityTypeIn, BlockPos at, BlockState state, Supplier<PersistentLightEnergyStorage> storageProvider) {
		this(tileEntityTypeIn, at, state, storageProvider.get());
	}
	
	/**
	 * Create a new hub with the given storage.
	 * @param tileEntityTypeIn The type of BlockEntity to create.
	 * @param at The location to create it at.
	 * @param state The BlockState to create it for.
	 * @param storage The energy storage data to use.
	 */
	public AbstractLightEnergyHub(BlockEntityType<?> tileEntityTypeIn, BlockPos at, BlockState state, @NotNull PersistentLightEnergyStorage storage) {
		super(tileEntityTypeIn, at, state);
		this.storage = storage;
		storage.markDirty = this::setChanged;
		
	}
	/*
	
	public Assembly getAssembly() {
		return assembly;
	}
	
	public void setAssembly(Assembly asm) {
		assembly = asm;
	}
	*/
	
	@Override
	public void setRemoved() {
		/*
		if (assembly != null) {
			assembly.disconnectHub(this);
		}
		
		 */
		super.setRemoved();
	}
	
	@Override
	public void setLevel(Level world) {
		super.setLevel(world);
		if (this.hasLevel()) {
			if (!world.isClientSide) {
				/*
				if (assembly != null) {
					assembly.disconnectHub(this);
					assembly = Assembly.getAssemblyFor(this);
				} else {
					assembly = Assembly.getAssemblyFor(this);
				}
				ChunkKeepAlive.setChunkKeptAlive((ServerLevel) world, getBlockPos(), true);
				*/
			}
		} else {
			/*
			if (assembly != null) {
				assembly.disconnectHub(this);
			}
			
			 */
		}
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
