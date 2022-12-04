package etithespirit.orimod.networking.potion;

import net.minecraft.resources.ResourceLocation;

/**
 * A class that represents the data necessary to replicate a potion effect's altered duration across the network.
 */
@SuppressWarnings ("ClassCanBeRecord")
public final class EffectReplicationPacket {
	
	/** The actual effect being modified. */
	public final ResourceLocation effect;
	
	/** The duration that was added to the effect. */
	public final int addedDuration;
	
	/**
	 * Constructs a new status effect replication packet.
	 * @param effect The effect that was modified.
	 * @param addedDuration The change in duration of this effect.
	 */
	public EffectReplicationPacket(ResourceLocation effect, int addedDuration) {
		this.effect = effect;
		this.addedDuration = addedDuration;
	}
	
}
