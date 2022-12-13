package etithespirit.orimod.common.item.crafting;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Item.Properties;

/**
 * An item used in crafting Light-based technologies.
 */
public class GenericLight16StackItem extends Item {
	public GenericLight16StackItem() {
		super(new Item.Properties().tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_PARTS).stacksTo(16));
	}
}
