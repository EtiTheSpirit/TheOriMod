package etithespirit.orimod.registry;

import etithespirit.orimod.client.render.entity.SpiritArrowRenderer;
import etithespirit.orimod.registry.gameplay.EntityRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;

public final class RenderRegistry {
	
	public static void registerBERenderers(EntityRenderersEvent.RegisterRenderers registerEvt) {
		//registerEvt.registerBlockEntityRenderer(TileEntityRegistry.LIGHT_CAPACITOR.get(), LightHubDebugRenderer::new);
		registerEvt.registerEntityRenderer(EntityRegistry.SPIRIT_ARROW.get(), SpiritArrowRenderer::new);
	}
	
	public static void registerShaders(RegisterShadersEvent evt) {
		/*
		try {
			evt.registerShader(new ShaderInstance(evt.getResourceManager(), new ResourceLocation(OriMod.MODID, "rendertype_lines_top"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> {
				RenderUtil.topLinesShader = shaderInstance;
			});
		} catch (Throwable ignored) { }*/
	}
	
}
