package etithespirit.orimod.common.item;

import etithespirit.orimod.OriMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class OriModItemTags {
	
	private OriModItemTags() {}
	
	public static final TagKey<Item> LIGHT_REPAIRABLE = ItemTags.create(new ResourceLocation(OriMod.MODID, "light_repairable"));
	public static final TagKey<Item> HARDLIGHT_ARMOR = ItemTags.create(new ResourceLocation(OriMod.MODID, "hardlight_armor"));
	
	
}
