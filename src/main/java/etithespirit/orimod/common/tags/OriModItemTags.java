package etithespirit.orimod.common.tags;

import etithespirit.orimod.OriMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import static etithespirit.orimod.common.tags.TagHelpers.oriMod;

public final class OriModItemTags {
	
	private OriModItemTags() {}
	
	public static final TagKey<Item> LIGHT_REPAIRABLE = ItemTags.create(oriMod("light_repairable"));
	public static final TagKey<Item> HARDLIGHT_ARMOR = ItemTags.create(oriMod("hardlight_armor"));
	
	
}
