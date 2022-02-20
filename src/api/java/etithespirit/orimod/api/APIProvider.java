package etithespirit.orimod.api;


import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.api.delegate.ISpiritMaterialAquisitionFunction;
import etithespirit.orimod.spiritmaterial.SpiritMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * Provides access to a copy of the APIs offered by this mod.
 * @author Eti
 */
public final class APIProvider {
	
	private APIProvider() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	private static ISpiritSoundAPI spiritSoundAPI = null;
	
	/**
	 * Returns an API that allows for custom block/material sound overrides to be registered to Spirits.
	 * Use this if you are interested in any of your mod blocks having different sounds when they are stepped on
	 * by spirits. This is, of course, purely cosmetic and exists for the sake of immersion.<br/>
	 * <br/>
	 * An example of when to use this is when looking at a block such as {@code biomesoplenty:flesh}.
	 * Its defined material for its Block class makes it use {@link SpiritMaterial#WOOL} as per the default mapping,
	 * which is not fitting. This API could be used to set it to something like {@link SpiritMaterial#SHROOM}, which
	 * would match the intended material of the block far better than its vanilla material.<br/>
	 * <br/>
	 * The test order for material overrides is: <ol>
	 * <li>Block -&gt; SpiritMaterial Conditional Overrides</li>
	 * <li>Custom Material -&gt; SpiritMaterial Conditional Overrides</li>
	 * <li>BlockState -&gt; SpiritMaterial Bindings</li>
	 * <li>Block -&gt; SpiritMaterial Bindings</li>
	 * <li>Custom Material -&gt; SpiritMaterial Bindings</li>
	 * <li>Vanilla Material -&gt; SpiritMaterial Bindings</li>
	 * </ol>
	 * The first one to yield a result is used. If none of these yield a result, the vanilla sound will be passed through.
	 * @return The instance of the sound API. If the mod is not installed (and only the API is), then a dummy API will be returned. Use {@link ISpiritSoundAPI#isInstalled()} to determine whether or not the returned API is usable, as the dummy API will raise an exception upon calling any of its members.
	 */
	static ISpiritSoundAPI getSpiritSoundAPI() {
		if (spiritSoundAPI == null) {
			try {
				Class<?> apiClass = Class.forName("etithespirit.orimod.apiimpl.SpiritSoundAPI");
				Object instance = apiClass.getDeclaredConstructor().newInstance();
				spiritSoundAPI = (ISpiritSoundAPI)instance;
			} catch (Exception exc) {
				spiritSoundAPI = new ISpiritSoundAPI() {
					
					@Override
					public boolean isInstalled() {
						return false;
					}
					
					@Override
					public void registerSpiritStepSound(Block entireBlockType, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void registerSpiritStepSound(BlockState specificState, SpiritMaterial material) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setUseIfIn(Block entireBlockType) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setUseIfIn(BlockState specificState) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void associateMaterialWith(Material material, SpiritMaterial spiritMaterial) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setSpecialMaterialPredicate(Block entireBlockType, ISpiritMaterialAquisitionFunction getter) throws ArgumentNullException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
					@Override
					public void setSpecialMaterialPredicate(Material entireBlockType, ISpiritMaterialAquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException {
						throw new IllegalStateException("OriMod is not installed.");
					}
					
				};
			}
		}
		
		return spiritSoundAPI;
	}
	
}
