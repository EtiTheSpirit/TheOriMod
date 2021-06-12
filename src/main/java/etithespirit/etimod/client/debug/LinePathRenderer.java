package etithespirit.etimod.client.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;

@Deprecated
public class LinePathRenderer {
	
	public static void render(Line line) {
		Vector3d camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		RenderSystem.pushMatrix();
		RenderSystem.disableTexture();
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.disableCull();
		RenderSystem.lineWidth(8.0F);
		renderPathLine(line, camPos);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.enableCull();
		RenderSystem.popMatrix();
	}
	
	private static void renderPathLine(Line line, Vector3d camera) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuilder();
		boolean live = true;
		builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
		
		for (Vector3d point : line.getPoints()) {
			if (point == null) {
				if (live) {
					live = false;
					builder.end();
				}
				continue;
			}
			if (!live) {
				builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
				live = true;
			}
			double distance = point.subtract(camera).length();
			if (distance < 120) {
				builder.vertex(point.x, point.y, point.z).color(255, 255, 0, 255).endVertex();
			}
		}
		
		tessellator.end();
	}

	public static class Line {
		
		private ArrayList<Vector3d> points;
		
		/**
		 * Construct a new line from the given points. A null point can be given to break the line.
		 * @param ordinalPoints
		 */
		public Line(BlockPos... ordinalPoints) {
			ArrayList<Vector3d> vecs = new ArrayList<Vector3d>(ordinalPoints.length);
			for (int idx = 0; idx < ordinalPoints.length; idx++) {
				vecs.add(Vector3d.atCenterOf(ordinalPoints[idx]));
			}
			this.points = vecs;
		}
		
		public void addPoint(BlockPos point) {
			points.add(Vector3d.atCenterOf(point));
		}
		
		public void addPoint(Vector3d point) {
			points.add(point);
		}
		
		public ArrayList<Vector3d> getPoints() {
			// Would do something like an immutable copy here but that's probably not
			// a good idea for render code.
			return points;
		}
		
	}
	
}
