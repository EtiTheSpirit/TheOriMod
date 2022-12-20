package etithespirit.orimod.registry.gameplay;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.potion.DecayEffect;
import etithespirit.orimod.common.potion.RadiantEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers my potions to the game. You don't know what you ask for traveler, my strongest potions could kill a dragon, let alone a man.
 *
 * @author Eti
 */
public final class PotionRegistry {
	
	private static final DeferredRegister<MobEffect> POTIONS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, OriMod.MODID);
	
	private static final Map<Class<? extends MobEffect>, RegistryObject<MobEffect>> REGISTRY_BINDINGS = new HashMap<>();
	
	public static MobEffect get(Class<? extends MobEffect> clazz) {
		return REGISTRY_BINDINGS.get(clazz).get();
	}
	
	/**
	 * Returns the ID of the given custom potion, or null if it is not a custom potion as defined here.
	 * @param clazz The class of the potion.
	 * @return The ID of the potion in the registry, or null if it is not from this registry.
	 */
	public static ResourceLocation getId(Class<? extends MobEffect> clazz) {
		return REGISTRY_BINDINGS.get(clazz).getId();
	}
	
	/**
	 * Returns the actual effect associated with the given ID.
	 * @param rsrc The ID
	 * @return The actual effect, or null if the ID is not from this registry.
	 */
	public static MobEffect get(ResourceLocation rsrc) {
		for (RegistryObject<MobEffect> fxo : REGISTRY_BINDINGS.values()) {
			if (fxo.getId().equals(rsrc)) return fxo.get();
		}
		return null;
	}
	
	private static void instantiateAll() {
		// REGISTRY_BINDINGS.put(SpiritEffect.class, POTIONS.register("spirit", SpiritEffect::new));
		REGISTRY_BINDINGS.put(DecayEffect.class, POTIONS.register("decay", DecayEffect::new));
		REGISTRY_BINDINGS.put(RadiantEffect.class, POTIONS.register("radiant", RadiantEffect::new));
	}
	
	/** */
	public static void registerAll() {
		instantiateAll();
		POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
