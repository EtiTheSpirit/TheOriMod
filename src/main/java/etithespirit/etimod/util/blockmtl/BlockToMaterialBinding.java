package etithespirit.etimod.util.blockmtl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Function3;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.block.ExtendedMaterial;
import etithespirit.etimod.imc.IMCRegistryError;
import etithespirit.etimod.imc.IMCStatusContainer;
import etithespirit.etimod.registry.BlockRegistry;
import etithespirit.etimod.util.EtiUtils;
import etithespirit.etimod.util.blockmtl.defaultimpl.DefaultImplementations;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A custom binding from block to material that may not necessarily use the Block's defined material.<br/>
 * If a block is not defined in this class to have a custom material, then the material it was constructed with is used.
 * @author Eti
 *
 */
@SuppressWarnings("unused")
public class BlockToMaterialBinding {
	
	private BlockToMaterialBinding() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * Maps blocks to materials. Searched after SPECIFIC_STATE_TO_SPIRIT_MTL
	 */
	private static final Map<Block, Material> BLOCK_TO_MATERIAL = new HashMap<Block, Material>();
	
	/**
	 * Maps blocks to specific spirit materials. Searched after SPECIFIC_STATE_TO_SPIRIT_MTL
	 */
	private static final Map<Block, SpiritMaterial> BLOCK_TO_SPIRIT_MTL = new HashMap<Block, SpiritMaterial>();
	
	/**
	 * A map from specific block states to spirit materials. Searched first.
	 */
	private static final Map<BlockState, SpiritMaterial> SPECIFIC_STATE_TO_SPIRIT_MTL = new HashMap<BlockState, SpiritMaterial>();
	
	/**
	 * Maps arbitrary resources to specific spirit materials. Should be searched last.
	 */
	private static final Map<ResourceLocation, SpiritMaterial> ARB_BLOCK_TO_SPIRIT_MTL = new HashMap<ResourceLocation, SpiritMaterial>();
	
	/**
	 * A lookup to access spirit materials from vanilla materials.
	 */
	private static final Map<Material, SpiritMaterial> MATERIAL_TO_SPIRIT_MTL = new HashMap<Material, SpiritMaterial>();
	
	/**
	 * A lookup from BlockState to a SpiritMaterialModState which determines the subtype of the sound for that block. This is for use when the state is constant for a given BlockState, and if that block's SpiritMaterial is CONDITIONAL
	 */
	private static final Map<BlockState, SpiritMaterialModState> BLOCK_CONSTANT_STATE_OVERRIDES = new HashMap<BlockState, SpiritMaterialModState>();
	
	/**
	 * Identical to BLOCK_CONDITIONAL_STATE_OVERRIDES, but it functions for entire vanilla materials.
	 */
	private static final Map<Material, SpiritMaterialModState> MATERIAL_CONSTANT_STATE_OVERRIDES = new HashMap<Material, SpiritMaterialModState>();
	
	/**
	 * Identical to BLOCK_MATERIAL_CONSTANT_STATE_OVERRIDES, except for the fact that it's used when the state is dynamic.<br/>
	 * <strong>The Function3 uses the following parameters:</strong> {@code Entity entityToTest, BlockState blockWalkingOn, BlockState blockWalkingIn}<br/>
	 * <strong>The Function3 returns:</strong> The material state to use given the circumstances.
	 */
	private static final Map<BlockState, Function3<Entity, BlockPos, BlockPos, SpiritMaterialModState>> BLOCK_CONDITIONAL_STATE_OVERRIDES = new HashMap<BlockState, Function3<Entity, BlockPos, BlockPos, SpiritMaterialModState>>();
	
	/**
	 * Identical to BLOCK_CONDITIONAL_STATE_OVERRIDES, but it functions for entire vanilla materials.
	 */
	private static final Map<Material, Function3<Entity, BlockPos, BlockPos, SpiritMaterialModState>> MATERIAL_CONDITIONAL_STATE_OVERRIDES = new HashMap<Material, Function3<Entity, BlockPos, BlockPos, SpiritMaterialModState>>();
	
