package etithespirit.orimod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.item.ISpiritLightItem;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Responsible for overriding the Player renderer such that they render as a Spirit.
 */
public class RenderPlayerAsSpirit {
	
	/**
	 * The texture for a spirit.
	 */
	public static final ResourceLocation SPIRIT_TEXTURE = new ResourceLocation(OriMod.MODID, "textures/entity/spirit.png");
	
	/**
	 * The model of a spirit.
	 */
	private static final SpiritModel MODEL = new SpiritModel();
	
	/**
	 * The armor that fits on a spirit.
	 */
	private static final SpiritArmorModel ARMOR = new SpiritArmorModel();
	
	/**
	 * A dummy biped armor layer. This is used for access to the getArmorResource method,
	 * which relies on having an instance despite not accessing any instance information.<br/>
	 * <strong>Naturally, this is not safe for use for any other purpose than access to this method.</strong>
	 */
	private static final HumanoidArmorLayer<?, ?, ?> DUMMY_BIP_ARMOR_LAYER = new HumanoidArmorLayer<>(null, null, null);
	
	/**
	 * An ordered list of the four armor slots available in vanilla.
	 */
	private static final EquipmentSlot[] SLOTS = new EquipmentSlot[] {
		EquipmentSlot.HEAD,
		EquipmentSlot.CHEST,
		EquipmentSlot.LEGS,
		EquipmentSlot.FEET
	};
	
