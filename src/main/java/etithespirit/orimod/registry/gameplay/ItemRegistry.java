package etithespirit.orimod.registry.gameplay;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.item.IModelPredicateProvider;
import etithespirit.orimod.common.item.armor.OriModArmorItem;
import etithespirit.orimod.common.item.combat.SpiritArc;
import etithespirit.orimod.common.item.combat.SpiritShield;
import etithespirit.orimod.common.item.crafting.BindingEssenceItem;
import etithespirit.orimod.common.item.crafting.GorlekIngotNuggetItem;
import etithespirit.orimod.common.item.crafting.GorlekNetheriteAlloyIngot;
import etithespirit.orimod.common.item.crafting.HardlightShardItem;
import etithespirit.orimod.common.item.crafting.LightLensItem;
import etithespirit.orimod.common.item.crafting.RawGorlekOreItem;
import etithespirit.orimod.common.item.data.IOriModItemTierProvider;
import etithespirit.orimod.common.item.data.UniversalOriModItemTier;
import etithespirit.orimod.common.item.tools.LumoWand;
import etithespirit.orimod.common.item.tools.OriModToolItem;
import etithespirit.orimod.common.tags.OriModItemTags;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.world.FluidRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;

/**
 * Registers all items of the mod to the game.
 */
public final class ItemRegistry {
	
