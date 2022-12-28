package etithespirit.orimod.spiritmaterial.implementation;

import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import etithespirit.orimod.common.material.ExtendedMaterials;
import etithespirit.orimod.spiritmaterial.data.SpiritMaterialContainer;

public final class OriModBindings {
	
	public static void initialize() {
		SpiritMaterialContainer ctr = SpiritMaterialContainer.getForThisMod();
		
		ctr.registerMaterial(ExtendedMaterials.LIGHT, SpiritMaterial.HARDLIGHT_GLASS);
	}
	
}
