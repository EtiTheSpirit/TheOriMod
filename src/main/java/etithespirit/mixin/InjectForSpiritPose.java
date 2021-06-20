package etithespirit.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import etithespirit.etimod.client.player.spiritbehavior.SpiritSize;
import etithespirit.etimod.info.spirit.SpiritIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

@Mixin(Entity.class)
public abstract class InjectForSpiritPose extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> {

	protected InjectForSpiritPose(Class<Entity> baseClass) {
		super(baseClass);
	}
	
	/** Shadow the original method as a fallback. */
	@Shadow
	public abstract AxisAlignedBB getBoundingBoxForPose(Pose pose);
	
	@Shadow
	public abstract double getX();
	
	@Shadow
	public abstract double getY();
	
	@Shadow
	public abstract double getZ();
	
	@Redirect(
		method = "canEnterPose(Lnet/minecraft/entity/Pose;)Z",
		at = @At(
			value="INVOKE",
			target="Lnet/minecraft/entity/Entity;getBoundingBoxForPose(Lnet/minecraft/entity/Pose;)Lnet/minecraft/util/math/AxisAlignedBB;"
		)
	)
	public AxisAlignedBB canEnterPose$getBoundingBoxForSpiritPose(Entity self, Pose pose) {
		if (SpiritIdentifier.isIDSpirit(self.getUUID())) {
			// We are a spirit. This calls for some special handling.
			EntitySize entitysize = SpiritSize.SPIRIT_SIZE_BY_POSE.get(pose);
			float f = entitysize.width / 2.0F;
			Vector3d vector3d = new Vector3d(getX() - (double)f, getY(), getZ() - (double)f);
			Vector3d vector3d1 = new Vector3d(getX() + (double)f, getY() + (double)entitysize.height, getZ() + (double)f);
			return new AxisAlignedBB(vector3d, vector3d1);
		} else {
			return getBoundingBoxForPose(pose);
		}
	}
	
}
