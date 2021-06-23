package etithespirit.etimod.info.spirit.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public final class SpiritCapabilityStorage implements Capability.IStorage<ISpiritCapabilities> {
	
	@Override
	public INBT writeNBT(Capability<ISpiritCapabilities> capability, ISpiritCapabilities instance, Direction side) {
		return instance.serializeNBT();
	}
	
	@Override
	public void readNBT(Capability<ISpiritCapabilities> capability, ISpiritCapabilities instance, Direction side, INBT nbt) {
		if (nbt instanceof CompoundNBT) {
			instance.deserializeNBT((CompoundNBT)nbt);
		}
	}
}
