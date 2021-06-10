package etithespirit.etimod.registry;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.mob.SpiritEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityRegistry {
	
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, EtiMod.MODID);
	
	public static final ResourceLocation SPIRIT_ID = new ResourceLocation(EtiMod.MODID, "spirit");
	public static final RegistryObject<EntityType<SpiritEntity>> SPIRIT = ENTITIES.register(SPIRIT_ID.getPath(), () -> { 
		return EntityType.Builder
			.of(SpiritEntity::new, EntityClassification.CREATURE)
			.sized(0.475f, 0.5f)
			.noSave()
		.build(SPIRIT_ID.toString()); 
	});
	
	public static void registerAll() {
		ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
