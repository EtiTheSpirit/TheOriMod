package etithespirit.orimod.modinterop.biomesoplenty;

import biomesoplenty.core.BiomesOPlenty;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import etithespirit.orimod.spiritmaterial.data.SpiritMaterialContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class BiomesOPlentySpiritSoundProvider {
	
	public static void initialize() {
		SpiritMaterialContainer ctr = SpiritMaterialContainer.getForOtherMod(BiomesOPlenty.MOD_ID);
		
		TagKey<Block> flesh = BlockTags.create(new ResourceLocation(BiomesOPlenty.MOD_ID, "flesh"));
		ctr.registerTag(flesh, SpiritMaterial.SHROOM);
		ctr.registerBlock(new ResourceLocation(BiomesOPlenty.MOD_ID, "glowshroom_block"), SpiritMaterial.SHROOM);
		
	}
	
}
