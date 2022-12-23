package etithespirit.orimod.common.item.crafting;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RawGorlekOreItem extends Item {
	public RawGorlekOreItem() {
		super((new Item.Properties()).tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_PARTS));
	}
	
}
