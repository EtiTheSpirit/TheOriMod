package etithespirit.etimod.info.spirit;

import java.util.UUID;

import etithespirit.etimod.common.mob.SpiritEntity;
import etithespirit.etimod.common.morph.PlayerToSpiritBinding;
import etithespirit.etimod.common.potion.SpiritEffect;
import etithespirit.etimod.registry.PotionRegistry;
import etithespirit.etimod.util.EtiUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SpiritIdentifier {
	
	/**
	 * Determines whether or not the given entity should classify as a Spirit Guardian.
	 * @param livingEntity The entity to test.
	 * @param identificationMethod See {@link etithespirit.etimod.info.spirit.SpiritIdentificationType} for more information on this value.
	 * @return
	 */
	public static boolean isSpirit(LivingEntity livingEntity, int identificationMethod) {
		if (EtiUtils.hasFlag(identificationMethod, SpiritIdentificationType.FROM_ENTITY)) {
			if (livingEntity instanceof SpiritEntity) {
				return true;
			}
		}
		if (EtiUtils.hasFlag(identificationMethod, SpiritIdentificationType.FROM_PLAYER_MODEL)) {
			if (livingEntity instanceof PlayerEntity) {
				UUID id = livingEntity.getUUID();
				return PlayerToSpiritBinding.get(id);
			}
		}
		if (EtiUtils.hasFlag(identificationMethod, SpiritIdentificationType.FROM_POTION_EFFECT)) {
			return livingEntity.getEffect(PotionRegistry.get(SpiritEffect.class)) != null;
		}
		return false;
	}
	
	/**
	 * A strict ID-Only test to see if the given UUID is registered as a spirit. This should only be used if isSpirit would be called with a PlayerEntity with identification type FROM_PLAYER_MODEL.
	 * @param id
	 * @return
	 */
	public static boolean isIDSpirit(UUID id) {
		return PlayerToSpiritBinding.get(id);
	}
	
}
