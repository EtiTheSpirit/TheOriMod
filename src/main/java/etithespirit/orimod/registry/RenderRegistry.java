package etithespirit.orimod.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.client.render.debug.LightHubDebugRenderer;
import etithespirit.orimod.client.render.debug.RenderUtil;
import etithespirit.orimod.common.tile.light.TileEntityLightCapacitor;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public final class RenderRegistry {
	
	public static void registerAll() {
	
	}
	
	public static void registerBERenderers(EntityRenderersEvent.RegisterRenderers registerEvt) {
		registerEvt.registerBlockEntityRenderer(TileEntityRegistry.LIGHT_CAPACITOR.get(), LightHubDebugRenderer::new);
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
