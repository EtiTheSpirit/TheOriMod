package etithespirit.mixin.mixins;

import etithespirit.orimod.spirit.SpiritIdentifier;
import etithespirit.orimod.spirit.SpiritSize;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin is responsible for managing the pose a Spirit player uses, since their qualifications for poses are different than that of a
 * default player due to the differences in height.
 */
@Mixin(Entity.class)
public abstract class RedirectForSpiritPose extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> {
	protected RedirectForSpiritPose(Class<Entity> baseClass) { super(baseClass); }
	
	/** Shadow the original method as a fallback. */
	@Shadow
	protected abstract AABB getBoundingBoxForPose(Pose pose);
	
	@Shadow
	public abstract double getX();
	
	@Shadow
	public abstract double getY();
	
	@Shadow
	public abstract double getZ();
	
	@Redirect (
		method = "canEnterPose(Lnet/minecraft/world/entity/Pose;)Z",
		at = @At (
			value="INVOKE",
			target="Lnet/minecraft/world/entity/Entity;getBoundingBoxForPose(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/phys/AABB;"
		)
	)
	public AABB orimod$canEnterPose$getBoundingBoxForSpiritPose(Entity self, Pose pose) {
		if (SpiritIdentifier.isSpirit(self)) {
			// We are a spirit. This calls for some special handling.
			EntityDimensions entitysize = SpiritSize.SPIRIT_SIZE_BY_POSE.get(pose);
			float f = entitysize.width / 2.0F;
			Vec3 vector3d = new Vec3(getX() - (double)f, getY(), getZ() - (double)f);
			Vec3 vector3d1 = new Vec3(getX() + (double)f, getY() + (double)entitysize.height, getZ() + (double)f);
			return new AABB(vector3d, vector3d1);
		} else {
			return getBoundingBoxForPose(pose);
		}
	}
}
