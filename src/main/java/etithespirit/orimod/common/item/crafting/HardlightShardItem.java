package etithespirit.orimod.common.item.crafting;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class HardlightShardItem extends Item {
	public HardlightShardItem() {
		super(new Item.Properties().stacksTo(64).fireResistant().tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_PARTS));
	}
	
	@Override
	public Component getName(ItemStack pStack) {
		return StaticData.getNameAsLight(super.getName(pStack));
	}
}
