package etithespirit.orimod.common.potion;

import etithespirit.orimod.combat.ExtendedDamageSource;
import etithespirit.orimod.registry.PotionRegistry;
import etithespirit.orimod.util.RichEffect;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.util.extension.MobEffectDataStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class DecayEffect extends RichEffect {
	
	public static final ResourceLocation DECAY_ICON = new ResourceLocation(OriMod.MODID, "textures/mob_effect/decay.png");
	public static final int MAX_AMP = 9;
	public static final int MIN_DAMAGE = 1;
	public static final int MAX_DAMAGE = 8;
	public static final int LONGEST_DELAY_TICKS = 40;
	public static final int SHORTEST_DELAY_TICKS = 10;
	
	@Override
	public MobEffectCategory getCategory() {
		return MobEffectCategory.HARMFUL;
	}
	
	@Override
	public int getTextMainColor() {
		return 0xFACFF4;
	}
	
	@Override
	public int getTextShadowColor() {
		return super.getTextShadowColor();
	}
	
	@Override
	public int getColor() {
		return 0x555555;
	}
	
	@Override
	public ResourceLocation getCustomIcon() {
		return DECAY_ICON;
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		// New behavior: Do they have radiant?
		if (entity.hasEffect(PotionRegistry.get(RadiantEffect.class))) {
			return; // Yeah, so don't do anything.
		}
		
		MobEffectInstance effect = entity.getEffect(this);
		int duration = effect.getDuration();
		int damageRate = getDamageRate(amplifier);
		int maxDuration = MobEffectDataStorage.accessData(effect).getInt("maxDuration");
		
		if (duration == maxDuration || (duration % damageRate) == 0) {
			// NOTE: "Why this instead of isDurationEffectTick?"
			// This is because of how changing levels of Decay apply. My goal is less of a "is the remaining duration a factor" and more of a
			// "has X time passed since the last damage" check, which is not possible under the default system.
			// This system allows the effect's level to change on the fly or be reset without doing more (or less) damage than it should.
			entity.hurt(ExtendedDamageSource.DECAY, getDamageAmount(amplifier));
		}
	}
	
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		//return duration % getDamageRate(amplifier) == 0;
		return true;
	}
	
	protected int getDamageRate(int amplifier) {
		// TARGETS:
		// Highest effective amplifier should be 4 (Decay V) => Damage once every half second (10 ticks)
		// Lowest effective amplifier is 0 (Decay I) => Damage once every two seconds (40 ticks)
		
		if (amplifier > 4) amplifier = 4;
		if (amplifier < 0) amplifier = 0;
		return (int) Mth.lerp(LONGEST_DELAY_TICKS, SHORTEST_DELAY_TICKS, (float)amplifier / Mth.floor(MAX_AMP / 2f));
	}
	
	protected float getDamageAmount(int amplifier) {
		// For all standard levels, damage should be one half heart.
		if (amplifier <= 4) {
			return 1;
		}
		
		if (amplifier > MAX_AMP) amplifier = MAX_AMP;
		return (int)Mth.lerp(MIN_DAMAGE, MAX_DAMAGE, (float)(amplifier-5) / (float)Math.floor(MAX_AMP / 2f));
	}
}
