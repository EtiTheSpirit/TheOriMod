package etithespirit.etimod.common.tile;

import etithespirit.etimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.etimod.energy.ILightEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;

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
	
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other instanceof AbstractLightEnergyTileEntity) {
			AbstractLightEnergyTileEntity alt = (AbstractLightEnergyTileEntity)other;
			if (this.getBlockPos().equals(alt.getBlockPos())) {
				return true;
			}
		}
		return false;
	}

}
