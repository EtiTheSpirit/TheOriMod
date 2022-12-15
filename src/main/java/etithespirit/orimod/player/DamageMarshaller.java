package etithespirit.orimod.player;

import etithespirit.orimod.combat.ExtendedDamageSource;
import etithespirit.orimod.common.tags.OriModItemTags;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.core.NonNullList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * The DamageMarshaller class is responsible for controlling how Damage is applied to Spirits. They should be immune to all impact damage (falling, hitting walls).
 */
public class DamageMarshaller {
	
	/**
	 * Connected to Forge manually. This occurs when the spirit is attacked, and exists mostly for coverage.
	 * @param evt The event forge sends.
	 */
	public static void onEntityAttacked(LivingAttackEvent evt) {
		LivingEntity target = evt.getEntity();
		
		if (target instanceof Player) {
			DamageSource src = evt.getSource();
			if (src == DamageSource.FLY_INTO_WALL || src == DamageSource.FALL || src == DamageSource.IN_WALL) {
				if (SpiritIdentifier.isSpirit(target)) {
					evt.setCanceled(true);
				}
			}
		}
	}
	
	/**
	 * Connected to Forge manually. This occurs when the spirit is damaged.
	 * @param evt The event forge sends.
	 */
	public static void onEntityDamaged(LivingDamageEvent evt) {
		LivingEntity target = evt.getEntity();
		
		if (target instanceof Player) {
			DamageSource src = evt.getSource();
			if (src == DamageSource.FLY_INTO_WALL || src == DamageSource.FALL || src == DamageSource.IN_WALL) {
				if (SpiritIdentifier.isSpirit(target)) {
					evt.setCanceled(true);
				}
			} else if (src == ExtendedDamageSource.DECAY) {
				if (SpiritIdentifier.isSpirit(target)) {
					ItemStack[] armorSlots = getArmorSlots(target);
					if (armorSlots.length == 0) return;
					
					float hardlightPercentage = 0;
					float increment = 1f / armorSlots.length;
					
					for (int index = 0; index < armorSlots.length; index++) {
						if (armorSlots[index].is(OriModItemTags.HARDLIGHT_ARMOR)) {
							hardlightPercentage += increment;
						}
					}
					
					float invHardlightPercentage = 1f - hardlightPercentage;
					evt.setAmount(evt.getAmount() * (invHardlightPercentage * 0.5f));
				}
			}
		}
	}
	
	private static ItemStack[] getArmorSlots(LivingEntity ent) {
		Iterable<ItemStack> armorSlots = ent.getArmorSlots();
		if (armorSlots instanceof NonNullList<ItemStack> nnl) {
			return nnl.toArray(new ItemStack[0]);
		} else if (armorSlots instanceof List<ItemStack> list) {
			return list.toArray(new ItemStack[0]);
		}
		
		// Eugh.
		ArrayList<ItemStack> stacks = new ArrayList<>(4);
		for (ItemStack stack : armorSlots) {
			stacks.add(stack);
		}
		return stacks.toArray(new ItemStack[0]);
	}
}
