package etithespirit.orimod.client.render.armor;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.tags.OriModItemTags;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ArmorRenderHelper {
	
	private static final Map<ArmorMaterial, ResourceLocation> MATERIAL_TO_FULL_TEXTURE_BINDINGS = new HashMap<>();
	
	/**
	 * A dummy biped armor layer. This is used for access to the getArmorResource method,
	 * which relies on having an instance despite not accessing any instance information.<br/>
	 * <strong>Naturally, this is not safe for use for any other purpose than access to this method.</strong>
	 */
	private static final HumanoidArmorLayer<?, ?, ?> DUMMY_BIP_ARMOR_LAYER = new HumanoidArmorLayer<>(null, null, null);
	
	/**
	 * Returns the appropriate armor texture for the given item.<br/>
	 * <br/>
	 * <strong>NOTE:</strong> The armor's material contains the namespace responsible for declaring the texture. This gets used in the path to the texture (NOT its namespace)!
	 * @param entity The entity the armor is being drawn on.
	 * @param stack The armor item being rendered.
	 * @param slot The slot said item is in.
	 * @param declaringModId The ID of the mod declaring this texture. It should be your own mod id. This is what gets used in the namespace of the texture's {@link ResourceLocation}.
	 * @param type Additional context for the vanilla armor texture. Not used for Spirit armors.
	 * @return The {@link ResourceLocation} to the appropriate texture.
	 */
	public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, String declaringModId, @Nullable String type) {
		if (!stack.is(OriModItemTags.SPECIALIZED_SPIRIT_ARMOR) || !SpiritIdentifier.isSpirit(entity)) {
			return DUMMY_BIP_ARMOR_LAYER.getArmorResource(entity, stack, slot, type);
		}
		
		/*
		ArmorItem item = (ArmorItem)stack.getItem();
		String mtlName = item.getMaterial().getName();
		ResourceLocation texture = MATERIAL_TO_NAME_BINDINGS.get(mtlName);
		if (texture == null) {
			texture = new ResourceLocation(mtlName);
			MATERIAL_TO_NAME_BINDINGS.put(mtlName, texture);
		}
		ResourceLocation realArmorLocation = ARMOR_LOCATION_CACHE.get(texture);
		if (realArmorLocation != null) {
			return realArmorLocation;
		}
		
		String rsrcName = String.format(java.util.Locale.ROOT, "%s:textures/models/spirit_armor/%s/%s", declaringModId, texture.getNamespace(), texture.getPath());
		if (type != null) rsrcName += "_" + type;
		rsrcName += ".png";
		realArmorLocation = new ResourceLocation(rsrcName);
		ARMOR_LOCATION_CACHE.put(texture, realArmorLocation);
		return realArmorLocation;
		*/
		
		ArmorItem item = (ArmorItem)stack.getItem();
		ArmorMaterial mtl = item.getMaterial();
		ResourceLocation result = MATERIAL_TO_FULL_TEXTURE_BINDINGS.get(mtl);
		if (result != null) return result;
		
		ResourceLocation texture = new ResourceLocation(mtl.getName());
		String rsrcName = String.format(java.util.Locale.ROOT, "%s:textures/models/spirit_armor/%s/%s", declaringModId, texture.getNamespace(), texture.getPath());
		if (type != null) rsrcName += "_" + type;
		rsrcName += ".png";
		
		result = new ResourceLocation(rsrcName);
		MATERIAL_TO_FULL_TEXTURE_BINDINGS.put(mtl, result);
		return result;
		
	}
}
