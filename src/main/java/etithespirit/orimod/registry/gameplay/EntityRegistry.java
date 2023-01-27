package etithespirit.orimod.registry.gameplay;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.combat.projectile.SpiritArrow;
import etithespirit.orimod.common.entity.DecayExploder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EntityRegistry {
	
	public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, OriMod.MODID);
	
	public static RegistryObject<EntityType<SpiritArrow>> SPIRIT_ARROW = register(
		"spirit_arrow",
		builderOf(SpiritArrow::new, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(12).updateInterval(20)
	);
	
	public static RegistryObject<EntityType<DecayExploder>> DECAY_EXPLODER = register(
		"decay_exploder",
		builderOf(DecayExploder::new, MobCategory.MONSTER).sized(0.8f, 0.2f).clientTrackingRange(8).updateInterval(5).setShouldReceiveVelocityUpdates(false)
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
	
	
	public static void registerAttributes(EntityAttributeCreationEvent evt) {
		evt.put(
			DECAY_EXPLODER.get(),
			AttributeSupplier.builder()
				.add(Attributes.MAX_HEALTH, 0.05)
		        .add(Attributes.MOVEMENT_SPEED, 0)
		        .add(Attributes.KNOCKBACK_RESISTANCE, 1D)
				.add(Attributes.FOLLOW_RANGE, 0)
				.add(Attributes.ATTACK_KNOCKBACK, 0)
				.add(Attributes.ATTACK_DAMAGE, 10)
				.add(Attributes.ARMOR, 0)
				.add(Attributes.ARMOR_TOUGHNESS, 0)
				.add(Attributes.ATTACK_SPEED, 0)
				.add(Attributes.JUMP_STRENGTH, 0)
				.add(Attributes.LUCK, 0)
				.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0)
				.add(ForgeMod.ENTITY_GRAVITY.get(), 0)
				.add(ForgeMod.SWIM_SPEED.get(), 0)
				.add(ForgeMod.NAMETAG_DISTANCE.get(), 0)
	        .build()
		);
	}
	
}
