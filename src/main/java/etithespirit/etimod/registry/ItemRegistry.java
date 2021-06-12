package etithespirit.etimod.registry;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.item.SpiritItemTier;
import etithespirit.etimod.item.combat.SpiritShield;
import etithespirit.etimod.item.tools.LumoWand;
import etithespirit.etimod.item.tools.SpiritOmniTool;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
	
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EtiMod.MODID);

	public static final RegistryObject<Item> LIGHT_TOOL = ITEMS.register("light_omnitool", SpiritOmniTool::new);
	public static final RegistryObject<Item> LIGHT_SHIELD = ITEMS.register("light_shield", SpiritShield::new);
	public static final RegistryObject<Item> LUMO_WAND = ITEMS.register("lumo_wand", LumoWand::new);
	
	public static void registerAll() {
		
		for (RegistryObject<Block> blockReg : BlockRegistry.BLOCKS_TO_REGISTER) {
			ITEMS.register(blockReg.getId().getPath(), () -> new BlockItem(blockReg.get(), new Item.Properties()));
		}
		
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
