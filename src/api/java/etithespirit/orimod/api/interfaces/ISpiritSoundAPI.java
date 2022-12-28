package etithespirit.orimod.api.interfaces;


import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.api.delegate.ISpiritMaterialAcquisitionFunction;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

/**
 * An API providing a means of registering custom sounds for when a spirit walks on your blocks or materials.<br/>
 * <br/>
 * An example of when to use this is when looking at a block such as {@code biomesoplenty:flesh}.
 * Its defined material for its Block class makes it use {@link SpiritMaterial#WOOL} as per the default mapping,
 * which is not fitting. This API could be used to set it to something like {@link SpiritMaterial#SHROOM}, which
 * would match the intended material of the block far better than its vanilla material.<br/>
 * <br/>
 * The test order for material overrides is: <ol>
 * <li>Block -&gt; {@link ISpiritMaterialAcquisitionFunction SpiritMaterial Conditional Overrides}</li>
 * <li>Custom Material -&gt; {@link ISpiritMaterialAcquisitionFunction SpiritMaterial Conditional Overrides}</li>
 * <li>Block Tag -&gt; {@link ISpiritMaterialAcquisitionFunction SpiritMaterial Conditional Overrides}</li>
 * <li>BlockState -&gt; SpiritMaterial Bindings</li>
 * <li>Block -&gt; SpiritMaterial Bindings</li>
 * <li>Block Tag -&gt; SpiritMaterial Bindings</li>
 * <li>Custom Material -&gt; SpiritMaterial Bindings</li>
 * <li>Vanilla Material -&gt; SpiritMaterial Bindings</li>
 * </ol>
 * The first one to yield a result is used. If none of these yield a result, the vanilla sound will be passed through.
 * @author Eti
 */
@SuppressWarnings("unused")
public interface ISpiritSoundAPI {
	
	/** <strong>MUST BE CHECKED BEFORE OTHER METHODS ARE USED.</strong> This validates that the mod is actually installed by the user.
	 * @return Whether or not the API is installed.
	 */
	boolean isInstalled();
	
	/**
	 * Associates all states of the given block with the given material's sound.
	 * When a player walks on this block, and if they are currently a spirit,
	 * the sound associated with the given SpiritMaterial will play instead of the vanilla sound.<br/><br/>
	 *
	 * Note that setting a block will <em>not</em> override definitions for any of its child states (if they have been defined). This means that setting the
	 * entire block to a given material with this method, and then calling {@link #registerBlockState(Supplier, SpiritMaterial)}
	 * for a number of specific states of this block, is a perfectly valid method of providing a general default with deviations
	 * for special cases. The order in which these are called does not matter.
	 *
	 * @param entireBlockType The block that will have its sounds replaced for Spirits
	 * @param material The material dictating the unique sound played.
	 * @throws ArgumentNullException If the input block or material is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void registerBlock(Supplier<Block> entireBlockType, SpiritMaterial material) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * Associates the specific block state to the given material's sound.
	 * When a player walks on this block, and if they are currently a spirit,
	 * the sound associated with the given SpiritMaterial will play instead of the vanilla sound.
	 *
	 * @param specificState The block in a specific state that will have its sounds replaced for Spirits
	 * @param material The material dictating the unique sound played.
	 * @throws ArgumentNullException If the input state or material is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void registerBlockState(Supplier<BlockState> specificState, SpiritMaterial material) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * Associates the specific block tag (and all child tags, assuming the tag's inheritence was made properly by you the modder) with the given material.
	 * Tags are a great way to generalize entire classifications of blocks as they tend to follow stricter rules than materials, but should be handled
	 * with care.<br/>
	 * <br/>
	 * <strong>Note:</strong> You <em>can not register</em> vanilla tags nor forge tags! They are already bound by this mod. Consider registering your own tags instead.
	 * Please note: A "forge tag" is constituted by what is present in {@link net.minecraftforge.common.Tags}. Many mods implement tags in the forge namespace (for collections
	 * like <a href="https://forge.gemwire.uk/wiki/Tags#Community_Tags">Forge Community Tags</a>) which, while they have the forge namespace, do not count as forge tags.
	 * <strong>Overriding a tag that has already been registered will display a warning. Be on the lookout!</strong>
	 *
	 * @param blockTag The tag of the block.
	 * @param material
	 * @throws ArgumentNullException If the input tag or block is null.
	 * @throws IllegalArgumentException If the tag is vanilla or defined by forge in {@link net.minecraftforge.common.Tags Tags}.
	 * @throws IllegalStateException
	 */
	void registerTag(TagKey<Block> blockTag, SpiritMaterial material) throws ArgumentNullException, IllegalArgumentException, IllegalStateException;
	
