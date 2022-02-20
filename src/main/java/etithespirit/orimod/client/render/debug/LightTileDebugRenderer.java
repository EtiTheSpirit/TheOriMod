package etithespirit.orimod.client.render.debug;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.orimod.lighttech.Assembly;
import etithespirit.orimod.lighttech.ConnectionHelper;
import etithespirit.orimod.lighttech.Line;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;

import java.util.ArrayList;

import static etithespirit.orimod.client.render.debug.RenderUtil.TOP_LINES;

/**
 * A utility for {@link AbstractLightEnergyHub} that visualizes all connections.
 */
public class LightTileDebugRenderer implements BlockEntityRenderer<AbstractLightEnergyHub> {
	
	private static final ArrayList<Assembly> alreadyRendered = new ArrayList<>();
	
	@Override
	public void render(AbstractLightEnergyHub tile, float partialTicks, PoseStack mtx, MultiBufferSource bufferProvider, int packedLightIn, int packedOverlayIn) {
		if (!GeneralUtils.isPlayerViewingDebugMenu()) return;
		
		
		Vec3i pos = tile.getBlockPos();
		Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
		Assembly asm = Assembly.getAssemblyFor(tile);
		int asmId = asm._id;
		
		boolean alreadyReg = alreadyRendered.contains(asm);
		
		mtx.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		RenderUtil.renderText((alreadyReg ? "ASM: " : "ASM CORE: ") + asmId, mtx, cam, bufferProvider, tile.getBlockPos().below(), 0xFF_FFFFFF, true);
		
		if (alreadyReg) return;
		alreadyRendered.add(asm);
		
		int lineIdx = 0;
		for (Line line : asm.getLines()) {
			Vec3 center = line.getCenter();
			//center = center.subtract(-pos.getX(), -pos.getY(), -pos.getZ());
			RenderUtil.renderText(asmId + ":" + lineIdx, mtx, cam, bufferProvider, center, 0xFF_FFFFFF, true);
			lineIdx++;
		}
		
		VertexConsumer vtxBuilder = bufferProvider.getBuffer(TOP_LINES);
		int idx = 0;
		for (Line line : asm.getLines()) {
			AbstractLightEnergyLink previous = null;
			for (AbstractLightEnergyLink link : line.getSegments()) {
				BlockPos start = previous != null ? previous.getBlockPos() : tile.getBlockPos();
				//int color = 0xFF_FF00FF;
				int color = 0xFF_FF0000;
				if (!ConnectionHelper.areNeighbors(start, link.getBlockPos())) {
					previous = link;
					continue;
					//color = 0xFF_FF0000;
				}
				RenderUtil.drawLine(vtxBuilder, mtx, color, start, link.getBlockPos());
				previous = link;
			}
			
			RenderUtil.drawCubeFrame(line.getBounds().deflate(0.1), mtx, vtxBuilder, RenderUtil.randomIndexedRGB(idx), 48);
			idx++;
		}
	}
	
	@Override
	public boolean shouldRenderOffScreen(AbstractLightEnergyHub tile) {
		return true;
	}
	
	// Must be registered (of course). Cleans up the already rendered list.
	@SuppressWarnings("unused")
	public static void onWorldFinishedRendering(RenderLevelLastEvent evt) {
		alreadyRendered.clear();
	}
}
