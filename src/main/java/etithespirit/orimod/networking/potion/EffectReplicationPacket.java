package etithespirit.orimod.networking.potion;

import net.minecraft.resources.ResourceLocation;

public final class EffectReplicationPacket {
	
	/** The actual effect being modified. */
	public final ResourceLocation effect;
	
	/** The duration that was added to the effect. */
	public final int addedDuration;
	
	public EffectReplicationPacket(ResourceLocation effect, int addedDuration) {
		this.effect = effect;
		this.addedDuration = addedDuration;
	}
	
}
