package etithespirit.etimod.common.potion;


import etithespirit.autoeffect.IAutoEffect;
import etithespirit.autoeffect.SimpleEffect;
import etithespirit.autoeffect.data.EffectTextDisplayType;
import etithespirit.etimod.EtiMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

public class SpiritEffect extends SimpleEffect implements IAutoEffect {
	
	// public static SpiritEffect INSTANCE = new SpiritEffect(EffectType.NEUTRAL, 0xB2FFE9);
	
	public static final ResourceLocation SPIRIT_ICON = new ResourceLocation(EtiMod.MODID, "textures/mob_effect/spirit.png");
	
	public static final double SPEED_MOD = 0.0425;
		
	private static final String SPIRIT_EFFECT_SPEED_UUID = "3B1CA2C0-2E94-4DAD-803D-8CFAE551DBD4";
	private static final String SPIRIT_EFFECT_KNOCKBACK_UUID = "3FB8A8D0-3C6A-436A-BFF8-2ADA5887D3BB";
	private static final String SPIRIT_EFFECT_MAX_HEALTH_UUID = "39BA687E-176C-11EB-ADC1-0242AC120002";
	
	public SpiritEffect() {
		this.addAttributesModifier(Attributes.KNOCKBACK_RESISTANCE, SPIRIT_EFFECT_KNOCKBACK_UUID, 1.0D, AttributeModifier.Operation.ADDITION);
		this.addAttributesModifier(Attributes.MOVEMENT_SPEED, SPIRIT_EFFECT_SPEED_UUID, SPEED_MOD, AttributeModifier.Operation.ADDITION);
		this.addAttributesModifier(Attributes.MAX_HEALTH, SPIRIT_EFFECT_MAX_HEALTH_UUID, -10D, AttributeModifier.Operation.ADDITION);
	}
	
	@Override
	public boolean shouldRenderHUD(EffectInstance effectIn) { return false; }
	
	@Override
	public ResourceLocation getCustomIcon() {
		return SPIRIT_ICON;
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
	public EffectTextDisplayType getInfoDisplayType() {
		return EffectTextDisplayType.NAME_ONLY;
	}
	
	@Override
	public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
		if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth()) {
			entityLivingBaseIn.setHealth(entityLivingBaseIn.getMaxHealth());
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public EffectType getType() {
		return EffectType.NEUTRAL;
	}

	@Override
	public int getColor() {
		return 0xB2FFE9;
	}
}
