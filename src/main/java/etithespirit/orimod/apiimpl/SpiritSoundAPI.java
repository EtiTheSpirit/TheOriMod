package etithespirit.orimod.apiimpl;


import etithespirit.exception.ArgumentNullException;
import etithespirit.exception.ConstantErrorMessages;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.api.interfaces.ISpiritSoundAPI;
import etithespirit.orimod.api.delegate.ISpiritMaterialAcquisitionFunction;
import etithespirit.orimod.spiritmaterial.BlockToMaterialBindingLgc;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import etithespirit.orimod.spiritmaterial.data.SpiritMaterialContainer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

/**
 * An implementation of the spirit sound API, which is used if the mod is installed.
 * @author Eti
 */
public class SpiritSoundAPI implements ISpiritSoundAPI {
	
	private static void throwLoadCompleteIfNeeded() {
		if (OriMod.forgeLoadingComplete()) throw new IllegalStateException(ConstantErrorMessages.FORGE_LOADING_COMPLETED);
	}
	
	public final String declaringModId;
	private final SpiritMaterialContainer container;
	
	public SpiritSoundAPI(String mod) {
		declaringModId = mod;
		container = SpiritMaterialContainer.getForThisMod();
	}
	
	@Override
	public boolean isInstalled() {
		return true;
	}
	
	@Override
	public void registerBlock(Supplier<Block> entireBlockType, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(entireBlockType, "entireBlockType");
		ArgumentNullException.throwIfNull(material, "material");
		container.registerBlock(entireBlockType, material);
	}
	
	@Override
	public void registerBlockState(Supplier<BlockState> specificState, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(specificState, "specificState");
		ArgumentNullException.throwIfNull(material, "material");
		container.registerState(specificState, material);
	}
	
	@Override
	public void registerTag(TagKey<Block> blockTag, SpiritMaterial material) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(blockTag, "blockTag");
		ArgumentNullException.throwIfNull(material, "material");
		container.registerTag(blockTag, material);
	}
	
	@Override
	public void setUseIfInBlock(Supplier<Block> entireBlockType, boolean useIfInside) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(entireBlockType, "entireBlockType");
		container.setUseIfInsideBlock(entireBlockType, useIfInside);
	}
	
	@Override
	public void setUseIfInState(Supplier<BlockState> specificState, boolean useIfInside) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(specificState, "specificState");
		container.setUseIfInsideState(specificState, useIfInside);
	}
	
	@Override
	public void setUseIfInBlock(TagKey<Block> blockTag, boolean useIfInside) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(blockTag, "blockTag");
		container.setUseIfInsideTag(blockTag, useIfInside);
	}
	
	@Override
	public void associateMaterialWith(Material material, SpiritMaterial spiritMaterial) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(material, "material");
		ArgumentNullException.throwIfNull(spiritMaterial, "spiritMaterial");
		container.registerMaterial(material, spiritMaterial);
	}
	
	@Override
	public void setSpecialMaterialPredicate(Supplier<Block> entireBlockType, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(entireBlockType, "entireBlockType");
		ArgumentNullException.throwIfNull(getter, "getter");
		container.registerBlock(entireBlockType, getter);
	}
	
	@Override
	public void setSpecialMaterialPredicate(Material material, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(material, "material");
		ArgumentNullException.throwIfNull(getter, "getter");
		container.registerMaterial(material, getter);
	}
	
	@Override
	public void setSpecialMaterialPredicate(TagKey<Block> blockTag, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		ArgumentNullException.throwIfNull(blockTag, "blockTag");
		ArgumentNullException.throwIfNull(getter, "getter");
		container.registerTag(blockTag, getter);
	}
	
	@Override
	public String toString() {
		return "SpiritSoundAPI[Mod=" + declaringModId + "]";
	}
}
