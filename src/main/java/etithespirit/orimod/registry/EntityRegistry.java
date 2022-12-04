package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.combat.projectile.SpiritArrow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class EntityRegistry {
	
	public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OriMod.MODID);
	
	public static RegistryObject<EntityType<SpiritArrow>> SPIRIT_ARROW = register(
		"spirit_arrow",
		builderOf(SpiritArrow::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20)
	);
	
	private static <T extends Entity> EntityType.Builder<T> builderOf(EntityType.EntityFactory<T> factory, MobCategory category) {
		return EntityType.Builder.of(factory, category);
	}
	
	private static <T extends Entity> RegistryObject<EntityType<T>> register(String rsrc, EntityType.Builder<T> builder) {
		return ENTITIES.register(rsrc, () -> builder.build(rsrc));
	}
 
	public static void registerAll() {
		ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
