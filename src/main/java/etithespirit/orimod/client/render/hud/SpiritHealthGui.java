package etithespirit.orimod.client.render.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import etithespirit.orimod.OriMod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * A reimplementation of the health GUI that draws health for Spirit players. Mimics vanilla behavior.
 */
public class SpiritHealthGui extends ForgeGui {
	
	private static final ResourceLocation DECAY_HEALTH_ICONS = new ResourceLocation(OriMod.MODID, "textures/hud/spiriticons.png");
	
	public SpiritHealthGui(Minecraft mc) {
		super(mc);
	}
	
	private static SpiritHealthGui spiritHealthGui = null;
	
	@SubscribeEvent
	public static void setupHealthElement(RegisterGuiOverlaysEvent evt) {
		spiritHealthGui = new SpiritHealthGui(Minecraft.getInstance());
		evt.registerAboveAll("Player Spirit Health", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
			Minecraft minecraft = Minecraft.getInstance();
			if (!minecraft.options.hideGui && gui.shouldDrawSurvivalElements()) {
				gui.setupOverlayRenderState(true, false);
				spiritHealthGui.renderHealth(gui, screenWidth, screenHeight, mStack, DECAY_HEALTH_ICONS);
			}
		});
	}
	
	void renderHealth(Gui gui, int width, int height, PoseStack pStack, ResourceLocation texture)
	{
		RenderSystem.setShaderTexture(0, texture);
		minecraft.getProfiler().push("health");
		RenderSystem.enableBlend();
		
		Player player = (Player)this.minecraft.getCameraEntity();
		int health = Mth.ceil(player.getHealth());
		boolean highlight = healthBlinkTime > (long)tickCount && (healthBlinkTime - (long)tickCount) / 3L %2L == 1L;
		
		if (health < this.lastHealth && player.invulnerableTime > 0)
		{
			this.lastHealthTime = Util.getMillis();
			this.healthBlinkTime = (long)(this.tickCount + 20);
		}
		else if (health > this.lastHealth && player.invulnerableTime > 0)
		{
			this.lastHealthTime = Util.getMillis();
			this.healthBlinkTime = (long)(this.tickCount + 10);
		}
		
		if (Util.getMillis() - this.lastHealthTime > 1000L)
		{
			this.lastHealth = health;
			this.displayHealth = health;
			this.lastHealthTime = Util.getMillis();
		}
		
		this.lastHealth = health;
		int healthLast = this.displayHealth;
		
		AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
		float healthMax = Math.max((float)attrMaxHealth.getValue(), Math.max(healthLast, health));
		int absorb = Mth.ceil(player.getAbsorptionAmount());
		
		int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
		int rowHeight = Math.max(10 - (healthRows - 2), 3);
		
		this.random.setSeed((long)(tickCount * 312871));
		
		int left = width / 2 - 91;
		int top = height - leftHeight;
		leftHeight += (healthRows * rowHeight);
		if (rowHeight != 10) leftHeight += 10 - rowHeight;
		
		int regen = -1;
		if (player.hasEffect(MobEffects.REGENERATION))
		{
			regen = this.tickCount % Mth.ceil(healthMax + 5.0F);
		}
		
		this.renderHearts(pStack, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);
		
		RenderSystem.disableBlend();
		minecraft.getProfiler().pop();
	}
	
}
