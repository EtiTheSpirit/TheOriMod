package etithespirit.etimod.registry;

import etithespirit.etimod.EtiMod;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityRegistry {

	private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EtiMod.MODID);
	
	public static RegistryObject<TileEntityType<?>> RF_CAPACITOR;
	public static RegistryObject<TileEntityType<?>> LIGHT_CAPACITOR;
	
	// TODO: Keep this? I plan on calling this AFTER I call registerAll() in my deferred block registry, that way I can safely access blocks.
	private static void lateRegister() {
		
	}
	
	public static void registerAll() {
		TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
