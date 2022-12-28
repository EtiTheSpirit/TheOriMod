package etithespirit.orimod.combat.projectile;

import etithespirit.orimod.combat.damage.OriModDamageSources;
import etithespirit.orimod.registry.SoundRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SpiritArrow extends AbstractArrow {
	public SpiritArrow(EntityType<? extends AbstractArrow> type, Level world) {
		super(type, world);
	}
	
	private int ticksAlive = 0;
	
	@Override
	protected ItemStack getPickupItem() {
		return ItemStack.EMPTY;
	}
	
	private void playSoundFromCharge() {
		this.playSound(this.isCritArrow() ? SoundRegistry.get("item.spirit_arc.impact.crit") : SoundRegistry.get("item.spirit_arc.impact.normal"), 1f, this.random.nextFloat() * 0.05F + 0.975F);
	}
	
	@Override
	protected void onHitBlock(BlockHitResult result) {
		BlockState blockstate = this.level.getBlockState(result.getBlockPos());
		blockstate.onProjectileHit(this.level, blockstate, result, this);
		Vec3 difference = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
		this.setDeltaMovement(difference);
		Vec3 tinyDifference = difference.normalize().scale((double)0.05F);
		this.setPosRaw(this.getX() - tinyDifference.x, this.getY() - tinyDifference.y, this.getZ() - tinyDifference.z);
		playSoundFromCharge();
		this.setCritArrow(false);
		this.setPierceLevel((byte)0);
		this.setShotFromCrossbow(false);
		// spawnPoofParticle(result.getDirection().getNormal());
		this.discard();
	}
	
	@Override
	public void tick() {
		super.tick();
		ticksAlive++;
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		Entity shooter = this.getOwner();
		Entity victim = result.getEntity();
		if (shooter.equals(victim)) {
			if (ticksAlive < 10) return;
		}
		
		DamageSource damage;
		double baseDamage = this.getBaseDamage();
		baseDamage *= 1 + this.random.nextDouble() / 7;
		
		if (shooter == null) {
			damage = OriModDamageSources.spiritArc(this, this);
		} else {
			damage = OriModDamageSources.spiritArc(this, shooter);
		}
		
		if (victim.hurt(damage, (float)baseDamage)) {
			playSoundFromCharge();
		} else {
			this.playSound(SoundRegistry.get("item.spirit_arc.impact.ricochet"), 0.6f, this.random.nextFloat() * 0.05F + 0.975F);
		}
		// spawnPoofParticle(new Vec3i(0, 1, 0));
		this.discard();
		
	}
}
