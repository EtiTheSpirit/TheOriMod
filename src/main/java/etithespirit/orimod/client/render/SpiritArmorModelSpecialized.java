package etithespirit.orimod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class SpiritArmorModelSpecialized extends Model implements IArmorVisibilityProvider {
	
	private ModelPart armor;
	
	private ModelPart helmet;
	private ModelPart chestplate;
	
	private ModelPart rightArm;
	private ModelPart leftArm;
	
	private ModelPart tail;
	
	private ModelPart leftLeg;
	private ModelPart rightLeg;
	
	private ModelPart legTopLeft;
	private ModelPart legMidLeft;
	private ModelPart legTopRight;
	private ModelPart legMidRight;
	
	private ModelPart legLowLeft;
	private ModelPart hoofLeft;
	private ModelPart legLowRight;
	private ModelPart hoofRight;
	
	
	public SpiritArmorModelSpecialized() {
		this(createBodyLayer().bakeRoot());
	}
	
	public SpiritArmorModelSpecialized(ModelPart org) {
		super(RenderType::entityCutout);
		
		ModelPart armor = org.getChild("SpiritArmor");
		this.armor = armor;
		helmet = armor.getChild("Helmet");
		chestplate = armor.getChild("Chestplate");
		
		rightArm = chestplate.getChild("RightArm");
		leftArm = chestplate.getChild("LeftArm");
		tail = chestplate.getChild("Tail");
		
		ModelPart legsAndBoots = armor.getChild("LegsAndBoots");
		ModelPart leftLeg = legsAndBoots.getChild("LLeg");
		ModelPart rightLeg = legsAndBoots.getChild("RLeg");
		this.leftLeg = leftLeg;
		this.rightLeg = rightLeg;
		legTopLeft = leftLeg.getChild("LeftLegHigh_r1");
		legMidLeft = leftLeg.getChild("LeftLegMid_r1");
		legLowLeft = leftLeg.getChild("LeftLegLow_r1");
		hoofLeft = leftLeg.getChild("LeftHoof_r1");
		
		legTopRight = rightLeg.getChild("RightLegHigh_r1");
		legMidRight = rightLeg.getChild("RightLegMid_r1");
		legLowRight = rightLeg.getChild("RightLegLow_r1");
		hoofRight = rightLeg.getChild("RightHoof_r1");
	}
	
	public static LayerDefinition createBodyLayer() {
		// Made with Blockbench 4.5.2
		
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition SpiritArmor = partdefinition.addOrReplaceChild("SpiritArmor", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
		
		PartDefinition Chestplate = SpiritArmor.addOrReplaceChild("Chestplate", CubeListBuilder.create().texOffs(0, 24).mirror().addBox(-3.5F, -16.0F, -2.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
			.texOffs(0, 16).mirror().addBox(-3.0F, -21.0F, -1.7F, 6.0F, 5.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false)
			.texOffs(40, 25).mirror().addBox(-4.0F, -24.0F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		PartDefinition LeftArm = Chestplate.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(22, 18).mirror().addBox(0.0F, -1.8F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(4.0F, -22.0F, 0.0F));
		
		PartDefinition RightArm = Chestplate.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(22, 18).addBox(-3.0F, -1.8F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offset(-4.0F, -22.0F, 0.0F));
		
		PartDefinition Tail = Chestplate.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, 2.0F));
		
		PartDefinition TailCmp1_r1 = Tail.addOrReplaceChild("TailCmp1_r1", CubeListBuilder.create().texOffs(52, 0).mirror().addBox(-1.0F, -0.2F, -1.1F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.20F)).mirror(false), PartPose.offsetAndRotation(0.0F, 5.675F, 5.15F, 1.5708F, 0.0F, 0.0F));
		
		PartDefinition TailCmp0_r1 = Tail.addOrReplaceChild("TailCmp0_r1", CubeListBuilder.create().texOffs(52, 0).mirror().addBox(-1.0F, 0.0F, -2.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
		
		PartDefinition LegsAndBoots = SpiritArmor.addOrReplaceChild("LegsAndBoots", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		PartDefinition LLeg = LegsAndBoots.addOrReplaceChild("LLeg", CubeListBuilder.create(), PartPose.offset(2.0F, -12.0F, 0.0F));
		
		PartDefinition LeftLegMid_r1 = LLeg.addOrReplaceChild("LeftLegMid_r1", CubeListBuilder.create().texOffs(24, 0).mirror().addBox(-1.8F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(0.4494F, 5.2412F, 0.5354F, 1.309F, -0.0262F, -0.0436F));
		
		PartDefinition LeftLegHigh_r1 = LLeg.addOrReplaceChild("LeftLegHigh_r1", CubeListBuilder.create().texOffs(32, 4).mirror().addBox(-1.8F, -4.0F, -1.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(0.1F, 1.6464F, -2.5718F, -0.5672F, -0.0436F, 0.0F));
		
		PartDefinition LeftLegLow_r1 = LLeg.addOrReplaceChild("LeftLegLow_r1", CubeListBuilder.create().texOffs(44, 0).mirror().addBox(-1.1F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(0.5442F, 8.4613F, 1.2628F, -0.4538F, -0.0436F, -0.0175F));
		
		PartDefinition LeftHoof_r1 = LLeg.addOrReplaceChild("LeftHoof_r1", CubeListBuilder.create().texOffs(36, 0).mirror().addBox(1.6F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
		
		PartDefinition RLeg = LegsAndBoots.addOrReplaceChild("RLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, -12.0F, 0.0F));
		
		PartDefinition RightLegMid_r1 = RLeg.addOrReplaceChild("RightLegMid_r1", CubeListBuilder.create().texOffs(24, 0).addBox(-1.2F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-0.4494F, 5.2412F, 0.5354F, 1.309F, 0.0262F, 0.0436F));
		
		PartDefinition RightLegHigh_r1 = RLeg.addOrReplaceChild("RightLegHigh_r1", CubeListBuilder.create().texOffs(32, 4).addBox(-2.2F, -4.0F, -1.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-0.1F, 1.6464F, -2.5718F, -0.5672F, 0.0436F, 0.0F));
		
		PartDefinition RightLegLow_r1 = RLeg.addOrReplaceChild("RightLegLow_r1", CubeListBuilder.create().texOffs(44, 0).addBox(-0.9F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-0.5442F, 8.4613F, 1.2628F, -0.4538F, 0.0436F, 0.0175F));
		
		PartDefinition RightHoof_r1 = RLeg.addOrReplaceChild("RightHoof_r1", CubeListBuilder.create().texOffs(36, 0).addBox(-3.6F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
		
		PartDefinition Helmet = SpiritArmor.addOrReplaceChild("Helmet", CubeListBuilder.create().texOffs(34, 25).mirror().addBox(-2.0F, -3.0F, -5.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.25F)).mirror(false)
			.texOffs(0, 0).mirror().addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(0.0F, -24.0F, 0.0F));
		
		PartDefinition RightAntenna_r1 = Helmet.addOrReplaceChild("RightAntenna_r1", CubeListBuilder.create().texOffs(60, 22).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-1.5F, -7.4F, -2.0F, -1.0472F, 0.0F, -0.0873F));
		
		PartDefinition LeftAntenna_r1 = Helmet.addOrReplaceChild("LeftAntenna_r1", CubeListBuilder.create().texOffs(60, 22).mirror().addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(1.5F, -7.4F, -2.0F, -1.0472F, 0.0F, 0.0873F));
		
		PartDefinition LeftEar = Helmet.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(44, 14).mirror().addBox(-0.5625F, -2.1863F, 0.065F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.25F)).mirror(false)
			.texOffs(36, 19).mirror().addBox(-0.5625F, -2.1863F, 7.065F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offsetAndRotation(3.3F, -6.0F, 2.0F, 0.5236F, 0.0698F, 0.0698F));
		
		PartDefinition RightEar = Helmet.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(44, 14).addBox(-0.4375F, -2.1863F, 0.065F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.25F))
			.texOffs(36, 19).addBox(-0.4375F, -2.1863F, 7.065F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-3.3F, -6.0F, 2.0F, 0.5236F, -0.0698F, -0.0698F));
		
		return LayerDefinition.create(meshdefinition, 70, 32);
	}
	
	/**
	 * Update the visibility of all parts on this model.
	 *
	 * @param hasHelmet
	 * @param hasChestplate
	 * @param hasLeggings
	 * @param hasBoots
	 */
	@Override
	public void updateVisibility(boolean hasHelmet, boolean hasChestplate, boolean hasLeggings, boolean hasBoots) {
		helmet.visible = hasHelmet;
		chestplate.visible = hasChestplate;
		
		legTopLeft.visible = hasLeggings;
		legTopRight.visible = hasLeggings;
		legMidLeft.visible = hasLeggings;
		legMidRight.visible = hasLeggings;
		
		legLowLeft.visible = hasBoots || hasLeggings;
		legLowRight.visible = hasBoots || hasLeggings;
		
		hoofLeft.visible = hasBoots;
		hoofRight.visible = hasBoots;
	}
	
	/**
	 * Rotate the head with the given axes in radians.
	 * @param x The x rotation
	 * @param y The y rotation
	 * @param z The z rotation
	 */
	private void setHeadRotation(final float x, final float y, final float z) {
		setRotationAngle(helmet, x, y, z);
	}
	
	/**
	 * Rotate the legs by the given numbers in radians.
	 * @param lx The left leg X rotation
	 * @param ly The left leg Y rotation
	 * @param lz The left leg Z rotation
	 * @param rx The right leg X rotation
	 * @param ry The right leg Y rotation
	 * @param rz The right leg Z rotation
	 */
	private void setLegRotation(final float lx, final float ly, final float lz, final float rx, final float ry, final float rz) {
		setRotationAngle(leftLeg, lx, ly, lz);
		setRotationAngle(rightLeg, rx, ry, rz);
	}
	
	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
	public void setRotationAngles(final @Nonnull LivingEntity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYawRads, final float headPitchRads, final @Nullable PlayerModel<AbstractClientPlayer> model) {
		// Looking
		setHeadRotation(headPitchRads / Mth.RAD_TO_DEG, netHeadYawRads / Mth.RAD_TO_DEG, 0.0f);
		
		// Walking (Legs)
		setLegRotation(Mth.cos(limbSwing * 2/3 + Mth.PI) * 1.4f * limbSwingAmount, 0.0f, 0.0f, Mth.cos(limbSwing * 2/3) * 1.4f * limbSwingAmount, 0.0f, 0.0f);
		
		// Arm Swing...
		// ... While moving:
		rightArm.xRot = Mth.cos(limbSwing * 2/3 + Mth.PI) * 2.0f * limbSwingAmount * 0.5f;
		rightArm.yRot = 0.0f;
		rightArm.zRot = 0.0f;
		leftArm.xRot = Mth.cos(limbSwing * 2/3) * 2.0f * limbSwingAmount * 0.5f;
		leftArm.yRot = 0.0f;
		leftArm.zRot = 0.0f;
		
		// ... When idle:
		rightArm.zRot += Mth.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
		leftArm.zRot -= Mth.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
		rightArm.xRot += Mth.cos(ageInTicks * 0.067f) * 0.05f;
		leftArm.xRot -= Mth.cos(ageInTicks * 0.067f) * 0.05f;
		
		// I want it to be affected by both idle animation and motion like arms are.
		// Wag tail...
		// ... While moving:
		setRotationAngle(tail, 0, Mth.sin(limbSwing * 2/3f) * limbSwingAmount / 2, 0);
		
		// ... While idle:
		tail.xRot += Mth.sin(ageInTicks / 20) / 40;
		tail.yRot += Mth.sin(ageInTicks / 20) / 40;
		
		setupAttackAnimation(entityIn);
		boolean isRightHanded = entityIn.getMainArm() == HumanoidArm.RIGHT;
		if (model != null) {
			boolean isOppositeHandTwoHanded = isRightHanded ? model.leftArmPose.isTwoHanded() : model.rightArmPose.isTwoHanded();
			if (isRightHanded != isOppositeHandTwoHanded) {
				this.poseLeftArm(entityIn, model);
				this.poseRightArm(entityIn, model);
			} else {
				this.poseRightArm(entityIn, model);
				this.poseLeftArm(entityIn, model);
			}
		}
	}
	private void poseRightArm(LivingEntity entity, PlayerModel<AbstractClientPlayer> model) {
		switch (model.rightArmPose) {
			case EMPTY -> this.rightArm.yRot = 0.0F;
			case BLOCK -> {
				this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
				this.rightArm.yRot = (-(float) Math.PI / 6F);
			}
			case ITEM -> {
				this.rightArm.xRot = this.rightArm.xRot * 0.5F - ((float) Math.PI / 10F);
				this.rightArm.yRot = 0.0F;
			}
			case THROW_SPEAR -> {
				this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) Math.PI;
				this.rightArm.yRot = 0.0F;
			}
			case BOW_AND_ARROW -> {
				this.rightArm.yRot = -0.1F + this.helmet.yRot;
				this.leftArm.yRot = 0.1F + this.helmet.yRot + 0.4F;
				this.rightArm.xRot = (-(float) Math.PI / 2F) + this.helmet.xRot;
				this.leftArm.xRot = (-(float) Math.PI / 2F) + this.helmet.xRot;
			}
			case CROSSBOW_CHARGE -> AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true);
			case CROSSBOW_HOLD -> AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.helmet, true);
		}
	}
	
	private void poseLeftArm(LivingEntity entity, PlayerModel<AbstractClientPlayer> model) {
		switch (model.leftArmPose) {
			case EMPTY -> this.leftArm.yRot = 0.0F;
			case BLOCK -> {
				this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
				this.leftArm.yRot = ((float) Math.PI / 6F);
			}
			case ITEM -> {
				this.leftArm.xRot = this.leftArm.xRot * 0.5F - ((float) Math.PI / 10F);
				this.leftArm.yRot = 0.0F;
			}
			case THROW_SPEAR -> {
				this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
				this.leftArm.yRot = 0.0F;
			}
			case BOW_AND_ARROW -> {
				this.rightArm.yRot = -0.1F + this.helmet.yRot - 0.4F;
				this.leftArm.yRot = 0.1F + this.helmet.yRot;
				this.rightArm.xRot = (-(float) Math.PI / 2F) + this.helmet.xRot;
				this.leftArm.xRot = (-(float) Math.PI / 2F) + this.helmet.xRot;
			}
			case CROSSBOW_CHARGE -> AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, false);
			case CROSSBOW_HOLD -> AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.helmet, false);
		}
	}
	
	private void setupAttackAnimation(LivingEntity entity) {
		if (entity.attackAnim > 0.0F) {
			HumanoidArm handSide = this.getAttackArm(entity);
			ModelPart armPart = this.getArm(handSide);
			float f = entity.attackAnim;
			this.armor.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
			if (handSide == HumanoidArm.LEFT) {
				this.armor.yRot *= -1.0F;
			}
			
			this.rightArm.yRot += this.armor.yRot;
			this.leftArm.yRot += this.armor.yRot;
			this.leftArm.xRot += this.armor.yRot;
			// ^^^ yes it should assign yrot to xrot
			
			f = 1.0F - entity.attackAnim;
			f = f * f;
			f = f * f;
			f = 1.0F - f;
			float f1 = Mth.sin(f * (float)Math.PI);
			float f2 = Mth.sin(entity.attackAnim * (float)Math.PI) * -(this.helmet.xRot - 0.7F) * 0.75F;
			armPart.xRot = (float)((double)armPart.xRot - ((double)f1 * 1.2D + (double)f2));
			armPart.yRot += this.armor.yRot * 2.0F;
			armPart.zRot += Mth.sin(entity.attackAnim * (float)Math.PI) * -0.4F;
		} else {
			this.armor.yRot = 0;
		}
	}
	
	protected ModelPart getArm(HumanoidArm handSide) {
		return handSide == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}
	
	protected HumanoidArm getAttackArm(LivingEntity spirit) {
		HumanoidArm handSide = spirit.getMainArm();
		return spirit.swingingArm == InteractionHand.MAIN_HAND ? handSide : handSide.getOpposite();
	}
	
	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
		armor.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
	}
}
