package etithespirit.mixin;

import etithespirit.etimod.info.spirit.SpiritData;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import etithespirit.etimod.client.player.spiritbehavior.SpiritSize;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Modifies the bounding box checked for entities to determine which pose they can enter.
 *
 * This is closely coupled with SpiritSize, and fixes a number of issues, most notably,
 * it fixes sneak speed not applying when under 1.5 block tall spaces (among other oddball quirks)
 *
 * @author Eti
 */
@Mixin(Entity.class)
@SuppressWarnings("unused")
public abstract class RedirectForSpiritPose extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> {

	protected RedirectForSpiritPose(Class<Entity> baseClass) {
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
		if (SpiritData.isSpirit((PlayerEntity)self)) {
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
