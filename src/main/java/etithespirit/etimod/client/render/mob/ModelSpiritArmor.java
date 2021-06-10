package etithespirit.etimod.client.render.mob;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import etithespirit.etimod.common.mob.SpiritEntity;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

//Made with Blockbench 3.8.2
//Exported for Minecraft version 1.15
//Paste this class into your mod and generate all required imports


public class ModelSpiritArmor extends EntityModel<SpiritEntity> {
	private final ModelRenderer Root;
	private final ModelRenderer Chestplate;
	private final ModelRenderer LeftArm;
	private final ModelRenderer RightArm;
	private final ModelRenderer Tail;
	private final ModelRenderer TailCmp1_r1;
	private final ModelRenderer TailCmp0_r1;
	private final ModelRenderer Helmet;
	private final ModelRenderer RightAntenna_r1;
	private final ModelRenderer LeftAntenna_r1;
	private final ModelRenderer LeftEar;
	private final ModelRenderer RightEar;
	private final ModelRenderer Legs_NotLeggings;
	private final ModelRenderer LLeg;
	private final ModelRenderer LActualLeg;
	private final ModelRenderer LeftLegMid_r1;
	private final ModelRenderer LeftLegHigher_r1;
	private final ModelRenderer LeftLegLow_r1;
	private final ModelRenderer LBoot;
	private final ModelRenderer LeftHoof_r1;
	private final ModelRenderer RLeg;
	private final ModelRenderer RActualLeg;
	private final ModelRenderer RightLegMid_r1;
	private final ModelRenderer RightLegHigher_r1;
	private final ModelRenderer RightLegLow_r1;
	private final ModelRenderer RBoot;
	private final ModelRenderer RightHoof_r1;

	public ModelSpiritArmor() {
		texWidth = 64;
		texHeight = 32;

		Root = new ModelRenderer(this);
		Root.setPos(0.0F, 24.0F, 0.0F);
		

		Chestplate = new ModelRenderer(this);
		Chestplate.setPos(0.0F, 0.0F, 0.0F);
		Root.addChild(Chestplate);
		Chestplate.texOffs(22, 21).addBox(-3.0F, -21.0F, -1.7F, 6.0F, 5.0F, 3.0F, 0.25F, true);
		Chestplate.texOffs(17, 21).addBox(-3.5F, -16.0F, -2.0F, 7.0F, 4.0F, 4.0F, 0.25F, true);
		Chestplate.texOffs(16, 21).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 3.0F, 4.0F, 0.25F, true);

		LeftArm = new ModelRenderer(this);
		LeftArm.setPos(4.0F, -22.0F, 0.0F);
		Chestplate.addChild(LeftArm);
		LeftArm.texOffs(42, 17).addBox(0.0F, -1.8F, -1.5F, 3.0F, 11.0F, 3.0F, 0.25F, true);

		RightArm = new ModelRenderer(this);
		RightArm.setPos(-4.0F, -22.0F, 0.0F);
		Chestplate.addChild(RightArm);
		RightArm.texOffs(42, 17).addBox(-3.0F, -1.8F, -1.5F, 3.0F, 11.0F, 3.0F, 0.25F, false);

		Tail = new ModelRenderer(this);
		Tail.setPos(0.0F, -13.0F, 2.0F);
		Chestplate.addChild(Tail);
		

		TailCmp1_r1 = new ModelRenderer(this);
		TailCmp1_r1.setPos(0.0F, 5.675F, 5.15F);
		Tail.addChild(TailCmp1_r1);
		setRotationAngle(TailCmp1_r1, 1.5708F, 0.0F, 0.0F);
		TailCmp1_r1.texOffs(16, 0).addBox(-1.0F, -0.2F, -1.1F, 2.0F, 9.0F, 2.0F, 0.25F, true);

		TailCmp0_r1 = new ModelRenderer(this);
		TailCmp0_r1.setPos(0.0F, -1.0F, 0.0F);
		Tail.addChild(TailCmp0_r1);
		setRotationAngle(TailCmp0_r1, 0.7854F, 0.0F, 0.0F);
		TailCmp0_r1.texOffs(16, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 9.0F, 2.0F, 0.25F, true);

