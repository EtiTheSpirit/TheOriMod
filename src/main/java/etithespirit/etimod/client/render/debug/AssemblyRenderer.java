package etithespirit.etimod.client.render.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import etithespirit.etimod.common.tile.AbstractLightEnergyAnchor;
import etithespirit.etimod.common.tile.light.ILightEnergyConduit;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.connection.ConnectionHelper;
import etithespirit.etimod.util.EtiUtils;
import etithespirit.etimod.util.collection.ConcurrentBag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import static etithespirit.etimod.client.render.debug.RenderUtil.TOP_LINES;

/**
 * A utility class designed to render {@link etithespirit.etimod.connection.Assembly Assemblies}
 */
@Deprecated
public final class AssemblyRenderer {
	
	private AssemblyRenderer() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static final ConcurrentBag<Assembly> ASSEMBLIES = new ConcurrentBag<>();
	
	public static void registerAssemblyForRender(Assembly assembly) {
		if (!ASSEMBLIES.contains(assembly)) ASSEMBLIES.add(assembly);
	}
	
	public static void unregisterAssemblyForRender(Assembly assembly) {
		if (ASSEMBLIES.contains(assembly)) ASSEMBLIES.remove(assembly);
	}
	
	public static void clearAll() {
		ASSEMBLIES.clear();
	}
	
	public static void onRender(RenderWorldLastEvent evt) {
		if (!EtiUtils.isPlayerViewingDebugMenu()) {
			return;
		}
		// ^ Only render in debug. That way it at least makes some sense. Open debug menu to see debug renders.
		// For viewers: identical to Minecraft.getInstance().options.renderDebug;
		
		IVertexBuilder vtxBuilder = evt.getContext().renderBuffers.bufferSource().getBuffer(TOP_LINES);
		// renderBuffers is AT'd
		
		MatrixStack mtx = evt.getMatrixStack();
		ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
		Vector3d camPos = renderInfo.getPosition();
		
		for (Assembly assembly : ASSEMBLIES) {
			mtx.pushPose();
			{
				
				//projection.invert();
				//mtx.last().pose().multiply(projection);
				
				AbstractLightEnergyAnchor tile = assembly.getCore();
				// In comparison to the original image, this is the white block.
				
				// This was done for the TE
				Vector3i pos = tile.getBlockPos();
				
				Matrix4f projection = evt.getProjectionMatrix();
				mtx.last().pose().multiply(projection);
				
				double cameraX = pos.getX() - camPos.x;
				double cameraY = pos.getY() - camPos.y;
				double cameraZ = camPos.z - pos.getZ();
				
				mtx.translate(cameraX, cameraY, cameraZ);
				mtx.translate(-pos.getX(), -pos.getY(), pos.getZ());
				
				mtx.scale(1, 1, -1);
				
				// mtx.mulPose(new Quaternion(renderInfo.getXRot(), renderInfo.getYRot(), 0, true));
				
				//mtx.scale(0.5f, 0.5f, 0.5f);
				
				//mtx.translate(pos.getX(), pos.getY(), pos.getZ());
				//mtx.translate(0, -5, 0);
				
				
				
				/*
				for (Line line : assembly.getLines()) {
					ILightEnergyConduit previous = null;
					for (ILightEnergyConduit conduit : line.getSegments()) {
						BlockPos start = previous != null ? previous.getBlockPos() : tile.getBlockPos();
						RenderUtil.drawLine(vtxBuilder, mtx, 0xFF_FF0000, start, conduit.getBlockPos());
						previous = conduit;
					}
				}
				*/
				ILightEnergyConduit previous = null;
				for (ILightEnergyConduit conduit : tile.getConnectedConduits(true)) {
					BlockPos start = previous != null ? previous.getBlockPos() : tile.getBlockPos();
					int color = 0xFF_FF00FF;
					if (ConnectionHelper.areNeighbors(start, conduit.getBlockPos())) {
						color = 0xFF_FF0000;
					}
					RenderUtil.drawLine(vtxBuilder, mtx, color, start, conduit.getBlockPos());
					previous = conduit;
				}
			}
			mtx.popPose();
		}
	}
	
}
