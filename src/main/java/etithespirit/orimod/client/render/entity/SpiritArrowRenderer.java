package etithespirit.orimod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.combat.projectile.SpiritArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SpiritArrowRenderer<T extends SpiritArrow> extends ArrowRenderer<T> {
	public SpiritArrowRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}
	
	public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
		pPackedLight = GeneralUtils.FULL_BRIGHT_LIGHT;
		pMatrixStack.pushPose();
		pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
		pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())));
		float f = 0.0F;
		float f1 = 0.5F;
		float f3 = 0.15625F;
		float f7 = 0.3125F;
		float f8 = 0.05625F;
		float f9 = (float)pEntity.shakeTime - pPartialTicks;
		if (f9 > 0.0F) {
			float f10 = -Mth.sin(f9 * 3.0F) * f9;
			pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(f10));
		}
		
		pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
		pMatrixStack.scale(f8, f8, f8);
		pMatrixStack.translate(-4.0D, 0.0D, 0.0D);
		VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));
		PoseStack.Pose posestack$pose = pMatrixStack.last();
		Matrix4f matrix4f = posestack$pose.pose();
		Matrix3f matrix3f = posestack$pose.normal();
		this.vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, -2, f, f3, -1, 0, 0, pPackedLight);
		this.vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, 2, f3, f3, -1, 0, 0, pPackedLight);
		this.vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, 2, f3, f7, -1, 0, 0, pPackedLight);
		this.vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, -2, f, f7, -1, 0, 0, pPackedLight);
		this.vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, -2, f, f3, 1, 0, 0, pPackedLight);
		this.vertex(matrix4f, matrix3f, vertexconsumer, -7, 2, 2, f3, f3, 1, 0, 0, pPackedLight);
		this.vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, 2, f3, f7, 1, 0, 0, pPackedLight);
		this.vertex(matrix4f, matrix3f, vertexconsumer, -7, -2, -2, f, f7, 1, 0, 0, pPackedLight);
		
		for(int j = 0; j < 4; ++j) {
			pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
			this.vertex(matrix4f, matrix3f, vertexconsumer, -8, -2, 0, f, f, 0, 1, 0, pPackedLight);
			this.vertex(matrix4f, matrix3f, vertexconsumer, 8, -2, 0, f1, f, 0, 1, 0, pPackedLight);
			this.vertex(matrix4f, matrix3f, vertexconsumer, 8, 2, 0, f1, f3, 0, 1, 0, pPackedLight);
			this.vertex(matrix4f, matrix3f, vertexconsumer, -8, 2, 0, f, f3, 0, 1, 0, pPackedLight);
		}
		
		pMatrixStack.popPose();
		super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
	}
	
	@Override
	public ResourceLocation getTextureLocation(@NotNull T pEntity) {
		return RSRC;
	}
	
	private static final ResourceLocation RSRC = new ResourceLocation(OriMod.MODID, "textures/entity/projectile/spirit_arrow.png");
}