	/**
	 * For blocks that the player's feet are inside of, if the block is in this list, it should be tested as the effective material rather than the block they are standing on
	 */
	private static final List<Block> BLOCKS_TO_TEST_IF_INSIDE = new ArrayList<Block>();
	
	/**
	 * For blocks that the player's feet are inside of, if the block is in this list, it should be tested as the effective material rather than the block they are standing on
	 */
	private static final List<ResourceLocation> ARB_BLOCKS_TO_TEST_IF_INSIDE = new ArrayList<ResourceLocation>();
	
	public static SpiritMaterial getMaterialFor(BlockState state) {		
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
	
	public static SpiritMaterial getMaterialFor(Entity entity, BlockPos blockStandingOn, @Nullable BlockPos blockStandingIn) {
		SpiritMaterialModState state = getStateWhen(entity, blockStandingOn, blockStandingIn);
		if (state.equals(SpiritMaterialModState.DEFAULT)) {
			return getMaterialFor(getAppropriateMaterialBetween(entity.world.getBlockState(blockStandingOn), blockStandingIn != null ? entity.world.getBlockState(blockStandingIn) : null));
		} else {
			return getMaterialFor(entity.world.getBlockState(blockStandingOn));
		}
	}
	
	/**
	 * References BLOCK_MATERIAL_CONSTANT_STATE_OVERRIDES (or the conditional variant of that map) to figure out what state to use.
	 * @param entity Only used for conditional cases. This is the entity to test, which is especially helpful when the condition relies on something about the world the entity is in.
	 * @param isStandingOn The block the entity is standing on.
	 * @param isStandingIn The block the entity is standing in.
	 * @return
	 */
	public static SpiritMaterialModState getStateWhen(Entity entity, BlockPos posStandingOn, @Nullable BlockPos posStandingIn) {
		BlockState isStandingOn = entity.world.getBlockState(posStandingOn);
		//BlockState isStandingIn = entity.world.getBlockState(posStandingIn);
		//BlockState target = getAppropriateMaterialBetween(isStandingOn, isStandingIn);
		BlockState target = isStandingOn;
		
		//for (int i = 0; i < 2; i++) {
			if (BLOCK_CONSTANT_STATE_OVERRIDES.containsKey(target)) {
				return BLOCK_CONSTANT_STATE_OVERRIDES.get(target);
			}
			
			if (MATERIAL_CONSTANT_STATE_OVERRIDES.containsKey(target.getMaterial())) {
				return MATERIAL_CONSTANT_STATE_OVERRIDES.get(target.getMaterial());
			}
			
			if (BLOCK_CONDITIONAL_STATE_OVERRIDES.containsKey(target)) {
				return BLOCK_CONDITIONAL_STATE_OVERRIDES.get(target).apply(entity, posStandingOn, posStandingIn);
			}
			
			if (MATERIAL_CONDITIONAL_STATE_OVERRIDES.containsKey(target.getMaterial())) {
				return MATERIAL_CONDITIONAL_STATE_OVERRIDES.get(target.getMaterial()).apply(entity, posStandingOn, posStandingIn);
			}
			/*
			if (getAppropriateMaterialBetween(isStandingOn, isStandingIn) == isStandingIn) {
				target = isStandingIn;
			} else {
				break;
			}
		}
		*/
		return SpiritMaterialModState.DEFAULT;
	}
	
	/**
	 * Given two blockstates, this determines which state should be used to grab the material from.
	 * @param blockStandingOn
	 * @param blockStandingIn
	 * @return
	 */
	public static BlockState getAppropriateMaterialBetween(BlockState blockStandingOn, @Nullable BlockState blockStandingIn) {
		if (blockStandingIn != null && 
				(BLOCKS_TO_TEST_IF_INSIDE.contains(blockStandingIn.getBlock()) || 
				ARB_BLOCKS_TO_TEST_IF_INSIDE.contains(blockStandingIn.getBlock().getRegistryName()))) {
			return blockStandingIn;
		}
		return blockStandingOn;
	}
	
	private static void setEffectiveMaterialFor(Block block, Material mtl) {
		BLOCK_TO_MATERIAL.put(block, mtl);
	}
	
	private static void setEffectiveMaterialFor(Block block, SpiritMaterial mtl) {
		BLOCK_TO_SPIRIT_MTL.put(block, mtl);
	}
	
	private static void setEffectiveMaterialForState(BlockState state, SpiritMaterial mtl) {
		SPECIFIC_STATE_TO_SPIRIT_MTL.put(state, mtl);
	}
	
	private static void setEffectiveMaterialFor(ResourceLocation loc, SpiritMaterial mtl) {
		ARB_BLOCK_TO_SPIRIT_MTL.put(loc, mtl);
	}
	
	private static void setEffectiveMaterialFor(RegistryObject<Block> block, SpiritMaterial mtl) {
		setEffectiveMaterialFor(block.getId(), mtl);
	}
	
	private static void setSubMaterialFor(BlockState state, SpiritMaterialModState subMtl) {
		BLOCK_CONSTANT_STATE_OVERRIDES.put(state, subMtl);
	}
	
	private static void setSubMaterialConditionFor(BlockState state, Function3<Entity, BlockPos, BlockPos, SpiritMaterialModState> condition) {
		BLOCK_CONDITIONAL_STATE_OVERRIDES.put(state, condition);
	}
	
	private static void setSubMaterialFor(Material vanillaMaterial, SpiritMaterialModState subMtl) {
		MATERIAL_CONSTANT_STATE_OVERRIDES.put(vanillaMaterial, subMtl);
	}
	
	private static void setSubMaterialConditionFor(Material vanillaMaterial, Function3<Entity, BlockPos, BlockPos, SpiritMaterialModState> condition) {
		MATERIAL_CONDITIONAL_STATE_OVERRIDES.put(vanillaMaterial, condition);
	}
	
	private static void setSubMaterialFor(Block block, SpiritMaterialModState subMtl) {
		ImmutableList<BlockState> states = block.getStateContainer().getValidStates();
		for (BlockState state : states) {
			setSubMaterialFor(state, subMtl);
		}
	}
	
	// Yes this needs to be public
	public static void setSubMaterialConditionFor(Block block, Function3<Entity, BlockPos, BlockPos, SpiritMaterialModState> condition) {
		ImmutableList<BlockState> states = block.getStateContainer().getValidStates();
		for (BlockState state : states) {
			setSubMaterialConditionFor(state, condition);
		}
	}
	
	private static void useIfIn(Block block) {
		BLOCKS_TO_TEST_IF_INSIDE.add(block);
	}
	
	private static void useIfIn(BlockState state) {
		useIfIn(state.getBlock());
	}
	
	private static void useIfIn(ResourceLocation loc) {
		ARB_BLOCKS_TO_TEST_IF_INSIDE.add(loc);
	}
	
	private static void useIfIn(RegistryObject<Block> block) {
		useIfIn(block.getId());
	}
	
	public static IMCStatusContainer IMC_TrySetEffectiveMaterialFor(String rawMessage) {
		String[] components = rawMessage.split("\\|");
		if (components.length < 2 || components.length > 3) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_INVALID_ARG_COUNT, "either 2 or 3", String.valueOf(components.length));
		}
		
