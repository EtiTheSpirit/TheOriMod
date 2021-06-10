package etithespirit.etimod.item.combat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.client.audio.SpiritSoundPlayer;
import etithespirit.etimod.client.audio.SpiritSoundProvider;
import etithespirit.etimod.client.render.item.SpiritShieldModel;
import etithespirit.etimod.event.EntityEmittedSoundEventProvider;
import etithespirit.etimod.registry.ItemRegistry;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;

import net.minecraft.item.Item.Properties;

public class SpiritShield extends ShieldItem {

	public SpiritShield(Properties builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}
	
	static {
		EntityEmittedSoundEventProvider.registerHandler(event -> {
			Entity ent = event.getEntity();
			if (ent instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity)ent;
				if (player.isUsingItem()) {
					ItemStack potentialShield = player.getItemInHand(player.getUsedItemHand());
					if (potentialShield.equals(new ItemStack(ItemRegistry.LIGHT_SHIELD.get()), false)) {
						if (event.getSound().equals(SoundEvents.SHIELD_BLOCK)) {
							// Player is holding a light shield and just blocked. Override the sound!
							event.setSound(SpiritSoundProvider.getSpiritShieldImpactSound(false));
							event.setPitch(SpiritSoundPlayer.getRandomPitch());
						} else if (event.getSound().equals(SoundEvents.SHIELD_BREAK)) {
							// Player is holding a light shield and just blocked. Override the sound!
							event.setSound(SpiritSoundProvider.getSpiritShieldImpactSound(true));
							event.setPitch(SpiritSoundPlayer.getRandomPitch());
						}
					}
				}
			}
		});
	}

	
	public static class SpiritShieldRenderer extends ItemStackTileEntityRenderer {
		
		private final SpiritShieldModel spiritShieldModel = new SpiritShieldModel();
		
		private static final ResourceLocation SHIELD_MATERIAL = new ResourceLocation(EtiMod.MODID, "textures/item/light_shield.png");
		
		@Override
		public void renderByItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
			matrixStack.pushPose();
			matrixStack.scale(1.0F, -1.0F, -1.0F);
			IVertexBuilder solidEntityBuffer = buffer.getBuffer(RenderType.entityTranslucent(SHIELD_MATERIAL));
			spiritShieldModel.renderToBuffer(matrixStack, solidEntityBuffer, combinedLight, combinedOverlay, 1, 1, 1, 1);
			matrixStack.popPose();
		}
		
	}
}
