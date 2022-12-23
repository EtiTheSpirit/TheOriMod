package etithespirit.orimod.common.item.crafting;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class GorlekNetheriteAlloyIngot extends Item {
	
	public GorlekNetheriteAlloyIngot() {
		super((new Item.Properties()).rarity(Rarity.EPIC).tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_PARTS).fireResistant());
	}
	
}
