package etithespirit.etimod.common.mob;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import etithespirit.etimod.registry.SoundRegistry;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;

public class SpiritEntity extends AnimalEntity {
	
    public SpiritEntity(EntityType<? extends AnimalEntity> eType, final World world) {
        super(eType, world);
    }
    
    @Override
	protected float getJumpPower() {
	    return 0.84f;
	}
    
    @Override
    protected SoundEvent getDeathSound() {
    	return SoundRegistry.get("entity.spirit.death");
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource src) {
    	return SoundRegistry.get("entity.spirit.hurt");
    }
    
    @Override
    public boolean hurt(DamageSource src, float amount) {
    	if (src == DamageSource.FLY_INTO_WALL || src == DamageSource.FALL) return false;
    	return super.hurt(src, amount);
    }

	@Override
	public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		return null;
	}
	
}
