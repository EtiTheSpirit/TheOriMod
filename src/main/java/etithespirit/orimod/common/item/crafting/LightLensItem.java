package etithespirit.orimod.common.item.crafting;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Item.Properties;

/**
 * An item used in crafting Light-based technologies.
 */
public class LightLensItem extends Item {
	public LightLensItem() {
		super(new Item.Properties().tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_PARTS).stacksTo(16));
	}
}
