package etithespirit.orimod.spiritmaterial.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public final class RegistrationHelpers {
	
	private RegistrationHelpers() {}
	
	private static Set<Material> vanillaMaterials = null;
	//private static Set<Block> vanillaBlocks = null;
	//private static Set<TagKey<Block>> vanillaBlockTags = null;
	
	/**
	 * Returns true if this material is vanilla based on whether or not it is a field in {@link Material}.
	 * @param material The material to check.
	 * @return True if the reference is one present in {@link Material}.
	 */
	public static boolean isMaterialVanilla(Material material) {
		if (vanillaMaterials == null) {
			vanillaMaterials = new HashSet<>();
			Field[] allFields = Material.class.getFields();
			for (Field field : allFields) {
				int fMods = field.getModifiers();
				if (Modifier.isFinal(fMods) && Modifier.isStatic(fMods)) {
					try {
						vanillaMaterials.add((Material)field.get(null));
					} catch (Exception ignored) { }
				}
			}
		}
		
		return vanillaMaterials.contains(material);
	}
	
	/**
	 * Returns true if this block tag is vanilla based on its namespace.
	 * @param tag The tag to check.
	 * @return True if the given tag is part of Minecraft.
	 */
	//@SuppressWarnings("unchecked")
	public static boolean isBlockTagVanilla(TagKey<Block> tag) {
		return tag.location().getNamespace().equals("minecraft");
		/*
		if (vanillaBlockTags == null) {
			vanillaBlockTags = new HashSet<>();
			Field[] allFields = BlockTags.class.getFields();
			for (Field field : allFields) {
				int fMods = field.getModifiers();
				if (Modifier.isFinal(fMods) && Modifier.isStatic(fMods)) {
					try {
						vanillaBlockTags.add((TagKey<Block>)field.get(null));
					} catch (Exception ignored) { }
				}
			}
		}
		*/
	}
	
	/**
	 * Returns true if this block is vanilla based on its namespace.
	 * @param block The block to check.
	 * @return True if the given block is part of Minecraft.
	 */
	public static boolean isBlockVanilla(Block block) {
		ResourceLocation rsrc = getIDOf(block);
		if (rsrc != null) {
			return rsrc.getNamespace().equals("minecraft");
		}
		throw new NullPointerException("The given block is not registered or vanilla?");
		/*
		if (vanillaBlocks == null) {
			vanillaBlocks = new HashSet<>();
			Field[] allFields = Blocks.class.getFields();
			for (Field field : allFields) {
				int fMods = field.getModifiers();
				if (Modifier.isFinal(fMods) && Modifier.isStatic(fMods)) {
					try {
						vanillaBlocks.add((Block)field.get(null));
					} catch (Exception ignored) { }
				}
			}
		}
		return vanillaBlocks.contains(block);
		*/
	}
	
	public static @Nullable ResourceLocation getIDOf(Block block) {
		ResourceLocation rsrc = ForgeRegistries.BLOCKS.getKey(block);
		if (rsrc == null) {
			rsrc = RegistryManager.VANILLA.getRegistry(ForgeRegistries.Keys.BLOCKS).getKey(block);
		}
		return rsrc;
	}
	
}
