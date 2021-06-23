package etithespirit.etimod.registry;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.info.spirit.capabilities.ISpiritCapabilities;
import etithespirit.etimod.info.spirit.capabilities.SpiritCapabilities;
import etithespirit.etimod.info.spirit.capabilities.SpiritCapabilitiesProvider;
import etithespirit.etimod.info.spirit.capabilities.SpiritCapabilityStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class CapabilityRegistry {
	
	@CapabilityInject(ISpiritCapabilities.class)
	public static Capability<ISpiritCapabilities> SPIRIT_CAPABILITIES;
	
	public static void registerAll() {
		CapabilityManager.INSTANCE.register(ISpiritCapabilities.class, new SpiritCapabilityStorage(), SpiritCapabilities::new);
	}
	
	public static void attachCapabilities(AttachCapabilitiesEvent<Entity> evt) {
		if (evt.getObject() instanceof PlayerEntity) {
			evt.addCapability(new ResourceLocation(EtiMod.MODID, "spiritdata"), new SpiritCapabilitiesProvider());
		}
	}
	
	public static void persistCapabilities(PlayerEvent.Clone cloneEvent) {
		if (!cloneEvent.isWasDeath()) return;
		PlayerEntity oldPlayer = cloneEvent.getOriginal();
		PlayerEntity newPlayer = cloneEvent.getPlayer();
		oldPlayer.getCapability(SPIRIT_CAPABILITIES).ifPresent(originalData -> newPlayer.getCapability(SPIRIT_CAPABILITIES).ifPresent(originalData::copyTo));
	}
	
}
