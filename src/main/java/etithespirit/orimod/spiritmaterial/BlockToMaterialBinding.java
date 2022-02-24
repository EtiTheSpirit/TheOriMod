package etithespirit.orimod.spiritmaterial;

import etithespirit.orimod.api.delegate.ISpiritMaterialAquisitionFunction;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import etithespirit.orimod.common.block.ExtendedMaterial;
import etithespirit.orimod.registry.BlockRegistry;
import etithespirit.orimod.spiritmaterial.defaults.DefaultImplementations;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom binding from block to material that may not necessarily use the Block's defined material.<br/>
 * If a block is not defined in this class to have a custom material, then the material it was constructed with is used.
 * @author Eti
 */
@SuppressWarnings("unused")
public final class BlockToMaterialBinding {
	
	private BlockToMaterialBinding() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/////////////////////////////////////////////////////////
	/// LOOKUPS
	/**
	 * A lookup to access spirit materials from vanilla materials.
	 */
	private static final Map<Material, SpiritMaterial> MATERIAL_TO_SPIRIT_MTL = new HashMap<>();
	
	/////////////////////////////////////////////////////////
	/// CONSTANT BINDINGS
	/**
	 * Maps blocks to replacement materials.
	 */
	private static final Map<Block, Material> BLOCK_TO_MATERIAL = new HashMap<>();
	
	/**
	 * Maps blocks to specific spirit materials.
	 */
	private static final Map<Block, SpiritMaterial> BLOCK_TO_SPIRIT_MTL = new HashMap<>();
	
	/**
	 * A map from specific block states to spirit materials.
	 */
	private static final Map<BlockState, SpiritMaterial> SPECIFIC_STATE_TO_SPIRIT_MTL = new HashMap<>();
	
	/**
	 * Maps arbitrary resources to specific spirit materials.
	 */
	private static final Map<ResourceLocation, SpiritMaterial> ARB_BLOCK_TO_SPIRIT_MTL = new HashMap<>();
	
	/**
	 * For blocks that the player's feet are inside of, if the block is in this list, it should be tested as the effective material rather than the block they are standing on
	 */
	private static final List<Block> BLOCKS_TO_TEST_IF_INSIDE = new ArrayList<>();
	
	/**
	 * For blocks that the player's feet are inside of, if the block is in this list, it should be tested as the effective material rather than the block they are standing on
	 */
	private static final List<BlockState> BLOCKSTATES_TO_TEST_IF_INSIDE = new ArrayList<>();
	
	/**
	 * For blocks that the player's feet are inside of, if the block is in this list, it should be tested as the effective material rather than the block they are standing on
	 */
	private static final List<ResourceLocation> ARB_BLOCKS_TO_TEST_IF_INSIDE = new ArrayList<>();
	
	/////////////////////////////////////////////////////////
	/// CONDITIONAL BINDINGS
	
	/**
	 * Similar to CONDITIONAL_STATE_OVERRIDES, but instead of returning a state override, it's an entire material override.
	 */
	private static final Map<Block, ISpiritMaterialAquisitionFunction> BLOCK_CONDITIONAL_MATERIAL_OVERRIDES = new HashMap<>();
	
	/**
	 * Identical to BLOCK_CONDITIONAL_MATERIAL_OVERRIDES, but it's for an entire material.
	 */
	private static final Map<Material, ISpiritMaterialAquisitionFunction> MATERIAL_CONDITIONAL_MATERIAL_OVERRIDES = new HashMap<>();
	
	/////////////////////////////////////////////////////////
	/// CORE CODE
	
