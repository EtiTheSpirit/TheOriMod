package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.item.combat.SpiritShield;
import etithespirit.orimod.common.item.tools.LumoWand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
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
	
	/***/
	public static void registerAll() {
		
		for (RegistryObject<Block> blockReg : BlockRegistry.BLOCKS_TO_REGISTER) {
			ITEMS.register(blockReg.getId().getPath(), () -> new BlockItem(blockReg.get(), new Item.Properties()));
		}
		
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
