package etithespirit.orimod.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.entity.DecayExploder;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.Random;

public class DecayExploderModel extends EntityModel<DecayExploder> {
	// Made with Blockbench 4.5.2
	private final ModelPart bb_main;
	
	public DecayExploderModel() {
		this(createBodyLayer().bakeRoot());
	}

	public DecayExploderModel(ModelPart root) {
		super(RenderType::entityCutout);
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.0F, -3.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition glowyBitAlt_r1 = bb_main.addOrReplaceChild("glowyBitAlt_r1", CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, -3.9F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition legE_r1 = bb_main.addOrReplaceChild("legE_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.75F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition legSE_r1 = bb_main.addOrReplaceChild("legSE_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, -0.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -2.0F, 5.5F, 0.7854F, -0.7854F, 0.0F));

		PartDefinition legS_r1 = bb_main.addOrReplaceChild("legS_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.25F, -0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 7.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition legSW_r1 = bb_main.addOrReplaceChild("legSW_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, -0.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -2.0F, 5.5F, 0.7854F, 0.7854F, 0.0F));

		PartDefinition legW_r1 = bb_main.addOrReplaceChild("legW_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.25F, -0.25F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition legNW_r1 = bb_main.addOrReplaceChild("legNW_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, -0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -2.0F, -5.5F, -0.7854F, -0.7854F, 0.0F));

		PartDefinition legN_r1 = bb_main.addOrReplaceChild("legN_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.25F, -0.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -7.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition legNE_r1 = bb_main.addOrReplaceChild("legNE_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, -0.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -2.0F, -5.5F, -2.3562F, -0.7854F, 3.1416F));

		PartDefinition mainBody_r1 = bb_main.addOrReplaceChild("mainBody_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -3.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.1F, 0.0F, 0.0F, 0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 40, 20);
	}
	
	@Override
	public void setupAnim(DecayExploder entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entity.deathTime > 0) {
			if (entity.startedAnimationAt == 0) {
				entity.startedAnimationAt = ageInTicks;
				entity.lastAnimatedAt = ageInTicks;
			}
			
			int ticksRemaining = DecayExploder.TIME_TO_BLOW - entity.deathTime;
			float deltaTime = ageInTicks - entity.lastAnimatedAt;
			float deltaTimeSinceStart = ageInTicks - entity.startedAnimationAt;
			float deathProgress = Mth.clamp(deltaTimeSinceStart / (float)DecayExploder.TIME_TO_BLOW, 0f, 1f);
			
			entity.trackedAnimatedHeight += (ticksRemaining * deltaTime) / 5f;
			
			RandomSource rng = entity.getLevel().getRandom();
			float ofstX = (rng.nextFloat() - 0.5f) * deathProgress * 4;
			float ofstY = ((rng.nextFloat() - 0.5f) * deathProgress) * 2f; // goal was div 2
			float ofstZ = (rng.nextFloat() - 0.5f) * deathProgress * 4;
			bb_main.setRotation(0, 0, Mth.PI);
			bb_main.setPos(ofstX, ofstY + entity.trackedAnimatedHeight, ofstZ);
			
			entity.lastAnimatedAt = ageInTicks;
		} else {
			bb_main.setRotation(0, 0, Mth.PI);
			bb_main.setPos(0, 0, 0);
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}