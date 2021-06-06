package etithespirit.etimod.registry;

import java.util.HashMap;
import java.util.Map;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.potion.DecayEffect;
import etithespirit.etimod.common.potion.RadiantEffect;
import etithespirit.etimod.common.potion.SpiritEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionRegistry {
	
	private static final DeferredRegister<Effect> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, EtiMod.MODID);
	
	private static final Map<Class<? extends Effect>, RegistryObject<Effect>> REGISTRY_BINDINGS = new HashMap<Class<? extends Effect>, RegistryObject<Effect>>();
	
	public static Effect get(Class<? extends Effect> clazz) {
		return REGISTRY_BINDINGS.get(clazz).get();
	}
	
	private static void instantiateAll() {
		REGISTRY_BINDINGS.put(SpiritEffect.class, POTIONS.register("spirit", SpiritEffect::new));
		REGISTRY_BINDINGS.put(DecayEffect.class, POTIONS.register("decay", DecayEffect::new));
		REGISTRY_BINDINGS.put(RadiantEffect.class, POTIONS.register("radiant", RadiantEffect::new));
	}
	
	public static void registerAll() {
		instantiateAll();
		POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
