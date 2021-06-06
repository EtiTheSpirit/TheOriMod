package etithespirit.etimod.common.potion;

import etithespirit.autoeffect.IAutoEffect;
import etithespirit.autoeffect.SimpleEffect;
import etithespirit.etimod.EtiMod;
import etithespirit.etimod.data.EtiModDamageSource;
import etithespirit.etimod.registry.PotionRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class DecayEffect extends SimpleEffect implements IAutoEffect {
	
	// public static final DecayEffect INSTANCE = new DecayEffect(EffectType.HARMFUL, 0x555555);

	public static final int MAX_AMP = 9;
	
	public static final ResourceLocation DECAY_ICON = new ResourceLocation(EtiMod.MODID, "textures/mob_effect/decay.png");
	
	@Override
	public ResourceLocation getCustomIcon() {
		return DECAY_ICON;
	}
	
	@Override
	public int getMaxDisplayAmplifier() {
		return MAX_AMP + 1;
	}
	
	@Override
	public int getNameColor() {
		return 0xFACFF4;
	}
	
	@Override
	public int getTimeColor() {
		return 0x75637A;
	}
	
	@Override
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
		// New behavior: Do they have radiant?
		if (entityLivingBaseIn.isPotionActive(PotionRegistry.get(RadiantEffect.class))) {
			return; // Yeah, so don't do anything.
		}
		entityLivingBaseIn.attackEntityFrom(EtiModDamageSource.DECAY, getDamageAmount(amplifier));
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		if (duration % getDamageRate(amplifier) == 0) return true;
		return false;
	}
	
	protected int getDamageRate(int amplifier) {
		// TARGETS:
		// Highest amplifier should be 4 (Decay V) => Damage once every half second (10 ticks)
		// Lowest amplifier is 0 (Decay I) => Damage once every three seconds (60 ticks)
		
		if (amplifier > 4) amplifier = 4;
		if (amplifier < 0) amplifier = 0;
		return lerp(40, 10, (float)amplifier / 4);
	}
	
	protected float getDamageAmount(int amplifier) {
		// For all standard levels, damage should be one half heart.
		if (amplifier <= 4) {
			return 1;
		}
		
		if (amplifier > MAX_AMP) amplifier = MAX_AMP;
		return lerp(1, 8, (float)(amplifier-5) / (float)Math.floor(MAX_AMP / 2f));
	}
	
	
	protected int lerp(int start, int goal, float alpha) {
		return (int)((goal - start) * alpha) + start;
	}

	@Override
	public EffectType getType() {
		return EffectType.HARMFUL;
	}

	@Override
	public int getColor() {
		return 0x555555;
	}
}
