package etithespirit.etimod.client.render.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.Random;

import static etithespirit.etimod.util.EtiUtils.FULL_BRIGHT_LIGHT;
import static net.minecraft.client.renderer.RenderState.*;
import static net.minecraft.client.renderer.RenderState.COLOR_DEPTH_WRITE;
import static net.minecraft.client.renderer.RenderType.create;
import static net.minecraft.util.ColorHelper.PackedColor.red;
import static net.minecraft.util.ColorHelper.PackedColor.green;
import static net.minecraft.util.ColorHelper.PackedColor.blue;
import static net.minecraft.util.ColorHelper.PackedColor.alpha;

@SuppressWarnings("unused")
public final class RenderUtil {

	// Static class def.
	private RenderUtil() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static final Random RNG = new Random(0x45746921);
	
	private static final HashMap<Integer, Integer> RNG_INDEX_MAP = new HashMap<>();
	
	/**
	 * Identical to {@link RenderType#LINES} but this always draws on top.
	 */
	// A lot of these are AT'd
	public static final RenderType.Type TOP_LINES = create("lines_top", DefaultVertexFormats.POSITION_COLOR, 1, 256, RenderType.State.builder().setLineState(new RenderState.LineState(OptionalDouble.empty())).setDepthTestState(NO_DEPTH_TEST).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
	
	/**
	 * Sets up a transparency state based on original GL calls from Compact Machines. (1.12.x)<br/>
	 * Borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/CCRenderTypes.java#L12">Compact Crafting CCRenderTypes</a> by robotgryphon.
	 */
	private static final RenderState.TransparencyState PROJECTION_TRANSPARENCY = new RenderState.TransparencyState("translucent_quads", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	
	/**
	 * A render type used for translucent cubes.<br/>
	 * Borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/CCRenderTypes.java#L12">Compact Crafting CCRenderTypes</a> by robotgryphon.
	 */
	@Deprecated // Used on a render mode that is not functional.
	public static final RenderType TRANSLUCENT_QUADS = RenderType.create("translucent_quads",
         DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256,
         RenderType.State.builder()
           .setTransparencyState(PROJECTION_TRANSPARENCY)
           .setCullState(new RenderState.CullState(true))
           .setWriteMaskState(new RenderState.WriteMaskState(true, false))
           .createCompositeState(false));
	
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
			.uv2(0, 240)
			.normal(1, 0, 0)
			.endVertex();
	}
	
	/**
	 * Returns a random RGB color with the given alpha.
	 * @param alpha The alpha to apply to this color (0-255)
	 * @return A numeric value in ARGB format.
	 */
	public static int randomRGB(int alpha) {
		alpha = (alpha & 0xFF) << 24;
		return alpha | RNG.nextInt(0xFFFFFF);
	}
	
	/**
	 * Returns a random RGB color with full alpha (maximum opacity).
	 * @return A numeric value in ARGB format.
	 */
	public static int randomRGB() {
		// Don't call randomRGB(alpha) because can skip a bitshift here (even though those are lightning fast already)
		return 0xFF_000000 | RNG.nextInt(0xFFFFFF);
	}
	
	/**
	 * @return A preset (but initially randomly selected) RGB color for the given index number.
	 */
	public static int randomIndexedRGB(int index) {
		if (RNG_INDEX_MAP.containsKey(index)) {
			return RNG_INDEX_MAP.get(index);
		}
		int value = randomRGB();
		RNG_INDEX_MAP.put(index, value);
		return value;
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
	
	/**
	 * Draw an axis-aligned bounding box's outline. To render an opaque cube,
	 * @param bounds The bounding box to draw.
	 * @param mtx The MatrixStack that controls the rendered position.
	 * @param vtxBuilder The vertex builder that allows drawing the lines.
	 * @param argb The ARGB integer color of the box.
	 * @param argbDivisor When converting ARGB integer into floating point, the value is divided by this. By default, this should be 255 for an exact representation. Lower values will cause a bias to brighter colors. 0 will break everything. Don't divide by 0.
	 */
	public static void drawCubeFrame(AxisAlignedBB bounds, MatrixStack mtx, IVertexBuilder vtxBuilder, int argb, float argbDivisor) {
		float alpha =   ((argb & 0xFF000000) >> 24) / argbDivisor;
		float red =     ((argb & 0x00FF0000) >> 16) / argbDivisor;
		float green =   ((argb & 0x0000FF00) >>  8) / argbDivisor;
		float blue =    ((argb & 0x000000FF) >>  0) / argbDivisor;
		
		WorldRenderer.renderLineBox(mtx, vtxBuilder, bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ, red, green, blue, alpha);
	}
	
	/**
	 * Politely borrowed from robotgryphon's "CompactCrafting" mod, the <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java">Field Projector renderer</a> specifcially.<br/>
	 * Draws a cube with translucent faces. This is a counterpart to {@link #drawCubeFrame(AxisAlignedBB, MatrixStack, IVertexBuilder, int, float)} in that this draws full faces rather than wireframe.
	 * @param cube The bounds of the cube.
	 * @param mtx The matrix that determines where the cube should go.
	 * @param builder The builder used to draw the vertices. This should use a {@link RenderType} that implements translucent quads, such as {@link #TRANSLUCENT_QUADS}
	 * @param argb The color of this cube in ARGB format.
	 */
	@Deprecated // The drawCubeFace method doesn't seem to work properly and it's not important enough to warrant fixing it.
	public static void drawCubeFaces(AxisAlignedBB cube, MatrixStack mtx, IVertexBuilder builder, int argb) {
		drawCubeFace(builder, mtx, cube, argb, Direction.NORTH);
		drawCubeFace(builder, mtx, cube, argb, Direction.SOUTH);
		drawCubeFace(builder, mtx, cube, argb, Direction.WEST);
		drawCubeFace(builder, mtx, cube, argb, Direction.EAST);
		drawCubeFace(builder, mtx, cube, argb, Direction.UP);
		drawCubeFace(builder, mtx, cube, argb, Direction.DOWN);
	}
	
	/**
	 * Renders text in a manner akin to an entity name at the given {@link Vector3d}
	 * @param text The text to display.
	 * @param mtx The matrix stack defining where this text should go.
	 * @param renderInfo The current player's camera.
	 * @param buffer Something to provide render buffers.
	 * @param at The location of the text in world space.
	 * @param argb The color of the text.
	 * @param renderBetterThroughWalls If true, text behind will render about half opaque instead of basically invisible.
	 */
	public static void renderText(String text, MatrixStack mtx, ActiveRenderInfo renderInfo, IRenderTypeBuffer buffer, Vector3d at, int argb, boolean renderBetterThroughWalls) {
		StringTextComponent component = new StringTextComponent(text);
		mtx.pushPose();
		mtx.translate(at.x, at.y + 1, at.z);
		mtx.mulPose(renderInfo.rotation());
		mtx.scale(-0.025F, -0.025F, 0.025F);
		Matrix4f matrix4f = mtx.last().pose();
		float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
		int alpha = (int)(backgroundOpacity * 255.0F) << 24;
		FontRenderer fontRenderer = TileEntityRendererDispatcher.instance.font;
		float widthOffset = (float)(-fontRenderer.width(component) / 2);
		fontRenderer.drawInBatch(component, widthOffset, 0, renderBetterThroughWalls ? 0x80_FFFFFF : 0x20_000000, false, matrix4f, buffer, true, alpha, FULL_BRIGHT_LIGHT);
		fontRenderer.drawInBatch(component, widthOffset, 0, argb, false, matrix4f, buffer, false, 0, FULL_BRIGHT_LIGHT);
		
		mtx.popPose();
	}
	
	/**
	 * Renders text in a manner akin to an entity name at the given {@link BlockPos}
	 * @param text The text to display.
	 * @param mtx The matrix stack defining where this text should go.
	 * @param renderInfo The current player's camera.
	 * @param buffer Something to provide render buffers.
	 * @param at The location of the text in world space.
	 * @param argb The color of the text.
	 * @param renderBetterThroughWalls If true, text behind will render about half opaque instead of basically invisible.
	 */
	public static void renderText(String text, MatrixStack mtx, ActiveRenderInfo renderInfo, IRenderTypeBuffer buffer, BlockPos at, int argb, boolean renderBetterThroughWalls) {
		renderText(text, mtx, renderInfo, buffer, Vector3d.atCenterOf(at).add(0, 1, 0), argb, renderBetterThroughWalls);
	}
	
	/**
	 * Politely borrowed from robotgryphon's "CompactCrafting" mod, the <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java">Field Projector renderer</a> specifcially.<br/>
	 * Draws a single face on a cube, as determined by {@code face}.
	 * @param builder The vertex builder used to draw the vertices.
	 * @param mtx The matrix that determines where the cube should go.
	 * @param cube The cube's bounding box.
	 * @param argb The color of the cube in ARGB format.
	 * @param face The direction of which face this should render.
	 */
	private static void drawCubeFace(IVertexBuilder builder, MatrixStack mtx, AxisAlignedBB cube, int argb, Direction face) {
		Vector3d BOTTOM_RIGHT = null,
			TOP_RIGHT = null,
			TOP_LEFT = null,
			BOTTOM_LEFT = null;
		
		switch (face) {
			case NORTH:
				BOTTOM_RIGHT = new Vector3d((float) cube.minX, (float) cube.minY, (float) cube.minZ);
				TOP_RIGHT = new Vector3d((float) cube.minX, (float) cube.maxY, (float) cube.minZ);
				TOP_LEFT = new Vector3d((float) cube.maxX, (float) cube.maxY, (float) cube.minZ);
				BOTTOM_LEFT = new Vector3d((float) cube.maxX, (float) cube.minY, (float) cube.minZ);
				break;
			
			case SOUTH:
				BOTTOM_RIGHT = new Vector3d((float) cube.maxX, (float) cube.minY, (float) cube.maxZ);
				TOP_RIGHT = new Vector3d((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ);
				TOP_LEFT = new Vector3d((float) cube.minX, (float) cube.maxY, (float) cube.maxZ);
				BOTTOM_LEFT = new Vector3d((float) cube.minX, (float) cube.minY, (float) cube.maxZ);
				break;
			
			case WEST:
				BOTTOM_RIGHT = new Vector3d((float) cube.minX, (float) cube.minY, (float) cube.maxZ);
				TOP_RIGHT = new Vector3d((float) cube.minX, (float) cube.maxY, (float) cube.maxZ);
				TOP_LEFT = new Vector3d((float) cube.minX, (float) cube.maxY, (float) cube.minZ);
				BOTTOM_LEFT = new Vector3d((float) cube.minX, (float) cube.minY, (float) cube.minZ);
				break;
			
			case EAST:
				BOTTOM_RIGHT = new Vector3d((float) cube.maxX, (float) cube.minY, (float) cube.minZ);
				TOP_RIGHT = new Vector3d((float) cube.maxX, (float) cube.maxY, (float) cube.minZ);
				TOP_LEFT = new Vector3d((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ);
				BOTTOM_LEFT = new Vector3d((float) cube.maxX, (float) cube.minY, (float) cube.maxZ);
				break;
			
			case UP:
				BOTTOM_RIGHT = new Vector3d((float) cube.minX, (float) cube.maxY, (float) cube.minZ);
				TOP_RIGHT = new Vector3d((float) cube.minX, (float) cube.maxY, (float) cube.maxZ);
				TOP_LEFT = new Vector3d((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ);
				BOTTOM_LEFT = new Vector3d((float) cube.maxX, (float) cube.maxY, (float) cube.minZ);
				break;
			
			case DOWN:
				BOTTOM_RIGHT = new Vector3d((float) cube.minX, (float) cube.minY, (float) cube.maxZ);
				TOP_RIGHT = new Vector3d((float) cube.minX, (float) cube.minY, (float) cube.minZ);
				TOP_LEFT = new Vector3d((float) cube.maxX, (float) cube.minY, (float) cube.minZ);
				BOTTOM_LEFT = new Vector3d((float) cube.maxX, (float) cube.minY, (float) cube.maxZ);
				break;
		}
		
		if (BOTTOM_RIGHT == null)
			return;
		
		addColoredVertex(builder, mtx, argb, BOTTOM_RIGHT);
		addColoredVertex(builder, mtx, argb, TOP_RIGHT);
		addColoredVertex(builder, mtx, argb, TOP_LEFT);
		addColoredVertex(builder, mtx, argb, BOTTOM_LEFT);
	}
}