	/**
	 * A more rich method of acquiring an appropriate block sound using contextual entity information. Allows checking conditional cases.
	 * @param entity The entity to test.
	 * @param standingOn The position of the block the entity is standing on.
	 * @param standingIn The position of the block the entity is standing in.
	 * @return An appropriate material for the information in the system.
	 */
	public static SpiritMaterial getMaterialFor(Entity entity, BlockPos standingOn, BlockPos standingIn) {
		Level world = entity.getCommandSenderWorld();
		BlockState on = world.getBlockState(standingOn);
		BlockState in = world.getBlockState(standingIn);
		BlockState state = getAppropriateMaterialBetween(on, in);
		boolean isStandingIn = state.equals(in);
		
		// Conditionals always come first.
		if (BLOCK_CONDITIONAL_MATERIAL_OVERRIDES.containsKey(state.getBlock())) {
			ISpiritMaterialAquisitionFunction func = BLOCK_CONDITIONAL_MATERIAL_OVERRIDES.get(state.getBlock());
			return func.getSpiritMaterial(entity, standingOn, standingIn, isStandingIn);
		}
		
		if (MATERIAL_CONDITIONAL_MATERIAL_OVERRIDES.containsKey(state.getMaterial())) {
			ISpiritMaterialAquisitionFunction func = MATERIAL_CONDITIONAL_MATERIAL_OVERRIDES.get(state.getMaterial());
			return func.getSpiritMaterial(entity, standingOn, standingIn, isStandingIn);
		}
		
		// No conditionals? Grab raw.
		return getMaterialForRaw(state);
	}
	
	/**
	 * Return raw, 1:1 bindings from blocks/materials to SpiritMaterials. Does not work with conditional statements as those require an entity and two BlockStates.
	 * @param state The state of the block to test.
	 * @return The appropriate material for this.
	 */
	public static SpiritMaterial getMaterialForRaw(BlockState state) {
		// Its state?
		if (SPECIFIC_STATE_TO_SPIRIT_MTL.containsKey(state)) {
			SpiritMaterial retn = SPECIFIC_STATE_TO_SPIRIT_MTL.get(state);
			if (retn != null) return retn;
			// If it's null just continue onward.
		}
		
		// The straight up block => spirit material binding.
		if (BLOCK_TO_SPIRIT_MTL.containsKey(state.getBlock())) {
			SpiritMaterial retn = BLOCK_TO_SPIRIT_MTL.get(state.getBlock());
			if (retn != null) return retn;
			// If it's null just continue onward.
		}
		
		// What about modded / arbitrary block => spirit material?
		ResourceLocation rsrc = state.getBlock().getRegistryName();
		if (ARB_BLOCK_TO_SPIRIT_MTL.containsKey(rsrc)) {
			SpiritMaterial retn = ARB_BLOCK_TO_SPIRIT_MTL.get(rsrc);
			if (retn != null) return retn;
			// If it's null just continue onward.
		}
		
		// Okay, that doesn't exist. Get it's state-defined material. But what about the override for MC materials?
		Material blockMaterial = state.getMaterial();
		if (BLOCK_TO_MATERIAL.containsKey(state.getBlock())) {
			// Yeah, an override sound exists. Use that.
			// This replaces one vanilla material with another.
			blockMaterial = BLOCK_TO_MATERIAL.get(state.getBlock());
		}
		
		// Does the specified material have a spirit material associated with it?
		if (MATERIAL_TO_SPIRIT_MTL.containsKey(blockMaterial)) {
			// Yeah. Return it.
			SpiritMaterial retn = MATERIAL_TO_SPIRIT_MTL.get(blockMaterial);
			if (retn != null) return retn;
			// If it's null just continue onward.
		}
		
		// Nope. Just use the vanilla sound.
		return SpiritMaterial.INHERITED;
	}
	
	/**
	 * Given two blockstates, this determines which state should be used to grab the material from.
	 * @param blockStandingOn The block that is being stood on.
	 * @param blockStandingIn The block that is being stood in.
	 * @return The appropriate {@link BlockState} to use given this material's settings.
	 */
	public static BlockState getAppropriateMaterialBetween(BlockState blockStandingOn, BlockState blockStandingIn) {
		if (BLOCKS_TO_TEST_IF_INSIDE.contains(blockStandingIn.getBlock()) || ARB_BLOCKS_TO_TEST_IF_INSIDE.contains(blockStandingIn.getBlock().getRegistryName()) || BLOCKSTATES_TO_TEST_IF_INSIDE.contains(blockStandingIn)) {
			return blockStandingIn;
		}
		return blockStandingOn;
	}
	
	/////////////////////////////////////////////////////////
	/// REGISTRY CODE
	
