package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.item.combat.SpiritArc;
import etithespirit.orimod.common.item.combat.SpiritShield;
import etithespirit.orimod.common.item.tools.LumoWand;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers all items of the mod to the game.
 */
public final class ItemRegistry {
	
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OriMod.MODID);
	
	// public static final RegistryObject<Item> LIGHT_TOOL = ITEMS.register("light_omnitool", SpiritOmniTool::new);
	/***/ public static final RegistryObject<Item> LIGHT_SHIELD = ITEMS.register("light_shield", SpiritShield::new);
	/***/ public static final RegistryObject<Item> LUMO_WAND = ITEMS.register("lumo_wand", LumoWand::new);
	
	//WATER_BUCKET = registerItem("water_bucket", new BucketItem(Fluids.WATER, (new Item.Properties()).craftRemainder(BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
	/***/ public static final RegistryObject<Item> POISON_BUCKET = ITEMS.register("decay_poison_bucket", () -> new BucketItem(FluidRegistry.DECAY_FLUID_STATIC::get, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
	
	public static final RegistryObject<Item> SPIRIT_ARC = ITEMS.register("spirit_arc", SpiritArc::new);
	
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
				return new BlockItem(block, props);
			});
		}
		
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
