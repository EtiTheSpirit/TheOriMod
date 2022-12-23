package etithespirit.orimod.client.gui.health;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.client.gui.health.heart.ExtendedHeartType;
import etithespirit.orimod.client.gui.health.heart.IHeartRenderType;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

/**
 * A reimplementation of the health GUI that draws health for Spirit players. Mimics vanilla behavior.
 */
public class SpiritHealthGui implements IGuiOverlay {
	
	public static final ResourceLocation GUI_ID = OriMod.rsrc("spirit_health");
	private static final int DESIRED_HEART_ROW_MAX_HEIGHT = 10; // The maximum amount of stacked rows of hearts before having more than one healthbar starts to shrink
	
	private int lastKnownHealthHeartCount;
	private long lastKnownHealthTime;
	private long remainingHealthBlinkTime;
	private int ticks;
	private int regenHeartWaveIndex;
	private int regenHeartWaveDelayer;
	private int halfContainersToDraw;
	private int halfHeartsToDraw;
	private int absorbHalfHeartsToDraw;
	
	public static void setupHealthElement(RegisterGuiOverlaysEvent evt) {
		SpiritHealthGui instance = new SpiritHealthGui();
		evt.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), GUI_ID.getPath(), instance);
	}
	
	public static void cancelHealthRender(RenderGuiOverlayEvent.Pre preRenderEvent) {
		if (preRenderEvent.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())) {
			preRenderEvent.setCanceled(true);
		}
	}
	
	private void updateTiming(int currentHealth, int invulnerableTime) {
		if (currentHealth < this.lastKnownHealthHeartCount && invulnerableTime > 0) {
			this.lastKnownHealthTime = Util.getMillis();
			this.remainingHealthBlinkTime = (this.ticks + 20);
		} else if (currentHealth > this.lastKnownHealthHeartCount && invulnerableTime > 0) {
			this.lastKnownHealthTime = Util.getMillis();
			this.remainingHealthBlinkTime = (this.ticks + 10);
		}
		
		this.lastKnownHealthHeartCount = currentHealth;
		if (Util.getMillis() - this.lastKnownHealthTime > 1000L) {
			this.lastKnownHealthTime = Util.getMillis();
		}
	}
	
	private float getEntityMaxHealth(LivingEntity ent) {
		AttributeInstance attrMaxHealth = ent.getAttribute(Attributes.MAX_HEALTH);
		if (attrMaxHealth == null) throw new IllegalStateException("Entity does not have max health?");
		return (float)attrMaxHealth.getValue();
	}
	
	
	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		this.ticks = gui.getGuiTicks();
		Minecraft minecraft = gui.getMinecraft();
		
		if (minecraft.options.hideGui || !gui.shouldDrawSurvivalElements()) return;
		gui.setupOverlayRenderState(true, false);
		
		minecraft.getProfiler().push("orimod:health");
		Entity entity = minecraft.getCameraEntity();
		if (entity instanceof LivingEntity livingEntity) {
			
			float maxHealth = getEntityMaxHealth(livingEntity);
			int currentHealthHalfHearts = Mth.ceil(livingEntity.getHealth());
			int currentAbsorbHalfHearts = Mth.ceil(livingEntity.getAbsorptionAmount());
			int totalHearts = currentHealthHalfHearts + currentAbsorbHalfHearts;
			boolean doFlashEffect = remainingHealthBlinkTime > ticks && (remainingHealthBlinkTime - ticks) / 3L % 2L == 1L;
			updateTiming(currentHealthHalfHearts, livingEntity.invulnerableTime);
			
			
			int healthRows = Mth.ceil((maxHealth + currentAbsorbHalfHearts) / 20);
			int rowHeightPx = Math.max(DESIRED_HEART_ROW_MAX_HEIGHT - (healthRows - 2), 3);
			int screenLeft = screenWidth / 2 - 91;
			int screenTop = screenHeight - gui.leftHeight;
			gui.leftHeight += (healthRows * rowHeightPx);
			if (rowHeightPx != 10) gui.leftHeight += 10 - rowHeightPx;
			
			// Which heart gets to be bumped up from the wave of regeneration?
			MobEffectInstance regen = livingEntity.getEffect(MobEffects.REGENERATION);
			if (regen != null) {
				if (this.ticks >= regenHeartWaveDelayer) {
					regenHeartWaveIndex++;
					regenHeartWaveDelayer = this.ticks + 1;
					if (regenHeartWaveIndex >= totalHearts) {
						int delay = Mth.clamp(4 - regen.getAmplifier(), 0, 4) * 10;
						regenHeartWaveDelayer = this.ticks + delay;
						regenHeartWaveIndex = -1;
					}
					
				}
			} else {
				regenHeartWaveIndex = -1;
				regenHeartWaveDelayer = 0;
			}
			
			halfContainersToDraw = Mth.ceil(maxHealth);
			halfHeartsToDraw = currentHealthHalfHearts;
			absorbHalfHeartsToDraw = currentAbsorbHalfHearts;
			for (int rowIndex = 0; rowIndex < healthRows; rowIndex++) {
				int heightOffset = rowHeightPx * rowIndex;
				renderRowOfHearts(gui, poseStack, livingEntity, screenLeft, screenTop + heightOffset, rowIndex, doFlashEffect); // min because this should never be larger than 10
			}
		}
		
		minecraft.getProfiler().pop();
	}
	
	private void renderRowOfHearts(ForgeGui gui, PoseStack poseStack, LivingEntity forEntity, int x, int y, int currentRowIndex, boolean flash) {
		int halfHeartsToDrawNow = Math.min(halfHeartsToDraw, 20);
		int halfContainersToDrawNow = Math.min(halfContainersToDraw, 20);
		int absorbHeartsToDrawNow = Math.min(absorbHalfHeartsToDraw, 20) - halfHeartsToDrawNow;
		
		int currentX = x;
		ExtendedHeartType container = ExtendedHeartType.getContainerForEntity(forEntity);
		ExtendedHeartType heart = ExtendedHeartType.getBaseHeartForEntity(forEntity);
		boolean isRadiant = container == ExtendedHeartType.RADIANT_CONTAINER;
		
		int currentHeartIndex = currentRowIndex * 10;
		while (halfContainersToDrawNow > 0) {
			int currentY = y;
			if (currentHeartIndex == regenHeartWaveIndex) {
				currentY -= 2;
			}
			IHeartRenderType.drawSingle(poseStack, container, forEntity, currentX, currentY, halfContainersToDrawNow == 1, flash);
			currentX += 8;
			halfContainersToDrawNow -= 2;
			halfContainersToDraw -= 2;
			currentHeartIndex += 1;
		}
		
		// Reset heart index here
		currentX = x;
		currentHeartIndex = currentRowIndex * 10;
		while (halfHeartsToDrawNow > 0) {
			int currentY = y;
			if (currentHeartIndex == regenHeartWaveIndex) {
				currentY -= 2;
			}
			IHeartRenderType.drawSingle(poseStack, heart, forEntity, currentX, currentY, halfHeartsToDrawNow == 1, flash);
			if (isRadiant) {
				IHeartRenderType.drawSingle(poseStack, ExtendedHeartType.RADIANT_OVERLAY, forEntity, currentX, currentY, halfHeartsToDrawNow == 1, flash);
			}
			currentX += 8;
			halfHeartsToDrawNow -= 2;
			halfHeartsToDraw -= 2;
			currentHeartIndex += 1;
		}
		
		// Do NOT reset heart index here
		currentX = x;
		while (absorbHeartsToDrawNow > 0) {
			int currentY = y;
			if (currentHeartIndex == regenHeartWaveIndex) {
				currentY -= 2;
			}
			IHeartRenderType.drawSingle(poseStack, ExtendedHeartType.VANILLA_ABSORBING, forEntity, currentX, currentY, absorbHeartsToDrawNow == 1, flash);
			currentX += 8;
			absorbHeartsToDrawNow -= 2;
			absorbHalfHeartsToDraw -= 2;
			currentHeartIndex += 1;
		}
	}
}
