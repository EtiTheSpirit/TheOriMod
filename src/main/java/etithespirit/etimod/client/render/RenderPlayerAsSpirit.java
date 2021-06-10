package etithespirit.etimod.client.render;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import etithespirit.etimod.client.render.mob.ModelSpirit;
import etithespirit.etimod.client.render.mob.ModelSpiritArmor;
import etithespirit.etimod.client.render.mob.RenderSpiritMob;
import etithespirit.etimod.registry.EntityRegistry;
import etithespirit.etimod.util.spirit.SpiritIdentificationType;
import etithespirit.etimod.util.spirit.SpiritIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class RenderPlayerAsSpirit {
	
	private static RenderSpiritMob RENDERER = null;
	private static ModelSpirit SPIRIT_MODEL = null;
	private static ModelSpiritArmor SPIRIT_ARMOR_MODEL = null;
	
	private static final EquipmentSlotType[] SLOTS = new EquipmentSlotType[] {
		EquipmentSlotType.HEAD,
		EquipmentSlotType.CHEST,
		EquipmentSlotType.LEGS,
		EquipmentSlotType.FEET
	};
	
	// Manually subscribed.
	@SuppressWarnings("resource")
	public static void whenRenderingPlayer(RenderPlayerEvent.Pre preRenderEvent) {
		
		if (RENDERER == null) {
			// Late population method.
			RENDERER = (RenderSpiritMob) Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(EntityRegistry.SPIRIT.get());
			SPIRIT_ARMOR_MODEL = new ModelSpiritArmor();
			SPIRIT_MODEL = RENDERER.getModel();
		}
		
		PlayerEntity player = preRenderEvent.getPlayer();
		if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
			IRenderTypeBuffer buf = preRenderEvent.getBuffers();
			IVertexBuilder solidEntityBuffer = buf.getBuffer(RenderType.entitySolid(RenderSpiritMob.SPIRIT_TEXTURE));
			MatrixStack mtx = preRenderEvent.getMatrixStack();
			int light = preRenderEvent.getLight();
			float partialTicks = preRenderEvent.getPartialRenderTick();
						
			// Control crouch events.
			Vector3d crouchTranslation = preRenderEvent.getRenderer().getRenderOffset((AbstractClientPlayerEntity)preRenderEvent.getPlayer(), partialTicks);
			mtx.translate(-crouchTranslation.x, -crouchTranslation.y, -crouchTranslation.z); // Undo the translation applied by crouching.
			mtx.translate(0, player.getForcedPose() == Pose.CROUCHING ? -0.125 : 0, 0);
			// ^ This is done here because the shadow moves inversely if it's done after pushing the matrix.
			
			preRenderEvent.getRenderer().shadowRadius = 0.5f;
			
			float r = 1;
			float g = 1;
			float b = 1;
			if (player.hurtTime > 0 && player.hurtTime % 3 == 0) {
				r = 1.0f;
				g = 0.5f;
				b = 0.5f;
			}
			
			
			mtx.pushPose(); // [
				mtx.scale(ModelSpirit.WIDTH_MOD, ModelSpirit.HEIGHT_MOD, ModelSpirit.WIDTH_MOD);
				setRotationAnglesFrom(partialTicks, mtx, player, preRenderEvent.getRenderer().getModel());
				
				SPIRIT_MODEL.renderToBuffer(mtx, solidEntityBuffer, light, 0xFFFFFF, r, g, b, 1f);
				
				//solidEntityBuffer.color(1, 1, 1, 1);
				r = 1;
				g = 1;
				b = 1;
				
				boolean hasHelmet = player.hasItemInSlot(EquipmentSlotType.HEAD);
				boolean hasChestplate = player.hasItemInSlot(EquipmentSlotType.CHEST);
				boolean hasLeggings = player.hasItemInSlot(EquipmentSlotType.LEGS);
				boolean hasBoots = player.hasItemInSlot(EquipmentSlotType.FEET);
				
				SPIRIT_ARMOR_MODEL.updateVisibility(hasHelmet, hasChestplate, hasLeggings, hasBoots);
				
				for (int i = 0; i < SLOTS.length; i++) {
					EquipmentSlotType slot = SLOTS[i];
					ItemStack item = player.getItemBySlot(slot);
					ResourceLocation armorRsrc = getArmorResource(player, item, slot, false);
					if (armorRsrc == null) continue; // next iteration
					
					// is this even a good idea lol
					IVertexBuilder drawBuffer = ItemRenderer.getArmorFoilBuffer(buf, RenderType.armorCutoutNoCull(armorRsrc), false, item.hasFoil());
					
					switch (i) {
					case 0:
						SPIRIT_ARMOR_MODEL.updateVisibility(hasHelmet, false, false, false);
						//SPIRIT_ARMOR_MODEL.renderHelmet(mtx, drawBuffer, light, 0xFFFFFF);
						break;
					case 1:
						SPIRIT_ARMOR_MODEL.updateVisibility(false, hasChestplate, false, false);
						//SPIRIT_ARMOR_MODEL.renderChestplate(mtx, drawBuffer, light, 0xFFFFFF);
						break;
					case 2:
						SPIRIT_ARMOR_MODEL.updateVisibility(false, false, hasLeggings, false);
						//SPIRIT_ARMOR_MODEL.renderLeggings(mtx, drawBuffer, light, 0xFFFFFF);
						break;
					case 3:
						SPIRIT_ARMOR_MODEL.updateVisibility(false, false, false, hasBoots);
						//SPIRIT_ARMOR_MODEL.renderBoots(mtx, drawBuffer, light, 0xFFFFFF);
						break;
					default:
						break;
					}
					
					if (item != null && item.getItem() instanceof IDyeableArmorItem) {
						int itemClr = ((IDyeableArmorItem)item.getItem()).getColor(item);
						r = (float)(itemClr >> 16 & 255) / 255.0F;
						g = (float)(itemClr >> 8 & 255) / 255.0F;
						b = (float)(itemClr & 255) / 255.0F;
						
						SPIRIT_ARMOR_MODEL.renderToBuffer(mtx, drawBuffer, light, 0xFFFFFF, r, g, b, 1);
						r = 1;
						g = 1;
						b = 1;
					} else {					
						SPIRIT_ARMOR_MODEL.renderToBuffer(mtx, drawBuffer, light, 0xFFFFFF, 1, 1, 1, 1);
					}
				}
				render(mtx, buf, light, player);
			mtx.popPose(); // ]
			
			if (!preRenderEvent.getPlayer().equals(Minecraft.getInstance().player)) {
				// Do not render our own nametag on our screen
				renderName(preRenderEvent.getRenderer(), (AbstractClientPlayerEntity)preRenderEvent.getPlayer(), preRenderEvent.getPlayer().getName(), mtx, buf, light);				
			}
			
			preRenderEvent.setCanceled(true);
		}
	}
	
	/**
	 * Duplicated from the biped armor layer renderer.
	 * It is intentional that this still calls the forge event, as I want to provide a means of mods overriding armor without (still)
	 * 
	 * @param entity Entity wearing the armor
	 * @param stack ItemStack for the armor
	 * @param slot Slot ID that the item is in
	 * @param isOverlay Whether or not to get the overlay texture.
	 * @return ResourceLocation pointing at the armor's texture, or null if there was no item.
	*/
	public static @Nullable ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlotType slot, boolean isOverlay) {
		if (stack == null || !(stack.getItem() instanceof ArmorItem)) {
			return null;
		}
		
		ArmorItem item = (ArmorItem)stack.getItem();
		
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');
		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}
		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, 1, isOverlay ? "_overlay" : "");
	
		s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, isOverlay ? "overlay" : null);
		ResourceLocation resourcelocation = BipedArmorLayer.ARMOR_LOCATION_CACHE.get(s1);
	
		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			BipedArmorLayer.ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
		}
	
		return resourcelocation;
	}

	@SuppressWarnings("resource")
	protected static void renderName(PlayerRenderer renderer, AbstractClientPlayerEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		double d0 = renderer.getDispatcher().distanceToSqr(entityIn);
	    if (!(d0 > 100.0D)) {
			boolean flag = !entityIn.isDiscrete();
			float f = entityIn.getBbHeight() + 0.5F;
			int i = "deadmau5".equals(displayNameIn.getString()) ? -10 : 0;
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.0D, (double)f, 0.0D);
			matrixStackIn.mulPose(renderer.getDispatcher().cameraOrientation());
			matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrixStackIn.last().pose();
			float f1 = (float) Minecraft.getInstance().options.textBackgroundOpacity;
			int j = (int)(f1 * 255.0F) << 24;
			FontRenderer fontrenderer = renderer.getFont();
			float f2 = (float)(-fontrenderer.width(displayNameIn) / 2);
			fontrenderer.drawInBatch(displayNameIn, f2, (float)i, 0x20FFFFFF, false, matrix4f, bufferIn, flag, j, packedLightIn);
			if (flag) {
			   fontrenderer.drawInBatch(displayNameIn, f2, (float)i, 0xFFFFFFFF, false, matrix4f, bufferIn, false, 0, packedLightIn);
			}
			
			matrixStackIn.popPose();
	    }
	}
	
	public static void render(MatrixStack matrix, IRenderTypeBuffer renBuf, int combinedLightIn, LivingEntity entity) {
		boolean flag = entity.getMainArm() == HandSide.RIGHT;
		ItemStack itemstack = flag ? entity.getOffhandItem() : entity.getMainHandItem();
		ItemStack itemstack1 = flag ? entity.getMainHandItem() : entity.getOffhandItem();
		if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
			matrix.pushPose();
			
			renderArmWithItem(entity, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT, matrix, renBuf, combinedLightIn);
			renderArmWithItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT, matrix, renBuf, combinedLightIn);
			matrix.popPose();
		}
	}

	private static void renderArmWithItem(LivingEntity entity, ItemStack heldItemStack, ItemCameraTransforms.TransformType trsType, HandSide handSide, MatrixStack mtx, IRenderTypeBuffer renType, int combinedLightIn) {
		if (!heldItemStack.isEmpty()) {
			mtx.pushPose();
			SPIRIT_MODEL.translateToHand(handSide, mtx);
			mtx.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			mtx.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			boolean flag = handSide == HandSide.LEFT;
			mtx.translate((double)((float)(flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
			Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, heldItemStack, trsType, flag, mtx, renType, combinedLightIn);
			mtx.popPose();
		}
	}
	
	private static void setRotationAnglesFrom(float partialTicks, MatrixStack mtx, LivingEntity entity, PlayerModel<AbstractClientPlayerEntity> playerModel) {
		
		float headYawOffset = entity.yHeadRot;
		float renderYawOffset = entity.yBodyRot;
		float pitchOffset = entity.xRot;
	    
	    float renderYawRadians = renderYawOffset / ModelSpirit.RADIAN_DIVISOR;
	    // An identical implementation of PlayerRenderer.applyRotations.
	    // Said method would be used, but type constraints prevent that from happening.
	    RENDERER.applyRotationsPlayer(entity, mtx, entity.tickCount, renderYawRadians, partialTicks);
		
	    Pose currentPose = entity.getPose();
	    if (entity instanceof PlayerEntity) {
	    	Pose forced = ((PlayerEntity)entity).getForcedPose();
	    	if (forced != null) currentPose = forced;
	    }
	    
	    // Upright poses first.
	    if (currentPose == Pose.STANDING || currentPose == Pose.CROUCHING || currentPose == Pose.SPIN_ATTACK) {
	    	mtx.mulPose(fromAxisAngle(0, 1, 0, -renderYawRadians + ModelSpirit.PI));
			mtx.mulPose(new Quaternion(0, 0, -1, 0));
			mtx.translate(0, -1.5, 0);
		
		// Lateral poses.
		} else if (currentPose == Pose.SWIMMING || currentPose == Pose.FALL_FLYING) {
			mtx.mulPose(new Quaternion(0, 0, -1, 0));
			mtx.mulPose(fromAxisAngle(0, 0, 1, -renderYawRadians + ModelSpirit.PI));
			mtx.translate(0, 0, 0.4); // x was -0.5
			pitchOffset -= 75; // If not sleeping, look up at least a little ways. This is so that we look forward when swimming.
		}
	    
	    // Pass in the entity (which is a player, not a spirit) into the model so that the biped stuff works
		SPIRIT_MODEL.setRotationAngles(entity, entity.animationPosition, entity.animationSpeed, entity.tickCount, headYawOffset - renderYawOffset, pitchOffset, playerModel);
		SPIRIT_ARMOR_MODEL.setRotationAngles(entity, entity.animationPosition, entity.animationSpeed, entity.tickCount, headYawOffset - renderYawOffset, pitchOffset, playerModel);
	}
	
	private static Quaternion fromAxisAngle(float x, float y, float z, float angle) {
		float sina = (float) Math.sin(angle / 2);
		return new Quaternion(x * sina, y * sina, z * sina, (float)Math.cos(angle/2));
	}
}
