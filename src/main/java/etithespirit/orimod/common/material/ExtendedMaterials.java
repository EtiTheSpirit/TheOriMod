package etithespirit.orimod.common.material;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public final class ExtendedMaterials {
	
	private ExtendedMaterials() {}
	
	public static final Material DECAY_LIQUID = (new Material.Builder(MaterialColor.COLOR_PURPLE)).liquid().noCollider().nonSolid().replaceable().destroyOnPush().notSolidBlocking().build();
	
	/**
	 * A material representing all light-composed blocks, for instance, any variant of a hard-light block.
	 */
	public static final Material LIGHT = new Material.Builder(MaterialColor.QUARTZ).build();
	
}
