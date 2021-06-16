package etithespirit.etimod.client.render.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import etithespirit.etimod.client.render.debug.RenderUtil;
import etithespirit.etimod.common.tile.AbstractLightEnergyAnchor;
import etithespirit.etimod.common.tile.light.ILightEnergyConduit;
import etithespirit.etimod.connection.Assembly;
import etithespirit.etimod.connection.ConnectionHelper;
import etithespirit.etimod.util.EtiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

import static etithespirit.etimod.client.render.debug.RenderUtil.TOP_LINES;

/**
 * A utility for {@link AbstractLightEnergyAnchor} that visualizes all connections.
 */
public class LightTileDebugRenderer extends TileEntityRenderer<AbstractLightEnergyAnchor> {
	public LightTileDebugRenderer(TileEntityRendererDispatcher p_i226006_1_) {
		super(p_i226006_1_);
	}
	
	@Override
	public void render(AbstractLightEnergyAnchor tile, float partialTicks, MatrixStack mtx, IRenderTypeBuffer bufferProvider, int p_225616_5_, int p_225616_6_) {
		if (!EtiUtils.isPlayerViewingDebugMenu()) return;
		
		IVertexBuilder vtxBuilder = bufferProvider.getBuffer(TOP_LINES);
		
		Vector3i pos = tile.getBlockPos();
		// renderName("ASSEMBLY: " + Assembly.getAssemblyFor(tile).id, mtx, Minecraft.getInstance().gameRenderer.getMainCamera(), bufferProvider);
		mtx.translate(-pos.getX(), -pos.getY(), -pos.getZ());
		
		
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
	
	private void renderName(String text, MatrixStack mtx, ActiveRenderInfo renderInfo, IRenderTypeBuffer buffer) {
		StringTextComponent component = new StringTextComponent(text);
		float height = 1f;
		mtx.pushPose();
		mtx.translate(0, 1, 0);
		mtx.mulPose(renderInfo.rotation());
		mtx.scale(-0.025F, -0.025F, 0.025F);
		Matrix4f matrix4f = mtx.last().pose();
		float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
		int j = (int)(f1 * 255.0F) << 24;
		FontRenderer fontrenderer = this.renderer.font;
		float f2 = (float)(-fontrenderer.width(component) / 2);
		fontrenderer.drawInBatch(component, f2, 0, 0x20_FFFFFF, false, matrix4f, buffer, false, j, 0xFFFFFFFF);
		
		mtx.popPose();
	}
	
	@Override
	public boolean shouldRenderOffScreen(AbstractLightEnergyAnchor tile) {
		return true;
	}
}
