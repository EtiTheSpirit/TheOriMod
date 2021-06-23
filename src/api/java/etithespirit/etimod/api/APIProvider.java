package etithespirit.etimod.api;

import etithespirit.etimod.api.delegate.ISpiritMaterialAquisitionFunction;
import etithespirit.etimod.exception.ArgumentNullException;
import etithespirit.etimod.spiritmaterial.SpiritMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;

/**
 * If you are looking for a means of acquiring the API instance, go to the actual interfaces themselves, not this.
 */
final class APIProvider {

	private APIProvider() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static ISpiritSoundAPI spiritSoundAPI = null;
	
	static ISpiritSoundAPI getSpiritSoundAPI() {
		if (spiritSoundAPI == null) {
			try {
				Class<?> apiClass = Class.forName("etithespirit.etimod.apiimpl.SpiritSoundAPI");
				@SuppressWarnings("deprecation") Object instance = apiClass.newInstance();
				spiritSoundAPI = (ISpiritSoundAPI)instance;
			} catch (Exception exc) {
				spiritSoundAPI = new ISpiritSoundAPI() {

					@Override
					public boolean isInstalled() {
						return false;
					}

					@Override
					public void registerSpiritStepSound(Block entireBlockType, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("EtiMod is not installed.");
					}

					@Override
					public void registerSpiritStepSound(BlockState specificState, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("EtiMod is not installed.");
					}

					@Override
					public void setUseIfIn(Block entireBlockType) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("EtiMod is not installed.");
					}

					@Override
					public void setUseIfIn(BlockState specificState) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("EtiMod is not installed.");
					}

					@Override
					public void associateMaterialWith(Material material, SpiritMaterial spiritMaterial) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw new IllegalStateException("EtiMod is not installed.");
					}

					@Override
					public void setSpecialMaterialPredicate(Block entireBlockType, ISpiritMaterialAquisitionFunction getter) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("EtiMod is not installed.");
					}

					@Override
					public void setSpecialMaterialPredicate(Material entireBlockType, ISpiritMaterialAquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw new IllegalStateException("EtiMod is not installed.");
					}
					
				};
			}
		}
		
		return spiritSoundAPI;
	}
	
}
