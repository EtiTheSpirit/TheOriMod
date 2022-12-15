package etithespirit.orimod.common.tags;

import etithespirit.orimod.OriMod;
import net.minecraft.resources.ResourceLocation;

final class TagHelpers {
	
	public static ResourceLocation oriMod(String path) {
		return new ResourceLocation(OriMod.MODID, path);
	}
	
}
