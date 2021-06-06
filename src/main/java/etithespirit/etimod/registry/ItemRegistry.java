package etithespirit.etimod.registry;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.item.SpiritItemTier;
import etithespirit.etimod.item.combat.SpiritShield;
import etithespirit.etimod.item.tools.SpiritOmniTool;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
	
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EtiMod.MODID);
	
	public static final RegistryObject<Item> DECAY_MYCELIUM = ITEMS.register("decay_mycelium", () -> new BlockItem(BlockRegistry.DECAY_MYCELIUM.get(), new Item.Properties()));
	public static final RegistryObject<Item> DECAY_LOG = ITEMS.register("decay_log", () -> new BlockItem(BlockRegistry.DECAY_LOG.get(), new Item.Properties()));
	public static final RegistryObject<Item> DECAY_STRIPPED_LOG = ITEMS.register("decay_stripped_log", () -> new BlockItem(BlockRegistry.DECAY_STRIPPED_LOG.get(), new Item.Properties()));
	public static final RegistryObject<Item> DECAY_SURFACE_MYCELIUM = ITEMS.register("decay_surface_mycelium", () -> new BlockItem(BlockRegistry.DECAY_SURFACE_MYCELIUM.get(), new Item.Properties()));

	public static final RegistryObject<Item> LIGHT_TOOL = ITEMS.register("light_omnitool", () -> new SpiritOmniTool
		(
			2f, 
			-1.6f, 
			SpiritItemTier.LIGHT, 
			new Item.Properties().rarity(Rarity.RARE),
			() -> { return 10; },
			() -> { return 15; }
		)
	);
	public static final RegistryObject<Item> LIGHT_SHIELD = ITEMS.register("light_shield", () -> new SpiritShield
		(
			new Item.Properties()
			.rarity(Rarity.RARE)
			.setISTER(() -> {
				return SpiritShield.SpiritShieldRenderer::new;
			})
		)
	);
	
	public static void registerAll() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
