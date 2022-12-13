package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.item.armor.LightArmorItem;
import etithespirit.orimod.common.item.combat.SpiritArc;
import etithespirit.orimod.common.item.combat.SpiritShield;
import etithespirit.orimod.common.item.crafting.HardlightShardItem;
import etithespirit.orimod.common.item.crafting.GenericLight16StackItem;
import etithespirit.orimod.common.item.tools.LightAxe;
import etithespirit.orimod.common.item.tools.LightHoe;
import etithespirit.orimod.common.item.tools.LightPickaxe;
import etithespirit.orimod.common.item.tools.LightShovel;
import etithespirit.orimod.common.item.tools.LightSword;
import etithespirit.orimod.common.item.tools.LumoWand;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
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
	/***/ public static final RegistryObject<Item> BINDING_ESSENCE = ITEMS.register("binding_essence", GenericLight16StackItem::new);
	/***/ public static final RegistryObject<Item> LARGE_LIGHT_LENS = ITEMS.register("large_light_lens", GenericLight16StackItem::new);
	
	public static final RegistryObject<Item> SPIRIT_ARC = ITEMS.register("spirit_arc", SpiritArc::new);
	public static final RegistryObject<Item> LIGHT_PICKAXE = ITEMS.register("light_pickaxe", LightPickaxe::new);
	public static final RegistryObject<Item> LIGHT_SHOVEL = ITEMS.register("light_shovel", LightShovel::new);
	public static final RegistryObject<Item> LIGHT_AXE = ITEMS.register("light_axe", LightAxe::new);
	public static final RegistryObject<Item> LIGHT_SWORD = ITEMS.register("light_sword", LightSword::new);
	public static final RegistryObject<Item> LIGHT_HOE = ITEMS.register("light_hoe", LightHoe::new);
	
	public static final RegistryObject<Item> LIGHT_HELMET = ITEMS.register("light_helmet", LightArmorItem::newHelmet);
	public static final RegistryObject<Item> LIGHT_CHESTPLATE = ITEMS.register("light_chestplate", LightArmorItem::newChestplate);
	public static final RegistryObject<Item> LIGHT_LEGS = ITEMS.register("light_leggings", LightArmorItem::newLegs);
	public static final RegistryObject<Item> LIGHT_BOOTS = ITEMS.register("light_boots", LightArmorItem::newBoots);
	
	/***/
	public static void registerAll() {
		for (RegistryObject<? extends Block> blockReg : BlockRegistry.BLOCKS_TO_REGISTER) {
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
	
	public static BlockItem getBlockItemOf(RegistryObject<? extends Block> block) {
		return LOOKUP_FOR_BLOCKS.get(block);
	}
	
	
	
	public static BlockItem getBlockItemOf(Block block) {
		return LOOKUP_FOR_REGISTERED_BLOCKS.get(block);
	}
	
}
