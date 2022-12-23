package etithespirit.orimod.common.potion;

import etithespirit.orimod.common.chat.ExtendedChatColors;
import etithespirit.orimod.util.RichEffect;
import etithespirit.orimod.OriMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class RadiantEffect extends MobEffect {
	
	public static final ResourceLocation RADIANT_ICON = new ResourceLocation(OriMod.MODID, "textures/potion/radiant.png");
	
	public RadiantEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xB2FFE9);
	}
	
	@Override
	public Component getDisplayName() {
		return ((MutableComponent)super.getDisplayName()).withStyle(ExtendedChatColors.LIGHT);
	}
//
//	@Override
//	public MobEffectCategory getCategory() {
//		return MobEffectCategory.BENEFICIAL;
//	}
//
//	@Override
//	public int getColor() {
//		return 0xB2FFE9;
//	}
//
//	@Override
//	public ResourceLocation getCustomIcon() {
//		return RADIANT_ICON;
//	}
}
