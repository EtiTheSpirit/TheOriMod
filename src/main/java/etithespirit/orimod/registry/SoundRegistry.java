package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.HashMap;

public final class SoundRegistry {
	
	private static final DeferredRegister<SoundEvent> SOUND_REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OriMod.MODID);
	
	public static final HashMap<String, RegistryObject<SoundEvent>> SOUNDS = new HashMap<>();
	
	public static SoundEvent get(@Nonnull String soundEventName) {
		RegistryObject<SoundEvent> sound = SOUNDS.get(soundEventName);
		if (sound == null || !sound.isPresent()) {
			throw new NullPointerException(String.format("ALERT: Something attempted to get a spirit sound that didn't exist! Key: %s, RegistryObject<SoundEvent> exists: %s (if TRUE, then the RegistryObject<SoundEvent> was empty)", soundEventName, (sound != null)));
		}
		return SOUNDS.get(soundEventName).get();
	}
	
	public static void registerSound(String soundEventName) {
		SOUNDS.put(soundEventName, SOUND_REGISTRY.register(soundEventName, () -> new SoundEvent(new ResourceLocation(OriMod.MODID, soundEventName))));
	}
	
	public static void registerAll() {
		registerSound("entity.spirit.hurt");
		registerSound("entity.spirit.hurt.takedamage");
		registerSound("entity.spirit.hurt.vo");
		
		registerSound("entity.spirit.death.generic");
		registerSound("entity.spirit.death.burn");
		registerSound("entity.spirit.death.drown");
		registerSound("entity.spirit.death.decay");
		registerSound("entity.spirit.death.vo");
		
		registerSound("entity.spirit.jump.single");
		registerSound("entity.spirit.jump.double");
		registerSound("entity.spirit.jump.triple");
		registerSound("entity.spirit.jump.wall");
		
		registerSound("entity.spirit.dash");
		registerSound("entity.spirit.dash.impactwall");
		
		registerSound("entity.spirit.respawn");
		registerSound("entity.spirit.respawn2");
		
		registerSound("entity.spirit.aquatic.breath.big");
		registerSound("entity.spirit.aquatic.breath.medium");
		registerSound("entity.spirit.aquatic.breath.little");
		registerSound("entity.spirit.aquatic.swim");
		registerSound("entity.spirit.aquatic.swim_above");
		
		registerSound("entity.spirit.attack.nothing");
		registerSound("entity.spirit.attack.weak");
		registerSound("entity.spirit.attack.standard");
		registerSound("entity.spirit.attack.strong");
		registerSound("entity.spirit.attack.crit");
		registerSound("entity.spirit.attack.knockback");
		registerSound("entity.spirit.attack.sweep");
		
		registerSound("entity.spirit.step.ash");
		registerSound("entity.spirit.step.bone");
		registerSound("entity.spirit.step.ceramic.solid");
		registerSound("entity.spirit.step.ceramic.broken");
		registerSound("entity.spirit.step.chitin");
		registerSound("entity.spirit.step.glass");
		registerSound("entity.spirit.step.grass.hard");
		registerSound("entity.spirit.step.grass.soft");
		registerSound("entity.spirit.step.gravel.dry");
		registerSound("entity.spirit.step.gravel.wet");
		registerSound("entity.spirit.step.gravel.snowy");
		registerSound("entity.spirit.step.ice");
		registerSound("entity.spirit.step.metal");
		registerSound("entity.spirit.step.organic"); // insect
		registerSound("entity.spirit.step.rock");
		registerSound("entity.spirit.step.sand");
		registerSound("entity.spirit.step.shroom");
		registerSound("entity.spirit.step.slimy");
		registerSound("entity.spirit.step.snow");
		registerSound("entity.spirit.step.wood.dry");
		registerSound("entity.spirit.step.wood.mossy");
		registerSound("entity.spirit.step.wood.snowy");
		registerSound("entity.spirit.step.wood.wet");
		registerSound("entity.spirit.step.water.shallow");
		registerSound("entity.spirit.step.water.deep");
		registerSound("entity.spirit.step.wool");
		
		registerSound("item.light_shield.impact");
		registerSound("item.light_shield.break");
		
		registerSound("entity.light_vessel.spawn");
		registerSound("entity.light_vessel.create");
		registerSound("entity.light_vessel.ambient");
		registerSound("entity.light_vessel.drop");
		registerSound("entity.light_vessel.scrape");
		
		registerSound("tile.light_tech.generic.activate");
		registerSound("tile.light_tech.generic.deactivate");
		registerSound("tile.light_tech.generic.active_loop");
		
		registerSound("tile.light_tech.ambientfield.activate");
		registerSound("tile.light_tech.ambientfield.deactivate");
		registerSound("tile.light_tech.ambientfield.loop_magic");
		
		registerSound("tile.light_tech.energize");
		registerSound("tile.light_tech.activate_large_a");
		registerSound("tile.light_tech.activate_large_b");
		
		registerSound("item.lumo_wand.swapconduitauto");
		
		registerSound("item.spirit_arc.start");
		registerSound("item.spirit_arc.charge");
		registerSound("item.spirit_arc.shot.base");
		registerSound("item.spirit_arc.shot.charge_overlay");
		registerSound("item.spirit_arc.impact.normal");
		registerSound("item.spirit_arc.impact.crit");
		registerSound("item.spirit_arc.impact.ricochet");
		
		registerSound("block.decay_poison.gurgle");
		
		registerSound("nullsound");
		
		// Legacy sounds
		/*
		registerSound("entity.spirit.fall.glass");
		registerSound("entity.spirit.fall.grass");
		registerSound("entity.spirit.fall.shroom");
		registerSound("entity.spirit.fall.stone");
		registerSound("entity.spirit.fall.shallow_water");
		registerSound("entity.spirit.fall.wood");
		
		registerSound("entity.spirit.step.glass");
		registerSound("entity.spirit.step.grass");
		registerSound("entity.spirit.step.sand");
		registerSound("entity.spirit.step.shroom");
		registerSound("entity.spirit.step.snow");
		registerSound("entity.spirit.step.stone");
		registerSound("entity.spirit.step.shallow_water");
		registerSound("entity.spirit.step.wood");
		registerSound("entity.spirit.step.wool");
		registerSound("entity.spirit.step.metal");
		registerSound("entity.spirit.step.netherite");
		registerSound("entity.spirit.step.ancient_debris");
		registerSound("entity.spirit.step.gravel");
		*/
		
		SOUND_REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