	/**
	 * If called, then all states of the given block will play their custom step sound if the
	 * player is walking <em>in</em> the same BlockPos rather than on top of it. This is useful for blocks without collisions.<br/>
	 * <br/>
	 * <strong>Note:</strong> This is <em>no longer acceptable</em> to use for thin blocks (i.e. carpet, snow) as the bug
	 * causing incorrect detection of these blocks was fixed in mod ver. 1.19.2-1.2.0
	 *
	 * @param entireBlockType The block from which all states will be tested for occupancy rather than being stepped on.
	 * @throws ArgumentNullException If the input block is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setUseIfInBlock(Supplier<Block> entireBlockType, boolean useIfInside) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * If called, then the specific BlockState will play its custom step sound if the player is walking
	 * <em>in</em> the same BlockPos rather than on top of it. This is useful for blocks without collisions.<br/>
	 * <br/>
	 * <strong>Note:</strong> This is <em>no longer acceptable</em> to use for thin blocks (i.e. carpet, snow) as the bug
	 * causing incorrect detection of these blocks was fixed in mod ver. 1.19.2-1.2.0
	 *
	 * @param specificState The BlockState that will be tested for occupancy rather than being stepped on.
	 * @throws ArgumentNullException If the input block is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setUseIfInState(Supplier<BlockState> specificState, boolean useIfInside) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * If called, all blocks with the given tag will play its custom step sound if the player is walking
	 * <em>in</em> the same BlockPos rather than on top of it. This is useful for blocks without collisions.<br/>
	 * <br/>
	 * <strong>Note:</strong> This is <em>no longer acceptable</em> to use for thin blocks (i.e. carpet, snow) as the bug
	 * causing incorrect detection of these blocks was fixed in mod ver. 1.19.2-1.2.0
	 *
	 * @param blockTag The tag that will be tested for occupancy rather than being stepped on.
	 * @throws ArgumentNullException If the input block is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setUseIfInBlock(TagKey<Block> blockTag, boolean useIfInside) throws ArgumentNullException, IllegalStateException;
	
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
	 * Associates the entire block type with the given {@link ISpiritMaterialAcquisitionFunction} which can be used to conditionally return a {@link SpiritMaterial} best suited for the block the entity is standing on and/or within.<br/>
	 * Note that this does not have a variant for a specific BlockState. Registering for an entire Block will cause it to only be run when that block is either under the player or occupying the same space as that player. This means it is possible to get its state from the world.
	 * @param entireBlockType The type of the block that this function will run on.
	 * @param getter The {@link ISpiritMaterialAcquisitionFunction} used to determine the appropriate material.
	 * @throws ArgumentNullException If the input block type or getter is null.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setSpecialMaterialPredicate(Supplier<Block> entireBlockType, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalStateException;
	
	/**
	 * Associates the entire material with the given {@link ISpiritMaterialAcquisitionFunction} which can be used to conditionally return a {@link SpiritMaterial} best suited for the material of the block the entity is standing on and/or within.
	 * @param material The type of the material that this function will run on.
	 * @param getter The {@link ISpiritMaterialAcquisitionFunction} used to determine the appropriate material.
	 *
	 * @throws ArgumentNullException If the input material or getter is null.
	 * @throws IllegalArgumentException If the input material is vanilla or defined by The Ori Mod.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setSpecialMaterialPredicate(Material material, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException;
	
	/**
	 * Associates the entire block tag with the given {@link ISpiritMaterialAcquisitionFunction} which can be used to conditionally return a {@link SpiritMaterial} best suited for the material of the block the entity is standing on and/or within.
	 * @param blockTag The tag to associate with the material function.
	 * @param getter The {@link ISpiritMaterialAcquisitionFunction} used to determine the appropriate material.
	 *
	 * @throws ArgumentNullException If the input material or getter is null.
	 * @throws IllegalArgumentException If the input material is vanilla or defined by The Ori Mod.
	 * @throws IllegalStateException If mod initialization has completed, or if {@link #isInstalled()} returns false. This MUST be called before mod loading is complete.
	 */
	void setSpecialMaterialPredicate(TagKey<Block> blockTag, ISpiritMaterialAcquisitionFunction getter) throws ArgumentNullException, IllegalArgumentException, IllegalStateException;
	
}