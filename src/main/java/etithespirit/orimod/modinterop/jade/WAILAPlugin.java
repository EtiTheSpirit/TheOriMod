package etithespirit.orimod.modinterop.jade;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaPlugin;

/**
 * Interopability for WAILA (now Jade): Core System
 *
 * @author Eti
 */
@WailaPlugin
public class WAILAPlugin implements IWailaPlugin {
	
	private final WAILADisplayLightStorage storage = new WAILADisplayLightStorage();
	private final WAILADisplayConnectableLightTechBlock conduit = new WAILADisplayConnectableLightTechBlock();
	
	@Override
	public void register(IRegistrar registrar) {
		storage.initialize(registrar);
		conduit.initialize(registrar);
	}
	
}
