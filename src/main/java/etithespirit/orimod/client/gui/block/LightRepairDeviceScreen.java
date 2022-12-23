package etithespirit.orimod.client.gui.block;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import etithespirit.orimod.client.render.hud.LightRepairDeviceMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The menu used in the Luxen Reconstructor block. It mimics that of the Dispenser.
 */
public class LightRepairDeviceScreen extends AbstractContainerScreen<LightRepairDeviceMenu> {
	private static final ResourceLocation CONTAINER_LOCATION = new ResourceLocation("textures/gui/container/dispenser.png");
	
	public LightRepairDeviceScreen(LightRepairDeviceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	protected void init() {
		super.init();
		titleLabelX = (imageWidth - font.width(title)) / 2;
	}
	
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		renderBackground(pPoseStack);
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
		renderTooltip(pPoseStack, pMouseX, pMouseY);
	}
	
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, CONTAINER_LOCATION);
		int cenX = (width - imageWidth) / 2;
		int cenY = (height - imageHeight) / 2;
		this.blit(pPoseStack, cenX, cenY, 0, 0, imageWidth, imageHeight);
	}
}