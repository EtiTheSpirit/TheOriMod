package etithespirit.etimod.item.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;

/**
 * The Lumo-Wand, a tool designed for Light-based circuitry.
 *
 * In the off chance that any other devs reading this code find this, yes, it is from SkySaga c:
 */
public class LumoWand extends Item {
	
	public LumoWand() {
		this(
			new Item.Properties()
			.rarity(Rarity.RARE)
			.tab(ItemGroup.TAB_TOOLS)
		);
	}
	
	public LumoWand(Properties props) {
		super(props);
	}
}
