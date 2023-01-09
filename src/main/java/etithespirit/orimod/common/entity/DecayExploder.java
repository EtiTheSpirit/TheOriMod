package etithespirit.orimod.common.entity;

import com.google.common.collect.ImmutableList;
import etithespirit.orimod.combat.damage.OriModDamageSources;
import etithespirit.orimod.common.tags.OriModEntityTags;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.world.CustomizableExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

import java.util.Optional;

public class DecayExploder extends Mob {
	
	public static final int TIME_TO_BLOW = 15;
	public static final float MAX_DETECTION_RANGE = 3;
	public static final float MAX_DETECTION_RANGE_SQR = MAX_DETECTION_RANGE * MAX_DETECTION_RANGE;
	private static final Iterable<ItemStack> ARMOR = ImmutableList.of(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
	private static final Iterable<ItemStack> HANDS = ImmutableList.of(ItemStack.EMPTY, ItemStack.EMPTY);
	private static final TargetingConditions TARGET_CONDITIONS = TargetingConditions.forCombat().range(3);
	
	private boolean isTripped = false;
	public float trackedAnimatedHeight = 0;
	public float startedAnimationAt = 0;
	public float lastAnimatedAt = 0;
	
	public DecayExploder(EntityType<? extends Mob> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		noPhysics = true;
	}
	
	@Override
	public Vec3 getDeltaMovement() {
		return Vec3.ZERO;
	}
	
	@Override
	public void travel(Vec3 pTravelVector) { }
	
	@Override
	protected int calculateFallDamage(float pFallDistance, float pDamageMultiplier) {
		return 0;
	}
	
	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}
	
	@Override
	public boolean shouldDropExperience() {
		return false;
	}
	
	@Override
	protected boolean shouldDropLoot() {
		return false;
	}
	
	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return ARMOR;
	}
	
	@Override
	public Iterable<ItemStack> getHandSlots() {
		return HANDS;
	}
	
	@Override
	public ItemStack getItemBySlot(EquipmentSlot pSlot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) { }
	
	
	@Override
	protected boolean shouldDespawnInPeaceful() {
		return true;
	}
	
	/**
	 * Returns true if the given entity would cause this to trigger and explode.
	 * @param livingEntity The entity to check.
	 * @return True if the given entity should set off the exploder, false if not.
	 */
	public boolean isTargetForThis(LivingEntity livingEntity) {
		if (livingEntity == this) return false;
		if (!EntitySelector.NO_SPECTATORS.test(livingEntity)) return false;
		if (livingEntity.getType().is(OriModEntityTags.ALIGNED_DECAY)) return false;
		if (livingEntity.distanceToSqr(this) > MAX_DETECTION_RANGE_SQR) return false;
		return TARGET_CONDITIONS.test(this, livingEntity);
	}
	
	/**
	 * @return True if there is a valid target nearby that should trigger this exploder.
	 */
	private boolean isValidTargetNearby() {
		return !level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(getPosition(0).add(0, 2, 0), 4, 4, 4), this::isTargetForThis).isEmpty();
	}
	
	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		boolean result = super.hurt(pSource, pAmount);
		if (pSource != DamageSource.OUT_OF_WORLD && !isAlive()) {
			isTripped = true;
		}
		return result;
	}
	
	/**
	 * Removes this entity, making it so that it does not explode.
	 */
	@Override
	public void kill() {
		super.kill();
		isTripped = false;
	}
	
	/**
	 * Identical to {@link #kill()} but this <em>does</em> cause the explosion.
	 */
	public void tripAndKill() {
		hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
		isTripped = true; // This explicitly sets tripped because hurt specifically does *NOT* set it if the source is void damage.
	}
	
	@Override
	protected void playHurtSound(DamageSource pSource) { }
	
	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.get("entity.decay_exploder.alert");
	}
	
	@Override
	protected float getSoundVolume() {
		return 0.3f;
	}
	
	private void playExplosionSounds(CustomizableExplosion explosion) {
		// this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
		level.playSound(null, explosion.x, explosion.y, explosion.z, SoundRegistry.get("entity.decay_exploder.detonate_overtone"), SoundSource.BLOCKS, 4, 1f + ((explosion.level.random.nextFloat() - 0.5f) / 10f));
		level.playSound(null, explosion.x, explosion.y, explosion.z, SoundRegistry.get("entity.decay_exploder.detonate_undertone"), SoundSource.BLOCKS, 4, 1f + ((explosion.level.random.nextFloat() - 0.5f) / 10f));
	}
	
	private Explosion explode(@Nullable Entity pExploder, double pX, double pY, double pZ) {
		CustomizableExplosion explosion = new CustomizableExplosion(level, pExploder, OriModDamageSources.DECAY, pX, pY, pZ, 1, false, Explosion.BlockInteraction.NONE);
		explosion.damageMultiplier = 0.6;
		explosion.falloffRangeMultiplier = 2.5;
		if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(level, explosion)) return explosion;
		explosion.playSoundsFunction = this::playExplosionSounds;
		explosion.explode();
		explosion.finalizeExplosion(level.isClientSide);
		
		// Do not send to client.
		
		return explosion;
	}
	
	@Override
	protected void tickDeath() {
		deathTime++;
		if (!this.level.isClientSide()) {
			if (deathTime == TIME_TO_BLOW - 1) {
				if (isTripped) {
					// public Explosion explode(@Nullable Entity pExploder, @Nullable DamageSource pDamageSource, @Nullable ExplosionDamageCalculator pContext, double pX, double pY, double pZ, float pSize, boolean pCausesFire, Explosion.BlockInteraction pMode) {
					explode(this, getX(), getY() + 0.5, getZ());
				}
			} else if (deathTime == TIME_TO_BLOW) {
				level.broadcastEntityEvent(this, (byte)60);
				remove(Entity.RemovalReason.KILLED);
			}
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		if (this.isAlive() && !isTripped) {
			if (isValidTargetNearby()) {
				tripAndKill();
			}
		}
	}
}
