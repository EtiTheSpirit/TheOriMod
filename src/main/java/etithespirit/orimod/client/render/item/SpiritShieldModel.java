package etithespirit.orimod.client.render.item;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import etithespirit.orimod.util.TypeErasure;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;

public class SpiritShieldModel extends Model {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	private final ModelPart bone;
	
	
	private final ModelPart[] rotatingParticles;
	private final HashMap<ModelPart, float[]> originalAngles = new HashMap<>();
	
	private float totalTimeExisted = 0;
	
	public SpiritShieldModel() {
		this(createBodyLayer().bakeRoot());
	}
	
	public SpiritShieldModel(ModelPart root) {
		super(RenderType::entityTranslucentCull);
		this.bone = root.getChild("bone");
		ModelPart rotatingParticlesCtr = bone.getChild("RotatingParticles");
		
		
		rotatingParticles = TypeErasure.translateArray(rotatingParticlesCtr.getAllParts().toArray(), ModelPart.class);
	}
	
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(22, 3).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
			.texOffs(0, 0).addBox(-3.0F, -5.0F, -4.0F, 6.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(14, 3).addBox(-4.0F, -3.0F, -4.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(14, 3).mirror().addBox(3.0F, -3.0F, -4.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(18, 3).mirror().addBox(4.0F, -2.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(18, 3).addBox(-5.0F, 0.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(18, 6).addBox(-1.0F, 7.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
			.texOffs(30, 3).mirror().addBox(-1.0F, -11.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
			.texOffs(14, 0).addBox(-1.0F, 5.0F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
		
		PartDefinition TopSpike0_r1 = bone.addOrReplaceChild("TopSpike0_r1", CubeListBuilder.create().texOffs(18, 6).mirror().addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.5F, -8.5F, -3.5F, 3.1416F, 0.0F, 0.0F));
		
		PartDefinition BottomSpike1_r1 = bone.addOrReplaceChild("BottomSpike1_r1", CubeListBuilder.create().texOffs(30, 3).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 9.5F, -3.5F, 3.1416F, 0.0F, 0.0F));
		
		PartDefinition TopBrick_r1 = bone.addOrReplaceChild("TopBrick_r1", CubeListBuilder.create().texOffs(14, 0).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -3.5F, 3.1416F, 3.1416F, 0.0F));
		
		PartDefinition RotatingParticles = bone.addOrReplaceChild("RotatingParticles", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		PartDefinition RParticle7_r1 = RotatingParticles.addOrReplaceChild("RParticle7_r1", CubeListBuilder.create().texOffs(20, 0).addBox(-8.0F, 0.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, -2.9671F));
		
		PartDefinition RParticle6_r1 = RotatingParticles.addOrReplaceChild("RParticle6_r1", CubeListBuilder.create().texOffs(20, 0).addBox(-8.0F, 0.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, -0.6109F));
		
		PartDefinition RParticle5_r1 = RotatingParticles.addOrReplaceChild("RParticle5_r1", CubeListBuilder.create().texOffs(20, 0).addBox(-8.0F, -6.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.2182F));
		
		PartDefinition RParticle4_r1 = RotatingParticles.addOrReplaceChild("RParticle4_r1", CubeListBuilder.create().texOffs(20, 0).addBox(5.0F, -8.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, -0.2182F));
		
		PartDefinition RParticle3_r1 = RotatingParticles.addOrReplaceChild("RParticle3_r1", CubeListBuilder.create().texOffs(20, 0).addBox(-6.0F, 9.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, -0.2182F));
		
		PartDefinition RParticle2_r1 = RotatingParticles.addOrReplaceChild("RParticle2_r1", CubeListBuilder.create().texOffs(22, 3).addBox(4.0F, 5.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 2.4435F));
		
		PartDefinition RParticle1_r1 = RotatingParticles.addOrReplaceChild("RParticle1_r1", CubeListBuilder.create().texOffs(22, 3).addBox(4.0F, 5.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, -0.2182F));
		
		PartDefinition RParticle0_r1 = RotatingParticles.addOrReplaceChild("RParticle0_r1", CubeListBuilder.create().texOffs(22, 3).addBox(6.0F, -9.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0873F, 0.0F, 0.3054F));
		
		return LayerDefinition.create(meshdefinition, 36, 12);
	}
	
	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		totalTimeExisted += 0.2;
		for (ModelPart rotatingParticle : rotatingParticles) {
			setRotationOffset(rotatingParticle, Math.sin(totalTimeExisted / 12D) / 6, Math.sin(2 + (totalTimeExisted / 24D)) / 4, Math.cos(totalTimeExisted / 10D) / 5);
		}
		this.bone.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	public float[] getRotationAngle(ModelPart modelRenderer) {
		return new float[] {
			modelRenderer.xRot,
			modelRenderer.yRot,
			modelRenderer.zRot
		};
	}
	
	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
	public void setRotationOffset(ModelPart modelRenderer, double x, double y, double z) {
		if (!originalAngles.containsKey(modelRenderer)) {
			originalAngles.put(modelRenderer, getRotationAngle(modelRenderer));
		}
		
		float[] originals = originalAngles.get(modelRenderer);
		setRotationAngle(modelRenderer, (float)(x + originals[0]), (float)(y + originals[1]), (float)(z + originals[2]));
	}
}