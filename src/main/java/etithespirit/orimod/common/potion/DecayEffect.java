package etithespirit.orimod.common.potion;

import etithespirit.orimod.combat.damage.OriModDamageSources;
import etithespirit.orimod.common.chat.ExtendedChatColors;
import etithespirit.orimod.registry.gameplay.EffectRegistry;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.util.extension.MobEffectDataStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/**
 * The status effect that occurs when someone is decaying.
 */
public final class DecayEffect extends MobEffect {
	
	/** The icon of the Decay effect. */
	public static final ResourceLocation DECAY_ICON = new ResourceLocation(OriMod.MODID, "textures/mob_effect/decay.png");
	
	/** The max effective level of Decay. */
	public static final int MAX_AMP = 9;
	
	/** The damage done per decay tick at level 0. */
	public static final int MIN_DAMAGE = 1;
	
	/** The damage done per decay tick at level MAX_AMP-1. */
	public static final int MAX_DAMAGE = 8;
	
	/** The longest delay in decay ticks measured in real ticks. This occurs at level 0. */
	public static final int LONGEST_DELAY_TICKS = 40;
	
	/** The shortest delay in decay ticks measured in real ticks. This occurs at level MAX_AMP-1. */
	public static final int SHORTEST_DELAY_TICKS = 10;
	
	public DecayEffect() {
		super(MobEffectCategory.HARMFUL, 0x555555);
	}
	
	@Override
	public Component getDisplayName() {
		return ((MutableComponent)super.getDisplayName()).withStyle(ExtendedChatColors.DECAY);
	}
	
//	@Override
//	public MobEffectCategory getCategory() {
//		return MobEffectCategory.HARMFUL;
//	}
//
//	@Override
//	public int getColor() {
//		return 0x555555;
//	}
//
//	@Override
//	public ResourceLocation getCustomIcon() {
//		return DECAY_ICON;
//	}
//
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		// New behavior: Do they have radiant?
		if (entity.hasEffect(EffectRegistry.RADIANT.get())) {
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
			entity.hurt(OriModDamageSources.DECAY, getDamageAmount(amplifier));
		}
	}
	
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		//return duration % getDamageRate(amplifier) == 0;
		return true;
	}
	
	private int getDamageRate(int amplifier) {
		// TARGETS:
		// Highest effective amplifier should be 4 (Decay V) => Damage once every half second (10 ticks)
		// Lowest effective amplifier is 0 (Decay I) => Damage once every two seconds (40 ticks)
		
		if (amplifier > 4) amplifier = 4;
		if (amplifier < 0) amplifier = 0;
		return (int) Mth.lerp((float)amplifier / Mth.floor(MAX_AMP / 2f), LONGEST_DELAY_TICKS, SHORTEST_DELAY_TICKS);
	}
	
	private float getDamageAmount(int amplifier) {
		// For all standard levels, damage should be one half heart.
		if (amplifier <= 4) {
			return 1;
		}
		
		if (amplifier > MAX_AMP) amplifier = MAX_AMP;
		return (int)Mth.lerp((float)(amplifier-5) / (float)Math.floor(MAX_AMP / 2f), MIN_DAMAGE, MAX_DAMAGE);
	}
}