	/**
	 * Associates the given Minecraft or Mod {@link Material} with the given {@link SpiritMaterial}
	 * @param mtl The Minecraft vanilla material (or custom Mod material) to associate.
	 * @param smtl The Spirit Material to associate it with.
	 */
	public static void associateMCMaterialWith(Material mtl, SpiritMaterial smtl) {
		MATERIAL_TO_SPIRIT_MTL.put(mtl, smtl);
	}
	
	/**
	 * Associates the specific {@link Block} with the given Minecraft or Mod {@link Material}, which can be used to alter its sound on a material basis.
	 * This is mostly intended for use with modded blocks. Only use this with vanilla blocks if, for whatever reason, you have completely overridden it
	 * and it requires unique behavior.
	 *
	 * This is strictly only useful if the auditory material is different than the block's assigned material.
	 *
	 * @param block The block to associate
	 * @param mtl The material to associate it with.
	 * @throws IllegalArgumentException If the block's material is identical to mtl.
	 */
	public static void setOverrideMaterialFor(Block block, Material mtl) throws IllegalArgumentException {
		if (block.defaultBlockState().getMaterial().equals(mtl)) throw new IllegalArgumentException(String.format("Something attempted to assign the given block (%s) such that its material would be overridden to a different material, however, the supposed \"override\" is identical to the block's existing material! This override call should be removed immediately.", block.getRegistryName().toString(), mtl));
		BLOCK_TO_MATERIAL.put(block, mtl);
	}
	
	/**
	 * Associates the specific {@link Block} with the given {@link SpiritMaterial}, which is used to alter its sound to a specific Spirit block material.
	 * @param block The block to associate.
	 * @param mtl The Spirit material to associate it with.
	 */
	public static void setSpiritMaterialFor(Block block, SpiritMaterial mtl) {
		BLOCK_TO_SPIRIT_MTL.put(block, mtl);
	}
	
	/**
	 * A more concise method that allows associating a specific {@link BlockState} to a specific material rather than the entire block type.
	 * @param state The specific state to associate.
	 * @param mtl The Spirit material to associate it with.
	 */
	public static void setSpiritMaterialForState(BlockState state, SpiritMaterial mtl) {
		SPECIFIC_STATE_TO_SPIRIT_MTL.put(state, mtl);
	}
	
	/**
	 * A sort of concise method of associating a block ID with a {@link SpiritMaterial}, which can be used for mods with conditionally registered blocks or blocks from other mods.<br/>
	 * <strong>Note:</strong> Conditionally registered blocks are considered bad practice by Forge. According to Forge, you should
	 * simply disable its recipe instead. This can also be used to register blocks from other mods, however it is generally a bad idea to do this unless
	 * it is your own mod.
	 * @param loc The ID of a hypothetical block to associate.
	 * @param mtl The Spirit material to associate it with.
	 */
	public static void setSpiritMaterialForArbitrary(ResourceLocation loc, SpiritMaterial mtl) {
		ARB_BLOCK_TO_SPIRIT_MTL.put(loc, mtl);
	}
	
	/**
	 * Associates the specific {@link Block} (by registry object) with the given {@link SpiritMaterial}, which is used to alter its sound to a specific Spirit block material.
	 * @param block The block to associate.
	 * @param mtl The Spirit material to associate it with.
	 */
	public static void setEffectiveMaterialForRegistryObject(RegistryObject<Block> block, SpiritMaterial mtl) {
		setSpiritMaterialForArbitrary(block.getId(), mtl);
	}
	
	/**
	 * Modify the test used for this block's material so that its sound is played if the player is <em>inside of</em> this block (as opposed to on top of it).
	 * @param block The block to set.
	 */
	public static void useIfIn(Block block) {
		BLOCKS_TO_TEST_IF_INSIDE.add(block);
	}
	
	/**
	 * Modify the test used for this specific state's material so that its sound is played if the player is <em>inside of</em> this block (as opposed to on top of it).
	 * @param state The specific block state to set.
	 */
	public static void useIfIn(BlockState state) {
		BLOCKSTATES_TO_TEST_IF_INSIDE.add(state);
	}
	
