package etithespirit.etimod.client.render.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import etithespirit.etimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.etimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.connection.ConnectionHelper;
import etithespirit.etimod.connection.Line;
import etithespirit.etimod.util.EtiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;

import static etithespirit.etimod.client.render.debug.RenderUtil.TOP_LINES;
// import static etithespirit.etimod.client.render.debug.RenderUtil.TRANSLUCENT_QUADS;

/**
 * A utility for {@link AbstractLightEnergyHub} that visualizes all connections.
 */
public class LightTileDebugRenderer extends TileEntityRenderer<AbstractLightEnergyHub> {
	public LightTileDebugRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}
	
	private static final ArrayList<Assembly> alreadyRendered = new ArrayList<>();
	
	@Override
	public void render(AbstractLightEnergyHub tile, float partialTicks, MatrixStack mtx, IRenderTypeBuffer bufferProvider, int p_225616_5_, int p_225616_6_) {
		if (!EtiUtils.isPlayerViewingDebugMenu()) return;
		
		
		Vector3i pos = tile.getBlockPos();
		ActiveRenderInfo cam = Minecraft.getInstance().gameRenderer.getMainCamera();
		Assembly asm = Assembly.getAssemblyFor(tile);
		int asmId = asm.debugId;
		
		boolean alreadyReg = alreadyRendered.contains(asm);
		
		mtx.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		RenderUtil.renderText((alreadyReg ? "ASM: " : "ASM CORE: ") + asmId, mtx, cam, bufferProvider, tile.getBlockPos().below(), 0xFF_FFFFFF, true);
		
		if (alreadyReg) return;
		alreadyRendered.add(asm);
		
		int lineIdx = 0;
		for (Line line : asm.getLines()) {
			Vector3d center = line.getCenter();
			//center = center.subtract(-pos.getX(), -pos.getY(), -pos.getZ());
			RenderUtil.renderText(asmId + ":" + lineIdx, mtx, cam, bufferProvider, center, 0xFF_FFFFFF, true);
			lineIdx++;
		}
		
		IVertexBuilder vtxBuilder = bufferProvider.getBuffer(TOP_LINES);
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
			
			RenderUtil.drawCubeFrame(line.getBounds(), mtx, vtxBuilder, RenderUtil.randomIndexedRGB(idx), 48);
			idx++;
		}
	}
	
	@Override
	public boolean shouldRenderOffScreen(AbstractLightEnergyHub tile) {
		return true;
	}
	
	// Must be registered (of course). Cleans up the already rendered list.
	public static void onWorldFinishedRendering(RenderWorldLastEvent evt) {
		alreadyRendered.clear();
	}
}
