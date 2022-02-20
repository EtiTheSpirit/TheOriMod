package etithespirit.orimod.common.potion;

import etithespirit.orimod.util.RichEffect;
import etithespirit.orimod.OriMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;

public class RadiantEffect extends RichEffect {
	
	public static final ResourceLocation RADIANT_ICON = new ResourceLocation(OriMod.MODID, "textures/potion/radiant.png");
	
	@Override
	public MobEffectCategory getCategory() {
		return MobEffectCategory.BENEFICIAL;
	}
	
	@Override
	public int getTextMainColor() {
		return 0xD9FAF7;
	}
	
	@Override
	public int getTextShadowColor() {
		return super.getTextShadowColor();
	}
	
	@Override
	public int getColor() {
		return 0xB2FFE9;
	}
	
	@Override
	public ResourceLocation getCustomIcon() {
		return RADIANT_ICON;
	}
}