	/**
	 * (Manually Subscribed)
	 * Occurs when the player is about to be rendered. This is used to override the rendered model to a spirit if needed.
	 * @param preRenderEvent The render event containing meaningful information about the player renderer.
	 */
	public static void whenRenderingPlayer(RenderPlayerEvent.Pre preRenderEvent) {
		if (preRenderEvent.getEntity() instanceof AbstractClientPlayer player && SpiritIdentifier.isSpirit(player)) {
			
			// Get some relevant information
			PlayerRenderer renderer = preRenderEvent.getRenderer();
			int packedLight = preRenderEvent.getPackedLight();
			float partialTicks = preRenderEvent.getPartialTick();
			
			// Now get the buffers, and set up the consumer to use the cutout renderer, using the Spirit texture.
			MultiBufferSource bufferProvider = preRenderEvent.getMultiBufferSource();
			VertexConsumer consumer = bufferProvider.getBuffer(RenderType.entityCutout(SPIRIT_TEXTURE));
			PoseStack mtx = preRenderEvent.getPoseStack();
			
			// Control crouch and shadow rendering.
			// This is a relatively lazy and honestly quite hacky solution to making the character just sink down when sneaking.
			// TODO: Bend legs when sneaking!
			renderer.shadowRadius = 0.4f; // AT'd
			Vec3 crouchTranslation = renderer.getRenderOffset(player, partialTicks);
			mtx.translate(-crouchTranslation.x, -crouchTranslation.y, -crouchTranslation.z);
			// Undo the translation applied by crouching.
			// n.b. do it this way for compatibility with any mods that might just decide they feel like changing how player crouching works, rather than hardcoding y+0.125D.
			mtx.translate(0, player.getForcedPose() == Pose.CROUCHING ? -0.125 : 0, 0);
			// ^ This is done here because the shadow moves inversely if it's done after pushing the matrix.
			// Effectively, I have to move the shadow down instead of up.
			
			
			// Damage flickering
			// n.b. these variables are reused for armor rendering later on, albeit via reassigning them.
			// TODO: Use packed overlay instead of this?
			float r = 1;
			float g = 1;
			float b = 1;
			if (player.hurtTime > 0 && player.hurtTime % 3 == 0) {
				// Red flicker if the player gets damaged.
				// This emulates the game spirits come from, rather than the solid red typically seen in MC.
				g = 0.5f;
				b = 0.5f;
			}
			
			
			mtx.pushPose(); {
				// Note:
				// WIDTH_MOD = 0.495f;
				// HEIGHT_MOD = 0.5f;
				// The model is built at player scale (2x) for the sake of texture resolution. Scale the model down to its intended size.
				mtx.scale(SpiritModel.WIDTH_MOD, SpiritModel.HEIGHT_MOD, SpiritModel.WIDTH_MOD);
				
				// Start by using the player's model as a frame of reference.
				// Call rotation methods on both the armor and the main body using this information.
				setRotationAnglesFrom(partialTicks, mtx, player, renderer.getModel());
				MODEL.renderToBuffer(mtx, consumer, packedLight, LivingEntityRenderer.getOverlayCoords(player, 0f), r, g, b, 1f);
				// ^ This is where things seem to be going wrong?
				
				// Figure out what gear the player has.
				boolean hasHelmet = player.hasItemInSlot(EquipmentSlot.HEAD);
				boolean hasChestplate = player.hasItemInSlot(EquipmentSlot.CHEST);
				boolean hasLeggings = player.hasItemInSlot(EquipmentSlot.LEGS);
				boolean hasBoots = player.hasItemInSlot(EquipmentSlot.FEET);
				
				// Update the visibility of the armor based on which parts the player has. This changes .visible properties.
				ARMOR.updateVisibility(hasHelmet, hasChestplate, hasLeggings, hasBoots);
				
				// ARMOR RENDERING:
				for (int i = 0; i < SLOTS.length; i++) {
					// TODO: Improve this, it takes up to four iterations to draw armor!
					EquipmentSlot slot = SLOTS[i];
					ItemStack item = player.getItemBySlot(slot);
					if (!(item.getItem() instanceof ArmorItem)) continue;
					ResourceLocation armorRsrc = DUMMY_BIP_ARMOR_LAYER.getArmorResource(player, item, slot, null);
					
					// Get the buffer for the armor.
					VertexConsumer drawBuffer = ItemRenderer.getArmorFoilBuffer(bufferProvider, RenderType.armorCutoutNoCull(armorRsrc), false, item.hasFoil());
					
					// Effectively what this does is for each iteration (where i corresponds to a slot), this sets the visiblity of everything to false
					// with the exception of the applicable slot, which is set to true, otherwise the iteration is skipped.
					switch (i) {
						case 0 -> {
							if (!hasHelmet) continue;
							ARMOR.updateVisibility(true, false, false, false);
						}
						case 1 -> {
							if (!hasChestplate) continue;
							ARMOR.updateVisibility(false, true, false, false);
						}
						case 2 -> {
							if (!hasLeggings) continue;
							ARMOR.updateVisibility(false, false, true, false);
						}
						case 3 -> {
							if (!hasBoots) continue;
							ARMOR.updateVisibility(false, false, false, true);
						}
						default ->
							// Throw exception here, as SLOTS is manually defined. Anything outside the range of 0 to 3 is caused by the me adding a new slot then not adding anything here
							throw new UnsupportedOperationException("Something caused the slots index to go beyond four, which has not been programmatically accounted for.");
					}
					
					if (item.getItem() instanceof DyeableArmorItem dyeableArmorItem) {
						int itemClr = dyeableArmorItem.getColor(item);
						r = (float) (itemClr >> 16 & 255) / 255.0F;
						g = (float) (itemClr >> 8 & 255) / 255.0F;
						b = (float) (itemClr & 255) / 255.0F;
						
						ARMOR.renderToBuffer(mtx, drawBuffer, packedLight, 0xFFFFFF, r, g, b, 1);
					} else {
						float alpha = 1;
						if (item.getItem() instanceof ISpiritLightItem) alpha = 0.5f;
						ARMOR.renderToBuffer(mtx, drawBuffer, packedLight, 0xFFFFFF, 1, 1, 1, 1);
					}
				}
				renderThirdPersonItems(mtx, bufferProvider, packedLight, player);
			} mtx.popPose();
			
			if (!player.equals(Minecraft.getInstance().player)) {
				// Do not render our own nametag on our screen
				renderer.renderNameTag(player, player.getName(), mtx, bufferProvider, packedLight);
			}
			
			preRenderEvent.setCanceled(true);
		}
	}
	
