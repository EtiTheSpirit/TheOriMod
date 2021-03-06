package etithespirit.orimod.combat.projectile;

import com.google.common.collect.Lists;
import etithespirit.orimod.client.render.particle.LightSparkParticle;
import etithespirit.orimod.combat.ExtendedDamageSource;
import etithespirit.orimod.registry.SoundRegistry;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class SpiritArrow extends AbstractArrow {
	public SpiritArrow(EntityType<? extends AbstractArrow> type, Level world) {
		super(type, world);
	}
	
	@Override
	protected ItemStack getPickupItem() {
		return null;
	}
	
	private void playSoundFromCharge() {
		this.playSound(this.isCritArrow() ? SoundRegistry.get("item.spirit_arc.impact.crit") : SoundRegistry.get("item.spirit_arc.impact.normal"), 1f, this.random.nextFloat() * 0.05F + 0.975F);
	}
	
	private void spawnPoofParticle(Vec3i normal) {
		if (this.level instanceof ClientLevel clientLevel) {
			for (int idx = 0; idx < 15; idx++) {
				Minecraft.getInstance().particleEngine.add(new LightSparkParticle.SpiritArcArrowImpactParticle(clientLevel, this.position(), new Vec3(normal.getX(), normal.getY(), normal.getZ())));
			}
		}
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
		spawnPoofParticle(result.getDirection().getNormal());
		this.discard();
	}
	
	@Override
	protected void onHitEntity(EntityHitResult result) {
		
		Entity shooter = this.getOwner();
		Entity victim = result.getEntity();
		
		DamageSource damage;
		double baseDamage = this.getBaseDamage();
		baseDamage *= 1 + this.random.nextDouble() / 7;
		
		if (shooter == null) {
			damage = ExtendedDamageSource.spiritArc(this, this);
		} else {
			damage = ExtendedDamageSource.spiritArc(this, shooter);
		}
		
		if (victim.hurt(damage, (float)baseDamage)) {
			playSoundFromCharge();
		} else {
			this.playSound(SoundRegistry.get("item.spirit_arc.impact.ricochet"), 0.6f, this.random.nextFloat() * 0.05F + 0.975F);
		}
		spawnPoofParticle(new Vec3i(0, 1, 0));
		this.discard();
		
	}
}
