package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.client.gui.LightRepairDeviceScreen;
import etithespirit.orimod.client.render.hud.LightRepairDeviceMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MenuRegistry {
	
	private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, OriMod.MODID);
	
	public static final RegistryObject<MenuType<LightRepairDeviceMenu>> LIGHT_REPAIR_DEVICE = CONTAINERS.register("light_repair_menu", LightRepairDeviceMenu::createContainerType);
	
	public static void registerAll() {
		CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
		//MenuScreens.register(LIGHT_REPAIR_DEVICE.get(), LightRepairDeviceScreen::new);
	}
}