	/**
	 * Modify the test used for this block's (by ID) material so that its sound is played if the player is <em>inside of</em> this block (as opposed to on top of it).
	 * @param loc The ID of a hypothetical block to set.
	 */
	public static void useIfIn(ResourceLocation loc) {
		ARB_BLOCKS_TO_TEST_IF_INSIDE.add(loc);
	}
	
	/**
	 * Modify the test used for this registered block's material so that its sound is played if the player is <em>inside of</em> this block (as opposed to on top of it).
	 * @param block The registry object of the block to set.
	 */
	public static void useIfIn(RegistryObject<Block> block) {
		useIfIn(block.getId());
	}
	
	/**
	 * Registers a means of applying a custom material to a block via a predicate function.
	 * @param block The block to apply the predicate to.
	 * @param func A function that determines which material to use based on the entity's information
	 */
	public static void setConditionForBlock(Block block, ISpiritMaterialAquisitionFunction func) {
		BLOCK_CONDITIONAL_MATERIAL_OVERRIDES.put(block, func);
	}
	
	/**
	 * Registers a means of applying a custom material to a different block material via a predicate function.
	 * @param mtl The material to apply the predicate to.
	 * @param func A function that determines which material to use based on the entity's information
	 */
	public static void setConditionForMaterial(Material mtl, ISpiritMaterialAquisitionFunction func) {
		MATERIAL_CONDITIONAL_MATERIAL_OVERRIDES.put(mtl, func);
	}
	
