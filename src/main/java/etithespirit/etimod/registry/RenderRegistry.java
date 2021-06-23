package etithespirit.etimod.registry;

import etithespirit.etimod.client.render.mob.RenderSpiritMob;
import etithespirit.etimod.client.render.debug.LightTileDebugRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/**
 * Registers all my renderers to the game.
 *
 * @author Eti
 */
public final class RenderRegistry {

	public static void registerAll() {
		RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.SPIRIT.get(), new RenderSpiritMob.RenderFactory());
		
		ClientRegistry.bindTileEntityRenderer(TileEntityRegistry.LIGHT_CAPACITOR.get(), LightTileDebugRenderer::new);
		
		//MinecraftForge.EVENT_BUS.addListener(AssemblyRenderer::onRender);
	}
	
}
