package etithespirit.orimod.client.render.debug;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Deprecated(forRemoval = true)
public final class AABBUtil {
	
	private AABBUtil() {}
	
	public static Vec3[] getVerticesOfFace(AABB box, Direction direction) {
		Vec3[] results = new Vec3[4];
		
		// 8 vertices on an AABB
		// The names of the following variables use mathematical space (that is, right, up, and front are positives)
		Vec3 vtxRUF = new Vec3(box.maxX, box.maxY, box.maxZ);
		Vec3 vtxLUF = new Vec3(box.minX, box.maxY, box.maxZ);
		
		Vec3 vtxRDF = new Vec3(box.maxX, box.minY, box.maxZ);
		Vec3 vtxLDF = new Vec3(box.minX, box.minY, box.maxZ);
		
		Vec3 vtxRUB = new Vec3(box.maxX, box.maxY, box.minZ);
		Vec3 vtxLUB = new Vec3(box.minX, box.maxY, box.minZ);
		
		Vec3 vtxRDB = new Vec3(box.maxX, box.minY, box.minZ);
		Vec3 vtxLDB = new Vec3(box.minX, box.minY, box.minZ);
		
		switch (direction) {
			case NORTH:
				results[0] = vtxRUB;
				results[1] = vtxLUB;
				results[2] = vtxLDB;
				results[3] = vtxRDB;
			case SOUTH:
				results[0] = vtxRUF;
				results[1] = vtxLUF;
				results[2] = vtxLDF;
				results[3] = vtxRDF;
			case EAST:
				results[0] = vtxRUB;
				results[1] = vtxRUF;
				results[2] = vtxRDF;
				results[3] = vtxRDB;
			case WEST:
				results[0] = vtxLUB;
				results[1] = vtxLUF;
				results[2] = vtxLDF;
				results[3] = vtxLDB;
			case UP:
				results[0] = vtxRUB;
				results[1] = vtxLUB;
				results[2] = vtxLUF;
				results[3] = vtxRUF;
			case DOWN:
				results[0] = vtxRDB;
				results[1] = vtxLDB;
				results[2] = vtxLDF;
				results[3] = vtxRDF;
		}
		
		return results;
	}
	
}
