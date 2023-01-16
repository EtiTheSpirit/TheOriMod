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

/**
 * Renders the Spirit Arrow by constructing its model and mapping the texture to the model. Closely related to the default arrow renderer.
 * @param <T>
 */
public class SpiritArrowRenderer<T extends SpiritArrow> extends ArrowRenderer<T> {
	public SpiritArrowRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}
	
	@Override
	public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack renderMtxStack, MultiBufferSource pBuffer, int pPackedLight) {
		pPackedLight = GeneralUtils.FULL_BRIGHT_LIGHT;
		renderMtxStack.pushPose();
		renderMtxStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
		renderMtxStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())));
		
		// TODO: Name these
		float f = 0.0F;
		float f1 = 0.5F;
		float f3 = 0.15625F;
		float f7 = 0.3125F;
		float f8 = 0.05625F;
		float f9 = (float)pEntity.shakeTime - pPartialTicks;
		if (f9 > 0.0F) {
			float f10 = -Mth.sin(f9 * 3.0F) * f9;
			renderMtxStack.mulPose(Vector3f.ZP.rotationDegrees(f10));
		}
		
		renderMtxStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
		renderMtxStack.scale(f8, f8, f8);
		renderMtxStack.translate(-4.0D, 0.0D, 0.0D);
		VertexConsumer renderer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));
		PoseStack.Pose latestPose = renderMtxStack.last();
		Matrix4f transform = latestPose.pose();
		Matrix3f normalMtx = latestPose.normal();
		this.vertex(transform, normalMtx, renderer, -7, -2, -2, f, f3, -1, 0, 0, pPackedLight);
		this.vertex(transform, normalMtx, renderer, -7, -2, 2, f3, f3, -1, 0, 0, pPackedLight);
		this.vertex(transform, normalMtx, renderer, -7, 2, 2, f3, f7, -1, 0, 0, pPackedLight);
		this.vertex(transform, normalMtx, renderer, -7, 2, -2, f, f7, -1, 0, 0, pPackedLight);
		this.vertex(transform, normalMtx, renderer, -7, 2, -2, f, f3, 1, 0, 0, pPackedLight);
		this.vertex(transform, normalMtx, renderer, -7, 2, 2, f3, f3, 1, 0, 0, pPackedLight);
		this.vertex(transform, normalMtx, renderer, -7, -2, 2, f3, f7, 1, 0, 0, pPackedLight);
		this.vertex(transform, normalMtx, renderer, -7, -2, -2, f, f7, 1, 0, 0, pPackedLight);
		
		for(int j = 0; j < 4; ++j) {
			renderMtxStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
			this.vertex(transform, normalMtx, renderer, -8, -2, 0, f, f, 0, 1, 0, pPackedLight);
			this.vertex(transform, normalMtx, renderer, 8, -2, 0, f1, f, 0, 1, 0, pPackedLight);
			this.vertex(transform, normalMtx, renderer, 8, 2, 0, f1, f3, 0, 1, 0, pPackedLight);
			this.vertex(transform, normalMtx, renderer, -8, 2, 0, f, f3, 0, 1, 0, pPackedLight);
		}
		
		renderMtxStack.popPose();
		super.render(pEntity, pEntityYaw, pPartialTicks, renderMtxStack, pBuffer, pPackedLight);
	}
	
	@Override
	public ResourceLocation getTextureLocation(@NotNull T pEntity) {
		return RSRC;
	}
	
	private static final ResourceLocation RSRC = OriMod.rsrc("textures/entity/projectile/spirit_arrow.png");
}