	/**
	 * Renders the items being held by this character
	 * @param matrix The current matrix stack
	 * @param renBuf A source of render buffers, used by the renderArmWithItem method.
	 * @param combinedLightIn The combined light on the item.
	 * @param entity The entity to render for.
	 */
	public static void renderThirdPersonItems(PoseStack matrix, MultiBufferSource renBuf, int combinedLightIn, LivingEntity entity) {
		boolean isRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
		ItemStack leftItem = isRightHanded ? entity.getOffhandItem() : entity.getMainHandItem();
		ItemStack rightItem = isRightHanded ? entity.getMainHandItem() : entity.getOffhandItem();
		if (!leftItem.isEmpty() || !rightItem.isEmpty()) {
			matrix.pushPose();
			renderArmWithItem(entity, rightItem, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, matrix, renBuf, combinedLightIn);
			renderArmWithItem(entity, leftItem, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, matrix, renBuf, combinedLightIn);
			matrix.popPose();
		}
	}
	
	/**
	 * Renders an item in the given arm for the entity.
	 * @param entity The entity to render the item for.
	 * @param heldItemStack The item being held.
	 * @param trsType Where the item is.
	 * @param handSide What side the item is on.
	 * @param mtx The current matrix stack.
	 * @param renType A container for render types.
	 * @param combinedLightIn The combined light on this item.
	 */
	private static void renderArmWithItem(LivingEntity entity, ItemStack heldItemStack, ItemTransforms.TransformType trsType, HumanoidArm handSide, PoseStack mtx, MultiBufferSource renType, int combinedLightIn) {
		if (!heldItemStack.isEmpty()) {
			mtx.pushPose();
			MODEL.translateToHand(handSide, mtx);
			mtx.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			mtx.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			boolean isLefty = handSide == HumanoidArm.LEFT;
			mtx.translate((isLefty ? -1D : 1D) / 16.0D, 0.125D, -0.625D);
			BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(heldItemStack, entity.level, entity, heldItemStack.getDamageValue());
			Minecraft.getInstance().getItemRenderer().render(heldItemStack, trsType, isLefty, mtx, renType, combinedLightIn, GeneralUtils.FULL_BRIGHT_LIGHT, itemModel);
			mtx.popPose();
		}
	}
	
	/**
	 * Using the given player entity model, this will do what it can to copy the rotations from the player and put them onto the matrix passed in.
	 * Additionally, it will use this information to tell the spirit model and spirit armor model how to rotate.
	 * @param partialTicks The amount of partial ticks, which is (of course) a higher time resolution than ticks.
	 * @param mtx The transformation matrix stack.,
	 * @param player The player that is being rotated.
	 * @param playerModel The model of the player.
	 */
	private static void setRotationAnglesFrom(float partialTicks, PoseStack mtx, LivingEntity player, PlayerModel<AbstractClientPlayer> playerModel) {
		
		float headYawOffset = player.yHeadRot;
		float renderYawOffset = player.yBodyRot;
		float pitchOffset = player.getXRot();
		
		float renderYaw = renderYawOffset / Mth.RAD_TO_DEG;
		// An identical implementation of PlayerRenderer.applyRotations.
		// Said method would be used, but type constraints prevent that from happening.
		applyRotationsPlayer(player, mtx, player.tickCount, renderYaw, partialTicks);
		
		Pose currentPose = player.getPose();
		if (player instanceof Player) {
			Pose forced = ((Player)player).getForcedPose();
			if (forced != null) currentPose = forced;
		}
		
		// Upright poses first.
		if (currentPose == Pose.STANDING || currentPose == Pose.CROUCHING || currentPose == Pose.SPIN_ATTACK) {
			mtx.mulPose(fromAxisAngle(0, 1, 0, -renderYaw + Mth.PI));
			mtx.mulPose(new Quaternion(0, 0, -1, 0));
			mtx.translate(0, -1.5, 0);
			
			// Lateral poses.
		} else if (currentPose == Pose.SWIMMING || currentPose == Pose.FALL_FLYING) {
			mtx.mulPose(new Quaternion(0, 0, -1, 0));
			mtx.mulPose(fromAxisAngle(0, 0, 1, -renderYaw + Mth.PI));
			mtx.translate(0, 0, 0.4); // x was -0.5
			pitchOffset -= 75; // If not sleeping, look up at least a little ways. This is so that we look forward when swimming.
		}
		
		// Pass in the entity (which is a player, not a spirit) into the model so that the biped stuff works
		MODEL.setRotationAngles(player, player.animationPosition, player.animationSpeed, player.tickCount, headYawOffset - renderYawOffset, pitchOffset, playerModel);
		ARMOR.setRotationAngles(player, player.animationPosition, player.animationSpeed, player.tickCount, headYawOffset - renderYawOffset, pitchOffset, playerModel);
	}
	
