package etithespirit.etimod.client.render.mob;

import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import etithespirit.etimod.common.mob.SpiritEntity;

@SuppressWarnings("unused")
public class ModelSpirit extends EntityModel<SpiritEntity> implements IHasArm, IHasHead {
	
	/** The mathematical constant pi */
	public static final float PI = 3.1415926535897932384f;
	
	/** A constant value used to convert to radians. Divide input degree values by this value to translate them into radians, and multiply to translate radians to degrees. */
	public static final float RADIAN_DIVISOR = 180f / PI;
	
	/** Spirits will be rendered at this amount of scale on the Y axis. */
	public static final float HEIGHT_MOD = 0.5f;
	
	/** Spirits will be rendered at this amount of scale on the width / depth axes. */
	public static final float WIDTH_MOD = 0.495f;
	
	private final ModelRenderer Torso;
	private final ModelRenderer Tail;
	private final ModelRenderer TailCmp1_r1;
	private final ModelRenderer TailCmp0_r1;
	private final ModelRenderer RLeg;
	private final ModelRenderer RightLegMid_r1;
	private final ModelRenderer RightLegHigh_r1;
	private final ModelRenderer RightLegLow_r1;
	private final ModelRenderer RightHoof_r1;
	private final ModelRenderer LLeg;
	private final ModelRenderer LeftLegMid_r1;
	private final ModelRenderer LeftLegHigh_r1;
	private final ModelRenderer LeftLegLow_r1;
	private final ModelRenderer LeftHoof_r1;
	private final ModelRenderer LeftArm;
	private final ModelRenderer RightArm;
	private final ModelRenderer Head;
	private final ModelRenderer RightAntenna_r1;
	private final ModelRenderer LeftAntenna_r1;
	private final ModelRenderer LeftEar;
	private final ModelRenderer RightEar;

	public ModelSpirit() {
		texWidth = 70;
		texHeight = 32;
		
		Torso = new ModelRenderer(this);
		Torso.setPos(0.0F, 24.0F, 0.0F);
		Torso.texOffs(0, 24).addBox(-3.5F, -16.0F, -2.0F, 7.0F, 4.0F, 4.0F, 0.0F, true);
		Torso.texOffs(0, 16).addBox(-3.0F, -21.0F, -1.7F, 6.0F, 5.0F, 3.0F, 0.0F, true);
		Torso.texOffs(40, 25).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 3.0F, 4.0F, 0.0F, true);

		Tail = new ModelRenderer(this);
		Tail.setPos(0.0F, -13.0F, 2.0F);
		Torso.addChild(Tail);
		

		TailCmp1_r1 = new ModelRenderer(this);
		TailCmp1_r1.setPos(0.0F, 5.675F, 5.15F);
		Tail.addChild(TailCmp1_r1);
		setRotationAngle(TailCmp1_r1, 1.5708F, 0.0F, 0.0F);
		TailCmp1_r1.texOffs(52, 0).addBox(-1.0F, -0.2F, -1.1F, 2.0F, 9.0F, 2.0F, -0.05F, true);

		TailCmp0_r1 = new ModelRenderer(this);
		TailCmp0_r1.setPos(0.0F, -1.0F, 0.0F);
		Tail.addChild(TailCmp0_r1);
		setRotationAngle(TailCmp0_r1, 0.7854F, 0.0F, 0.0F);
		TailCmp0_r1.texOffs(52, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 9.0F, 2.0F, 0.0F, true);

		RLeg = new ModelRenderer(this);
		RLeg.setPos(-2.0F, -12.0F, 0.0F);
		Torso.addChild(RLeg);
		

		RightLegMid_r1 = new ModelRenderer(this);
		RightLegMid_r1.setPos(-0.4494F, 5.2412F, 0.5354F);
		RLeg.addChild(RightLegMid_r1);
		setRotationAngle(RightLegMid_r1, 1.309F, 0.0262F, 0.0436F);
		RightLegMid_r1.texOffs(24, 0).addBox(-1.2F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, false);

