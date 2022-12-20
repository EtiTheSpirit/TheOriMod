package etithespirit.orimod.registry.gameplay;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.capabilities.SpiritCapabilities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Optional;

public final class CapabilityRegistry {
	
	public static void registerPlayerCaps(RegisterCapabilitiesEvent evt) {
		evt.register(SpiritCapabilities.class);
	}
	
	public static void attachPlayerCaps(AttachCapabilitiesEvent<Entity> entEvent) {
		if (entEvent.getObject() instanceof Player) {
			entEvent.addCapability(SpiritCapabilities.ID, new SpiritCapabilities());
		}
	}
	
}
