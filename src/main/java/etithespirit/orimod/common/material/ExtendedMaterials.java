package etithespirit.orimod.common.material;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public final class ExtendedMaterials {
	
	private ExtendedMaterials() {}
	
	public static final Material DECAY = (new Material.Builder(MaterialColor.COLOR_PURPLE)).liquid().noCollider().nonSolid().replaceable().destroyOnPush().notSolidBlocking().build();
	
}
