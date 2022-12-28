package etithespirit.orimod.common.tags;

import etithespirit.orimod.OriMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class OriModEntityTags {
	
	private OriModEntityTags() {}
	
	private static TagKey<EntityType<?>> create(String pName) {
		return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, OriMod.rsrc(pName));
	}
	
	public static final TagKey<EntityType<?>> ALIGNED = create("aligned");
	public static final TagKey<EntityType<?>> ALIGNED_LIGHT = create("aligned/light_aligned");
	public static final TagKey<EntityType<?>> ALIGNED_DECAY = create("aligned/decay_aligned");
	
}
