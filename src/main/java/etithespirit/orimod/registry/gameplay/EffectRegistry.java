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
public final class EffectRegistry {
	
	private static final Map<Class<? extends MobEffect>, RegistryObject<MobEffect>> REGISTRY_BINDINGS = new HashMap<>();
	private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, OriMod.MODID);
	
	public static final RegistryObject<MobEffect> DECAY = MOB_EFFECTS.register("decay", DecayEffect::new);
	public static final RegistryObject<MobEffect> RADIANT = MOB_EFFECTS.register("radiant", RadiantEffect::new);
	
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
	// TODO: Better solution to this?
	public static MobEffect fromId(ResourceLocation rsrc) {
		for (RegistryObject<MobEffect> fxo : REGISTRY_BINDINGS.values()) {
			if (fxo.getId().equals(rsrc)) return fxo.get();
		}
		return null;
	}
	
	static {
		REGISTRY_BINDINGS.put(DecayEffect.class, DECAY);
		REGISTRY_BINDINGS.put(RadiantEffect.class, RADIANT);
	}
	
	public static void registerAll() {
		MOB_EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
