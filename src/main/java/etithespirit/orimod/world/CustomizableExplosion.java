package etithespirit.orimod.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import etithespirit.orimod.OriMod;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import javax.sound.sampled.Clip;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomizableExplosion extends Explosion {
	private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
	private static final int MAX_DROPS_PER_COMBINED_STACK = 16;
	public final boolean causeFire;
	public final Explosion.BlockInteraction blockInteraction;
	public final RandomSource random = RandomSource.create();
	public final Level level;
	public final double x;
	public final double y;
	public final double z;
	@Nullable private final Entity source;
	public final float radius;
	public final DamageSource damageSource;
	public final ExplosionDamageCalculator damageCalculator;
	private final ObjectArrayList<BlockPos> toBlow = new ObjectArrayList<>();
	private final Map<Player, Vec3> hitPlayers = Maps.newHashMap();
	public final Vec3 position;
	public double damageMultiplier = 1;
	public double falloffRangeMultiplier = 1;
	public Consumer<CustomizableExplosion> playSoundsFunction = explosion -> {
		explosion.level.playLocalSound(explosion.x, explosion.y, explosion.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (explosion.level.random.nextFloat() - explosion.level.random.nextFloat()) * 0.2F) * 0.7F, false);
	};
	public Supplier<SimpleParticleType> getSmallParticleType = () -> ParticleTypes.EXPLOSION_EMITTER;
	public Supplier<SimpleParticleType> getBigParticleType = () -> ParticleTypes.EXPLOSION;
	
	private ExplosionDamageCalculator makeDamageCalculator(Entity entity) {
		return entity == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(entity);
	}
	
	public CustomizableExplosion(Level inWorld, @Nullable Entity explodingEntity, @Nullable DamageSource damageType, double x, double y, double z, float blastRadius, boolean causeFire) {
		super(inWorld, explodingEntity, damageType, null, x, y, z, blastRadius, causeFire, BlockInteraction.NONE);
		this.level = inWorld;
		this.source = explodingEntity;
		this.radius = blastRadius;
		this.x = x;
		this.y = y;
		this.z = z;
		this.causeFire = causeFire;
		this.blockInteraction = BlockInteraction.NONE;
		this.damageSource = damageType == null ? DamageSource.explosion(this) : damageType;
		this.damageCalculator = this.makeDamageCalculator(explodingEntity);
		this.position = new Vec3(this.x, this.y, this.z);
	}
	
	/**
	 * @deprecated The block interaction is ignored due to networking incompatibility.
	 */
	@Deprecated
	public CustomizableExplosion(Level inWorld, @Nullable Entity explodingEntity, @Nullable DamageSource damageType, double x, double y, double z, float blastRadius, boolean causeFire, BlockInteraction blockInteraction) {
		this(inWorld, explodingEntity, damageType, x, y, z, blastRadius, causeFire);
	}
	
	private static List<Vec3> cornersOf(AABB bounds) {
		Vec3 center = bounds.getCenter();
		double x = bounds.getXsize() / 2;
		double y = bounds.getYsize() / 2;
		double z = bounds.getZsize() / 2;
		return List.of(
			center.add(-x, -y, -z),
			center.add(-x, -y, z),
			center.add(-x, y, -z),
			center.add(-x, y, z),
			center.add(x, -y, -z),
			center.add(x, -y, z),
			center.add(x, y, -z),
			center.add(x, y, z)
		);
	}
	
	private static boolean noObstructionsBetween(Entity entity, Vec3 origin, Vec3 target) {
		return entity.level.clip(new ClipContext(
			origin,
			target,
			ClipContext.Block.COLLIDER,
			ClipContext.Fluid.NONE,
			entity
		)).getType() == HitResult.Type.MISS;
	}
	
	public static float getSeenPercent(Vec3 explosionAt, Entity entity) {
		List<Vec3> cornersOfEntity = cornersOf(entity.getBoundingBox().inflate(-0.05));
		float clear = 0;
		for (Vec3 corner : cornersOfEntity) {
			if (noObstructionsBetween(entity, explosionAt, corner)) {
				clear++;
			}
		}
		if (noObstructionsBetween(entity, explosionAt, entity.getBoundingBox().getCenter())) {
			clear++;
		}
		return clear / 9f;
	}
	
	
	/**
	 * Does the first part of the explosion (destroy blocks)
	 */
	public void explode() {
		this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
		Set<BlockPos> set = Sets.newHashSet();
		int i = 16;
		
		for(int x = 0; x < 16; ++x) {
			for(int y = 0; y < 16; ++y) {
				for(int z = 0; z < 16; ++z) {
					if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15) {
						double d0 = ((float)x / 15.0F * 2.0F - 1.0F);
						double d1 = ((float)y / 15.0F * 2.0F - 1.0F);
						double d2 = ((float)z / 15.0F * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 /= d3;
						d1 /= d3;
						d2 /= d3;
						float randomRadius = radius * (0.7F + level.random.nextFloat() * 0.6F);
						double currentX = this.x;
						double currentY = this.y;
						double currentZ = this.z;
						
						for (float f1 = 0.3F; randomRadius > 0.0F; randomRadius -= 0.225F) {
							BlockPos blockpos = new BlockPos(currentX, currentY, currentZ);
							BlockState blockstate = level.getBlockState(blockpos);
							FluidState fluidstate = level.getFluidState(blockpos);
							if (!level.isInWorldBounds(blockpos)) {
								break;
							}
							
							Optional<Float> optional = damageCalculator.getBlockExplosionResistance(this, level, blockpos, blockstate, fluidstate);
							if (optional.isPresent()) {
								randomRadius -= (optional.get() + 0.3F) * 0.3F;
							}
							
							if (randomRadius > 0.0F && damageCalculator.shouldBlockExplode(this, level, blockpos, blockstate, randomRadius)) {
								set.add(blockpos);
							}
							
							currentX += d0 * 0.3;
							currentY += d1 * 0.3;
							currentZ += d2 * 0.3;
						}
					}
				}
			}
		}
		
		toBlow.addAll(set);
		float diameter = radius * 2f * (float)falloffRangeMultiplier;
		List<Entity> entitiesInRange = level.getEntities(
			source,
			new AABB(
				Mth.floor(x - (double)diameter - 1),
				Mth.floor(y - (double)diameter - 1),
				Mth.floor(z - (double)diameter - 1),
				Mth.floor(x + (double)diameter + 1),
				Mth.floor(y + (double)diameter + 1),
				Mth.floor(z + (double)diameter + 1)
			)
		);
		net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(level, this, entitiesInRange, diameter);
		
		for (Entity entity : entitiesInRange) {
			if (!entity.ignoreExplosion()) {
				double distancePercent = Math.sqrt(entity.distanceToSqr(position)) / (double)diameter;
				if (distancePercent <= 1.0D) {
					double deltaX = entity.getX() - this.x;
					double deltaY = entity.getY() - this.y;
					double deltaZ = entity.getZ() - this.z;
					double distanceToEntity = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
					if (distanceToEntity != 0) {
						deltaX /= distanceToEntity;
						deltaY /= distanceToEntity;
						deltaZ /= distanceToEntity;
						double seenPercent = CustomizableExplosion.getSeenPercent(position, entity);
						double damageByDistanceAndSeen = (1.0D - distancePercent) * seenPercent;
						double resultDamage = damageByDistanceAndSeen * damageByDistanceAndSeen + damageByDistanceAndSeen / 2 * 7 * diameter + 1;
						
						entity.hurt(
							getDamageSource(),
							(float) (resultDamage * damageMultiplier)
						);
						double knockbackForce = damageByDistanceAndSeen;
						if (entity instanceof LivingEntity) {
							knockbackForce = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) entity, damageByDistanceAndSeen);
						}
						
						entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * knockbackForce, deltaY * knockbackForce, deltaZ * knockbackForce));
						if (entity instanceof Player player) {
							if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
								this.hitPlayers.put(player, new Vec3(deltaX * damageByDistanceAndSeen, deltaY * damageByDistanceAndSeen, deltaZ * damageByDistanceAndSeen));
							}
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Does the second part of the explosion (sound, particles, drop spawn)
	 */
	public void finalizeExplosion(boolean pSpawnParticles) {
		playSoundsFunction.accept(this);
		
		boolean flag = this.blockInteraction != Explosion.BlockInteraction.NONE;
		if (pSpawnParticles) {
			if (!(this.radius < 2.0F) && flag) {
				this.level.addParticle(getSmallParticleType.get(), this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
			} else {
				this.level.addParticle(getBigParticleType.get(), this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
			}
		}
		
		if (flag) {
			ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
			boolean flag1 = this.getSourceMob() instanceof Player;
			Util.shuffle(this.toBlow, this.level.random);
			
			for(BlockPos blockpos : this.toBlow) {
				BlockState blockstate = this.level.getBlockState(blockpos);
				Block block = blockstate.getBlock();
				if (!blockstate.isAir()) {
					BlockPos blockpos1 = blockpos.immutable();
					this.level.getProfiler().push("explosion_blocks");
					if (blockstate.canDropFromExplosion(this.level, blockpos, this)) {
						Level $$9 = this.level;
						if ($$9 instanceof ServerLevel) {
							ServerLevel serverlevel = (ServerLevel)$$9;
							BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
							LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverlevel)).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
							if (this.blockInteraction == Explosion.BlockInteraction.DESTROY) {
								lootcontext$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
							}
							
							blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, flag1);
							blockstate.getDrops(lootcontext$builder).forEach((p_46074_) -> {
								addBlockDrops(objectarraylist, p_46074_, blockpos1);
							});
						}
					}
					
					blockstate.onBlockExploded(this.level, blockpos, this);
					this.level.getProfiler().pop();
				}
			}
			
			for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
				Block.popResource(this.level, pair.getSecond(), pair.getFirst());
			}
		}
		
		if (this.causeFire) {
			for(BlockPos blockpos2 : this.toBlow) {
				if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
					this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
				}
			}
		}
		
	}
	
	private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> pDropPositionArray, ItemStack pStack, BlockPos pPos) {
		int i = pDropPositionArray.size();
		
		for(int j = 0; j < i; ++j) {
			Pair<ItemStack, BlockPos> pair = pDropPositionArray.get(j);
			ItemStack itemstack = pair.getFirst();
			if (ItemEntity.areMergable(itemstack, pStack)) {
				ItemStack itemstack1 = ItemEntity.merge(itemstack, pStack, MAX_DROPS_PER_COMBINED_STACK);
				pDropPositionArray.set(j, Pair.of(itemstack1, pair.getSecond()));
				if (pStack.isEmpty()) {
					return;
				}
			}
		}
		
		pDropPositionArray.add(Pair.of(pStack, pPos));
	}
	
	public DamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public Map<Player, Vec3> getHitPlayers() {
		return this.hitPlayers;
	}
	
	/**
	 * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
	 */
	@Nullable
	public LivingEntity getSourceMob() {
		if (this.source == null) {
			return null;
		} else if (this.source instanceof PrimedTnt) {
			return ((PrimedTnt)this.source).getOwner();
		} else if (this.source instanceof LivingEntity) {
			return (LivingEntity)this.source;
		} else {
			if (this.source instanceof Projectile) {
				Entity entity = ((Projectile)this.source).getOwner();
				if (entity instanceof LivingEntity) {
					return (LivingEntity)entity;
				}
			}
			
			return null;
		}
	}
	
	public void clearToBlow() {
		this.toBlow.clear();
	}
	
	public List<BlockPos> getToBlow() {
		return this.toBlow;
	}
	
	public Vec3 getPosition() {
		return this.position;
	}
	
	@Nullable
	public Entity getExploder() {
		return this.source;
	}
}
