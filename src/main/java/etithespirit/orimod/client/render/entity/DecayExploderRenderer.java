package etithespirit.orimod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.client.render.model.DecayExploderModel;
import etithespirit.orimod.common.entity.DecayExploder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DecayExploderRenderer extends LivingEntityRenderer<DecayExploder, DecayExploderModel> {
	
	private static final ResourceLocation TEXTURE = OriMod.rsrc("textures/entity/decay/decay_exploder.png");
	private static final DecayExploderModel MODEL = new DecayExploderModel();
	private static final float SHADOW_SIZE = 0.4f;
	
	public DecayExploderRenderer(EntityRendererProvider.Context pContext) {
		super(pContext, MODEL, SHADOW_SIZE);
	}
	
	@Override
	public void render(DecayExploder pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
		pMatrixStack.pushPose();
		
		Minecraft minecraft = Minecraft.getInstance();
		boolean isVisible = this.isBodyVisible(pEntity);
		boolean isTranslucent = !isVisible && !pEntity.isInvisibleTo(Objects.requireNonNull(minecraft.player));
		boolean isGlowing = minecraft.shouldEntityAppearGlowing(pEntity);
		RenderType rType = getRenderType(pEntity, isVisible, isTranslucent, isGlowing);
		rType = rType == null ? model.renderType(getTextureLocation(pEntity)) : rType;
		
		model.setupAnim(pEntity, 0, 0, pEntity.tickCount + pPartialTicks, 0, 0);
		model.renderToBuffer(pMatrixStack, pBuffer.getBuffer(rType), pPackedLight, GeneralUtils.NO_OVERLAY, 1, 1, 1, 1);
		
		pMatrixStack.popPose();
	}
	
	@Override
	public ResourceLocation getTextureLocation(DecayExploder pEntity) {
		return TEXTURE;
	}
}