	private static final Map<RegistryObject<? extends Block>, BlockItem> LOOKUP_FOR_BLOCKS = new HashMap<>();
	private static final Map<Block, BlockItem> LOOKUP_FOR_REGISTERED_BLOCKS = new HashMap<>();
	
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OriMod.MODID);
	
	// public static final RegistryObject<Item> LIGHT_TOOL = ITEMS.register("light_omnitool", SpiritOmniTool::new);
	/***/ public static final RegistryObject<Item> LIGHT_SHIELD = ITEMS.register("light_shield", SpiritShield::new);
	/***/ public static final RegistryObject<Item> LUMO_WAND = ITEMS.register("lumo_wand", LumoWand::new);
	
	//WATER_BUCKET = registerItem("water_bucket", new BucketItem(Fluids.WATER, (new Item.Properties()).craftRemainder(BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
	/***/ public static final RegistryObject<Item> POISON_BUCKET = ITEMS.register("decay_poison_bucket", () -> new BucketItem(FluidRegistry.DECAY_FLUID_STATIC::get, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
	
	/***/ public static final RegistryObject<Item> HARDLIGHT_SHARD = ITEMS.register("hardlight_shard", HardlightShardItem::new);
	/***/ public static final RegistryObject<Item> BINDING_ESSENCE = ITEMS.register("binding_essence", BindingEssenceItem::new);
	/***/ public static final RegistryObject<Item> LARGE_LIGHT_LENS = ITEMS.register("large_light_lens", LightLensItem::new);
	
	public static final RegistryObject<Item> SPIRIT_ARC = ITEMS.register("spirit_arc", SpiritArc::new);
	
	public static final RegistryObject<Item> LIGHT_PICKAXE;
	public static final RegistryObject<Item> LIGHT_SHOVEL;
	public static final RegistryObject<Item> LIGHT_AXE;
	public static final RegistryObject<Item> LIGHT_SWORD;
	public static final RegistryObject<Item> LIGHT_HOE;
	
	public static final RegistryObject<Item> GORLEK_STEEL_PICKAXE;
	public static final RegistryObject<Item> GORLEK_STEEL_SHOVEL;
	public static final RegistryObject<Item> GORLEK_STEEL_AXE;
	public static final RegistryObject<Item> GORLEK_STEEL_SWORD;
	public static final RegistryObject<Item> GORLEK_STEEL_HOE;
	
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_PICKAXE;
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_SHOVEL;
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_AXE;
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_SWORD;
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_HOE;
	
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_PICKAXE;
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_SHOVEL;
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_AXE;
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_SWORD;
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_HOE;
	
	public static final RegistryObject<Item> LIGHT_HELMET;
	public static final RegistryObject<Item> LIGHT_CHESTPLATE;
	public static final RegistryObject<Item> LIGHT_LEGGINGS;
	public static final RegistryObject<Item> LIGHT_BOOTS;
	
	public static final RegistryObject<Item> GORLEK_STEEL_HELMET;
	public static final RegistryObject<Item> GORLEK_STEEL_CHESTPLATE;
	public static final RegistryObject<Item> GORLEK_STEEL_LEGGINGS;
	public static final RegistryObject<Item> GORLEK_STEEL_BOOTS;
	
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_HELMET;
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_CHESTPLATE;
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_LEGGINGS;
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_BOOTS;
	
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_HELMET;
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_CHESTPLATE;
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_LEGGINGS;
	public static final RegistryObject<Item> LUXEN_GORLEK_NETHERITE_ALLOY_BOOTS;
	
	public static final RegistryObject<Item> RAW_GORLEK_ORE = ITEMS.register("raw_gorlek_ore", RawGorlekOreItem::new);
	public static final RegistryObject<Item> GORLEK_STEEL_INGOT = ITEMS.register("gorlek_steel_ingot", GorlekIngotNuggetItem::new);
	public static final RegistryObject<Item> GORLEK_STEEL_NUGGET = ITEMS.register("gorlek_steel_nugget", GorlekIngotNuggetItem::new);
	public static final RegistryObject<Item> GORLEK_NETHERITE_ALLOY_INGOT = ITEMS.register("gorlek_netherite_alloy_ingot", GorlekNetheriteAlloyIngot::new);
	
	
	static {
		RegistryObject<Item>[] entries;
		
		entries = OriModArmorItem.autoRegisterAllSlotsOfType(ITEMS, UniversalOriModItemTier.LIGHT);
		LIGHT_HELMET = entries[0];
		LIGHT_CHESTPLATE = entries[1];
		LIGHT_LEGGINGS = entries[2];
		LIGHT_BOOTS = entries[3];
		
		
		entries = OriModArmorItem.autoRegisterAllSlotsOfType(ITEMS, UniversalOriModItemTier.GORLEK_STEEL);
		GORLEK_STEEL_HELMET = entries[0];
		GORLEK_STEEL_CHESTPLATE = entries[1];
		GORLEK_STEEL_LEGGINGS = entries[2];
		GORLEK_STEEL_BOOTS = entries[3];
		
		
		entries = OriModArmorItem.autoRegisterAllSlotsOfType(ITEMS, UniversalOriModItemTier.GORLEK_NETHERITE_ALLOY);
		GORLEK_NETHERITE_ALLOY_HELMET = entries[0];
		GORLEK_NETHERITE_ALLOY_CHESTPLATE = entries[1];
		GORLEK_NETHERITE_ALLOY_LEGGINGS = entries[2];
		GORLEK_NETHERITE_ALLOY_BOOTS = entries[3];
		
		
		entries = OriModArmorItem.autoRegisterAllSlotsOfType(ITEMS, UniversalOriModItemTier.LUXEN_GORLEK_NETHERITE_ALLOY);
		LUXEN_GORLEK_NETHERITE_ALLOY_HELMET = entries[0];
		LUXEN_GORLEK_NETHERITE_ALLOY_CHESTPLATE = entries[1];
		LUXEN_GORLEK_NETHERITE_ALLOY_LEGGINGS = entries[2];
		LUXEN_GORLEK_NETHERITE_ALLOY_BOOTS = entries[3];
		
		///////////////////////////////////////////////////////////////////
		
		entries = OriModToolItem.autoRegisterEntireTier(ITEMS, UniversalOriModItemTier.LIGHT);
		LIGHT_PICKAXE = entries[0];
		LIGHT_SHOVEL = entries[1];
		LIGHT_AXE = entries[2];
		LIGHT_SWORD = entries[3];
		LIGHT_HOE = entries[4];
		
		
		entries = OriModToolItem.autoRegisterEntireTier(ITEMS, UniversalOriModItemTier.GORLEK_STEEL);
		GORLEK_STEEL_PICKAXE = entries[0];
		GORLEK_STEEL_SHOVEL = entries[1];
		GORLEK_STEEL_AXE = entries[2];
		GORLEK_STEEL_SWORD = entries[3];
		GORLEK_STEEL_HOE = entries[4];
		
		
		entries = OriModToolItem.autoRegisterEntireTier(ITEMS, UniversalOriModItemTier.GORLEK_NETHERITE_ALLOY);
		GORLEK_NETHERITE_ALLOY_PICKAXE = entries[0];
		GORLEK_NETHERITE_ALLOY_SHOVEL = entries[1];
		GORLEK_NETHERITE_ALLOY_AXE = entries[2];
		GORLEK_NETHERITE_ALLOY_SWORD = entries[3];
		GORLEK_NETHERITE_ALLOY_HOE = entries[4];
		
		
		entries = OriModToolItem.autoRegisterEntireTier(ITEMS, UniversalOriModItemTier.LUXEN_GORLEK_NETHERITE_ALLOY);
		LUXEN_GORLEK_NETHERITE_ALLOY_PICKAXE = entries[0];
		LUXEN_GORLEK_NETHERITE_ALLOY_SHOVEL = entries[1];
		LUXEN_GORLEK_NETHERITE_ALLOY_AXE = entries[2];
		LUXEN_GORLEK_NETHERITE_ALLOY_SWORD = entries[3];
		LUXEN_GORLEK_NETHERITE_ALLOY_HOE = entries[4];
	}
	
	/***/
	public static void registerAll() {
		for (RegistryObject<? extends Block> blockReg : BlockRegistry.BLOCKS.getEntries()) {
			ITEMS.register(blockReg.getId().getPath(), () -> {
				Block block = blockReg.get();
				Item.Properties props;
				if (block instanceof IBlockItemPropertiesProvider provider) {
					props = provider.getPropertiesOfItem();
				} else {
					props = new Item.Properties();
				}
				BlockItem bi = new BlockItem(block, props);
				LOOKUP_FOR_BLOCKS.put(blockReg, bi);
				LOOKUP_FOR_REGISTERED_BLOCKS.put(block, bi);
				return bi;
			});
		}
		
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static void registerPredicates() {
		for (RegistryObject<? extends Item> item : ITEMS.getEntries()) {
			if (item.get() instanceof IModelPredicateProvider provider) {
				Map<ResourceLocation, ItemPropertyFunction> predicates = new HashMap<>();
				provider.getPredicates(predicates);
				for (ResourceLocation key : predicates.keySet()) {
					ItemProperties.register(item.get(), key, predicates.get(key)); // this line, this is the one you are interested in, future xan
				}
			}
		}
	}
	
	/**
	 * <strong>IDE ONLY!</strong> Verify that all tags and interface capabilities match for armor and tools implementing hardlight mechanics.
	 */
	public static void validateLightRepairableTags() {
		if (FMLEnvironment.production) throw new IllegalStateException("This method cannot be called during runtime - it *MUST* be called in the IDE!");
		
		for (RegistryObject<? extends Item> itemReg : ITEMS.getEntries()) {
			Item item = itemReg.get();
			if (item instanceof IOriModItemTierProvider provider) {
				if (provider.getOriModTier().getLuxenRepairCost().isEmpty() && item.builtInRegistryHolder().is(OriModItemTags.LIGHT_REPAIRABLE)) {
					throw new InputMismatchException("Item " + itemReg.getId() + " uses material-tier " + provider.getOriModTier().name() + " which does not declare a Light repair cost, but the item is in data/orimod/tags/items/light_repairable!");
				}
			}
		}
	}
	
	public static BlockItem getBlockItemOf(RegistryObject<? extends Block> block) {
		return LOOKUP_FOR_BLOCKS.get(block);
	}
	
	public static BlockItem getBlockItemOf(Block block) {
		return LOOKUP_FOR_REGISTERED_BLOCKS.get(block);
	}
	
}
