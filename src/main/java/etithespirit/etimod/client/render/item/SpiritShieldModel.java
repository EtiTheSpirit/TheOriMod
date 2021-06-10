package etithespirit.etimod.client.render.item;

import java.util.HashMap;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SpiritShieldModel extends Model {
	private final ModelRenderer Root;
	private final ModelRenderer TopSpike0_r1;
	private final ModelRenderer BottomSpike1_r1;
	private final ModelRenderer TopBrick_r1;
	private final ModelRenderer RotatingParticles;
	private final ModelRenderer RParticle7_r1;
	private final ModelRenderer RParticle6_r1;
	private final ModelRenderer RParticle5_r1;
	private final ModelRenderer RParticle4_r1;
	private final ModelRenderer RParticle3_r1;
	private final ModelRenderer RParticle2_r1;
	private final ModelRenderer RParticle1_r1;
	private final ModelRenderer RParticle0_r1;
	
	private final ModelRenderer[] rotatingParticles;
	private final HashMap<ModelRenderer, float[]> originalAngles = new HashMap<ModelRenderer, float[]>();
	
	private float totalTimeExisted = 0;

	public SpiritShieldModel() {
		super(RenderType::entityTranslucentCull);
		texWidth = 36;
		texHeight = 12;

		Root = new ModelRenderer(this);
		Root.setPos(0.0F, 0.0F, 0.0F);
		Root.texOffs(22, 3).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);
		Root.texOffs(0, 0).addBox(-3.0F, -5.0F, -4.0F, 6.0F, 10.0F, 1.0F, 0.0F, false);
		Root.texOffs(14, 3).addBox(-4.0F, -3.0F, -4.0F, 1.0F, 6.0F, 1.0F, 0.0F, false);
		Root.texOffs(14, 3).addBox(3.0F, -3.0F, -4.0F, 1.0F, 6.0F, 1.0F, 0.0F, true);
		Root.texOffs(18, 3).addBox(4.0F, -2.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, true);
		Root.texOffs(18, 3).addBox(-5.0F, 0.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		Root.texOffs(18, 6).addBox(-1.0F, 7.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		Root.texOffs(30, 3).addBox(-1.0F, -11.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F, true);
		Root.texOffs(14, 0).addBox(-1.0F, 5.0F, -4.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

		TopSpike0_r1 = new ModelRenderer(this);
		TopSpike0_r1.setPos(0.5F, -8.5F, -3.5F);
		Root.addChild(TopSpike0_r1);
		setRotationAngle(TopSpike0_r1, 3.1416F, 0.0F, 0.0F);
		TopSpike0_r1.texOffs(18, 6).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, true);

		BottomSpike1_r1 = new ModelRenderer(this);
		BottomSpike1_r1.setPos(0.5F, 9.5F, -3.5F);
		Root.addChild(BottomSpike1_r1);
		setRotationAngle(BottomSpike1_r1, 3.1416F, 0.0F, 0.0F);
		BottomSpike1_r1.texOffs(30, 3).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		TopBrick_r1 = new ModelRenderer(this);
		TopBrick_r1.setPos(0.0F, -6.0F, -3.5F);
		Root.addChild(TopBrick_r1);
		setRotationAngle(TopBrick_r1, 3.1416F, 3.1416F, 0.0F);
		TopBrick_r1.texOffs(14, 0).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, 0.0F, false);

		RotatingParticles = new ModelRenderer(this);
		RotatingParticles.setPos(0.0F, 0.0F, 0.0F);
		Root.addChild(RotatingParticles);
		

		RParticle7_r1 = new ModelRenderer(this);
		RParticle7_r1.setPos(0.0F, 0.0F, 0.0F);
		RotatingParticles.addChild(RParticle7_r1);
		setRotationAngle(RParticle7_r1, 0.2182F, 0.0F, -2.9671F);
		RParticle7_r1.texOffs(20, 0).addBox(-8.0F, 0.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		RParticle6_r1 = new ModelRenderer(this);
		RParticle6_r1.setPos(0.0F, 0.0F, 0.0F);
		RotatingParticles.addChild(RParticle6_r1);
		setRotationAngle(RParticle6_r1, 0.2182F, 0.0F, -0.6109F);
		RParticle6_r1.texOffs(20, 0).addBox(-8.0F, 0.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		RParticle5_r1 = new ModelRenderer(this);
		RParticle5_r1.setPos(0.0F, 0.0F, 0.0F);
		RotatingParticles.addChild(RParticle5_r1);
		setRotationAngle(RParticle5_r1, -0.1309F, 0.0F, 0.2182F);
		RParticle5_r1.texOffs(20, 0).addBox(-8.0F, -6.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		RParticle4_r1 = new ModelRenderer(this);
		RParticle4_r1.setPos(0.0F, 0.0F, 0.0F);
		RotatingParticles.addChild(RParticle4_r1);
		setRotationAngle(RParticle4_r1, -0.1309F, 0.0F, -0.2182F);
		RParticle4_r1.texOffs(20, 0).addBox(5.0F, -8.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		RParticle3_r1 = new ModelRenderer(this);
		RParticle3_r1.setPos(0.0F, 0.0F, 0.0F);
		RotatingParticles.addChild(RParticle3_r1);
		setRotationAngle(RParticle3_r1, 0.2618F, 0.0F, -0.2182F);
		RParticle3_r1.texOffs(20, 0).addBox(-6.0F, 9.0F, -4.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		RParticle2_r1 = new ModelRenderer(this);
		RParticle2_r1.setPos(0.0F, 0.0F, 0.0F);
		RotatingParticles.addChild(RParticle2_r1);
		setRotationAngle(RParticle2_r1, 0.0873F, 0.0F, 2.4435F);
		RParticle2_r1.texOffs(22, 3).addBox(4.0F, 5.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		RParticle1_r1 = new ModelRenderer(this);
		RParticle1_r1.setPos(0.0F, 0.0F, 0.0F);
		RotatingParticles.addChild(RParticle1_r1);
		setRotationAngle(RParticle1_r1, 0.0873F, 0.0F, -0.2182F);
		RParticle1_r1.texOffs(22, 3).addBox(4.0F, 5.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		RParticle0_r1 = new ModelRenderer(this);
		RParticle0_r1.setPos(0.0F, 0.0F, 0.0F);
		RotatingParticles.addChild(RParticle0_r1);
		setRotationAngle(RParticle0_r1, -0.0873F, 0.0F, 0.3054F);
		RParticle0_r1.texOffs(22, 3).addBox(6.0F, -9.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		
		rotatingParticles = new ModelRenderer[] {
			RParticle0_r1,
			RParticle1_r1,
			RParticle2_r1,
			RParticle3_r1,
			RParticle4_r1,
			RParticle5_r1,
			RParticle6_r1,
			RParticle7_r1
		};
	}


	@Override
	public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		totalTimeExisted += 0.2;
		for (int index = 0; index < rotatingParticles.length; index++) {
			setRotationOffset(rotatingParticles[index], Math.sin(totalTimeExisted / 12D) / 6, Math.sin(2 + (totalTimeExisted / 24D)) / 4, Math.cos(totalTimeExisted / 10D) / 5);
		}
		this.Root.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	public float[] getRotationAngle(ModelRenderer modelRenderer) {
		return new float[] {
			modelRenderer.xRot,
			modelRenderer.yRot,
			modelRenderer.zRot
		};
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
	
	public void setRotationOffset(ModelRenderer modelRenderer, double x, double y, double z) {
		if (!originalAngles.containsKey(modelRenderer)) {
			originalAngles.put(modelRenderer, getRotationAngle(modelRenderer));
		}
		
		float[] originals = originalAngles.get(modelRenderer);
		setRotationAngle(modelRenderer, (float)(x + originals[0]), (float)(y + originals[1]), (float)(z + originals[2]));
	}
}
