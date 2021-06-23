package etithespirit.etimod.client.render.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.HashMap;

@SuppressWarnings("unused")
public class LumoWandModel extends Model {
	private final ModelRenderer handle;
	private final ModelRenderer wandcore;
	
	private final ModelRenderer[] rotatingParticles;
	private final HashMap<ModelRenderer, float[]> originalAngles = new HashMap<>();
	
	private float totalTimeExisted = 0;
	
	public LumoWandModel() {
		super(RenderType::entityDecal);
		texWidth = 16;
		texHeight = 16;
		
		handle = new ModelRenderer(this);
		handle.setPos(0.0F, 0.0F, 0.0F);
		handle.texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		handle.texOffs(0, 3).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.1F, false);
		
		wandcore = new ModelRenderer(this);
		wandcore.setPos(0.0F, 0.0F, 0.0F);
		wandcore.texOffs(4, 0).addBox(-1.0F, -6.0F, 0.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		wandcore.texOffs(4, 4).addBox(-1.0F, -7.0F, 0.0F, 1.0F, 2.0F, 1.0F, -0.2F, false);
		
		ModelRenderer a = new ModelRenderer(this);
		ModelRenderer b = new ModelRenderer(this);
		ModelRenderer c = new ModelRenderer(this);
		a.setPos(0.5F, 5.5F, 0.5F);
		a.texOffs(8, 0).addBox(-1.0F, -5.0F, -0.125F, 1.0F, 1.0F, 1.0F, 0.1F, false);
		
		b.setPos(0.5F, 4.5F, 0.5F);
		b.texOffs(8, 2).addBox(0.0F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.1F, false);
		
		c.setPos(0.5F, 3.5F, 0.5F);
		c.texOffs(8, 4).addBox(-1.0F, -2.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.1F, false);
		
		wandcore.addChild(a);
		wandcore.addChild(b);
		wandcore.addChild(c);
		
		rotatingParticles = new ModelRenderer[] {a, b, c};
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
	
	@Override
	public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		totalTimeExisted += 0.2;
		for (ModelRenderer rotatingParticle : rotatingParticles) {
			setRotationOffset(rotatingParticle, 0, totalTimeExisted / 52, 0);
		}
		this.handle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.wandcore.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
	
	public void setRotationOffset(ModelRenderer modelRenderer, double x, double y, double z) {
		if (!originalAngles.containsKey(modelRenderer)) {
			originalAngles.put(modelRenderer, getRotationAngle(modelRenderer));
		}
		
		float[] originals = originalAngles.get(modelRenderer);
		setRotationAngle(modelRenderer, (float)(x + originals[0]), (float)(y + originals[1]), (float)(z + originals[2]));
	}
}