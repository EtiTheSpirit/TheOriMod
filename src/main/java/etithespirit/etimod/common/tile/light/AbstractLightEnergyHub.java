package etithespirit.etimod.common.tile.light;

import etithespirit.etimod.common.tile.INetworkNBTProvider;
import etithespirit.etimod.common.tile.IWorldUpdateListener;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.energy.ILightEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * A superclass representing all Light-based energy blocks that manage energy in some way, such as adding it, removing it, or both.<br/>
 * Hubs are a core component of {@link Assembly assemblies} which manage and optimize networks of Light equipment.
 * @author Eti
 */
@SuppressWarnings("unused")
public abstract class AbstractLightEnergyHub extends TileEntity implements IWorldUpdateListener, ILightEnergyStorage, ITickableTileEntity, INetworkNBTProvider {
	
	/** The assembly associated with this instance (a collection of conduits and instances of {@link AbstractLightEnergyHub}) */
	protected Assembly assembly = null;
	
	/** A container used to store energy. */
	protected PersistentLightEnergyStorage storage;
	
	public AbstractLightEnergyHub(TileEntityType<?> tileEntityTypeIn) {
		this(tileEntityTypeIn, null);
	}
	
	public AbstractLightEnergyHub(TileEntityType<?> tileEntityTypeIn, PersistentLightEnergyStorage storage) {
		super(tileEntityTypeIn);
		this.storage = storage;
	}
	
	@Override
	public CompoundNBT getNBTForUpdatePacket(CompoundNBT existingTag) {
		if (assembly != null) existingTag.putUUID("assemblyId", assembly.assemblyId);
		return existingTag;
	}
	
	@Override
	public void tick() {
		if (assembly == null) {
			// Should always have an assembly.
			assembly = Assembly.getAssemblyFor(this);
		}
	}
	
	@Override
	public void setRemoved() {
		if (assembly != null) {
			assembly.disconnectHub(this);
		}
		super.setRemoved();
	}
	
	@Override
	public void setLevelAndPosition(World world, BlockPos pos) {
		super.setLevelAndPosition(world, pos);
		if (assembly != null) {
			assembly.disconnectHub(this);
			assembly = Assembly.getAssemblyFor(this);
		}
	}
	
	@Override
	public void setPosition(BlockPos pos) {
		super.setPosition(pos);
		
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
	public void neighborAddedOrRemoved(BlockState state, World world, BlockPos at, BlockPos changedAt, TileEntity replacedTile, boolean isMoving) {
	
	}
	
	@Override
	public void changed(IWorld world, BlockPos at) {
	
	}
	
	
	@Override
	public double getViewDistance() {
		return 32D;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
}
