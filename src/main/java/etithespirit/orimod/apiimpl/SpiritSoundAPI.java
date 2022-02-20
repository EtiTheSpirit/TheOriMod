package etithespirit.orimod.apiimpl;


import etithespirit.exception.ArgumentNullException;
import etithespirit.exception.ConstantErrorMessages;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.api.ISpiritSoundAPI;
import etithespirit.orimod.api.delegate.ISpiritMaterialAquisitionFunction;
import etithespirit.orimod.spiritmaterial.BlockToMaterialBinding;
import etithespirit.orimod.spiritmaterial.SpiritMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * An implementation of the spirit sound API, which is used if the mod is installed.
 * @author Eti
 */
public class SpiritSoundAPI implements ISpiritSoundAPI {
	
	private static void throwLoadCompleteIfNeeded() {
		if (OriMod.forgeLoadingComplete()) throw new IllegalStateException(ConstantErrorMessages.FORGE_LOADING_COMPLETED);
	}
	
	public SpiritSoundAPI() { } // For reflection
	
	@Override
	public boolean isInstalled() {
		return true;
	}
	
	@Override
	public void registerSpiritStepSound(Block entireBlockType, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		if (entireBlockType == null) throw new ArgumentNullException("entireBlockType");
		if (material == null) throw new ArgumentNullException("material");
		BlockToMaterialBinding.setSpiritMaterialFor(entireBlockType, material);
	}
	
	@Override
	public void registerSpiritStepSound(BlockState specificState, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		if (specificState == null) throw new ArgumentNullException("specificState");
		if (material == null) throw new ArgumentNullException("material");
		BlockToMaterialBinding.setSpiritMaterialForState(specificState, material);
	}
	
	@Override
	public void setUseIfIn(Block entireBlockType) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		if (entireBlockType == null) throw new ArgumentNullException("entireBlockType");
		BlockToMaterialBinding.useIfIn(entireBlockType);
	}
	
	@Override
	public void setUseIfIn(BlockState specificState) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		if (specificState == null) throw new ArgumentNullException("specificState");
		BlockToMaterialBinding.useIfIn(specificState);
	}
	
	@Override
	public void associateMaterialWith(Material material, SpiritMaterial spiritMaterial) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		if (material == null) throw new ArgumentNullException("material");
		if (spiritMaterial == null) throw new ArgumentNullException("spiritMaterial");
		BlockToMaterialBinding.associateMCMaterialWith(material, spiritMaterial);
	}
	
	@Override
	public void setSpecialMaterialPredicate(Block entireBlockType, ISpiritMaterialAquisitionFunction getter) throws ArgumentNullException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		if (entireBlockType == null) throw new ArgumentNullException("entireBlockType");
		if (getter == null) throw new ArgumentNullException("getter");
		BlockToMaterialBinding.setConditionForBlock(entireBlockType, getter);
	}
	
	@Override
	public void setSpecialMaterialPredicate(Material material, ISpiritMaterialAquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
		throwLoadCompleteIfNeeded();
		if (material == null) throw new ArgumentNullException("material");
		if (getter == null) throw new ArgumentNullException("getter");
		BlockToMaterialBinding.setConditionForMaterial(material, getter);
	}
	
}
