package etithespirit.orimod.common.block.decay;

import etithespirit.orimod.config.OriModConfigs;

public final class DecayWorldConfigHelper {
	
	private DecayWorldConfigHelper() {}
	
	public static DecayWorldConfigBehavior getSpreadLimits(SpreadType spreadType) {
		if (spreadType == SpreadType.GENERAL) {
			// general decay blocks
			return OriModConfigs.DECAY_SPREADING.get();
			
		} else if (spreadType == SpreadType.SURFACE_MYCELIUM) {
			// surface mycelium
			if (OriModConfigs.DECAY_SPREADING.get().permissiveness < OriModConfigs.DECAY_COATING_SPREADING.get().permissiveness) {
				return OriModConfigs.DECAY_SPREADING.get();
			}
			return OriModConfigs.DECAY_COATING_SPREADING.get();
			
		} else {
			// fluid
			if (OriModConfigs.DECAY_SPREADING.get().permissiveness < OriModConfigs.DECAY_FLUID_SPREADING.get().permissiveness) {
				return OriModConfigs.DECAY_SPREADING.get();
			}
			return OriModConfigs.DECAY_FLUID_SPREADING.get();
		}
	}
	
	public enum SpreadType {
		GENERAL,
		SURFACE_MYCELIUM,
		FLUID
	}
	
}
