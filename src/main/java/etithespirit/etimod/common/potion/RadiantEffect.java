package etithespirit.etimod.common.potion;

import etithespirit.autoeffect.IAutoEffect;
import etithespirit.autoeffect.SimpleEffect;
import etithespirit.etimod.EtiMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class RadiantEffect extends SimpleEffect implements IAutoEffect {
	
	//public static final RadiantEffect INSTANCE = new RadiantEffect(EffectType.BENEFICIAL, 0xB2FFE9);
	
	public static final ResourceLocation RADIANT_ICON = new ResourceLocation(EtiMod.MODID, "textures/potion/radiant.png");
	
	public static final int MAX_AMP = 4;
	
	@Override
	public ResourceLocation getCustomIcon() {
		return RADIANT_ICON;
	}
	
	@Override
	public int getMaxDisplayAmplifier() {
		return MAX_AMP + 1;
	}
	
	@Override
	public int getNameColor() {
		return 0xD9FAF7;
	}
	
	@Override
	public int getTimeColor() {
		return 0x6D7575;
	}
	
	@Override
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
		if (amplifier > 0) {
			entityLivingBaseIn.heal(amplifier);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		if (amplifier > MAX_AMP) amplifier = MAX_AMP;
		int ticksRequired = lerp(40, 10, (float)amplifier / MAX_AMP);
		return duration % ticksRequired == 0;
	}
	
	protected int lerp(int start, int goal, float alpha) {
		return (int)((goal - start) * alpha) + start;
	}

	@Override
	public EffectType getType() {
		return EffectType.BENEFICIAL;
	}

	@Override
	public int getColor() {
		return 0xB2FFE9;
	}
	
}
