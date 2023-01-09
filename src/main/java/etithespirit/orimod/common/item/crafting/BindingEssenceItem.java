package etithespirit.orimod.common.item.crafting;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BindingEssenceItem extends Item {
	
	public BindingEssenceItem() {
		super(new Item.Properties().tab(OriModCreativeModeTabs.SPIRIT_MACHINERY_PARTS).stacksTo(16));
	}
	
	@Override
	public Component getName(ItemStack pStack) {
		return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
	}
	
}