		Helmet = new ModelRenderer(this);
		Helmet.setPos(0.0F, -24.0F, 0.0F);
		Root.addChild(Helmet);
		Helmet.texOffs(54, 0).addBox(-2.0F, -3.0F, -5.0F, 4.0F, 3.0F, 1.0F, 0.25F, true);
		Helmet.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F, true);

		RightAntenna_r1 = new ModelRenderer(this);
		RightAntenna_r1.setPos(-1.5F, -7.4F, -2.0F);
		Helmet.addChild(RightAntenna_r1);
		setRotationAngle(RightAntenna_r1, -1.0472F, 0.0F, -0.0873F);
		RightAntenna_r1.texOffs(26, 12).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.25F, false);

		LeftAntenna_r1 = new ModelRenderer(this);
		LeftAntenna_r1.setPos(1.5F, -7.4F, -2.0F);
		Helmet.addChild(LeftAntenna_r1);
		setRotationAngle(LeftAntenna_r1, -1.0472F, 0.0F, 0.0873F);
		LeftAntenna_r1.texOffs(26, 12).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.25F, true);

		LeftEar = new ModelRenderer(this);
		LeftEar.setPos(3.3F, -6.0F, 2.0F);
		Helmet.addChild(LeftEar);
		setRotationAngle(LeftEar, 0.5236F, 0.0698F, 0.0698F);
		LeftEar.texOffs(48, 9).addBox(-0.5625F, -2.1863F, 0.065F, 1.0F, 4.0F, 7.0F, 0.25F, true);
		LeftEar.texOffs(4, 26).addBox(-0.5625F, -2.1863F, 7.065F, 1.0F, 3.0F, 3.0F, 0.25F, true);

		RightEar = new ModelRenderer(this);
		RightEar.setPos(-3.3F, -6.0F, 2.0F);
		Helmet.addChild(RightEar);
		setRotationAngle(RightEar, 0.5236F, -0.0698F, -0.0698F);
		RightEar.texOffs(48, 9).addBox(-0.4375F, -2.1863F, 0.065F, 1.0F, 4.0F, 7.0F, 0.25F, false);
		RightEar.texOffs(4, 26).addBox(-0.4375F, -2.1863F, 7.065F, 1.0F, 3.0F, 3.0F, 0.25F, false);

		Legs_NotLeggings = new ModelRenderer(this);
		Legs_NotLeggings.setPos(0.0F, -24.0F, 0.0F);
		Root.addChild(Legs_NotLeggings);
		

		LLeg = new ModelRenderer(this);
		LLeg.setPos(2.0F, 12.0F, 0.0F);
		Legs_NotLeggings.addChild(LLeg);
		

		LActualLeg = new ModelRenderer(this);
		LActualLeg.setPos(0.0F, 0.0F, 0.0F);
		LLeg.addChild(LActualLeg);
		

		LeftLegMid_r1 = new ModelRenderer(this);
		LeftLegMid_r1.setPos(0.4494F, 5.2412F, 0.5354F);
		LActualLeg.addChild(LeftLegMid_r1);
		setRotationAngle(LeftLegMid_r1, 1.309F, -0.0262F, -0.0436F);
		LeftLegMid_r1.texOffs(24, 21).addBox(-1.8F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F, 0.25F, true);

		LeftLegHigher_r1 = new ModelRenderer(this);
		LeftLegHigher_r1.setPos(0.1F, 1.6464F, -2.5718F);
		LActualLeg.addChild(LeftLegHigher_r1);
		setRotationAngle(LeftLegHigher_r1, -0.5672F, -0.0436F, 0.0F);
		LeftLegHigher_r1.texOffs(0, 22).addBox(-1.8F, -4.0F, -1.0F, 4.0F, 5.0F, 4.0F, 0.25F, true);
		LeftLegHigher_r1.texOffs(0, 26).addBox(-1.8F, 3.0F, -1.0F, 4.0F, 1.0F, 4.0F, 0.25F, true);

		LeftLegLow_r1 = new ModelRenderer(this);
		LeftLegLow_r1.setPos(0.5442F, 8.4613F, 1.2628F);
		LActualLeg.addChild(LeftLegLow_r1);
		setRotationAngle(LeftLegLow_r1, -0.4538F, -0.0436F, -0.0175F);
		LeftLegLow_r1.texOffs(46, 18).addBox(-1.1F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.25F, true);

		LBoot = new ModelRenderer(this);
		LBoot.setPos(0.0F, 0.0F, 0.0F);
		LLeg.addChild(LBoot);
		

		LeftHoof_r1 = new ModelRenderer(this);
		LeftHoof_r1.setPos(-2.0F, 12.0F, 0.0F);
		LBoot.addChild(LeftHoof_r1);
		setRotationAngle(LeftHoof_r1, 1.5708F, 0.0F, 0.0F);
		LeftHoof_r1.texOffs(25, 8).addBox(1.6F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, 0.25F, true);

		RLeg = new ModelRenderer(this);
		RLeg.setPos(-2.0F, 12.0F, 0.0F);
		Legs_NotLeggings.addChild(RLeg);
		

		RActualLeg = new ModelRenderer(this);
		RActualLeg.setPos(0.0F, 0.0F, 0.0F);
		RLeg.addChild(RActualLeg);
		

		RightLegMid_r1 = new ModelRenderer(this);
		RightLegMid_r1.setPos(-0.4494F, 5.2412F, 0.5354F);
		RActualLeg.addChild(RightLegMid_r1);
		setRotationAngle(RightLegMid_r1, 1.309F, 0.0262F, 0.0436F);
		RightLegMid_r1.texOffs(24, 21).addBox(-1.2F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F, 0.25F, false);

		RightLegHigher_r1 = new ModelRenderer(this);
		RightLegHigher_r1.setPos(-0.1F, 1.6464F, -2.5718F);
		RActualLeg.addChild(RightLegHigher_r1);
		setRotationAngle(RightLegHigher_r1, -0.5672F, 0.0436F, 0.0F);
		RightLegHigher_r1.texOffs(0, 22).addBox(-2.2F, -4.0F, -1.0F, 4.0F, 5.0F, 4.0F, 0.25F, false);
		RightLegHigher_r1.texOffs(0, 26).addBox(-2.2F, 3.0F, -1.0F, 4.0F, 1.0F, 4.0F, 0.25F, false);

		RightLegLow_r1 = new ModelRenderer(this);
		RightLegLow_r1.setPos(-0.5442F, 8.4613F, 1.2628F);
		RActualLeg.addChild(RightLegLow_r1);
		setRotationAngle(RightLegLow_r1, -0.4538F, 0.0436F, 0.0175F);
		RightLegLow_r1.texOffs(46, 18).addBox(-0.9F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.25F, false);

		RBoot = new ModelRenderer(this);
		RBoot.setPos(0.0F, 0.0F, 0.0F);
		RLeg.addChild(RBoot);
		

		RightHoof_r1 = new ModelRenderer(this);
		RightHoof_r1.setPos(2.0F, 12.0F, 0.0F);
		RBoot.addChild(RightHoof_r1);
		setRotationAngle(RightHoof_r1, 1.5708F, 0.0F, 0.0F);
		RightHoof_r1.texOffs(25, 8).addBox(-3.6F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, 0.25F, false);
	}
	
	/**
	 * Rotate the head with the given axes in radians.
	 * @param x
	 * @param y
	 * @param z
	 */
	private void setHeadRotation(final float x, final float y, final float z) {
		setRotationAngle(Helmet, x, y, z);
	}
	
	/**
	 * Rotate the legs by the given numbers in radians.
	 * @param lx
	 * @param ly
	 * @param lz
	 * @param rx
	 * @param ry
	 * @param rz
	 */
	private void setLegRotation(final float lx, final float ly, final float lz, final float rx, final float ry, final float rz) {
		setRotationAngle(LLeg, lx, ly, lz);
		setRotationAngle(RLeg, rx, ry, rz);
	}
	
	/**
	 * Update the visibility of all parts on this model.
	 * @param hasHelmet
	 * @param hasChestplate
	 * @param hasLeggings
	 * @param hasBoots
	 */
	public void updateVisibility(boolean hasHelmet, boolean hasChestplate, boolean hasLeggings, boolean hasBoots) {
		Helmet.visible = hasHelmet;
		
		Chestplate.visible = hasChestplate;
		
		//LActualLeg.showModel = hasLeggings;
		//RActualLeg.showModel = hasLeggings;
		LeftLegHigher_r1.visible = hasLeggings;
		RightLegHigher_r1.visible = hasLeggings;
		LeftLegMid_r1.visible = hasLeggings;
		RightLegMid_r1.visible = hasLeggings;
		LeftLegLow_r1.visible = hasLeggings | hasBoots;
		RightLegLow_r1.visible = hasLeggings | hasBoots;
		
		LBoot.visible = hasBoots;
		RBoot.visible = hasBoots;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		Root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
	
	@Override
	public void setupAnim(final @Nullable SpiritEntity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYawDegrees, final float headPitchDegrees) {
		setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYawDegrees, headPitchDegrees, null);
	}
	
	public void setRotationAngles(final @Nonnull LivingEntity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYawDegrees, final float headPitchDegrees, final @Nullable PlayerModel<AbstractClientPlayerEntity> model) {
		// Looking
		setHeadRotation(headPitchDegrees / ModelSpirit.RADIAN_DIVISOR, netHeadYawDegrees / ModelSpirit.RADIAN_DIVISOR, 0.0f);
		
		// Walking (Legs)
		setLegRotation(MathHelper.cos(limbSwing * 2/3 + ModelSpirit.PI) * 1.4f * limbSwingAmount, 0.0f, 0.0f, MathHelper.cos(limbSwing * 2/3) * 1.4f * limbSwingAmount, 0.0f, 0.0f);
		
		// Arm Swing...
		// ... While moving:
		RightArm.xRot = MathHelper.cos(limbSwing * 2/3 + ModelSpirit.PI) * 2.0f * limbSwingAmount * 0.5f;
		RightArm.yRot = 0.0f;
		RightArm.zRot = 0.0f;
		LeftArm.xRot = MathHelper.cos(limbSwing * 2/3) * 2.0f * limbSwingAmount * 0.5f;
		LeftArm.yRot = 0.0f;
		LeftArm.zRot = 0.0f;
		
		// ... When idle:
		RightArm.zRot += MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
		LeftArm.zRot -= MathHelper.cos(ageInTicks * 0.09f) * 0.05f + 0.05f;
		RightArm.xRot += MathHelper.cos(ageInTicks * 0.067f) * 0.05f;
		LeftArm.xRot -= MathHelper.cos(ageInTicks * 0.067f) * 0.05f;
		
		// I want it to be affected by both idle animation and motion like arms are.
		// Wag tail... 
		// ... While moving:
		setRotationAngle(Tail, 0, MathHelper.sin(limbSwing * 2/3f) * limbSwingAmount / 2, 0);		
		
		// ... While idle:
		
		// Sync
		Tail.xRot += MathHelper.sin(ageInTicks / 20) / 40;
		Tail.yRot += MathHelper.sin(ageInTicks / 20) / 40;
		
		setupAttackAnimation(entityIn);
		boolean isRightHanded = entityIn.getMainArm() == HandSide.RIGHT;
		boolean isOppositeHandTwoHanded = isRightHanded ? model.leftArmPose.isTwoHanded() : model.rightArmPose.isTwoHanded();
		if (isRightHanded != isOppositeHandTwoHanded) {
			this.poseLeftArm(entityIn, model);
			this.poseRightArm(entityIn, model);
		} else {
			this.poseRightArm(entityIn, model);
			this.poseLeftArm(entityIn, model);
		}
	}
	
	private void poseRightArm(LivingEntity entity, PlayerModel<AbstractClientPlayerEntity> model) {
		switch(model.rightArmPose) {
			case EMPTY:
				this.RightArm.yRot = 0.0F;
				break;
			case BLOCK:
				this.RightArm.xRot = this.RightArm.xRot * 0.5F - 0.9424779F;
				this.RightArm.yRot = (-(float)Math.PI / 6F);
				break;
			case ITEM:
				this.RightArm.xRot = this.RightArm.xRot * 0.5F - ((float)Math.PI / 10F);
				this.RightArm.yRot = 0.0F;
				break;
			case THROW_SPEAR:
				this.RightArm.xRot = this.RightArm.xRot * 0.5F - (float)Math.PI;
				this.RightArm.yRot = 0.0F;
				break;
			case BOW_AND_ARROW:
				this.RightArm.yRot = -0.1F + this.Helmet.yRot;
				this.LeftArm.yRot = 0.1F + this.Helmet.yRot + 0.4F;
				this.RightArm.xRot = (-(float)Math.PI / 2F) + this.Helmet.xRot;
				this.LeftArm.xRot = (-(float)Math.PI / 2F) + this.Helmet.xRot;
				break;
			case CROSSBOW_CHARGE:
				ModelHelper.animateCrossbowCharge(this.RightArm, this.LeftArm, entity, true);
				break;
			case CROSSBOW_HOLD:
				ModelHelper.animateCrossbowHold(this.RightArm, this.LeftArm, this.Helmet, true);
		}
	}

	private void poseLeftArm(LivingEntity entity, PlayerModel<AbstractClientPlayerEntity> model) {
		switch(model.leftArmPose) {
			case EMPTY:
				this.LeftArm.yRot = 0.0F;
				break;
			case BLOCK:
				this.LeftArm.xRot = this.LeftArm.xRot * 0.5F - 0.9424779F;
				this.LeftArm.yRot = ((float)Math.PI / 6F);
				break;
			case ITEM:
				this.LeftArm.xRot = this.LeftArm.xRot * 0.5F - ((float)Math.PI / 10F);
				this.LeftArm.yRot = 0.0F;
				break;
			case THROW_SPEAR:
				this.LeftArm.xRot = this.LeftArm.xRot * 0.5F - (float)Math.PI;
				this.LeftArm.yRot = 0.0F;
				break;
			case BOW_AND_ARROW:
				this.RightArm.yRot = -0.1F + this.Helmet.yRot - 0.4F;
				this.LeftArm.yRot = 0.1F + this.Helmet.yRot;
				this.RightArm.xRot = (-(float)Math.PI / 2F) + this.Helmet.xRot;
				this.LeftArm.xRot = (-(float)Math.PI / 2F) + this.Helmet.xRot;
				break;
			case CROSSBOW_CHARGE:
				ModelHelper.animateCrossbowCharge(this.RightArm, this.LeftArm, entity, false);
				break;
			case CROSSBOW_HOLD:
				ModelHelper.animateCrossbowHold(this.RightArm, this.LeftArm, this.Helmet, false);
		}
	}
	
	private void setupAttackAnimation(LivingEntity entity) {
		if (entity.attackAnim > 0.0F) {
			HandSide handside = this.getAttackArm(entity);
			ModelRenderer modelrenderer = this.getArm(handside);
			float f = entity.attackAnim;
			this.Root.yRot = MathHelper.sin(MathHelper.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
			if (handside == HandSide.LEFT) {
				this.Root.yRot *= -1.0F;
			}

			/*
			this.RightArm.z = MathHelper.sin(this.Torso.rotateAngleY) * 5.0F;
			this.RightArm.x = -MathHelper.cos(this.Torso.rotateAngleY) * 5.0F;
			this.LeftArm.z = -MathHelper.sin(this.Torso.rotateAngleY) * 5.0F;
			this.LeftArm.x = MathHelper.cos(this.Torso.rotateAngleY) * 5.0F;
			*/
			this.RightArm.yRot += this.Root.yRot;
			this.LeftArm.yRot += this.Root.yRot;
			this.LeftArm.xRot += this.Root.yRot;
			f = 1.0F - entity.attackAnim;
			f = f * f;
			f = f * f;
			f = 1.0F - f;
			float f1 = MathHelper.sin(f * (float)Math.PI);
			float f2 = MathHelper.sin(entity.attackAnim * (float)Math.PI) * -(this.Helmet.xRot - 0.7F) * 0.75F;
			modelrenderer.xRot = (float)((double)modelrenderer.xRot - ((double)f1 * 1.2D + (double)f2));
			modelrenderer.yRot += this.Root.yRot * 2.0F;
			modelrenderer.zRot += MathHelper.sin(entity.attackAnim * (float)Math.PI) * -0.4F;
		} else {
			this.Root.yRot = 0;
		}
	}

	protected ModelRenderer getArm(HandSide handSide) {
		return handSide == HandSide.LEFT ? this.LeftArm : this.RightArm;
	}

	protected HandSide getAttackArm(LivingEntity spirit) {
		HandSide handside = spirit.getMainArm();
		return spirit.swingingArm == Hand.MAIN_HAND ? handside : handside.getOpposite();
	}
}