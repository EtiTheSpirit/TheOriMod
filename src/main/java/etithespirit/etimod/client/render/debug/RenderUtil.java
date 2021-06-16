package etithespirit.etimod.client.render.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

import java.util.OptionalDouble;

import static net.minecraft.client.renderer.RenderState.*;
import static net.minecraft.client.renderer.RenderState.COLOR_DEPTH_WRITE;
import static net.minecraft.client.renderer.RenderType.create;
import static net.minecraft.util.ColorHelper.PackedColor.red;
import static net.minecraft.util.ColorHelper.PackedColor.green;
import static net.minecraft.util.ColorHelper.PackedColor.blue;
import static net.minecraft.util.ColorHelper.PackedColor.alpha;

public final class RenderUtil {

	// Static class def.
	private RenderUtil() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * Identical to {@link RenderType#LINES} but this always draws on top.
	 */
	// A lot of these are AT'd
	public static final RenderType.Type TOP_LINES = create("lines_top", DefaultVertexFormats.POSITION_COLOR, 1, 256, RenderType.State.builder().setLineState(new RenderState.LineState(OptionalDouble.empty())).setDepthTestState(NO_DEPTH_TEST).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
	
	/**
	 * Politely borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java#L150">Compact Crafting</a>.
	 * Provides a means of rendering colored lines.
	 * @param vtxBuilder The vertex builder.
	 * @param mtx The matrix that this vertex is affected by.
	 * @param argb The color of the vertex.
	 * @param position The location of the vertex.
	 */
	public static void addColoredVertex(IVertexBuilder vtxBuilder, MatrixStack mtx, int argb, BlockPos position) {
		addColoredVertex(vtxBuilder, mtx, argb, Vector3d.atCenterOf(position));
	}
	
	/**
	 * Politely borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java#L150">Compact Crafting</a>.
	 * Provides a means of rendering colored lines.
	 * @param vtxBuilder The vertex builder.
	 * @param mtx The matrix that this vertex is affected by.
	 * @param argb The color of the vertex.
	 * @param position The location of the vertex.
	 */
	public static void addColoredVertex(IVertexBuilder vtxBuilder, MatrixStack mtx, int argb, Vector3d position) {
		float x = (float)position.x();
		float y = (float)position.y();
		float z = (float)position.z();
		Matrix4f pose = mtx.last().pose();
		vtxBuilder
			.vertex(pose, x, y, z)
			.color(red(argb), green(argb), blue(argb), alpha(argb))
			.uv2(0, 0)
			.normal(1, 0, 0)
			.endVertex();
	}
	
	/**
	 * Draw a line between two block positions.
	 * @param vtxBuilder The vertex builder.
	 * @param mtx The matrix that this vertex is affected by.
	 * @param argb The color of the line.
	 * @param blockStart The starting block, one of two points on the line.
	 * @param blockEnd The ending block, the other of two points on the line.
	 */
	public static void drawLine(IVertexBuilder vtxBuilder, MatrixStack mtx, int argb, BlockPos blockStart, BlockPos blockEnd) {
		addColoredVertex(vtxBuilder, mtx, argb, blockStart);
		addColoredVertex(vtxBuilder, mtx, argb, blockEnd);
	}
	
}