		String blockRef = components[0];
		String spiritMaterialName = components[1];
		boolean useIfIn = false;
		if (components.length == 3) {
			useIfIn = Boolean.parseBoolean(components[2]);
			if (useIfIn == false && !components[2].toLowerCase().equals("false")) {
				// false but the word "false" wasn't input.
				return new IMCStatusContainer(IMCRegistryError.FAILURE_INVALID_TYPE, components[2], "java.lang.Boolean");
			}
		}
		
		// Find spirit material
		SpiritMaterial toBind = null;
		try {
			toBind = Enum.valueOf(SpiritMaterial.class, spiritMaterialName);
		} catch (Exception exc) { }
		if (toBind == null) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_INVALID_SPIRIT_MATERIAL, spiritMaterialName);
		}
		
		ResourceLocation block = new ResourceLocation(blockRef);
		if (block.getNamespace().equalsIgnoreCase("minecraft")) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_BLOCK_IS_UNOWNED, block.toString());
		}
		//RegistryObject.of(block, ForgeRegistries.BLOCKS);
		ARB_BLOCK_TO_SPIRIT_MTL.put(block, toBind);
		
		if (useIfIn) {
			useIfIn(block);
		}
		
		return IMCStatusContainer.SUCCESS;
	}
	
	private static List<Material> vanillaMaterials = null;
	private static boolean isMaterialVanilla(Material userDefined) {
		if (vanillaMaterials == null) {
			vanillaMaterials = new ArrayList<Material>();
			Field[] allFields = Material.class.getFields();
			for (Field field : allFields) {
				if (EtiUtils.hasFlag(field.getModifiers(), Modifier.FINAL) && EtiUtils.hasFlag(field.getModifiers(), Modifier.STATIC)) {
					try {
						vanillaMaterials.add((Material)field.get(null));
					} catch (Exception exc) { }
				}
			}
		}
		
		return vanillaMaterials.contains(userDefined);
	}
	public static IMCStatusContainer IMC_TrySetSoundForMaterial(String rawMessage) {
		String[] components = rawMessage.split("\\|");
		if (components.length != 2) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_INVALID_ARG_COUNT, "2", String.valueOf(components.length));
		}
		
		String className = components[0];
		String spiritMaterialName = components[1];
		String field = "ERR_FIELD_NOT_POPULATED";
		Material mtl = null;
		try {
			int lastDot = className.lastIndexOf(".");
			Class<?> mtlTarget = Class.forName(className.substring(0, lastDot));
			
			if (mtlTarget.equals(Material.class)) {
				return new IMCStatusContainer(IMCRegistryError.FAILURE_MATERIAL_IS_VANILLA);
			}
			
			field = className.substring(lastDot + 1);
			Field material = mtlTarget.getField(field);
			
			try {
				Field realMtl = Material.class.getField(field);
				if (realMtl.equals(material)) {
					// ^ This is comparing the actual fields.
					return new IMCStatusContainer(IMCRegistryError.FAILURE_MATERIAL_IS_VANILLA);
				}
			} catch (NoSuchFieldException | SecurityException e) { }
			
			try {
				material.setAccessible(true);
			} catch (Exception e) {
				return new IMCStatusContainer(IMCRegistryError.FAILURE_MATERIAL_FIELD_IS_PRIVATE_AND_LOCKED);
			}
			mtl = (Material)material.get(null);
			
			if (isMaterialVanilla(mtl)) {
				return new IMCStatusContainer(IMCRegistryError.FAILURE_MATERIAL_IS_VANILLA);
			}
		} catch (ClassNotFoundException e) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_COULD_NOT_RESOLVE_CLASS, className);
		} catch (NoSuchFieldException | SecurityException e) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_COULD_NOT_RESOLVE_FIELD, field, className);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_FIELD_IS_NOT_STATIC, field, className);
		} catch (ClassCastException e) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_NOT_MATERIAL_CLASS, className);
		}
		
		if (mtl == null) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_MATERIAL_IS_NULL);
		}
		
		// Find spirit material
		SpiritMaterial toBind = null;
		try {
			toBind = Enum.valueOf(SpiritMaterial.class, spiritMaterialName);
		} catch (Exception exc) { }
		if (toBind == null) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_INVALID_SPIRIT_MATERIAL, spiritMaterialName);
		}
		MATERIAL_TO_SPIRIT_MTL.put(mtl, toBind);
		
		return IMCStatusContainer.SUCCESS;
	}
	
	public static IMCStatusContainer IMC_SetUseIfIn(String rawMessage) {
		String[] components = rawMessage.split("\\|");
		if (components.length != 2) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_INVALID_ARG_COUNT, "2", String.valueOf(components.length));
		}
		
		String blockRef = components[0];
		ResourceLocation block = new ResourceLocation(blockRef);
		if (block.getNamespace().equalsIgnoreCase("minecraft")) {
			return new IMCStatusContainer(IMCRegistryError.FAILURE_BLOCK_IS_UNOWNED, block.toString());
		}
		
		boolean useIfIn = Boolean.parseBoolean(components[1]);
		if (useIfIn == false && !components[1].toLowerCase().equals("false")) {
			// false but the word "false" wasn't input.
			return new IMCStatusContainer(IMCRegistryError.FAILURE_INVALID_TYPE, components[1], "java.lang.Boolean");
		}
		
		if (useIfIn) {
			useIfIn(block);
		}
		
		return IMCStatusContainer.SUCCESS;
	}
	
	private static void populateMaterialToSpiritMtl() {
		// VANILLA MATERIALS
		MATERIAL_TO_SPIRIT_MTL.put(Material.AIR, SpiritMaterial.NULL);
		MATERIAL_TO_SPIRIT_MTL.put(Material.BAMBOO, SpiritMaterial.WOOD_DRY);
		MATERIAL_TO_SPIRIT_MTL.put(Material.BAMBOO_SAPLING, SpiritMaterial.GRASS_CRISP);
		MATERIAL_TO_SPIRIT_MTL.put(Material.STRUCTURE_VOID, SpiritMaterial.INHERITED); // mojmap: STRUCTURAL_AIR
		MATERIAL_TO_SPIRIT_MTL.put(Material.BUBBLE_COLUMN, SpiritMaterial.INHERITED);
		MATERIAL_TO_SPIRIT_MTL.put(Material.BARRIER, SpiritMaterial.GLASS);
		MATERIAL_TO_SPIRIT_MTL.put(Material.CACTUS, SpiritMaterial.SHROOM);
		MATERIAL_TO_SPIRIT_MTL.put(Material.CAKE, SpiritMaterial.SHROOM);
		MATERIAL_TO_SPIRIT_MTL.put(Material.CLAY, SpiritMaterial.SLIMY);
		MATERIAL_TO_SPIRIT_MTL.put(Material.CARPET, SpiritMaterial.WOOL); // mojmap: CLOTH_DECORATION
		MATERIAL_TO_SPIRIT_MTL.put(Material.CORAL, SpiritMaterial.ICE);
		MATERIAL_TO_SPIRIT_MTL.put(Material.MISCELLANEOUS, SpiritMaterial.INHERITED); // mojmap: DECORATION
		MATERIAL_TO_SPIRIT_MTL.put(Material.EARTH, SpiritMaterial.SAND); // mojmap: DIRT
		MATERIAL_TO_SPIRIT_MTL.put(Material.DRAGON_EGG, SpiritMaterial.ICE); // mojmap: EGG
		MATERIAL_TO_SPIRIT_MTL.put(Material.TNT, SpiritMaterial.SAND); // mojmap: EXPLOSIVE
		MATERIAL_TO_SPIRIT_MTL.put(Material.FIRE, SpiritMaterial.INHERITED);
		MATERIAL_TO_SPIRIT_MTL.put(Material.GLASS, SpiritMaterial.GLASS);
		MATERIAL_TO_SPIRIT_MTL.put(Material.PLANTS, SpiritMaterial.GRASS_SOFT); // mojmap: PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.ANVIL, SpiritMaterial.METAL); // mojmap: HEAVY_METAL
		MATERIAL_TO_SPIRIT_MTL.put(Material.ICE, SpiritMaterial.ICE);
		MATERIAL_TO_SPIRIT_MTL.put(Material.PACKED_ICE, SpiritMaterial.ICE); // mojmap: ICE_SOLID
		MATERIAL_TO_SPIRIT_MTL.put(Material.LAVA, SpiritMaterial.INHERITED);
		MATERIAL_TO_SPIRIT_MTL.put(Material.LEAVES, SpiritMaterial.GRASS_SOFT);
		MATERIAL_TO_SPIRIT_MTL.put(Material.IRON, SpiritMaterial.METAL); // mojmap: METAL
		MATERIAL_TO_SPIRIT_MTL.put(Material.NETHER_WOOD, SpiritMaterial.CONDITIONAL_WOOD);
		MATERIAL_TO_SPIRIT_MTL.put(Material.PISTON, SpiritMaterial.ROCK);
		MATERIAL_TO_SPIRIT_MTL.put(Material.TALL_PLANTS, SpiritMaterial.GRASS_SOFT); // mojmap: REPLACEABLE_PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.PORTAL, SpiritMaterial.GLASS);
		MATERIAL_TO_SPIRIT_MTL.put(Material.NETHER_PLANTS, SpiritMaterial.GRASS_CRISP); // mojmap: REPLACEABLE_FIREPROOF_PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.PLANTS, SpiritMaterial.GRASS_SOFT); // mojmap: PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.OCEAN_PLANT, SpiritMaterial.SHROOM); // mojmap: WATER_PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.SAND, SpiritMaterial.SAND);
		MATERIAL_TO_SPIRIT_MTL.put(Material.SHULKER, SpiritMaterial.ROCK); // mojmap: SHULKER_SHELL
		MATERIAL_TO_SPIRIT_MTL.put(Material.SNOW_BLOCK, SpiritMaterial.SNOW); // mojmap: SNOW
		MATERIAL_TO_SPIRIT_MTL.put(Material.SPONGE, SpiritMaterial.WOOL);
		MATERIAL_TO_SPIRIT_MTL.put(Material.ROCK, SpiritMaterial.ROCK); // mojmap: STONE
		MATERIAL_TO_SPIRIT_MTL.put(Material.SNOW, SpiritMaterial.SNOW); // mojmap: TOP_SNOW
		MATERIAL_TO_SPIRIT_MTL.put(Material.GOURD, SpiritMaterial.SHROOM); // mojmap: VEGETABLE
		MATERIAL_TO_SPIRIT_MTL.put(Material.WATER, SpiritMaterial.CONDITIONAL_WATER);
		MATERIAL_TO_SPIRIT_MTL.put(Material.SEA_GRASS, SpiritMaterial.SHROOM); // mojmap: REPLACEABLE_WATER_PLANT
		MATERIAL_TO_SPIRIT_MTL.put(Material.WEB, SpiritMaterial.INHERITED);
		MATERIAL_TO_SPIRIT_MTL.put(Material.WOOD, SpiritMaterial.CONDITIONAL_WOOD);
		MATERIAL_TO_SPIRIT_MTL.put(Material.WOOL, SpiritMaterial.WOOL);
		
		// MY MATERIALS
		MATERIAL_TO_SPIRIT_MTL.put(ExtendedMaterial.LIGHT, SpiritMaterial.GLASS);
	}
	
	private static void populateEffectiveMaterialsForBlocks() {
		// STATE BINDINGS
		setEffectiveMaterialForState(Blocks.GRASS_BLOCK.getDefaultState().with(BlockStateProperties.SNOWY, Boolean.TRUE), SpiritMaterial.SNOW);
		
		// VANILLA BLOCKS
		// Grass variants & General Overworld
		setEffectiveMaterialFor(Blocks.PODZOL, SpiritMaterial.SNOW);
		setEffectiveMaterialFor(Blocks.GRASS_BLOCK, SpiritMaterial.GRASS_SOFT); // wat
		setEffectiveMaterialFor(Blocks.MYCELIUM, SpiritMaterial.SHROOM); // from grass
		setEffectiveMaterialFor(Blocks.GRAVEL, SpiritMaterial.GRAVEL_DRY); // from sand
		
		setEffectiveMaterialFor(Blocks.SAND, SpiritMaterial.SAND);
		setEffectiveMaterialFor(Blocks.SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.CHISELED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SANDSTONE_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SANDSTONE_WALL, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.CUT_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.CUT_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SMOOTH_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SMOOTH_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SMOOTH_SANDSTONE_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		
		setEffectiveMaterialFor(Blocks.RED_SAND, SpiritMaterial.SAND);
		setEffectiveMaterialFor(Blocks.RED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.CHISELED_RED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.RED_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.RED_SANDSTONE_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.RED_SANDSTONE_WALL, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.CUT_RED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.CUT_RED_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SMOOTH_RED_SANDSTONE, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SMOOTH_RED_SANDSTONE_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		
		
		// Shrooms (there is a fungus among us)
		setEffectiveMaterialFor(Blocks.RED_MUSHROOM_BLOCK, SpiritMaterial.SHROOM); // from wood
		setEffectiveMaterialFor(Blocks.BROWN_MUSHROOM_BLOCK, SpiritMaterial.SHROOM); // from wood
		setEffectiveMaterialFor(Blocks.MUSHROOM_STEM, SpiritMaterial.SHROOM); // from wood
		setEffectiveMaterialFor(Blocks.CHORUS_FLOWER, SpiritMaterial.SHROOM); // from wood
		setEffectiveMaterialFor(Blocks.CHORUS_PLANT, SpiritMaterial.SHROOM); // from wood
		setEffectiveMaterialFor(Blocks.NETHER_WART_BLOCK, SpiritMaterial.SHROOM);
		setEffectiveMaterialFor(Blocks.CRIMSON_FUNGUS, SpiritMaterial.SHROOM);
		setEffectiveMaterialFor(Blocks.WARPED_FUNGUS, SpiritMaterial.SHROOM);
		
		
		// Ores n Spawners
		setEffectiveMaterialFor(Blocks.SPAWNER, SpiritMaterial.METAL); // from stone
		setEffectiveMaterialFor(Blocks.EMERALD_BLOCK, SpiritMaterial.ROCK); // from metal
		setEffectiveMaterialFor(Blocks.LAPIS_BLOCK, SpiritMaterial.ROCK); // from metal
		setEffectiveMaterialFor(Blocks.DIAMOND_BLOCK, SpiritMaterial.ROCK); // from metal
		
		
		// The Netha (i do not have a nether pass)
		setEffectiveMaterialFor(Blocks.NETHER_QUARTZ_ORE, SpiritMaterial.INHERITED); // from stone
		setEffectiveMaterialFor(Blocks.NETHER_GOLD_ORE, SpiritMaterial.INHERITED); // from stone
		setEffectiveMaterialFor(Blocks.NETHERRACK, SpiritMaterial.INHERITED); // duh
		setEffectiveMaterialFor(Blocks.BASALT, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.POLISHED_BASALT, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.GILDED_BLACKSTONE, SpiritMaterial.INHERITED);
		// setEffectiveMaterialFor(Blocks.NETHERITE_BLOCK, SpiritMaterial.INHERITED); // from metal
		setEffectiveMaterialFor(Blocks.ANCIENT_DEBRIS, SpiritMaterial.INHERITED); // from metal
		
		
		// Command Blocks
		setEffectiveMaterialFor(Blocks.COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		setEffectiveMaterialFor(Blocks.CHAIN_COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		setEffectiveMaterialFor(Blocks.REPEATING_COMMAND_BLOCK, SpiritMaterial.METAL); // from stone
		
		
		// Redstone
		setEffectiveMaterialFor(Blocks.DAYLIGHT_DETECTOR, SpiritMaterial.ROCK); // from wood
		setEffectiveMaterialFor(Blocks.REDSTONE_LAMP, SpiritMaterial.GLASS); // from stone
		
		
		// Da Goop & Da Bees
		setEffectiveMaterialFor(Blocks.CLAY, SpiritMaterial.SLIMY); // from gravel?
		setEffectiveMaterialFor(Blocks.SLIME_BLOCK, SpiritMaterial.SLIMY);
		setEffectiveMaterialFor(Blocks.HONEY_BLOCK, SpiritMaterial.SLIMY);
		setEffectiveMaterialFor(Blocks.HONEYCOMB_BLOCK, SpiritMaterial.SHROOM); // NOT slimy
		setEffectiveMaterialFor(Blocks.BEE_NEST, SpiritMaterial.SHROOM); // from wood
		
		
		// B R I C K S
		setEffectiveMaterialFor(Blocks.BRICKS, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.BRICK_SLAB, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.BRICK_STAIRS, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.BRICK_WALL, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.NETHER_BRICKS, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.NETHER_BRICK_SLAB, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.NETHER_BRICK_STAIRS, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.NETHER_BRICK_WALL, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.NETHER_BRICK_FENCE, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.CHISELED_NETHER_BRICKS, SpiritMaterial.SOLID_CERAMICS);
		setEffectiveMaterialFor(Blocks.CRACKED_NETHER_BRICKS, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.RED_NETHER_BRICKS, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.RED_NETHER_BRICK_STAIRS, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.RED_NETHER_BRICK_SLAB, SpiritMaterial.CRACKED_CERAMICS);
		setEffectiveMaterialFor(Blocks.RED_NETHER_BRICK_WALL, SpiritMaterial.CRACKED_CERAMICS);
		
		
		// outsourced to ungengengingan vilag (twomad home)
		setEffectiveMaterialFor(Blocks.TARGET, SpiritMaterial.WOOL); // from wood -- literal target block (like you shoot arrows at it, not a "goal")
		setEffectiveMaterialFor(Blocks.DRIED_KELP_BLOCK, SpiritMaterial.GRASS_CRISP); // from shroom
		setEffectiveMaterialFor(Blocks.SMITHING_TABLE, SpiritMaterial.METAL); // from wood
		setEffectiveMaterialFor(Blocks.HAY_BLOCK, SpiritMaterial.GRASS_CRISP); // from ... wool? forgot tbh
		setEffectiveMaterialFor(Blocks.BLACKSTONE, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.BLACKSTONE_SLAB, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.BLACKSTONE_STAIRS, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.BLACKSTONE_WALL, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.POLISHED_BLACKSTONE, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.POLISHED_BLACKSTONE_BRICKS, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.CHISELED_POLISHED_BLACKSTONE, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, SpiritMaterial.ASH);
		setEffectiveMaterialFor(Blocks.GILDED_BLACKSTONE, SpiritMaterial.ASH);
		
		useIfIn(Blocks.SNOW);
		useIfIn(Blocks.VINE);
	}
	
	private static void populateConditionalBlockSoundStates() {
		setSubMaterialConditionFor(Material.WOOD, DefaultImplementations::getModStateForWoodBlock);
		setSubMaterialConditionFor(Material.WATER, DefaultImplementations::getModStateForWater);
		// setSubMaterialConditionFor(Blocks.GRAVEL, DefaultImplementations::getModStateForGravelBlock);
	}
	
	static {
		// Default material bindings.
		populateMaterialToSpiritMtl();
		
		// Default block bindings.
		populateEffectiveMaterialsForBlocks();
		
		// For conditional sounds.
		populateConditionalBlockSoundStates();
		
		// MY BLOCKS
		setEffectiveMaterialFor(BlockRegistry.DECAY_SURFACE_MYCELIUM, SpiritMaterial.SHROOM);
		useIfIn(BlockRegistry.DECAY_SURFACE_MYCELIUM);
		
		// MOD BLOCKS
		setEffectiveMaterialFor(new ResourceLocation("biomesoplenty", "flesh"), SpiritMaterial.SHROOM);
		// TODO: Not hardcode these and allow them to be set via configuration.
	}
}