		RightLegHigh_r1 = new ModelRenderer(this);
		RightLegHigh_r1.setPos(-0.1F, 1.6464F, -2.5718F);
		RLeg.addChild(RightLegHigh_r1);
		setRotationAngle(RightLegHigh_r1, -0.5672F, 0.0436F, 0.0F);
		RightLegHigh_r1.texOffs(32, 4).addBox(-2.2F, -4.0F, -1.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

		RightLegLow_r1 = new ModelRenderer(this);
		RightLegLow_r1.setPos(-0.5442F, 8.4613F, 1.2628F);
		RLeg.addChild(RightLegLow_r1);
		setRotationAngle(RightLegLow_r1, -0.4538F, 0.0436F, 0.0175F);
		RightLegLow_r1.texOffs(44, 0).addBox(-0.9F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);

		RightHoof_r1 = new ModelRenderer(this);
		RightHoof_r1.setPos(2.0F, 12.0F, 0.0F);
		RLeg.addChild(RightHoof_r1);
		setRotationAngle(RightHoof_r1, 1.5708F, 0.0F, 0.0F);
		RightHoof_r1.texOffs(36, 0).addBox(-3.6F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);

		LLeg = new ModelRenderer(this);
		LLeg.setPos(2.0F, -12.0F, 0.0F);
		Torso.addChild(LLeg);
		

		LeftLegMid_r1 = new ModelRenderer(this);
		LeftLegMid_r1.setPos(0.4494F, 5.2412F, 0.5354F);
		LLeg.addChild(LeftLegMid_r1);
		setRotationAngle(LeftLegMid_r1, 1.309F, -0.0262F, -0.0436F);
		LeftLegMid_r1.texOffs(24, 0).addBox(-1.8F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F, 0.0F, true);

		LeftLegHigh_r1 = new ModelRenderer(this);
		LeftLegHigh_r1.setPos(0.1F, 1.6464F, -2.5718F);
		LLeg.addChild(LeftLegHigh_r1);
		setRotationAngle(LeftLegHigh_r1, -0.5672F, -0.0436F, 0.0F);
		LeftLegHigh_r1.texOffs(32, 4).addBox(-1.8F, -4.0F, -1.0F, 4.0F, 8.0F, 4.0F, 0.0F, true);

		LeftLegLow_r1 = new ModelRenderer(this);
		LeftLegLow_r1.setPos(0.5442F, 8.4613F, 1.2628F);
		LLeg.addChild(LeftLegLow_r1);
		setRotationAngle(LeftLegLow_r1, -0.4538F, -0.0436F, -0.0175F);
		LeftLegLow_r1.texOffs(44, 0).addBox(-1.1F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.0F, true);

		LeftHoof_r1 = new ModelRenderer(this);
		LeftHoof_r1.setPos(-2.0F, 12.0F, 0.0F);
		LLeg.addChild(LeftHoof_r1);
		setRotationAngle(LeftHoof_r1, 1.5708F, 0.0F, 0.0F);
		LeftHoof_r1.texOffs(36, 0).addBox(1.6F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, true);

		LeftArm = new ModelRenderer(this);
		LeftArm.setPos(4.0F, -22.0F, 0.0F);
		Torso.addChild(LeftArm);
		LeftArm.texOffs(22, 18).addBox(0.0F, -1.8F, -1.5F, 3.0F, 11.0F, 3.0F, 0.0F, true);

		RightArm = new ModelRenderer(this);
		RightArm.setPos(-4.0F, -22.0F, 0.0F);
		Torso.addChild(RightArm);
		RightArm.texOffs(22, 18).addBox(-3.0F, -1.8F, -1.5F, 3.0F, 11.0F, 3.0F, 0.0F, false);

		Head = new ModelRenderer(this);
		Head.setPos(0.0F, -24.0F, 0.0F);
		Torso.addChild(Head);
		Head.texOffs(34, 25).addBox(-2.0F, -3.0F, -5.0F, 4.0F, 3.0F, 1.0F, 0.0F, true);
		Head.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, true);

