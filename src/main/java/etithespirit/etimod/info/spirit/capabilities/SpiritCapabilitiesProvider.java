package etithespirit.etimod.info.spirit.capabilities;

import etithespirit.etimod.registry.CapabilityRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpiritCapabilitiesProvider implements ICapabilityProvider {
	
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityRegistry.SPIRIT_CAPABILITIES) {
			return LazyOptional.of(cap::getDefaultInstance);
		}
		return LazyOptional.empty();
	}
	
}
