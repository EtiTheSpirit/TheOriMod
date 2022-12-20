package etithespirit.orimod.common.item.crafting;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class GorlekIngotItem extends Item {
	public GorlekIngotItem() {
		super((new Item.Properties()).rarity(Rarity.RARE).tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_PARTS));
	}
}