	/**
	 * @param x The X component of the unit vector.
	 * @param y The Y component of the unit vector.
	 * @param z The Z component of the unit vector.
	 * @param angle The angle to rotate around that vector, in radians. The rotation is applied in a counter-clockwise fashion.
	 * @return A quaternion rotated around the given unit vector (in radians).
	 */
	private static Quaternion fromAxisAngle(float x, float y, float z, float angle) {
		float sina = Mth.sin(angle / 2);
		return new Quaternion(x * sina, y * sina, z * sina, Mth.cos(angle/2));
	}
	
	/**
	 * Converts a direction to a facing angle in degrees.
	 * @param facingIn The direction that is being faced.
	 * @return A rotation in degrees for the given direction.
	 */
	private static float getFacingAngle(Direction facingIn) {
		return switch (facingIn) {
			case SOUTH -> 90.0F;
			default -> 0.0F;
			case NORTH -> 270.0F;
			case EAST -> 180.0F;
		};
	}
	
	/**
	 * Applies rotations based on circumstances of the entity.
	 * @param entityLiving The entity to apply to.
	 * @param matrixStackIn The transformation matrix stack.
	 * @param ageInTicks The age of this entity, in ticks.
	 * @param rotationYaw The Y rotation of this entity.
	 * @param partialTicks The current render tick.
	 */
	// TODO: Better name, as this previously was a copy of the superclass's applyRotations method when the custom renderer operated as a duplicate of the player renderer.
	private static void superApplyRotations(LivingEntity entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		Pose pose = entityLiving.getPose();
		
		if (entityLiving.deathTime > 0) {
			float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
			f = Mth.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}
			
			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * 90F));
		} else if (entityLiving.isAutoSpinAttack()) {
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.getXRot()));
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(((float)entityLiving.tickCount + partialTicks) * -75.0F));
		} else if (pose == Pose.SLEEPING) {
			Direction direction = entityLiving.getBedOrientation();
			float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90F));
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270.0F));
		}
		
	}
	
	/**
	 * An adapted copy of the player renderer's applyRotations method.
	 * @param entityLiving The entity to apply to.
	 * @param matrixStackIn The transformation matrix stack.
	 * @param ageInTicks The age of this entity, in ticks.
	 * @param rotationYaw The Y rotation of this entity.
	 * @param partialTicks The current render tick.
	 */
	private static void applyRotationsPlayer(LivingEntity entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		float swimAmount = entityLiving.getSwimAmount(partialTicks);
		if (entityLiving.isFallFlying()) {
			superApplyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
			float f1 = (float)entityLiving.getFallFlyingTicks() + partialTicks;
			float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
			if (!entityLiving.isAutoSpinAttack()) {
				matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entityLiving.getXRot())));
			}
			
			Vec3 lookAngle = entityLiving.getLookAngle();
			Vec3 effectiveVelocity = entityLiving.getDeltaMovement();
			double horzVelocity = effectiveVelocity.horizontalDistanceSqr();
			double horzLookAngle = lookAngle.horizontalDistanceSqr();
			if (horzVelocity > 0.0D && horzLookAngle > 0.0D) {
				double d2 = (effectiveVelocity.x * lookAngle.x + effectiveVelocity.z * lookAngle.z) / Math.sqrt(horzVelocity * horzLookAngle);
				double d3 = effectiveVelocity.x * lookAngle.z - effectiveVelocity.z * lookAngle.x;
				matrixStackIn.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
			}
		} else if (swimAmount > 0.0F) {
			superApplyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
			float waterRotation = entityLiving.isInWater() ? -90.0F - entityLiving.getXRot() : -90.0F;
			float animatedWaterRotation = Mth.lerp(swimAmount, 0.0F, waterRotation);
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(animatedWaterRotation));
		} else {
			superApplyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
		}
		
	}
	
	
}
