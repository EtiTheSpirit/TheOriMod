package etithespirit.orimod.client.render.debug;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import etithespirit.orimod.GeneralUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import org.codehaus.plexus.util.dag.Vertex;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Random;

import static etithespirit.orimod.GeneralUtils.FULL_BRIGHT_LIGHT;
import static net.minecraft.client.renderer.RenderStateShard.ITEM_ENTITY_TARGET;
import static net.minecraft.client.renderer.RenderStateShard.POSITION_COLOR_SHADER;
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
	
	/** A box used for rendering translucent cubes. */
	private static final ModelPart BOX;
	
	static {
		ModelPart.Cube block = new ModelPart.Cube(
			0, 0,
			0, 0, 0,
			16, 16, 16,
			0, 0, 0,
			false,
			0, 0
		);
		BOX = new ModelPart(List.of(block), Map.of());
	}
	
	/**
	 * A render type used for translucent quads.<br/>
	 * Adapted heavily from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/CCRenderTypes.java#L12">Compact Crafting CCRenderTypes</a> by robotgryphon.
	 */
	public static final RenderType TRANSLUCENT_SOLID = RenderType.create("translucent_quads",
	                                                                     DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
	                                                                     RenderType.CompositeState.builder()
		                                                                     .setShaderState(POSITION_COLOR_SHADER)
		                                                                     .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
		                                                                     .setCullState(new RenderStateShard.CullStateShard(true))
		                                                                     .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
		                                                                     .createCompositeState(false));
	
	/**
	 * Provides a means of rendering colored lines or quads.
	 * Politely borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java#L150">Compact Crafting</a>.
	 * @param vtxBuilder The vertex builder.
	 * @param mtx The matrix that this vertex is affected by.
	 * @param argb The color of the vertex.
	 * @param position The location of the vertex.
	 */
	public static void addColoredVertex(VertexConsumer vtxBuilder, PoseStack mtx, int argb, float argbDivisor, Vec3 position) {
		float x = (float)position.x();
		float y = (float)position.y();
		float z = (float)position.z();
		Matrix4f pose = mtx.last().pose();
		vtxBuilder
			.vertex(pose, x, y, z)
			.color(red(argb) / argbDivisor, green(argb) / argbDivisor, blue(argb) / argbDivisor, alpha(argb) / argbDivisor)
			.uv2(0, 240) // TODO: Why 240?
			.normal(1, 0, 0)
			.endVertex();
		
	}
	
	/**
	 * Provides a means of rendering colored lines or quads.
	 * Politely borrowed from <a href="https://github.com/CompactMods/CompactCrafting/blob/d166e58807417004db6546da4d07c32c0fe80253/src/main/java/com/robotgryphon/compactcrafting/projector/render/FieldProjectorRenderer.java#L150">Compact Crafting</a>
	 * @param vtxBuilder The vertex builder.
	 * @param mtx The matrix that this vertex is affected by.
	 * @param argb The color of the vertex.
	 * @param position The location of the vertex.
	 */
	public static void addColoredVertex(VertexConsumer vtxBuilder, PoseStack mtx, int argb, float argbDivisor, Vec3 position, Direction normal) {
		float x = (float)position.x();
		float y = (float)position.y();
		float z = (float)position.z();
		Matrix4f pose = mtx.last().pose();
		Vec3i nrm = normal.getNormal();
		vtxBuilder
			.vertex(pose, x, y, z) // had pose as first arg
			.color(red(argb) / argbDivisor, green(argb) / argbDivisor, blue(argb) / argbDivisor, alpha(argb) / argbDivisor)
			.uv2(0, 240) // TODO: Why 240?
			.normal(nrm.getX(), nrm.getY(), nrm.getZ())
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
		int value = randomRGB(0x7F);
		RNG_INDEX_MAP.put(index, value);
		return value;
	}
	
	/**
	 * Returns an AABB containing the two given BlockPos center points. The size property dictates how much to expand it by on all axes.
	 * @param blockStart The start position.
	 * @param blockEnd The end position.
	 * @param size The "extra room" given for the AABB. A value of 1 makes it surround both of the blocks on their edges.
	 * @return An AABB containing the two BlockPos instances.
	 */
	public static AABB getAABBEnclosing(BlockPos blockStart, BlockPos blockEnd, double size) {
		Vec3 centerStart = new Vec3(blockStart.getX() + 0.5D, blockStart.getY() + 0.5D, blockStart.getZ() + 0.5D);
		Vec3 centerEnd = new Vec3(blockEnd.getX() + 0.5D, blockEnd.getY() + 0.5D, blockEnd.getZ() + 0.5D);
		AABB bounds = new AABB(centerStart, centerEnd).inflate(size);
		return bounds;
	}
	
	/**
	 * Draws a wireframe tube between two block positions.
	 * @param vtxBuilder The vertex builder. This should be one that builds with quads.
	 * @param mtx The matrix stack for positioning the vertices.
	 * @param argb The color in ARGB format.
	 * @param argbDivisor A divisor applied to all four components of the ARGB value. This should be 255 for floating point colors and 1 for byte colors, but it can really be anything.
	 * @param blockStart The start position of the frame. This uses the center of the block.
	 * @param blockEnd The end position of the frame. This uses the center of the block.
	 * @param size The diameter of the wireframe tube.
	 */
	public static void drawWideCubeFrame(VertexConsumer vtxBuilder, PoseStack mtx, int argb, float argbDivisor, BlockPos blockStart, BlockPos blockEnd, double size) {
		drawCubeFrame(getAABBEnclosing(blockStart, blockEnd, size), mtx, vtxBuilder, argb, argbDivisor);
	}
	
	/**
	 * Draw an axis-aligned bounding box's outline. To render an opaque cube, use {@link #drawBox(AABB, PoseStack, VertexConsumer, int, float)}/
	 * @param bounds The bounding box to draw.
	 * @param mtx The MatrixStack that controls the rendered position.
	 * @param vtxBuilder The vertex builder that allows drawing the lines.
	 * @param argb The ARGB integer color of the box.
	 * @param argbDivisor When converting ARGB integer into floating point, the value is divided by this. By default, this should be 255 for an exact representation. Lower values will cause a bias to brighter colors. 0 will break everything. Don't divide by 0.
	 */
	public static void drawCubeFrame(AABB bounds, PoseStack mtx, VertexConsumer vtxBuilder, int argb, float argbDivisor) {
		float alpha =   alpha(argb) / argbDivisor;
		float red =     red(argb) / argbDivisor;
		float green =   green(argb) / argbDivisor;
		float blue =    blue(argb) / argbDivisor;
		
		LevelRenderer.renderLineBox(mtx, vtxBuilder, bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ, red, green, blue, alpha);
	}
	
	/**
	 * Draws a cube frame as a tube between two block positions.
	 * @param vtxBuilder The vertex builder. This should be one that builds with quads, and that uses translucent rendering.
	 * @param mtx The matrix stack for positioning the vertices.
	 * @param argb The color in ARGB format.
	 * @param argbDivisor A divisor applied to all four components of the ARGB value. This should be 255 for floating point colors and 1 for byte colors, but it can really be anything.
	 * @param blockStart The start position of the frame. This uses the center of the block.
	 * @param blockEnd The end position of the frame. This uses the center of the block.
	 * @param size The diameter of the wireframe tube.
	 */
	public static void drawWideCubeSolid(VertexConsumer vtxBuilder, PoseStack mtx, int argb, float argbDivisor, BlockPos blockStart, BlockPos blockEnd, double size) {
		drawBox(getAABBEnclosing(blockStart, blockEnd, size), mtx, vtxBuilder, argb, argbDivisor);
	}
	
	/**
	 * Draws a box representing the given AABB.
	 * @param bounds The box to draw.
	 * @param mtx The matrix used to place the vertices.
	 * @param builder The thing used to actually put the vertices down in the first place.
	 * @param argb The color of the box in ARGB format.
	 * @param argbDivisor A divisor applied to all four components of the ARGB value. This should be 255 for floating point colors and 1 for byte colors, but it can really be anything.
	 */
	public static void drawBox(AABB bounds, PoseStack mtx, VertexConsumer builder, int argb, float argbDivisor) {
		float alpha =   alpha(argb) / argbDivisor;
		float red =     red(argb) / argbDivisor;
		float green =   green(argb) / argbDivisor;
		float blue =    blue(argb) / argbDivisor;
		
		mtx.pushPose();
		mtx.translate(bounds.minX, bounds.minY, bounds.minZ);
		mtx.scale((float)bounds.getXsize(), (float)bounds.getYsize(), (float)bounds.getZsize());
		BOX.render(mtx, builder, FULL_BRIGHT_LIGHT, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
		mtx.popPose();
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
	 * Renders text in a manner akin to an entity name at the given {@link BlockPos}. This will add 1 to the Y component of the given position.
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
	
}
