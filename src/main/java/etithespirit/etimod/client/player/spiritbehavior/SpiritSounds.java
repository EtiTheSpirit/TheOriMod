package etithespirit.etimod.client.player.spiritbehavior;

import etithespirit.etimod.client.audio.SpiritSoundPlayer;
import etithespirit.etimod.client.audio.SpiritSoundProvider;
import etithespirit.etimod.client.audio.VanillaSoundIdentifier;
import etithespirit.etimod.client.audio.VanillaSoundIdentifier.CustomSoundType;
import etithespirit.etimod.client.audio.variation.BreathLevel;
import etithespirit.etimod.event.EntityEmittedSoundEvent;
import etithespirit.etimod.event.EntityEmittedSoundEventProvider;
import etithespirit.etimod.util.spirit.SpiritIdentificationType;
import etithespirit.etimod.util.spirit.SpiritIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles when certain sounds should play.
 * @author Eti
 *
 */

public class SpiritSounds {
	
	static {
		EntityEmittedSoundEventProvider.registerHandler(evt -> onSoundPlayedMixin(evt));
	}

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
	 * @param evt
	 */
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void performAirSounds(PlayerTickEvent evt) {
		if (evt.player == null) return;
		if (evt.phase == TickEvent.Phase.END) return;
		if (!SpiritIdentifier.isSpirit(evt.player, SpiritIdentificationType.FROM_PLAYER_MODEL)) return;
		if (!evt.player.getEntityWorld().isRemote) return; // Never run on the server, even in sp
		if (evt.player != Minecraft.getInstance().player) return; // Ignore other players in the world.
		if (evt.player.canBreatheUnderwater()) {
			airOnLastTick = 300;
			airOnLatestSubmerge = 300;
			hasGoneUnderAgain = false;
			return;
		}
		int prev = airOnLastTick;
		int current = evt.player.getAir();
		int max = evt.player.getMaxAir();
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
				SoundEvent targetSound = null;
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
				SpiritSoundPlayer.playSoundAtPlayer(evt.player, targetSound, SoundCategory.PLAYERS, 0.175f);
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
	 * @param event
	 */
	private static void onSoundPlayedMixin(EntityEmittedSoundEvent event) {
		if (event.getSound() == null) return;
		// Skip if there's nothing.
		
		Entity entity = event.getEntity();
		if (entity instanceof PlayerEntity) {
			// Only players will need this sound override.
			PlayerEntity player = (PlayerEntity)entity;
			ResourceLocation rsrc = event.getSound().getRegistryName();
			
			if (rsrc.getNamespace() != "minecraft") return; 
			// The replaced sounds are only from MC, so don't bother testing if it's a mod source.
			// Also prevents a stack overflow.
			
			if (SpiritIdentifier.isSpirit(player, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
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
					sound = SpiritSoundProvider.getSpiritSwimSound(player.areEyesInFluid(FluidTags.WATER));
					event.setVolume(0.3f);
					event.setPitch(SpiritSoundPlayer.getRandomPitch());
					
				} else if (type == CustomSoundType.SPLASH) {
					sound = SpiritSoundProvider.getSpiritSplashSound(false);
					event.setVolume(0.3f);
					
				} else if (type == CustomSoundType.SPLASH_BIG) {
					sound = SpiritSoundProvider.getSpiritSplashSound(true);
					event.setVolume(0.3f);	
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
	
	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity instanceof PlayerEntity) {
			if (SpiritIdentifier.isSpirit(entity, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
				PlayerEntity player = (PlayerEntity)entity;
				if (entity.getHealth() > 0 && entity.getHealth() - event.getAmount() > 0 && event.getAmount() > 0.05) {
					SpiritSoundPlayer.playHurtSound(player, event.getSource());
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onEntityDied(LivingDeathEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)entity;
			if (SpiritIdentifier.isSpirit(entity, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
				SpiritSoundPlayer.playDeathSound(player, event.getSource());
			}
		}
	}
	
	
	
	// Jump and fall handling performed in JumpBehaviors because they modify the amount of jumps stored.
}
