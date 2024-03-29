package etithespirit.orimod.spirit;


import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import etithespirit.orimod.client.audio.SpiritSoundProvider;
import etithespirit.orimod.client.audio.VanillaSoundIdentifier;
import static etithespirit.orimod.client.audio.VanillaSoundIdentifier.CustomSoundType;
import etithespirit.orimod.client.audio.variation.BreathLevel;
import etithespirit.orimod.event.EntityEmittedSoundEvent;
import etithespirit.orimod.event.EntityEmittedSoundEventProvider;
import etithespirit.orimod.registry.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import etithespirit.orimod.client.audio.VanillaSoundIdentifier.CustomSoundType;
import etithespirit.orimod.client.audio.variation.BreathLevel;
import ru.dbotthepony.mc.otm.OverdriveThatMatters;

/**
 * Handles when certain sounds should play.
 * @author Eti
 *
 */

public final class SpiritSounds {
	
	/**
	 * The air the local player had on the last tick.
	 */
	private static int airOnLastTick = 300;
	
	/**
	 * Player air replenishes slowly as they are above water. To prevent unwanted breath sound events, this is used to keep track of if they just emerged or not.
	 */
	private static boolean hasGoneUnderAgain = false;
	
	/**
	 * The amount of air the player had just before they submerged.
	 */
	private static int airOnLatestSubmerge = 300;
	
	
	/**
	 * A tick based update method that calculates when to play air sounds.
	 * @param evt The tick event.
	 */
	public static void performAirSounds(TickEvent.PlayerTickEvent evt) {
		if (evt.player == null) return;
		if (evt.phase == TickEvent.Phase.END) return;
		if (!SpiritIdentifier.isSpirit(evt.player)) return;
		if (!evt.player.getCommandSenderWorld().isClientSide) return; // Never run on the server, even in sp
		if (evt.player != Minecraft.getInstance().player) return; // Ignore other players in the world.
		if (evt.player.canBreatheUnderwater()) {
			airOnLastTick = 300;
			airOnLatestSubmerge = 300;
			hasGoneUnderAgain = false;
			return;
		}
		int prev = airOnLastTick;
		int current = evt.player.getAirSupply();
		int max = evt.player.getMaxAirSupply();
		float bubbleInc = max / 10f;
		
		int bubbles = (int)Math.ceil(current / bubbleInc);
		int prevBubbles = (int)Math.ceil(prev / bubbleInc);
		
		if (bubbles > prevBubbles) {
			// Air is being restored.
			if (hasGoneUnderAgain) {
				// They just got done draining air.
				hasGoneUnderAgain = false;
				// Okay, how much air did they actually lose?
				// The air on their latest submerge will be higher because this occurs when they have been underwater and just got back up -- their air only could have decreased.
				int netAirLoss = airOnLatestSubmerge - current;
				if (netAirLoss < bubbleInc) return;
				
				// Okay, so how much air do they have?
				SoundEvent targetSound;
				if (bubbles <= 4) {
					// 2.5 bubbles or less: big
					targetSound = SpiritSoundProvider.getSpiritBreathSound(BreathLevel.BIG);
				} else if (bubbles <= 7) {
					// 5.5 bubbles or less: medium
					targetSound = SpiritSoundProvider.getSpiritBreathSound(BreathLevel.MEDIUM);
				} else {
					// more than 5.5 bubbles: small
					targetSound = SpiritSoundProvider.getSpiritBreathSound(BreathLevel.LITTLE);
				}
				SpiritSoundPlayer.playSoundAtPlayer(evt.player, targetSound, SoundSource.PLAYERS, 0.35f);
			}
		} else if (bubbles < prevBubbles) {
			// Air is being drained.
			if (!hasGoneUnderAgain) {
				airOnLatestSubmerge = prev;
			}
			hasGoneUnderAgain = true;
		}
		airOnLastTick = current;
	}
	
