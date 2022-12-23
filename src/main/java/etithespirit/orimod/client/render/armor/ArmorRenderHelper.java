package etithespirit.orimod.client.render.armor;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.tags.OriModItemTags;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ArmorRenderHelper {
	
	private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = new HashMap<>();
	private static final Map<String, ResourceLocation> MATERIAL_TO_NAME_BINDINGS = new HashMap<>();
	
	/**
	 * A dummy biped armor layer. This is used for access to the getArmorResource method,
	 * which relies on having an instance despite not accessing any instance information.<br/>
	 * <strong>Naturally, this is not safe for use for any other purpose than access to this method.</strong>
	 */
	private static final HumanoidArmorLayer<?, ?, ?> DUMMY_BIP_ARMOR_LAYER = new HumanoidArmorLayer<>(null, null, null);
	
	/**
	 * Returns the appropriate armor texture for the given item.
	 * @param entity The entity the armor is being drawn on.
	 * @param stack The armor item being rendered.
	 * @param slot The slot said item is in.
	 * @param type Additional context for the vanilla armor texture. Not used for Spirit armors.
	 * @return The {@link ResourceLocation} to the appropriate texture.
	 */
	public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
		if (!stack.is(OriModItemTags.SPECIALIZED_SPIRIT_ARMOR)) {
			return DUMMY_BIP_ARMOR_LAYER.getArmorResource(entity, stack, slot, type);
		}
		
		ArmorItem item = (ArmorItem)stack.getItem();
		String mtlName = item.getMaterial().getName();
		ResourceLocation texture = MATERIAL_TO_NAME_BINDINGS.get(mtlName);
		if (texture == null) {
			texture = new ResourceLocation(mtlName);
			MATERIAL_TO_NAME_BINDINGS.put(mtlName, texture);
		}
		String rsrcName = String.format(java.util.Locale.ROOT, "%s:textures/models/spirit_armor/%s.png", texture.getNamespace(), texture.getPath());
		ResourceLocation rsrc = ARMOR_LOCATION_CACHE.get(rsrcName);
		if (rsrc == null) {
			rsrc = new ResourceLocation(rsrcName);
			ARMOR_LOCATION_CACHE.put(rsrcName, rsrc);
		}
		
		return rsrc;
	}
}
