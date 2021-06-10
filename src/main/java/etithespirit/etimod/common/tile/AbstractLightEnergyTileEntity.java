package etithespirit.etimod.common.tile;

import etithespirit.etimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.etimod.energy.ILightEnergyStorage;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

/** A superclass representing all Light-based energy blocks' TEs.
 *  @author Eti
 */
public abstract class AbstractLightEnergyTileEntity extends TileEntity implements ILightEnergyStorage, ITickableTileEntity {
	
	protected PersistentLightEnergyStorage storage = null;

	public AbstractLightEnergyTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	
	public AbstractLightEnergyTileEntity(TileEntityType<?> tileEntityTypeIn, PersistentLightEnergyStorage storage) {
		super(tileEntityTypeIn);
		this.storage = storage;
	}

}