	/**
	 * Implements a crude version of <a href="https://github.com/MinecraftForge/MinecraftForge/pull/7491">Forge PR #7941</a>
	 * @param event The custom mixin-provided event.
	 */
	public static void onSoundPlayedMixin(EntityEmittedSoundEvent event) {
		if (event.getSound() == null) return;
		// Skip if there's nothing.
		
		Entity entity = event.getEntity();
		if (entity instanceof Player player) {
			// Only players will need this sound override.
			ResourceLocation rsrc = event.getSound().getLocation();
			
			/*
			if (rsrc.getNamespace().equals(OverdriveThatMatters.MOD_ID) && rsrc.getPath().equals("android.shockwave")) {
				event.setSound(SoundRegistry.get("entity.spirit.stomp"));
				return;
			}
			*/
			
			if (!rsrc.getNamespace().equals("minecraft")) return;
			// The replaced sounds are only from MC, so don't bother testing if it's a mod source.
			// Also prevents a stack overflow.
			
			if (SpiritIdentifier.isSpirit(player)) {
				// Lookup to the HashMap<UUID, Boolean> to determine if this player is using the model.
				String path = rsrc.getPath();
				CustomSoundType type = VanillaSoundIdentifier.getTypeOf(path);
				// Based on a binding from sounds.json entries (vanilla) to an enum describing the
				// overarching type of that sound, e.g. `block.BLOCKNAME.step` entries are all classified
				// as CustomSoundType.STEP
				
				// Early stops:
				if (type == CustomSoundType.NO_OVERWRITE) return; // We don't want to overwrite this sound, so just abort here.
				if (type == CustomSoundType.OMIT) {
					// We want to omit this sound, so cancel the event and abort here since there's nothing else to do.
					event.setCanceled(true);
					return;
				}
				
				SoundEvent sound = event.getSound();
				
				// Specialized handling:
				// GetSpirit######Sound does a lookup to my sound registry based on the context of the sound.
				if (type == CustomSoundType.STEP) {
					sound = SpiritSoundProvider.getSpiritStepSound(player, sound);
					event.setVolume(0.25f);
					event.setPitch(SpiritSoundPlayer.getRandomPitch());
					
				} else if (type == CustomSoundType.FALL) {
					// Uses fall event handler in JumpBehaviors.
					event.setCanceled(true);
					return;
				} else if (type == CustomSoundType.HURT) {
					// Uses hurt event handler.
					event.setCanceled(true);
					return;
				} else if (type == CustomSoundType.DEATH) {
					// Uses death event handler.
					event.setCanceled(true);
					return;
				} else if (type == CustomSoundType.SWIM) {
					sound = SpiritSoundProvider.getSpiritSwimSound(player.isEyeInFluid(FluidTags.WATER));
					event.setVolume(0.3f);
					event.setPitch(SpiritSoundPlayer.getRandomPitch());
					
				/*
				} else if (type == CustomSoundType.SPLASH) {
					sound = SpiritSoundProvider.getSpiritSplashSound(false);
					event.setVolume(0.3f);
					
				} else if (type == CustomSoundType.SPLASH_BIG) {
					sound = SpiritSoundProvider.getSpiritSplashSound(true);
					event.setVolume(0.3f);
				}
				*/
				}
				
				if (sound == null) {
					// No sound was bound (this should never happen though), so just play nothing in this case.
					event.setCanceled(true);
					return;
				}
				
				event.setSound(sound);
			}
		}
	}
	
	public static void onEntityHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player player) {
			if (!player.getCommandSenderWorld().isClientSide && SpiritIdentifier.isSpirit(player)) {
				if (entity.getHealth() > 0 && entity.getHealth() - event.getAmount() > 0 && event.getAmount() > 0.05) {
					SpiritSoundPlayer.playHurtSound(player, event.getSource());
				}
			}
		}
	}
	
	public static void onEntityDied(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player player) {
			if (!player.getCommandSenderWorld().isClientSide && SpiritIdentifier.isSpirit(player)) {
				SpiritSoundPlayer.playDeathSound(player, event.getSource());
			}
		}
	}
	
	// Jump and fall handling performed in JumpBehaviors because they modify the amount of jumps stored.
}
