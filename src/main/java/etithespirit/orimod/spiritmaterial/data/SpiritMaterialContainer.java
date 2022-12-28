package etithespirit.orimod.spiritmaterial.data;

import com.google.common.collect.ImmutableMap;
import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.api.delegate.ISpiritMaterialAcquisitionFunction;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class SpiritMaterialContainer {
	
	/** The ID of the mod that declared this container. */
	public final String owner;
	private final boolean isMinecraft;
	private boolean completed = false;
	
	private static final Map<String, SpiritMaterialContainer> ALL_CONTAINERS = new HashMap<>();
	private static ImmutableMap<String, SpiritMaterialContainer> ALL_CONTAINERS_IMMUTABLE;
	
	// These are sorted in test order, visually (as in, here in the text, from top to bottom).
	private final Map<Block, ISpiritMaterialAcquisitionFunction> blockToFuncBindings = new HashMap<>();
	private final Map<Material, ISpiritMaterialAcquisitionFunction> mtlToFuncBindings = new HashMap<>();
	private final Map<TagKey<Block>, ISpiritMaterialAcquisitionFunction> tagToFuncBindings = new HashMap<>();
	private final Map<Block, ISpiritMaterialAcquisitionFunction> _runtimeTagBlockToFuncBindings = new HashMap<>();
	private final Map<BlockState, SpiritMaterial> stateToMtlBindings = new HashMap<>();
	private final Map<Block, SpiritMaterial> blockToMtlBindings = new HashMap<>();
	private final Map<ResourceLocation, SpiritMaterial> idToMtlBindings = new HashMap<>();
	private final Map<TagKey<Block>, SpiritMaterial> blockTagToMtlBindings = new HashMap<>();
	private final Map<Material, SpiritMaterial> customMtlToSpiritMtlBindings = new HashMap<>();
	
	private final Map<Block, Boolean> blocksToUseIfInside = new HashMap<>();
	private final Map<BlockState, Boolean> statesToUseIfInside = new HashMap<>();
	private final Map<TagKey<Block>, Boolean> tagsToUseIfInside = new HashMap<>();
	
	/** A cache for {@link #tagsToUseIfInside} so that the entire tag array only has to be iterated once. */
	private final Map<Block, Boolean> cachedBlocksByTagIfInside = new HashMap<>();
	
	/** A cache for {@link #blockTagToMtlBindings} so that the entire tag array only has to be iterated once. See the code within getMaterialExcludingConditionals for more info and additional behaviors. */
	private final Map<Block, SpiritMaterial> cachedBlocksByTagForMaterial = new HashMap<>();
	
	// Blocks and BlockStates are the only hard ones.
	// These cannot be referenced before init is complete, but this API must also be used before init is complete.
	// This means suppliers must be used, and then the list must be built.
	private final Map<Supplier<Block>, ISpiritMaterialAcquisitionFunction> pendingBlockFuncBindings = new HashMap<>();
	private final Map<Supplier<BlockState>, SpiritMaterial> pendingStateBindings = new HashMap<>();
	private final Map<Supplier<Block>, SpiritMaterial> pendingBlockBindings = new HashMap<>();
	
	private final Map<Supplier<Block>, Boolean> pendingBlocksToUseIfInside = new HashMap<>();
	private final Map<Supplier<BlockState>, Boolean> pendingStatesToUseIfInside = new HashMap<>();
	
	private final Map<BlockState, Boolean> cacheUsedFuncOn = new HashMap<>();
	
	/** This set stores all known objects that this container can handle. */
	private final Set<Object> knownValidObjects = new HashSet<>();
	private final Set<Block> skipReadingTagsForAcquisition = new HashSet<>();
	
	private SpiritMaterialContainer(String forModId) {
		owner = forModId;
		isMinecraft = owner.equals("minecraft");
	}
	
	/**
	 * @return All instances of {@link SpiritMaterialContainer} by mod id.
	 * @throws IllegalStateException If the Forge Init Cycle has not yet completed.
	 */
	public static ImmutableMap<String, SpiritMaterialContainer> getAllContainers() {
		if (OriMod.forgeLoadingComplete()) {
			if (ALL_CONTAINERS_IMMUTABLE == null) {
				ALL_CONTAINERS_IMMUTABLE = ImmutableMap.copyOf(ALL_CONTAINERS);
			}
			return ALL_CONTAINERS_IMMUTABLE;
		}
		throw new IllegalStateException("Mod loading has not yet completed.");
	}
	
	/**
	 * Returns a {@link SpiritMaterialContainer} for the current loading mod. As such, this must be called in the context of a mod that is loading.
	 * @return A new or pre-existing {@link SpiritMaterialContainer} for this mod.
	 */
	public static SpiritMaterialContainer getForThisMod() {
		String modId = ModLoadingContext.get().getActiveNamespace();
		if (modId.equals("minecraft")) throw new IllegalCallerException("This must be called in a mod's initialization cycle, like in its constructor.");

		SpiritMaterialContainer result = ALL_CONTAINERS.get(modId);
		if (result == null) {
			result = new SpiritMaterialContainer(modId);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(result::complete);
			ALL_CONTAINERS.put(modId, result);
		}
		return result;
	}
	
	/**
	 * <strong>Only callable by The Ori Mod, for interoperability.</strong>
	 * @param modId The ID of the mod to get the container for, or "minecraft" for vanilla.
	 * @return The instance of a material container for another mod.
	 */
	public static SpiritMaterialContainer getForOtherMod(String modId) {
		String callerId = ModLoadingContext.get().getActiveNamespace();
		if (!callerId.equals(OriMod.MODID)) throw new IllegalCallerException("Only The Ori Mod can call this method.");
		
		SpiritMaterialContainer result = ALL_CONTAINERS.get(modId);
		if (result == null) {
			result = new SpiritMaterialContainer(modId);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(result::complete);
			ALL_CONTAINERS.put(modId, result);
		}
		return result;
	}
	
	/**
	 * <strong>Only callable by The Ori Mod, for interoperability.</strong>
	 * @return The instance of a material container for vanilla blocks.
	 */
	public static SpiritMaterialContainer getForMinecraft() {
		return getForOtherMod("minecraft");
	}
	
	/** Completes this object by acquiring all blocks and states from their suppliers where applicable. */
	private void complete(final FMLLoadCompleteEvent evt) {
		if (completed) throw new IllegalStateException("This has already been completed.");
		completed = true;
		pendingBlockFuncBindings.forEach((supplier, func) -> {
			Block block = supplier.get();
			if (!isMinecraft && RegistrationHelpers.isBlockVanilla(block)) throw new IllegalArgumentException("Attempt to set ISpiritMaterialAcquisitionFunction of vanilla block.");
			blockToFuncBindings.put(block, func);
		});
		pendingStateBindings.forEach((supplier, mtl) -> {
			BlockState state = supplier.get();
			if (!isMinecraft && RegistrationHelpers.isBlockVanilla(state.getBlock())) throw new IllegalArgumentException("Attempt to set SpiritMaterial of vanilla block.");
			stateToMtlBindings.put(state, mtl);
		});
		pendingBlockBindings.forEach((supplier, mtl) -> {
			Block block = supplier.get();
			if (!isMinecraft && RegistrationHelpers.isBlockVanilla(block)) throw new IllegalArgumentException("Attempt to set SpiritMaterial of vanilla block.");
			blockToMtlBindings.put(block, mtl);
		});
		pendingBlocksToUseIfInside.forEach((supplier, use) -> {
			Block block = supplier.get();
			if (!isMinecraft && RegistrationHelpers.isBlockVanilla(block)) throw new IllegalArgumentException("Attempt to set the use-inside state of a vanilla block.");
			blocksToUseIfInside.put(block, use);
		});
		pendingStatesToUseIfInside.forEach((supplier, use) -> {
			BlockState state = supplier.get();
			if (!isMinecraft && RegistrationHelpers.isBlockVanilla(state.getBlock())) throw new IllegalArgumentException("Attempt to set the use-inside state of a vanilla block state.");
			statesToUseIfInside.put(state, use);
		});
		
		// And now the most expensive operation:
		knownValidObjects.addAll(blockToFuncBindings.keySet());
		knownValidObjects.addAll(mtlToFuncBindings.keySet());
		knownValidObjects.addAll(stateToMtlBindings.keySet());
		knownValidObjects.addAll(blockToMtlBindings.keySet());
		knownValidObjects.addAll(blockTagToMtlBindings.keySet()); // blockTagToMtlBindings
		knownValidObjects.addAll(customMtlToSpiritMtlBindings.keySet());
		
		knownValidObjects.addAll(blocksToUseIfInside.keySet());
		knownValidObjects.addAll(statesToUseIfInside.keySet());
		knownValidObjects.addAll(tagsToUseIfInside.keySet()); // tagsToUseIfInside
	}
	
	/**
	 * Returns true if this {@link SpiritMaterialContainer} is known to contain the given block state.<br/>
	 * <br/>
	 * Note: This is not guaranteed to work for blocks that have {@link TagKey}s stored in this array. That is, if a block has <code>modid:foo</code>, then
	 * that tag itself should be checked in {@link #shouldHandle(TagKey)} rather than in this method.
	 * @param state The block state to check.
	 * @return True if the given block state is registered in this container as one with any type of sound behavior.
	 */
	public boolean shouldHandle(BlockState state) {
		boolean quick = knownValidObjects.contains(state) || shouldHandle(state.getBlock()) || shouldHandle(state.getMaterial());
		if (quick) return true;
		return state.getTags().anyMatch(this::shouldHandle);
	}
	
	/**
	 * Returns true if this {@link SpiritMaterialContainer} is known to contain the given block.<br/>
	 * <br/>
	 * Note: This is not guaranteed to work for blocks that have {@link TagKey}s stored in this array. That is, if a block has <code>modid:foo</code>, then
	 * that tag itself should be checked in {@link #shouldHandle(TagKey)} rather than in this method.
	 * @param block The block to check.
	 * @return True if the given block is registered in this container as one with any type of sound behavior.
	 */
	public boolean shouldHandle(Block block) {
		return knownValidObjects.contains(block);
	}
	
	/**
	 * Returns true if this {@link SpiritMaterialContainer} is known to contain the given material.<br/>
	 * <br/>
	 * Note: This is not guaranteed to work for blocks that have {@link TagKey}s stored in this array. That is, if a block has <code>modid:foo</code>, then
	 * that tag itself should be checked in {@link #shouldHandle(TagKey)} rather than in this method.
	 * @param material The material to check.
	 * @return True if the given block is registered in this container as one with any type of sound behavior.
	 */
	public boolean shouldHandle(Material material) {
		return knownValidObjects.contains(material);
	}
	
	
	/**
	 * Returns true if this {@link SpiritMaterialContainer} is known to contain the given block tag.
	 * @param tag The tag to check.
	 * @return True if the given block tag is registered in this container as one with any type of sound behavior.
	 */
	public boolean shouldHandle(TagKey<Block> tag) {
		return knownValidObjects.contains(tag);
	}
	
	/**
	 * Uses the use-if-in bindings to automatically call {@link #shouldHandle(BlockState)} on the proper state between the two.
	 * <br/>
	 * Note: This is not guaranteed to work for blocks that have {@link TagKey}s stored in this array. That is, if a block has <code>modid:foo</code>, then
	 * that tag itself should be checked in {@link #shouldHandle(TagKey)} rather than in this method.
	 * @param standingOn The block state that the entity is standing on.
	 * @param standingIn The block state that the entity is standing inside of.
	 * @return True if the given block state is registered in this container as one with any type of sound behavior.
	 */
	public boolean shouldHandle(BlockState standingOn, BlockState standingIn) {
		return shouldHandle(getAppropriateStateBetween(standingOn, standingIn));
	}
	
	
	private void throwIfComplete() throws IllegalStateException {
		if (completed) throw new IllegalStateException("This container has already been locked.");
	}
	
	private void throwIfNotComplete() throws IllegalStateException {
		if (!completed) throw new IllegalStateException("This container has not been locked yet.");
	}
	
	public void setUseIfInsideBlock(Supplier<Block> block, boolean useIfInside) {
		throwIfComplete();
		pendingBlocksToUseIfInside.put(block, useIfInside);
	}
	
	public void setUseIfInsideState(Supplier<BlockState> state, boolean useIfInside) {
		throwIfComplete();
		pendingStatesToUseIfInside.put(state, useIfInside);
	}
	
	public void setUseIfInsideTag(TagKey<Block> blockTag, boolean useIfInside) {
		throwIfComplete();
		if (!isMinecraft && RegistrationHelpers.isBlockTagVanilla(blockTag)) throw new IllegalArgumentException("Attempt to set the use-inside state of a vanilla block tag.");
		tagsToUseIfInside.put(blockTag, useIfInside);
	}
	
	public void registerBlock(Supplier<Block> block, ISpiritMaterialAcquisitionFunction acquisitionFunction) {
		throwIfComplete();
		pendingBlockFuncBindings.put(block, acquisitionFunction);
	}
	
	public void registerMaterial(Material material, ISpiritMaterialAcquisitionFunction acquisitionFunction) {
		throwIfComplete();
		if (!isMinecraft && RegistrationHelpers.isMaterialVanilla(material)) throw new IllegalArgumentException("Attempt to set the conditional SpiritMaterial associated with a vanilla material.");
		mtlToFuncBindings.put(material, acquisitionFunction);
	}
	
	public void registerState(Supplier<BlockState> blockState, SpiritMaterial material) {
		throwIfComplete();
		pendingStateBindings.put(blockState, material);
	}
	
	public void registerBlock(Supplier<Block> block, SpiritMaterial material) {
		throwIfComplete();
		pendingBlockBindings.put(block, material);
	}
	
	public void registerTag(TagKey<Block> blockTag, SpiritMaterial material) {
		throwIfComplete();
		if (!isMinecraft && RegistrationHelpers.isBlockTagVanilla(blockTag)) throw new IllegalArgumentException("Attempt to set the SpiritMaterial associated with a vanilla block tag.");
		blockTagToMtlBindings.put(blockTag, material);
	}
	
	public void registerTag(TagKey<Block> blockTag, ISpiritMaterialAcquisitionFunction acquisitionFunction) {
		throwIfComplete();
		if (!isMinecraft && RegistrationHelpers.isBlockTagVanilla(blockTag)) throw new IllegalArgumentException("Attempt to set the SpiritMaterial associated with a vanilla block tag.");
		tagToFuncBindings.put(blockTag, acquisitionFunction);
	}
	
	public void registerMaterial(Material custom, SpiritMaterial material) {
		throwIfComplete();
		if (!isMinecraft && RegistrationHelpers.isMaterialVanilla(custom)) throw new IllegalArgumentException("Attempt to set the SpiritMaterial associated with a vanilla material.");
		customMtlToSpiritMtlBindings.put(custom, material);
	}
	
	public void registerBlock(ResourceLocation blockId, SpiritMaterial material) {
		throwIfComplete();
		if (!isMinecraft && blockId.getNamespace().equals("minecraft")) throw new IllegalArgumentException("Attempt to set the SpiritMaterial associated with a vanilla block.");
		idToMtlBindings.put(blockId, material);
	}
	
	/**
	 * Assuming an entity is standing on and in the given two parameters respectively, this uses the lookup of blocks that
	 * require their sound to be used when standing inside of them to figure out which of these two blocks should be used
	 * to sample a sound from.
	 * @param blockStandingOn The block an arbitrary entity is standing on top of.
	 * @param blockStandingIn The block an arbitrary entity is standing inside of.
	 * @return The block the entity is inside of, if the registry reports that said block should be used in favor of the block being stood on top of.
	 */
	public BlockState getAppropriateStateBetween(BlockState blockStandingOn, BlockState blockStandingIn) {
		if (blocksToUseIfInside.getOrDefault(blockStandingIn.getBlock(), false)) {
			// The entire block is registered, but...
			boolean useStateInside = statesToUseIfInside.getOrDefault(blockStandingIn, true); // Default to true.
			// Reason: If the block is declared, and the state is *missing*, then the state inherits that of its parent block.
			return useStateInside ? blockStandingIn : blockStandingOn;
		}
		boolean useStateInside;
		if (cachedBlocksByTagIfInside.containsKey(blockStandingIn.getBlock())) {
			useStateInside = cachedBlocksByTagIfInside.get(blockStandingIn.getBlock());
		} else {
			useStateInside = blockStandingIn.getTags().anyMatch(blockTagKey -> tagsToUseIfInside.getOrDefault(blockTagKey, false));
			cachedBlocksByTagIfInside.put(blockStandingIn.getBlock(), useStateInside);
		}
		return useStateInside ? blockStandingIn : blockStandingOn;
	}
	
	/**
	 * Returns whether or not full context is known to be required for the given pair of blocks. If this returns null, it has not yet been evaluated.
	 * If this returns true, the result of directly calling {@link #getMaterialExcludingConditionals(BlockState)} should be cached by the caller.
	 * @param standingOn The block an entity is standing on top of.
	 * @param standingIn The block an entity is standing within.
	 * @return True if the result can (and should) be cached, false if {@link #getMaterialFor(Entity, BlockPos, BlockPos)} should always be called, and null if unknown.
	 */
	public @Nullable Boolean requiresFullContext(BlockState standingOn, BlockState standingIn) {
		return cacheUsedFuncOn.getOrDefault(getAppropriateStateBetween(standingOn, standingIn), null);
	}
	
	/**
	 * Given the entity, the block it is standing on, and the block it is standing inside of, this attempts to figure out what SpiritMaterial the systems for sound should use when this entity walks.
	 * @param entity The entity to check for.
	 * @param standingOn The position of the block it is standing on top of.
	 * @param standingIn The position of the block its feet are inside of.
	 * @return The appropriate material given all context about how to handle sounds for the blocks at the two respective positions.
	 * @throws NullPointerException If complex functions (see {@link ISpiritMaterialAcquisitionFunction}) are used to determine the material, and one of these functions returns null.
	 */
	public @Nonnull SpiritMaterial getMaterialFor(@Nonnull Entity entity, @Nonnull BlockPos standingOn, @Nonnull BlockPos standingIn) {
		ArgumentNullException.throwIfNull(entity, "entity");
		ArgumentNullException.throwIfNull(standingOn, "standingOn");
		ArgumentNullException.throwIfNull(standingIn, "standingIn");
		
		Level world = entity.getCommandSenderWorld();
		BlockState on = world.getBlockState(standingOn);
		BlockState in = world.getBlockState(standingIn);
		BlockState state = getAppropriateStateBetween(on, in);
		
		Boolean hadFunc = cacheUsedFuncOn.get(state);
		if (Boolean.FALSE.equals(hadFunc)) {
			return getMaterialExcludingConditionals(state);
		}
		
		boolean useStandingIn = state.equals(in);
		
		ISpiritMaterialAcquisitionFunction func = blockToFuncBindings.get(state.getBlock());
		if (func != null) {
			SpiritMaterial result = func.getSpiritMaterial(entity, standingOn, standingIn, useStandingIn);
			//noinspection ConstantConditions
			if (result == null) throw new NullPointerException("SpiritMaterial returned by function " + func.getClass() + " was null.");
			cacheUsedFuncOn.putIfAbsent(state, true);
			return result;
		}
		func = mtlToFuncBindings.get(state.getMaterial());
		if (func != null) {
			SpiritMaterial result = func.getSpiritMaterial(entity, standingOn, standingIn, useStandingIn);
			//noinspection ConstantConditions
			if (result == null) throw new NullPointerException("SpiritMaterial returned by function " + func.getClass() + " was null.");
			cacheUsedFuncOn.putIfAbsent(state, true);
			return result;
		}
		func = _runtimeTagBlockToFuncBindings.get(state.getBlock());
		if (func != null) {
			SpiritMaterial result = func.getSpiritMaterial(entity, standingOn, standingIn, useStandingIn);
			//noinspection ConstantConditions
			if (result == null) throw new NullPointerException("SpiritMaterial returned by function " + func.getClass() + " was null.");
			cacheUsedFuncOn.putIfAbsent(state, true);
			return result;
			
		} else if (!skipReadingTagsForAcquisition.contains(state.getBlock())) {
			for (TagKey<Block> tag : state.getTags().toList()) {
				func = tagToFuncBindings.get(tag);
				if (func != null) {
					SpiritMaterial result = func.getSpiritMaterial(entity, standingOn, standingIn, useStandingIn);
					//noinspection ConstantConditions
					if (result == null) throw new NullPointerException("SpiritMaterial returned by function " + func.getClass() + " was null.");
					_runtimeTagBlockToFuncBindings.put(state.getBlock(), func);
					skipReadingTagsForAcquisition.add(state.getBlock());
					cacheUsedFuncOn.putIfAbsent(state, true);
					return result;
				}
			}
			skipReadingTagsForAcquisition.add(state.getBlock());
		}
		
		cacheUsedFuncOn.putIfAbsent(state, false);
		
		// No conditionals? Grab raw.
		return getMaterialExcludingConditionals(state);
	}
	
	/**
	 * The simpler sibling of {@link #getMaterialFor(Entity, BlockPos, BlockPos)}. This method is incapable of using conditionals (via {@link ISpiritMaterialAcquisitionFunction}), and
	 * only returns statically-bound block(state) to material associations.
	 * @param state The state to check.
	 * @return The appropriate material for the given block. Returns {@link SpiritMaterial#INHERITED} if there is no specific overridden sound and the vanilla should be used.
	 */
	public @Nonnull SpiritMaterial getMaterialExcludingConditionals(@Nonnull BlockState state) {
		ArgumentNullException.throwIfNull(state, "state");
		throwIfNotComplete();
		
		// Please remember that the order of these checks does matter.
		SpiritMaterial result = notNullOfEither(
			stateToMtlBindings.get(state),
			notNullOfEither(
				blockToMtlBindings.get(state.getBlock()),
				cachedBlocksByTagForMaterial.get(state.getBlock())
				// This serves some additional purpose. Not only is it to cache tags, but it also serves as an early return value.
				// If the result of this block is null, the block's tags are searched (see below).
				// The result of that search is put into this map as expected, granted the result is not null.
				
				// However, if *that* result is null, then the next lookup stage is evaluated (search customMtlToSpiritMtlBindings). This result
				// (which will be a user-defined material, or SpiritMaterial.INHERITED), gets stored into this map (cachedBlocksByTagForMaterial).
				
				// The consequence of this action ensures that this map (cachedBlocksByTagForMaterial) always functions as the final fallback/result
				// of this method after it has been called for any given block. This saves a lot of time for future invocations, at little extra cost to the first.
			)
		);
		if (result != null) return result;
		
		// New behavior: Start with the arbitrary resource
		ResourceLocation key = RegistrationHelpers.getIDOf(state.getBlock());
		SpiritMaterial finalResult;
		if (key != null) {
			finalResult = idToMtlBindings.get(key);
			if (finalResult != null) {
				blockToMtlBindings.put(state.getBlock(), finalResult);
			}
		}
		
		AtomicReference<SpiritMaterial> boxedResult = new AtomicReference<>();
		// This only executes if, implicitly, cachedStatesByTagForMaterial does not contain this state.
		state.getTags().forEach(blockTagKey -> {
			SpiritMaterial resultMtl = blockTagToMtlBindings.get(blockTagKey);
			if (resultMtl != null) {
				cachedBlocksByTagForMaterial.put(state.getBlock(), resultMtl);
				boxedResult.set(resultMtl);
			}
		});
		
		finalResult = notNullOfEither(
			boxedResult.get(),
			notNullOfEither(
				customMtlToSpiritMtlBindings.get(state.getMaterial()),
				SpiritMaterial.INHERITED
			)
		);
		if (boxedResult.get() == null) {
			// There was no tag for this block. To avoid returning it in the future, this special processing must be done.
			cachedBlocksByTagForMaterial.put(state.getBlock(), finalResult);
		}
		return finalResult;
	}
	
	/**
	 * @param first The first parameter is returned if it is not null.
	 * @param second The second parameter is returned iff first is null.
	 * @param <T> The type of value to return.
	 * @return If first is not null, first is returned. If first is null, second is returned. If second is also null, null is returned.
	 */
	@Nullable
	private static <T> T notNullOfEither(@Nullable T first, @Nullable T second) {
		return (first != null) ? first : second;
	}
	
}
