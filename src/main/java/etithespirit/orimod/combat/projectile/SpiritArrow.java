package etithespirit.orimod.combat.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpiritArrow extends AbstractArrow {
	public SpiritArrow(EntityType<? extends AbstractArrow> type, Level world) {
		super(type, world);
	}
	
	@Override
	protected ItemStack getPickupItem() {
		return null;
	}
}
