package etithespirit.etimod.registry;

import etithespirit.etimod.client.render.mob.RenderSpiritMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public class RenderRegistry {

	public static void registerAll() {
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.SPIRIT.get(), new RenderSpiritMob.RenderFactory());
	}
	
}
