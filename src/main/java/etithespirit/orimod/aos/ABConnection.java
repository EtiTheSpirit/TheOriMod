package etithespirit.orimod.aos;

import etithespirit.orimod.common.tile.light.AbstractLightEnergyHub;
import etithespirit.orimod.common.tile.light.AbstractLightEnergyLink;
import etithespirit.orimod.config.OriModConfigs;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;

public final class ABConnection {
	
	private static boolean USE_GREEDY_MEMORY;
	public static boolean _greedySet = false;
	
	public final AbstractLightEnergyHub from;
	public final AbstractLightEnergyHub to;
	private final List<AbstractLightEnergyLink> links;
	
	
	private ABConnection(AbstractLightEnergyHub alpha, AbstractLightEnergyHub bravo, List<AbstractLightEnergyLink> links) {
		this.from = alpha;
		this.to = bravo;
		this.links = List.copyOf(links);
		if (!_greedySet) {
			_greedySet = true;
			USE_GREEDY_MEMORY = OriModConfigs.GREEDY_ASSEMBLY_OPTIMIZATION.get();
		}
	}
	
	/**
	 * Verifies the integrity of this connection by checking if all links exist.
	 * @return True if this connection is OK and usable, false if it should be disposed of due to being invalid.
	 */
	public boolean verify() {
		return false;
	}
	
	
}
