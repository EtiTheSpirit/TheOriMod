package etithespirit.etimod.common.tile;

import etithespirit.etimod.energy.ILightEnergyStorage;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class AbstractLightEnergyTileEntity extends TileEntity implements ILightEnergyStorage, ITickableTileEntity {

	public AbstractLightEnergyTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

}
