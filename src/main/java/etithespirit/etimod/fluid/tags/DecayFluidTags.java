package etithespirit.etimod.fluid.tags;

import etithespirit.etimod.EtiMod;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class DecayFluidTags {

	public static final ITag.INamedTag<Fluid> DECAY = createFluidTag("decay");
	
	private static ITag.INamedTag<Fluid> createFluidTag(String fluidName) {
		ResourceLocation location = new ResourceLocation(EtiMod.MODID, fluidName);
		return FluidTags.makeWrapperTag(location.toString());
	}
	
	/** Does nothing. Serves the purpose of allowing something to reference this class so that it initializes and does the registry that it needs to do. */
	public static void registerAll() { }
	
	//@Deprecated
	//public static final ITag.INamedTag<Fluid> PARTIAL_DECAY = FluidTags.makeWrapperTag("partial_decay_poison");
	
}
