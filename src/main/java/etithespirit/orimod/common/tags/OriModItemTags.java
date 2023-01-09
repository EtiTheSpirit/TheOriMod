package etithespirit.orimod.common.tags;

import etithespirit.orimod.OriMod;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class OriModItemTags {
	
	private OriModItemTags() {}
	
	public static final TagKey<Item> LIGHT_REPAIRABLE = ItemTags.create(OriMod.rsrc("light_repairable"));
	public static final TagKey<Item> DECAY_RESISTANT = ItemTags.create(OriMod.rsrc("decay_resistant"));
	public static final TagKey<Item> SPECIALIZED_SPIRIT_ARMOR = ItemTags.create(OriMod.rsrc("specialized_spirit_armors"));
	
	
}
