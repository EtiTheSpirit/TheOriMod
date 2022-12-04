package etithespirit.orimod.common.block;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

/**
 * A class containing extended materials.
 * @author Eti
 *
 */
public final class ExtendedMaterial {
	
	private ExtendedMaterial() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * A material representing all light-composed blocks, for instance, any variant of a hard-light block.
	 */
	public static final Material LIGHT = new Material.Builder(MaterialColor.QUARTZ).build();
	
	
	
}