	private static void populateMaterialToSpiritMtl() {
		// VANILLA MATERIALS
		MATERIAL_TO_SPIRIT_MTL.put(Material.AIR, SpiritMaterial.NULL);
		MATERIAL_TO_SPIRIT_MTL.put(Material.BAMBOO, SpiritMaterial.WOOD_DRY);
		MATERIAL_TO_SPIRIT_MTL.put(Material.BAMBOO_SAPLING, SpiritMaterial.GRASS_CRISP);
		MATERIAL_TO_SPIRIT_MTL.put(Material.STRUCTURAL_AIR, SpiritMaterial.INHERITED); // mojmap: STRUCTURAL_AIR
		MATERIAL_TO_SPIRIT_MTL.put(Material.BUBBLE_COLUMN, SpiritMaterial.INHERITED);
		MATERIAL_TO_SPIRIT_MTL.put(Material.BARRIER, SpiritMaterial.GLASS);
		MATERIAL_TO_SPIRIT_MTL.put(Material.CACTUS, SpiritMaterial.SHROOM);
		MATERIAL_TO_SPIRIT_MTL.put(Material.CAKE, SpiritMaterial.SHROOM);
		MATERIAL_TO_SPIRIT_MTL.put(Material.CLAY, SpiritMaterial.SLIMY);
		MATERIAL_TO_SPIRIT_MTL.put(Material.CLOTH_DECORATION, SpiritMaterial.WOOL); // mojmap: CLOTH_DECORATION
		MATERIAL_TO_SPIRIT_MTL.put(Material.DECORATION, SpiritMaterial.INHERITED); // mojmap: DECORATION
		MATERIAL_TO_SPIRIT_MTL.put(Material.DIRT, SpiritMaterial.SAND); // mojmap: DIRT
		MATERIAL_TO_SPIRIT_MTL.put(Material.EGG, SpiritMaterial.ICE); // mojmap: EGG
		MATERIAL_TO_SPIRIT_MTL.put(Material.EXPLOSIVE, SpiritMaterial.SAND); // mojmap: EXPLOSIVE
		MATERIAL_TO_SPIRIT_MTL.put(Material.FIRE, SpiritMaterial.INHERITED);
		MATERIAL_TO_SPIRIT_MTL.put(Material.GLASS, SpiritMaterial.GLASS);
		MATERIAL_TO_SPIRIT_MTL.put(Material.PLANT, SpiritMaterial.GRASS_SOFT); // mojmap: PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.HEAVY_METAL, SpiritMaterial.METAL); // mojmap: HEAVY_METAL
		MATERIAL_TO_SPIRIT_MTL.put(Material.ICE, SpiritMaterial.ICE);
		MATERIAL_TO_SPIRIT_MTL.put(Material.ICE_SOLID, SpiritMaterial.ICE); // mojmap: ICE_SOLID
		MATERIAL_TO_SPIRIT_MTL.put(Material.LAVA, SpiritMaterial.INHERITED);
		MATERIAL_TO_SPIRIT_MTL.put(Material.LEAVES, SpiritMaterial.GRASS_SOFT);
		MATERIAL_TO_SPIRIT_MTL.put(Material.METAL, SpiritMaterial.METAL); // mojmap: METAL
		// MATERIAL_TO_SPIRIT_MTL.put(Material.NETHER_WOOD, SpiritMaterial.CONDITIONAL_WOOD);
		MATERIAL_TO_SPIRIT_MTL.put(Material.PISTON, SpiritMaterial.ROCK);
		MATERIAL_TO_SPIRIT_MTL.put(Material.REPLACEABLE_PLANT, SpiritMaterial.GRASS_SOFT); // mojmap: REPLACEABLE_PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.PORTAL, SpiritMaterial.GLASS);
		MATERIAL_TO_SPIRIT_MTL.put(Material.REPLACEABLE_FIREPROOF_PLANT, SpiritMaterial.GRASS_CRISP); // mojmap: REPLACEABLE_FIREPROOF_PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.WATER_PLANT, SpiritMaterial.SHROOM); // mojmap: WATER_PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.SAND, SpiritMaterial.SAND);
		MATERIAL_TO_SPIRIT_MTL.put(Material.SHULKER_SHELL, SpiritMaterial.ROCK); // mojmap: SHULKER_SHELL
		MATERIAL_TO_SPIRIT_MTL.put(Material.SNOW, SpiritMaterial.SNOW); // mojmap: SNOW
		MATERIAL_TO_SPIRIT_MTL.put(Material.SPONGE, SpiritMaterial.WOOL);
		MATERIAL_TO_SPIRIT_MTL.put(Material.STONE, SpiritMaterial.ROCK); // mojmap: STONE
		MATERIAL_TO_SPIRIT_MTL.put(Material.TOP_SNOW, SpiritMaterial.SNOW); // mojmap: TOP_SNOW
		MATERIAL_TO_SPIRIT_MTL.put(Material.VEGETABLE, SpiritMaterial.SHROOM); // mojmap: VEGETABLE
		// MATERIAL_TO_SPIRIT_MTL.put(Material.WATER, SpiritMaterial.CONDITIONAL_WATER);
		MATERIAL_TO_SPIRIT_MTL.put(Material.REPLACEABLE_WATER_PLANT, SpiritMaterial.SHROOM); // mojmap: REPLACEABLE_WATER_PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.WEB, SpiritMaterial.INHERITED);
		// MATERIAL_TO_SPIRIT_MTL.put(Material.WOOD, SpiritMaterial.CONDITIONAL_WOOD);
		MATERIAL_TO_SPIRIT_MTL.put(Material.WOOL, SpiritMaterial.WOOL);
		
		// MY MATERIALS
		MATERIAL_TO_SPIRIT_MTL.put(ExtendedMaterial.LIGHT, SpiritMaterial.GLASS);
		
		// CUSTOM MATERIAL PREDICATES
		MATERIAL_CONDITIONAL_MATERIAL_OVERRIDES.put(Material.NETHER_WOOD, DefaultImplementations::getWoodMaterial);
		MATERIAL_CONDITIONAL_MATERIAL_OVERRIDES.put(Material.WOOD, DefaultImplementations::getWoodMaterial);
		MATERIAL_CONDITIONAL_MATERIAL_OVERRIDES.put(Material.WATER, DefaultImplementations::getWaterMaterial);
	}
	
