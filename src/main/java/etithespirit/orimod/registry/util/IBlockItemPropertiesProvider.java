package etithespirit.orimod.registry.util;

import net.minecraft.world.item.Item;

/**
 * A lazy utility for forge registries to provide the BlockItem properties of a block.
 */
public interface IBlockItemPropertiesProvider {
	
	/**
	 * Returns the properties for this ItemBlock for use in forge registries.
	 * @return
	 */
	Item.Properties getPropertiesOfItem();
	
}
