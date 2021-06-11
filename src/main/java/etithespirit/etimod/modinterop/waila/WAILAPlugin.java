package etithespirit.etimod.modinterop.waila;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class WAILAPlugin implements IWailaPlugin {
	
	private WAILADisplayLightStorage hook = new WAILADisplayLightStorage();

	@Override
	public void register(IRegistrar registrar) {
		hook.initialize(registrar);
	}

}
