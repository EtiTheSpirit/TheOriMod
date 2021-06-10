package etithespirit.etimod.api;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.meta.When;

import etithespirit.etimod.api.delegate.ISpiritMaterialAquisitionFunction;
import etithespirit.etimod.exception.ArgumentNullException;
import etithespirit.etimod.routine.SimplePromise;
import etithespirit.etimod.util.blockmtl.SpiritMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;

final class Storage {

	private Storage() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static ISpiritStateAPI spiritStateAPI = null;
	
	private static ISpiritSoundAPI spiritSoundAPI = null;
	
	static final ISpiritStateAPI getSpiritStateAPI() {
		if (spiritStateAPI == null) {
			try {
				Class<?> spiritAPIImpl = Class.forName("etithespirit.etimod.api.impl.SpiritAPI");
				@SuppressWarnings("deprecation")Object instance = spiritAPIImpl.newInstance();
				spiritStateAPI = (ISpiritStateAPI)instance;
			} catch (Exception exc) {
				spiritStateAPI = new ISpiritStateAPI() {

					@Override
					public boolean isInstalled() {
						return false;
					}

					@Override
					public SimplePromise<Boolean> isPlayerSpirit(@Nonnull(when=When.MAYBE) UUID playerId, boolean forceSkipLocal) {
						throw new IllegalStateException("EtiMod is not installed.");
					}
					
				};
			}
		}
		return spiritStateAPI;
	}
	
	static final ISpiritSoundAPI getSpiritSoundAPI() {
		if (spiritSoundAPI == null) {
			try {
				Class<?> apiClass = Class.forName("etithespirit.etimod.api.impl.SpiritSoundAPI");
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
