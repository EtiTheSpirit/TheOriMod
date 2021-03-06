package etithespirit.orimod.api;


import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.api.delegate.ISpiritMaterialAquisitionFunction;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * An API providing a means of registering custom sounds for when a spirit walks on your blocks or materials.
 * @author Eti
 */
@SuppressWarnings("unused")
public interface ISpiritSoundAPI {
	
	/** <strong>MUST BE CHECKED BEFORE OTHER METHODS ARE USED.</strong>
	 * @return Whether or not the API is installed.
	 */
	boolean isInstalled();
	
	/**
	 * Associates all states of the given block with the given material's sound.
	 * When a player walks on this block, and if they are currently a spirit,
	 * the sound associated with the given SpiritMaterial will play instead of the vanilla sound.<br/><br/>
	 *
	 * Note that setting a block will <em>not</em> override definitions for any of its child states (if they have been defined). This means that setting the
	 * entire block to a given material with this method, and then calling {@link #registerSpiritStepSound(BlockState, SpiritMaterial)}
	 * for a number of specific states of this block, is a perfectly valid method of providing a general default with deviations
	 * for special cases. The order in which these are called does not matter.
	 *
	 * @param entireBlockType The block that will have its sounds replaced for Spirits
	 * @param material The material dictating the unique sound played.
	 * @throws IllegalArgumentException If the input block or material is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void registerSpiritStepSound(Block entireBlockType, SpiritMaterial material) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * Associates the specific block state to the given material's sound.
	 * When a player walks on this block, and if they are currently a spirit,
	 * the sound associated with the given SpiritMaterial will play instead of the vanilla sound.
	 *
	 * @param specificState The block in a specific state that will have its sounds replaced for Spirits
	 * @param material The material dictating the unique sound played.
	 * @throws IllegalArgumentException If the input state or material is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void registerSpiritStepSound(BlockState specificState, SpiritMaterial material) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * If called, then all states of the given block will play their custom step sound if the
	 * player is walking <em>in</em> the same BlockPos rather than on top of it. This is useful for things like snow layer blocks,
	 * which should cause the player to sound like they are walking in snow.
	 *
	 * @param entireBlockType The block from which all states will be tested for occupancy rather than being stepped on.
	 * @throws IllegalArgumentException If the input block is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setUseIfIn(Block entireBlockType) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * If called, then the specific BlockState will play its custom step sound if the player is walking
	 * <em>in</em> the same BlockPos rather than on top of it. This is useful for things like snow layer blocks,
	 * which should cause the player to sound like they are walking in snow.
	 *
	 * @param specificState The BlockState that will be tested for occupancy rather than being stepped on.
	 * @throws IllegalArgumentException If the input block is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setUseIfIn(BlockState specificState) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * Associates the given custom material with a given SpiritMaterial.
	 * @param material The material to associate.
	 * @param spiritMaterial The {@link SpiritMaterial} that will be associated with this custom material.
	 * @throws ArgumentNullException If the input material is null.
	 * @throws IllegalArgumentException If the input material is vanilla or defined by EtiMod.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void associateMaterialWith(Material material, SpiritMaterial spiritMaterial) throws ArgumentNullException, IllegalArgumentException, IllegalStateException;
	
	/**
	 * Associates the entire block type with the given {@link ISpiritMaterialAquisitionFunction} which can be used to conditionally return a {@link SpiritMaterial} best suited for the block the entity is standing on and/or within.<br/>
	 * Note that this does not have a variant for a specific BlockState. Registering for an entire Block will cause it to only be run when that block is either under the player or occupying the same space as that player. This means it is possible to get its state from the world.
	 * @param entireBlockType The type of the block that this function will run on.
	 * @param getter The {@link ISpiritMaterialAquisitionFunction} used to determine the appropriate material.
	 * @throws ArgumentNullException If the input block type or getter is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setSpecialMaterialPredicate(Block entireBlockType, ISpiritMaterialAquisitionFunction getter) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * Associates the entire material with the given {@link ISpiritMaterialAquisitionFunction} which can be used to conditionally return a {@link SpiritMaterial} best suited for the material of the block the entity is standing on and/or within.
	 * @param material The type of the material that this function will run on.
	 * @param getter The {@link ISpiritMaterialAquisitionFunction} used to determine the appropriate material.
	 *
	 * @throws ArgumentNullException If the input material or getter is null.
	 * @throws IllegalArgumentException If the input material is vanilla or defined by EtiMod.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setSpecialMaterialPredicate(Material material, ISpiritMaterialAquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException;
	
}