	private static void populateEffectiveMaterialsForBlocks() {
		// STATE BINDINGS
		setSpiritMaterialForState(Blocks.GRASS_BLOCK.defaultBlockState().setValue(BlockStateProperties.SNOWY, Boolean.TRUE), SpiritMaterial.SNOW);
		
		// VANILLA BLOCKS
		// Grass variants & General Overworld
		setSpiritMaterialFor(Blocks.PODZOL, SpiritMaterial.SNOW);
		setSpiritMaterialFor(Blocks.GRASS_BLOCK, SpiritMaterial.GRASS_SOFT); // wat
		setSpiritMaterialFor(Blocks.MYCELIUM, SpiritMaterial.SHROOM); // from grass
		setSpiritMaterialFor(Blocks.GRAVEL, SpiritMaterial.GRAVEL_DRY); // from sand
		
		setSpiritMaterialFor(Blocks.SAND, SpiritMaterial.SAND);
		setSpiritMaterialFor(Blocks.SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.CHISELED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SANDSTONE_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SANDSTONE_WALL, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.CUT_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.CUT_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SMOOTH_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SMOOTH_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SMOOTH_SANDSTONE_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		
		setSpiritMaterialFor(Blocks.RED_SAND, SpiritMaterial.SAND);
		setSpiritMaterialFor(Blocks.RED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.CHISELED_RED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.RED_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.RED_SANDSTONE_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.RED_SANDSTONE_WALL, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.CUT_RED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.CUT_RED_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SMOOTH_RED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SMOOTH_RED_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		
		
		// Shrooms (there is a fungus among us)
		setSpiritMaterialFor(Blocks.RED_MUSHROOM_BLOCK, SpiritMaterial.SHROOM); // from wood
		setSpiritMaterialFor(Blocks.BROWN_MUSHROOM_BLOCK, SpiritMaterial.SHROOM); // from wood
		setSpiritMaterialFor(Blocks.MUSHROOM_STEM, SpiritMaterial.SHROOM); // from wood
		setSpiritMaterialFor(Blocks.CHORUS_FLOWER, SpiritMaterial.SHROOM); // from wood
		setSpiritMaterialFor(Blocks.CHORUS_PLANT, SpiritMaterial.SHROOM); // from wood
		setSpiritMaterialFor(Blocks.NETHER_WART_BLOCK, SpiritMaterial.SHROOM);
		setSpiritMaterialFor(Blocks.CRIMSON_FUNGUS, SpiritMaterial.SHROOM);
		setSpiritMaterialFor(Blocks.WARPED_FUNGUS, SpiritMaterial.SHROOM);
		
		
		// Ores n Spawners
		setSpiritMaterialFor(Blocks.SPAWNER, SpiritMaterial.METAL); // from stone
		setSpiritMaterialFor(Blocks.EMERALD_BLOCK, SpiritMaterial.ROCK); // from metal
		setSpiritMaterialFor(Blocks.LAPIS_BLOCK, SpiritMaterial.ROCK); // from metal
		setSpiritMaterialFor(Blocks.DIAMOND_BLOCK, SpiritMaterial.ROCK); // from metal
		
		
		// The Netha (i do not have a nether pass)
		setSpiritMaterialFor(Blocks.NETHER_QUARTZ_ORE, SpiritMaterial.INHERITED); // from stone
		setSpiritMaterialFor(Blocks.NETHER_GOLD_ORE, SpiritMaterial.INHERITED); // from stone
		setSpiritMaterialFor(Blocks.NETHERRACK, SpiritMaterial.INHERITED); // duh
		setSpiritMaterialFor(Blocks.BASALT, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.POLISHED_BASALT, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.GILDED_BLACKSTONE, SpiritMaterial.INHERITED);
		// setEffectiveMaterialFor(Blocks.NETHERITE_BLOCK, SpiritMaterial.INHERITED); // from metal
		setSpiritMaterialFor(Blocks.ANCIENT_DEBRIS, SpiritMaterial.INHERITED); // from metal
		
		
		// Command Blocks
		setSpiritMaterialFor(Blocks.COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		setSpiritMaterialFor(Blocks.CHAIN_COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		setSpiritMaterialFor(Blocks.REPEATING_COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		
		
		// Redstone
		setSpiritMaterialFor(Blocks.DAYLIGHT_DETECTOR, SpiritMaterial.ROCK); // from wood
		setSpiritMaterialFor(Blocks.REDSTONE_LAMP, SpiritMaterial.GLASS); // from stone
		
		
		// Da Goop & Da Bees
		setSpiritMaterialFor(Blocks.CLAY, SpiritMaterial.SLIMY); // from gravel?
		setSpiritMaterialFor(Blocks.SLIME_BLOCK, SpiritMaterial.SLIMY);
		setSpiritMaterialFor(Blocks.HONEY_BLOCK, SpiritMaterial.SLIMY);
		setSpiritMaterialFor(Blocks.HONEYCOMB_BLOCK, SpiritMaterial.SHROOM); // NOT slimy
		setSpiritMaterialFor(Blocks.BEE_NEST, SpiritMaterial.SHROOM); // from wood
		
		
		// B R I C K S
		setSpiritMaterialFor(Blocks.BRICKS, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.BRICK_SLAB, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.BRICK_STAIRS, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.BRICK_WALL, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.NETHER_BRICKS, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.NETHER_BRICK_SLAB, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.NETHER_BRICK_STAIRS, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.NETHER_BRICK_WALL, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.NETHER_BRICK_FENCE, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.CHISELED_NETHER_BRICKS, SpiritMaterial.SOLID_CERAMICS);
		setSpiritMaterialFor(Blocks.CRACKED_NETHER_BRICKS, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.RED_NETHER_BRICKS, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.RED_NETHER_BRICK_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.RED_NETHER_BRICK_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setSpiritMaterialFor(Blocks.RED_NETHER_BRICK_WALL, SpiritMaterial.CRACKED_CERAMICS);
		
		
		// outsourced to ungengengingan vilag (twomad home)
		setSpiritMaterialFor(Blocks.TARGET, SpiritMaterial.WOOL); // from wood -- literal target block (like you shoot arrows at it, not a "goal")
		setSpiritMaterialFor(Blocks.DRIED_KELP_BLOCK, SpiritMaterial.GRASS_CRISP); // from shroom
		setSpiritMaterialFor(Blocks.SMITHING_TABLE, SpiritMaterial.METAL); // from wood
		setSpiritMaterialFor(Blocks.HAY_BLOCK, SpiritMaterial.GRASS_CRISP); // from ... wool? forgot tbh
		setSpiritMaterialFor(Blocks.BLACKSTONE, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.BLACKSTONE_SLAB, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.BLACKSTONE_STAIRS, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.BLACKSTONE_WALL, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.POLISHED_BLACKSTONE, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.POLISHED_BLACKSTONE_BRICKS, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.CHISELED_POLISHED_BLACKSTONE, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, SpiritMaterial.ASH);
		setSpiritMaterialFor(Blocks.GILDED_BLACKSTONE, SpiritMaterial.ASH);
		
		useIfIn(Blocks.SNOW);
		useIfIn(Blocks.VINE);
	}
	
	private static List<Material> vanillaMaterials = null;
	private static List<Block> vanillaBlocks = null;
	
	/**
	 * Returns true if this material is vanilla based on whether or not it is a field in {@link Material}.
	 * @param material The material to check.
	 * @return True if the reference is one present in {@link Material}.
	 */
	public static boolean isMaterialVanilla(Material material) {
		if (vanillaMaterials == null) {
			vanillaMaterials = new ArrayList<>();
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
	 * Returns true if this block is vanilla based on whether or not it is a field in {@link Blocks}.
	 * @param block The block to check.
	 * @return True if the reference is one present in {@link Blocks}.
	 */
	public static boolean isBlockVanilla(Block block) {
		if (vanillaBlocks == null) {
			vanillaBlocks = new ArrayList<>();
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
	}
	
	static {
		// Default material bindings.
		populateMaterialToSpiritMtl();
		
		// Default block bindings.
		populateEffectiveMaterialsForBlocks();
		
		// MY BLOCKS
		setEffectiveMaterialForRegistryObject(BlockRegistry.DECAY_SURFACE_MYCELIUM, SpiritMaterial.SHROOM);
		useIfIn(BlockRegistry.DECAY_SURFACE_MYCELIUM);
		
		// MOD BLOCKS
		setSpiritMaterialForArbitrary(new ResourceLocation("biomesoplenty", "flesh"), SpiritMaterial.SHROOM);
		// TODO: Not hardcode these and allow them to be set via configuration.
	}
}
