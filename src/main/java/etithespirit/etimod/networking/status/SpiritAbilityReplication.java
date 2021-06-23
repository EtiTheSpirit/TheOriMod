package etithespirit.etimod.networking.status;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.networking.ReplicationData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Unused for now, replicates ability changes.
 *
 * @author Eti
 */
@SuppressWarnings("unused")
public class SpiritAbilityReplication {
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(EtiMod.MODID, "replicate_ability"),
		() -> ReplicationData.PROTOCOL_VERSION,
		ReplicationData.PROTOCOL_VERSION::equals,
		ReplicationData.PROTOCOL_VERSION::equals
	);

}
