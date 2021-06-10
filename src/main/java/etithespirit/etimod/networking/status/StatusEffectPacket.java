package etithespirit.etimod.networking.status;

import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class StatusEffectPacket {
	
	/**
	 * The name of the potion.
	 */
	public ResourceLocation statusEffect;
	
	/**
	 * If true, the effect is added. If false, it is removed.
	 */
	public boolean shouldBeAdding;
	
	/**
	 * How long this potion lasts in ticks.
	 */
	public int duration;
	
	/**
	 * The amplifier of this potion.
	 */
	public int amplifier;
	
	/**
	 * Whether or not to show particles.
	 */
	public boolean particles;
	
	/**
	 * Whether or not this was applied from an ambient source.
	 */
	public boolean ambient;
	
	/**
	 * Whether or not to display the icon in the HUD.
	 */
	public boolean showIcon;
	
	public StatusEffectPacket() {
		statusEffect = null;
		shouldBeAdding = false;
		duration = 0;
		amplifier = 0;
		particles = false;
		ambient = false;
		showIcon = false;
	}
	
	public StatusEffectPacket(EffectInstance fromEffect, boolean add) {
		statusEffect = fromEffect.getEffect().getRegistryName();
		shouldBeAdding = add;
		duration = fromEffect.getDuration();
		amplifier = fromEffect.getAmplifier();
		particles = fromEffect.isVisible();
		ambient = fromEffect.isAmbient();
		showIcon = fromEffect.showIcon();
	}
	
	public void PopulateBuffer(PacketBuffer buffer) {
		String fx = statusEffect.toString();
		buffer.writeInt(fx.length());
		buffer.writeUtf(fx);
		if (!shouldBeAdding) {
			buffer.writeBoolean(false);
			return;
		}
		buffer.writeBoolean(true);
		buffer.writeInt(duration);
		buffer.writeInt(amplifier);
		buffer.writeBoolean(particles);
		buffer.writeBoolean(ambient);
		buffer.writeBoolean(showIcon);
	}
	
	/**
	 * Attempts to return an Effect from the given StatusEffect resource, or null if it could not be found.
	 * @return
	 */
	public Effect GetPotion() {
		if (statusEffect == null) return null;
		if (ForgeRegistries.POTIONS.containsKey(statusEffect)) return ForgeRegistries.POTIONS.getValue(statusEffect);
		return null;
	}
	
	public EffectInstance CreateNewEffectInstance() {
		Effect potion = GetPotion();
		// Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean particles, boolean icon
		return new EffectInstance(potion, duration, amplifier, ambient, particles, showIcon);
	}
	
	public static StatusEffectPacket BufferToPacket(PacketBuffer buffer) {
		StatusEffectPacket packet = new StatusEffectPacket();
		int len = buffer.readInt();
		packet.statusEffect = new ResourceLocation(buffer.readUtf(len));
		packet.shouldBeAdding = buffer.readBoolean();
		if (packet.shouldBeAdding) {
			packet.duration = buffer.readInt();
			packet.amplifier = buffer.readInt();
			packet.particles = buffer.readBoolean();
			packet.ambient = buffer.readBoolean();
			packet.showIcon = buffer.readBoolean();
		}
		return packet;
	}
	
	public static void PacketToBuffer(StatusEffectPacket packet, PacketBuffer buffer) {
		packet.PopulateBuffer(buffer);
	}
	
}
