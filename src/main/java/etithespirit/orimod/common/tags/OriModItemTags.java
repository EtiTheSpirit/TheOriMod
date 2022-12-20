package etithespirit.orimod.common.tags;

import etithespirit.orimod.OriMod;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class OriModItemTags {
	
	private OriModItemTags() {}
	
	public static final TagKey<Item> LIGHT_REPAIRABLE = ItemTags.create(OriMod.rsrc("light_repairable"));
	public static final TagKey<Item> HARDLIGHT_ARMOR = ItemTags.create(OriMod.rsrc("hardlight_armor"));
	
	
}