		RightAntenna_r1 = new ModelRenderer(this);
		RightAntenna_r1.setPos(-1.5F, -7.4F, -2.0F);
		Head.addChild(RightAntenna_r1);
		setRotationAngle(RightAntenna_r1, -1.0472F, 0.0F, -0.0873F);
		RightAntenna_r1.texOffs(60, 22).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, false);

		LeftAntenna_r1 = new ModelRenderer(this);
		LeftAntenna_r1.setPos(1.5F, -7.4F, -2.0F);
		Head.addChild(LeftAntenna_r1);
		setRotationAngle(LeftAntenna_r1, -1.0472F, 0.0F, 0.0873F);
		LeftAntenna_r1.texOffs(60, 22).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, 0.0F, true);

		LeftEar = new ModelRenderer(this);
		LeftEar.setPos(3.3F, -6.0F, 2.0F);
		Head.addChild(LeftEar);
		setRotationAngle(LeftEar, 0.5236F, 0.0698F, 0.0698F);
		LeftEar.texOffs(44, 14).addBox(-0.5625F, -2.1863F, 0.065F, 1.0F, 4.0F, 7.0F, 0.0F, true);
		LeftEar.texOffs(36, 19).addBox(-0.5625F, -2.1863F, 7.065F, 1.0F, 3.0F, 3.0F, 0.0F, true);

		RightEar = new ModelRenderer(this);
		RightEar.setPos(-3.3F, -6.0F, 2.0F);
		Head.addChild(RightEar);
		setRotationAngle(RightEar, 0.5236F, -0.0698F, -0.0698F);
		RightEar.texOffs(44, 14).addBox(-0.4375F, -2.1863F, 0.065F, 1.0F, 4.0F, 7.0F, 0.0F, false);
		RightEar.texOffs(36, 19).addBox(-0.4375F, -2.1863F, 7.065F, 1.0F, 3.0F, 3.0F, 0.0F, false);
	}
	
	/**
	 * Rotate the head with the given axes in radians.
	 * @param x The x rotation
	 * @param y The y rotation
	 * @param z The z rotation
	 */
	private void setHeadRotation(final float x, final float y, final float z) {
		setRotationAngle(Head, x, y, z);
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
		setRotationAngle(LLeg, lx, ly, lz);
		setRotationAngle(RLeg, rx, ry, rz);
	}

	@Override
	public void setupAnim(final @Nullable SpiritEntity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYawDegrees, final float headPitchDegrees) {
		setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYawDegrees, headPitchDegrees, null);
	}
	
	public void setRotationAngles(final @Nonnull LivingEntity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYawDegrees, final float headPitchDegrees, final @Nullable PlayerModel<AbstractClientPlayerEntity> model) {
		// Looking
		setHeadRotation(headPitchDegrees / RADIAN_DIVISOR, netHeadYawDegrees / RADIAN_DIVISOR, 0.0f);
		
		// Walking (Legs)
		setLegRotation(MathHelper.cos(limbSwing * 2/3 + PI) * 1.4f * limbSwingAmount, 0.0f, 0.0f, MathHelper.cos(limbSwing * 2/3) * 1.4f * limbSwingAmount, 0.0f, 0.0f);
		
		// Arm Swing...
		// ... While moving:
		RightArm.xRot = MathHelper.cos(limbSwing * 2/3 + PI) * 2.0f * limbSwingAmount * 0.5f;
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

	@Override
	public void renderToBuffer(final MatrixStack matrixStackIn, final IVertexBuilder bufferIn, final int packedLightIn, final int packedOverlayIn, final float red, final float green, final float blue, final float alpha) {
		Torso.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	private void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

	@Override
	public ModelRenderer getHead() {
		return Head;
	}
	
	private void translateAndRotate(ModelRenderer renderer, MatrixStack transformation) {
		transformation.translate((renderer.x / (8.0D / WIDTH_MOD)), 1/8D, 0);
		if (renderer.zRot != 0.0F) {
			transformation.mulPose(Vector3f.ZP.rotation(renderer.zRot));
		}

		if (renderer.yRot != 0.0F) {
			transformation.mulPose(Vector3f.YP.rotation(renderer.yRot));
		}

		if (renderer.xRot != 0.0F) {
			transformation.mulPose(Vector3f.XP.rotation(renderer.xRot));
		}
	}

	@Override
	public void translateToHand(HandSide handSide, MatrixStack transformationMatrix) {
		translateAndRotate(this.getArm(handSide), transformationMatrix);
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
				this.RightArm.yRot = -0.1F + this.Head.yRot;
				this.LeftArm.yRot = 0.1F + this.Head.yRot + 0.4F;
				this.RightArm.xRot = (-(float)Math.PI / 2F) + this.Head.xRot;
				this.LeftArm.xRot = (-(float)Math.PI / 2F) + this.Head.xRot;
				break;
			case CROSSBOW_CHARGE:
				ModelHelper.animateCrossbowCharge(this.RightArm, this.LeftArm, entity, true);
				break;
			case CROSSBOW_HOLD:
				ModelHelper.animateCrossbowHold(this.RightArm, this.LeftArm, this.Head, true);
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
				this.RightArm.yRot = -0.1F + this.Head.yRot - 0.4F;
				this.LeftArm.yRot = 0.1F + this.Head.yRot;
				this.RightArm.xRot = (-(float)Math.PI / 2F) + this.Head.xRot;
				this.LeftArm.xRot = (-(float)Math.PI / 2F) + this.Head.xRot;
				break;
			case CROSSBOW_CHARGE:
				ModelHelper.animateCrossbowCharge(this.RightArm, this.LeftArm, entity, false);
				break;
			case CROSSBOW_HOLD:
				ModelHelper.animateCrossbowHold(this.RightArm, this.LeftArm, this.Head, false);
		}
	}
	
	private void setupAttackAnimation(LivingEntity entity) {
		if (entity.attackAnim > 0.0F) {
			HandSide handside = this.getAttackArm(entity);
			ModelRenderer modelrenderer = this.getArm(handside);
			float f = entity.attackAnim;
			this.Torso.yRot = MathHelper.sin(MathHelper.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
			if (handside == HandSide.LEFT) {
				this.Torso.yRot *= -1.0F;
			}

			/*
			this.RightArm.z = MathHelper.sin(this.Torso.rotateAngleY) * 5.0F;
			this.RightArm.x = -MathHelper.cos(this.Torso.rotateAngleY) * 5.0F;
			this.LeftArm.z = -MathHelper.sin(this.Torso.rotateAngleY) * 5.0F;
			this.LeftArm.x = MathHelper.cos(this.Torso.rotateAngleY) * 5.0F;
			*/
			this.RightArm.yRot += this.Torso.yRot;
			this.LeftArm.yRot += this.Torso.yRot;
			this.LeftArm.xRot += this.Torso.yRot;
			// yes it should
			
			f = 1.0F - entity.attackAnim;
			f = f * f;
			f = f * f;
			f = 1.0F - f;
			float f1 = MathHelper.sin(f * (float)Math.PI);
			float f2 = MathHelper.sin(entity.attackAnim * (float)Math.PI) * -(this.Head.xRot - 0.7F) * 0.75F;
			modelrenderer.xRot = (float)((double)modelrenderer.xRot - ((double)f1 * 1.2D + (double)f2));
			modelrenderer.yRot += this.Torso.yRot * 2.0F;
			modelrenderer.zRot += MathHelper.sin(entity.attackAnim * (float)Math.PI) * -0.4F;
		} else {
			this.Torso.yRot = 0;
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
