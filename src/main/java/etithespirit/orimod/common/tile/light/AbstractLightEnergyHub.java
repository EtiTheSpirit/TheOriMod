package etithespirit.orimod.common.tile.light;


import etithespirit.orimod.common.tile.INetworkNBTProvider;
import etithespirit.orimod.common.tile.IWorldUpdateListener;
import etithespirit.orimod.energy.ILightEnergyStorage;
import etithespirit.orimod.lighttech.Assembly;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/**
 * A superclass representing all Light-based energy blocks that manage energy in some way, such as adding it, removing it, or both.<br/>
 * Hubs are a core component of {@link Assembly assemblies} which manage and optimize networks of Light equipment.
 * @author Eti
 */
@SuppressWarnings("unused")
public abstract class AbstractLightEnergyHub extends BlockEntity implements EntityBlock, IWorldUpdateListener, ILightEnergyStorage, INetworkNBTProvider {
	
	private static final BlockEntityTicker<AbstractLightEnergyHub> ABSTRACT_HUB_TICKER = (world, at, state, hub) -> {
		if (hub.assembly == null) {
			// Should always have an assembly.
			hub.assembly = Assembly.getAssemblyFor(hub);
		}
	};
	
	/** The assembly associated with this instance (a collection of conduits and instances of {@link AbstractLightEnergyHub}) */
	protected Assembly assembly = null;
	
	/** A container used to store energy. */
	protected PersistentLightEnergyStorage storage;
	
	/**
	 * Create a new hub using no storage.
	 * @param tileEntityTypeIn The type of BlockEntity to create.
	 * @param at The location to create it at.
	 * @param state The BlockState to create it for.
	 */
	protected AbstractLightEnergyHub(BlockEntityType<?> tileEntityTypeIn, BlockPos at, BlockState state) {
		this(tileEntityTypeIn, at, state, null);
	}
	
	/**
	 * Create a new hub with the given storage.
	 * @param tileEntityTypeIn The type of BlockEntity to create.
	 * @param at The location to create it at.
	 * @param state The BlockState to create it for.
	 * @param storage The energy storage data to use.
	 */
	public AbstractLightEnergyHub(BlockEntityType<?> tileEntityTypeIn, BlockPos at, BlockState state, PersistentLightEnergyStorage storage) {
		super(tileEntityTypeIn, at, state);
		this.storage = storage;
	}
	
	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (BlockEntityTicker<T>) ABSTRACT_HUB_TICKER;
	}
	
	@Override
	public CompoundTag getNBTForUpdatePacket(CompoundTag existingTag) {
		if (assembly != null) existingTag.putUUID("assemblyId", assembly.assemblyId);
		return existingTag;
	}
	
	@Override
	public void setRemoved() {
		if (assembly != null) {
			assembly.disconnectHub(this);
		}
		super.setRemoved();
	}
	
	@Override
	public void setLevel(Level world) {
		super.setLevel(world);
		if (assembly != null) {
			assembly.disconnectHub(this);
			assembly = Assembly.getAssemblyFor(this);
		}
	}
	
	
	@Override
	public void neighborAddedOrRemoved(BlockState state, Level world, BlockPos at, BlockPos changedAt, BlockEntity replacedTile, boolean isMoving) {
	
	}
	
	@Override
	public void changed(LevelAccessor world, BlockPos at) {
		// See if we're still part of the same assembly
		if (assembly != null) {
			assembly.disconnectHub(this);
			assembly = Assembly.getAssemblyFor(this);
			// By calling this after disconnection, it will skip the cache, but then check the links this is
			// connected to instead. This can be used to either:
			// A) Create a new assembly if needed (slow!), or
			// B) Reconnect this to the assembly it was just disconnected from (fast!)
		}
	}
	
	@Override
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
}
