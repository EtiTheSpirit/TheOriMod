package etithespirit.etimod.client.gui;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.potion.DecayEffect;
import etithespirit.etimod.common.potion.RadiantEffect;
import etithespirit.etimod.registry.PotionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class CustomHealthForEffects {
	
	public static final ResourceLocation DECAY_HEALTH_ICONS = new ResourceLocation(EtiMod.MODID, "textures/hud/decayhealthicons.png");
	
	public static final ResourceLocation RADIANT_HEALTH_ICONS = new ResourceLocation(EtiMod.MODID, "textures/hud/radianthealthicons.png");
	
	public static final ResourceLocation DECAY_RADIANT_HEALTH_ICONS = new ResourceLocation(EtiMod.MODID, "textures/hud/radiantdecayhealthicons.png");
	
	public static void onElementDrawn(RenderGameOverlayEvent evt) {
		if (evt.getType() == ElementType.HEALTH) {
			Minecraft minecraft = Minecraft.getInstance();
			boolean isDecaying = minecraft.player.isPotionActive(PotionRegistry.get(DecayEffect.class));
			boolean isRadiant = minecraft.player.isPotionActive(PotionRegistry.get(RadiantEffect.class));
			if (isDecaying && isRadiant) minecraft.getTextureManager().bindTexture(DECAY_RADIANT_HEALTH_ICONS);
			else if (isDecaying) minecraft.getTextureManager().bindTexture(DECAY_HEALTH_ICONS);
			else if (isRadiant) minecraft.getTextureManager().bindTexture(RADIANT_HEALTH_ICONS);
		}
	}
	
}
