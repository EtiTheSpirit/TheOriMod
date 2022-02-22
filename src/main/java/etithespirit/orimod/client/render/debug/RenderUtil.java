package etithespirit.orimod.client.render.debug;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.codehaus.plexus.util.dag.Vertex;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.Random;

import static etithespirit.orimod.GeneralUtils.FULL_BRIGHT_LIGHT;
import static net.minecraft.client.renderer.RenderStateShard.ITEM_ENTITY_TARGET;
import static net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_LINES_SHADER;
import static net.minecraft.client.renderer.RenderType.create;
import static net.minecraft.util.FastColor.ARGB32.alpha;
import static net.minecraft.util.FastColor.ARGB32.blue;
import static net.minecraft.util.FastColor.ARGB32.green;
import static net.minecraft.util.FastColor.ARGB32.red;

@SuppressWarnings("unused")
public final class RenderUtil {
	
	// Static class def.
	private RenderUtil() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static final Random RNG = new Random(0x45746921);
	
	private static final HashMap<Integer, Integer> RNG_INDEX_MAP = new HashMap<>();
	
	protected static final RenderStateShard.LineStateShard ASM_LINE = new RenderStateShard.LineStateShard(OptionalDouble.of(4.0D));
	
	/**
	 * Identical to {@link RenderType#LINES} but this always draws on top.
	 */
	// A lot of these are AT'd
	public static final RenderType.CompositeRenderType TOP_LINES = create(
		"lines_top",
		DefaultVertexFormat.POSITION_COLOR,
		VertexFormat.Mode.LINES,
		256,
		false,
		true,
		RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_LINES_SHADER)
			.setLineState(ASM_LINE)
			.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
			.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
			.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
			.setOutputState(ITEM_ENTITY_TARGET)
			.createCompositeState(false)
		
	);
	
	/**
	 * Sets up a transparency state based on original GL calls from Compact Machines. (1.12.x)<br/>
	 * Borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/CCRenderTypes.java#L12">Compact Crafting CCRenderTypes</a> by robotgryphon.
	 */
	private static final RenderStateShard.TransparencyStateShard PROJECTION_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
		"translucent_quads", () -> {
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		},
		() -> {
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		});
	
	/**
	 * A render type used for translucent cubes.<br/>
	 * Borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/CCRenderTypes.java#L12">Compact Crafting CCRenderTypes</a> by robotgryphon.
	 */
	@Deprecated // Used on a render mode that is not functional.
	public static final RenderType TRANSLUCENT_QUADS = RenderType.create("translucent_quads",
	                                                                     DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
	                                                                     RenderType.CompositeState.builder()
		                                                                     .setTransparencyState(PROJECTION_TRANSPARENCY)
		                                                                     .setCullState(new RenderStateShard.CullStateShard(true))
		                                                                     .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
		                                                                     .createCompositeState(false));
	
	/**
	 * Politely borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java#L150">Compact Crafting</a>.
	 * Provides a means of rendering colored lines.
	 * @param vtxBuilder The vertex builder.
	 * @param mtx The matrix that this vertex is affected by.
	 * @param argb The color of the vertex.
	 * @param position The location of the vertex.
	 */
	public static void addColoredVertex(VertexConsumer vtxBuilder, PoseStack mtx, int argb, BlockPos position) {
		addColoredVertex(vtxBuilder, mtx, argb, Vec3.atCenterOf(position));
	}
	
	/**
	 * Politely borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java#L150">Compact Crafting</a>.
	 * Provides a means of rendering colored lines.
	 * @param vtxBuilder The vertex builder.
	 * @param mtx The matrix that this vertex is affected by.
	 * @param argb The color of the vertex.
	 * @param position The location of the vertex.
	 */
	public static void addColoredVertex(VertexConsumer vtxBuilder, PoseStack mtx, int argb, Vec3 position) {
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
	public static void drawLine(VertexConsumer vtxBuilder, PoseStack mtx, int argb, BlockPos blockStart, BlockPos blockEnd) {
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
	public static void drawCubeFrame(AABB bounds, PoseStack mtx, VertexConsumer vtxBuilder, int argb, float argbDivisor) {
		float alpha =   ((argb & 0xFF000000) >> 24) / argbDivisor;
		float red =     ((argb & 0x00FF0000) >> 16) / argbDivisor;
		float green =   ((argb & 0x0000FF00) >>  8) / argbDivisor;
		//noinspection PointlessBitwiseExpression
		float blue =    ((argb & 0x000000FF) >>  0) / argbDivisor;
		
		LevelRenderer.renderLineBox(mtx, vtxBuilder, bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ, red, green, blue, alpha);
	}
	
	/**
	 * Politely borrowed from robotgryphon's "CompactCrafting" mod, the <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java">Field Projector renderer</a> specifcially.<br/>
	 * Draws a cube with translucent faces. This is a counterpart to {@link #drawCubeFrame(AABB, PoseStack, VertexConsumer, int, float)} in that this draws full faces rather than wireframe.
	 * @param cube The bounds of the cube.
	 * @param mtx The matrix that determines where the cube should go.
	 * @param builder The builder used to draw the vertices. This should use a {@link RenderType} that implements translucent quads, such as {@link #TRANSLUCENT_QUADS}
	 * @param argb The color of this cube in ARGB format.
	 */
	@Deprecated // The drawCubeFace method doesn't seem to work properly and it's not important enough to warrant fixing it.
	public static void drawCubeFaces(AABB cube, PoseStack mtx, VertexConsumer builder, int argb) {
		drawCubeFace(builder, mtx, cube, argb, Direction.NORTH);
		drawCubeFace(builder, mtx, cube, argb, Direction.SOUTH);
		drawCubeFace(builder, mtx, cube, argb, Direction.WEST);
		drawCubeFace(builder, mtx, cube, argb, Direction.EAST);
		drawCubeFace(builder, mtx, cube, argb, Direction.UP);
		drawCubeFace(builder, mtx, cube, argb, Direction.DOWN);
	}
	
	/**
	 * Renders text in a manner akin to an entity name at the given {@link Vec3}
	 * @param text The text to display.
	 * @param mtx The matrix stack defining where this text should go.
	 * @param renderInfo The current player's camera.
	 * @param buffer Something to provide render buffers.
	 * @param at The location of the text in world space.
	 * @param argb The color of the text.
	 * @param renderBetterThroughWalls If true, text behind will render about half opaque instead of basically invisible.
	 */
	public static void renderText(String text, PoseStack mtx, Camera renderInfo, MultiBufferSource buffer, Vec3 at, int argb, boolean renderBetterThroughWalls) {
		mtx.pushPose();
		
		mtx.translate(at.x, at.y + 1, at.z);
		mtx.mulPose(renderInfo.rotation());
		mtx.scale(-0.025F, -0.025F, 0.025F);
		Matrix4f matrix4f = mtx.last().pose();
		float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
		int alpha = (int)(backgroundOpacity * 255.0F) << 24;
		Font fontRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().font;
		float widthOffset = (float)(-fontRenderer.width(text) / 2);
		fontRenderer.drawInBatch(text, widthOffset, 0, renderBetterThroughWalls ? 0x80_FFFFFF : 0x20_000000, false, matrix4f, buffer, true, alpha, FULL_BRIGHT_LIGHT);
		fontRenderer.drawInBatch(text, widthOffset, 0, argb, false, matrix4f, buffer, false, 0, FULL_BRIGHT_LIGHT);
		
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
	public static void renderText(String text, PoseStack mtx, Camera renderInfo, MultiBufferSource buffer, BlockPos at, int argb, boolean renderBetterThroughWalls) {
		renderText(text, mtx, renderInfo, buffer, Vec3.atCenterOf(at).add(0, 1, 0), argb, renderBetterThroughWalls);
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
	private static void drawCubeFace(VertexConsumer builder, PoseStack mtx, AABB cube, int argb, Direction face) {
		Vec3 BOTTOM_RIGHT = null,
			TOP_RIGHT = null,
			TOP_LEFT = null,
			BOTTOM_LEFT = null;
		
		switch (face) {
			case NORTH:
				BOTTOM_RIGHT = new Vec3((float) cube.minX, (float) cube.minY, (float) cube.minZ);
				TOP_RIGHT = new Vec3((float) cube.minX, (float) cube.maxY, (float) cube.minZ);
				TOP_LEFT = new Vec3((float) cube.maxX, (float) cube.maxY, (float) cube.minZ);
				BOTTOM_LEFT = new Vec3((float) cube.maxX, (float) cube.minY, (float) cube.minZ);
				break;
			
			case SOUTH:
				BOTTOM_RIGHT = new Vec3((float) cube.maxX, (float) cube.minY, (float) cube.maxZ);
				TOP_RIGHT = new Vec3((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ);
				TOP_LEFT = new Vec3((float) cube.minX, (float) cube.maxY, (float) cube.maxZ);
				BOTTOM_LEFT = new Vec3((float) cube.minX, (float) cube.minY, (float) cube.maxZ);
				break;
			
			case WEST:
				BOTTOM_RIGHT = new Vec3((float) cube.minX, (float) cube.minY, (float) cube.maxZ);
				TOP_RIGHT = new Vec3((float) cube.minX, (float) cube.maxY, (float) cube.maxZ);
				TOP_LEFT = new Vec3((float) cube.minX, (float) cube.maxY, (float) cube.minZ);
				BOTTOM_LEFT = new Vec3((float) cube.minX, (float) cube.minY, (float) cube.minZ);
				break;
			
			case EAST:
				BOTTOM_RIGHT = new Vec3((float) cube.maxX, (float) cube.minY, (float) cube.minZ);
				TOP_RIGHT = new Vec3((float) cube.maxX, (float) cube.maxY, (float) cube.minZ);
				TOP_LEFT = new Vec3((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ);
				BOTTOM_LEFT = new Vec3((float) cube.maxX, (float) cube.minY, (float) cube.maxZ);
				break;
			
			case UP:
				BOTTOM_RIGHT = new Vec3((float) cube.minX, (float) cube.maxY, (float) cube.minZ);
				TOP_RIGHT = new Vec3((float) cube.minX, (float) cube.maxY, (float) cube.maxZ);
				TOP_LEFT = new Vec3((float) cube.maxX, (float) cube.maxY, (float) cube.maxZ);
				BOTTOM_LEFT = new Vec3((float) cube.maxX, (float) cube.maxY, (float) cube.minZ);
				break;
			
			case DOWN:
				BOTTOM_RIGHT = new Vec3((float) cube.minX, (float) cube.minY, (float) cube.maxZ);
				TOP_RIGHT = new Vec3((float) cube.minX, (float) cube.minY, (float) cube.minZ);
				TOP_LEFT = new Vec3((float) cube.maxX, (float) cube.minY, (float) cube.minZ);
				BOTTOM_LEFT = new Vec3((float) cube.maxX, (float) cube.minY, (float) cube.maxZ);
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
