package etithespirit.orimod.common.item.crafting;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Item.Properties;

/**
 * An item used in crafting Light-based technologies.
 */
public class LightLens extends Item {
	public LightLens(Properties pProperties) {
		super(pProperties.tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_PARTS));
	}
	
	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 16;
	}
}
