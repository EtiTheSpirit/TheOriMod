package etithespirit.etimod.info.spirit.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public final class SpiritCapabilityStorage implements Capability.IStorage<ISpiritCapabilities> {
	
	@Override
	public INBT writeNBT(Capability<ISpiritCapabilities> capability, ISpiritCapabilities instance, Direction side) {
		CompoundNBT tag = new CompoundNBT();
		tag.putBoolean("isSpirit", instance.getIsSpirit());
		instance.getSpiritAbilities().writeToNBT(tag);
		return tag;
	}
	
	@Override
	public void readNBT(Capability<ISpiritCapabilities> capability, ISpiritCapabilities instance, Direction side, INBT nbt) {
		if (nbt instanceof CompoundNBT) {
			CompoundNBT tag = (CompoundNBT)nbt;
			instance.setIsSpirit(tag.getBoolean("isSpirit"));
			instance.getSpiritAbilities().readFromNBT(tag);
		}
	}
}
