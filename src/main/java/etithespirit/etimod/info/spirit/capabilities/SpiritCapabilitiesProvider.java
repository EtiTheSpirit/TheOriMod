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

public final class SpiritCapabilitiesProvider implements ICapabilitySerializable<CompoundNBT> {
	
	private final LazyOptional<ISpiritCapabilities> spiritCapabilities = LazyOptional.of(SpiritCapabilities::new);
	
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityRegistry.SPIRIT_CAPABILITIES) {
			return spiritCapabilities.cast();
		}
		return LazyOptional.empty();
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		if (spiritCapabilities.isPresent()) {
			ISpiritCapabilities caps = spiritCapabilities.resolve().get();
			return caps.serializeNBT();
		}
		return new CompoundNBT();
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (spiritCapabilities.isPresent()) {
			ISpiritCapabilities caps = spiritCapabilities.resolve().get();
			caps.deserializeNBT(nbt);
		}
	}
}
