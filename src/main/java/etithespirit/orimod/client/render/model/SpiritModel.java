package etithespirit.orimod.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The actual model of a Spirit.
 */
public class SpiritModel extends Model implements ArmedModel, HeadedModel {
	
	/** Spirits will be rendered at this amount of scale on the Y axis. */
	public static final float HEIGHT_MOD = 0.5f;
	
	/** Spirits will be rendered at this amount of scale on the width / depth axes. */
	public static final float WIDTH_MOD = 0.495f;
	
	private final ModelPart head;
	private final ModelPart torso;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart tail;
	
	public SpiritModel() {
		this(createBodyLayer().bakeRoot());
	}
	
	public SpiritModel(ModelPart root) {
		super(RenderType::entityCutout);
		this.torso = root.getChild("Torso");
		this.head = torso.getChild("Head");
		this.tail = torso.getChild("Tail");
		this.leftArm = torso.getChild("LeftArm");
		this.rightArm = torso.getChild("RightArm");
		this.leftLeg = torso.getChild("LLeg");
		this.rightLeg = torso.getChild("RLeg");
	}
	
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 24).mirror().addBox(-3.5F, -16.0F, -2.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(0, 16).mirror().addBox(-3.0F, -21.0F, -1.7F, 6.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(40, 25).mirror().addBox(-4.0F, -24.0F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 24.0F, 0.0F));
		
		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -13.0F, 2.0F));
		
		PartDefinition TailCmp1_r1 = Tail.addOrReplaceChild("TailCmp1_r1", CubeListBuilder.create().texOffs(52, 0).mirror().addBox(-1.0F, -0.2F, -1.1F, 2.0F, 9.0F, 2.0F, new CubeDeformation(-0.05F)).mirror(false), PartPose.offsetAndRotation(0.0F, 5.675F, 5.15F, 1.5708F, 0.0F, 0.0F));
		
		PartDefinition TailCmp0_r1 = Tail.addOrReplaceChild("TailCmp0_r1", CubeListBuilder.create().texOffs(52, 0).mirror().addBox(-1.0F, 0.0F, -2.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
		
		PartDefinition RLeg = Torso.addOrReplaceChild("RLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, -12.0F, 0.0F));
		
		PartDefinition RightLegMid_r1 = RLeg.addOrReplaceChild("RightLegMid_r1", CubeListBuilder.create().texOffs(24, 0).addBox(-1.2F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4494F, 5.2412F, 0.5354F, 1.309F, 0.0262F, 0.0436F));
		
		PartDefinition RightLegHigh_r1 = RLeg.addOrReplaceChild("RightLegHigh_r1", CubeListBuilder.create().texOffs(32, 4).addBox(-2.2F, -4.0F, -1.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, 1.6464F, -2.5718F, -0.5672F, 0.0436F, 0.0F));
		
		PartDefinition RightLegLow_r1 = RLeg.addOrReplaceChild("RightLegLow_r1", CubeListBuilder.create().texOffs(44, 0).addBox(-0.9F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5442F, 8.4613F, 1.2628F, -0.4538F, 0.0436F, 0.0175F));
		
		PartDefinition RightHoof_r1 = RLeg.addOrReplaceChild("RightHoof_r1", CubeListBuilder.create().texOffs(36, 0).addBox(-3.6F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
		
		PartDefinition LLeg = Torso.addOrReplaceChild("LLeg", CubeListBuilder.create(), PartPose.offset(2.0F, -12.0F, 0.0F));
		
		PartDefinition LeftLegMid_r1 = LLeg.addOrReplaceChild("LeftLegMid_r1", CubeListBuilder.create().texOffs(24, 0).mirror().addBox(-1.8F, -2.5F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.4494F, 5.2412F, 0.5354F, 1.309F, -0.0262F, -0.0436F));
		
		PartDefinition LeftLegHigh_r1 = LLeg.addOrReplaceChild("LeftLegHigh_r1", CubeListBuilder.create().texOffs(32, 4).mirror().addBox(-1.8F, -4.0F, -1.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.1F, 1.6464F, -2.5718F, -0.5672F, -0.0436F, 0.0F));
		
		PartDefinition LeftLegLow_r1 = LLeg.addOrReplaceChild("LeftLegLow_r1", CubeListBuilder.create().texOffs(44, 0).mirror().addBox(-1.1F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5442F, 8.4613F, 1.2628F, -0.4538F, -0.0436F, -0.0175F));
		
		PartDefinition LeftHoof_r1 = LLeg.addOrReplaceChild("LeftHoof_r1", CubeListBuilder.create().texOffs(36, 0).mirror().addBox(1.6F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
		
		PartDefinition LeftArm = Torso.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(22, 18).mirror().addBox(0.0F, -1.8F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, -22.0F, 0.0F));
		
		PartDefinition RightArm = Torso.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(22, 18).addBox(-3.0F, -1.8F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -22.0F, 0.0F));
		
		PartDefinition Head = Torso.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(34, 25).mirror().addBox(-2.0F, -3.0F, -5.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(0, 0).mirror().addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, -24.0F, 0.0F));
		
		PartDefinition RightAntenna_r1 = Head.addOrReplaceChild("RightAntenna_r1", CubeListBuilder.create().texOffs(60, 22).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -7.4F, -2.0F, -1.0472F, 0.0F, -0.0873F));
		
		PartDefinition LeftAntenna_r1 = Head.addOrReplaceChild("LeftAntenna_r1", CubeListBuilder.create().texOffs(60, 22).mirror().addBox(-0.5F, -6.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(1.5F, -7.4F, -2.0F, -1.0472F, 0.0F, 0.0873F));
		
		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(44, 14).mirror().addBox(-0.5625F, -2.1863F, 0.065F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(36, 19).mirror().addBox(-0.5625F, -2.1863F, 7.065F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(3.3F, -6.0F, 2.0F, 0.5236F, 0.0698F, 0.0698F));
		
		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(44, 14).addBox(-0.4375F, -2.1863F, 0.065F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
			.texOffs(36, 19).addBox(-0.4375F, -2.1863F, 7.065F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3F, -6.0F, 2.0F, 0.5236F, -0.0698F, -0.0698F));
		
		return LayerDefinition.create(meshdefinition, 70, 32);
	}
	
	/**
	 * Rotate the head with the given axes in radians.
	 * @param x The x rotation
	 * @param y The y rotation
	 * @param z The z rotation
	 */
	private void setHeadRotation(final float x, final float y, final float z) {
		setRotationAngle(head, x, y, z);
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
	
	@Override
	public void renderToBuffer(final PoseStack matrixStackIn, final VertexConsumer bufferIn, final int packedLightIn, final int packedOverlayIn, final float red, final float green, final float blue, final float alpha) {
		torso.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	private void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
	@Override
	public ModelPart getHead() {
		return head;
	}
	
	private void translateAndRotate(ModelPart renderer, PoseStack transformation) {
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
	public void translateToHand(HumanoidArm handSide, PoseStack transformationMatrix) {
		translateAndRotate(this.getArm(handSide), transformationMatrix);
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
				this.rightArm.yRot = -0.1F + this.head.yRot;
				this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
				this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
				this.leftArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
			}
			case SPYGLASS -> {
				// TODO: Spyglass angles!
			}
			case CROSSBOW_CHARGE -> AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true);
			case CROSSBOW_HOLD -> AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
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
				this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
				this.leftArm.yRot = 0.1F + this.head.yRot;
				this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
				this.leftArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
			}
			case SPYGLASS -> {
				// TODO: Spyglass angles!
			}
			case CROSSBOW_CHARGE -> AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, false);
			case CROSSBOW_HOLD -> AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
		}
	}
	
	private void setupAttackAnimation(LivingEntity entity) {
		if (entity.attackAnim > 0.0F) {
			HumanoidArm handSide = this.getAttackArm(entity);
			ModelPart armPart = this.getArm(handSide);
			float f = entity.attackAnim;
			this.torso.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
			if (handSide == HumanoidArm.LEFT) {
				this.torso.yRot *= -1.0F;
			}
			
			this.rightArm.yRot += this.torso.yRot;
			this.leftArm.yRot += this.torso.yRot;
			
			//noinspection SuspiciousNameCombination
			this.leftArm.xRot += this.torso.yRot;
			
			f = 1.0F - entity.attackAnim;
			f = f * f;
			f = f * f;
			f = 1.0F - f;
			float f1 = Mth.sin(f * (float)Math.PI);
			float f2 = Mth.sin(entity.attackAnim * (float)Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
			armPart.xRot = (float)((double)armPart.xRot - ((double)f1 * 1.2D + (double)f2));
			armPart.yRot += this.torso.yRot * 2.0F;
			armPart.zRot += Mth.sin(entity.attackAnim * (float)Math.PI) * -0.4F;
		} else {
			this.torso.yRot = 0;
		}
	}
	
	protected ModelPart getArm(HumanoidArm handSide) {
		return handSide == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
	}
	
	protected HumanoidArm getAttackArm(LivingEntity spirit) {
		HumanoidArm handSide = spirit.getMainArm();
		return spirit.swingingArm == InteractionHand.MAIN_HAND ? handSide : handSide.getOpposite();
	}
}
