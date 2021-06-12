package etithespirit.etimod.modinterop.waila;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class WAILAPlugin implements IWailaPlugin {
	
	private WAILADisplayLightStorage storage = new WAILADisplayLightStorage();
	private WAILADisplayLightConduit conduit = new WAILADisplayLightConduit();

	@Override
	public void register(IRegistrar registrar) {
		storage.initialize(registrar);
		conduit.initialize(registrar);
	}

}
