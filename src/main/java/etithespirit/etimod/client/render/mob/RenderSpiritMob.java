package etithespirit.etimod.client.render.mob;

import com.mojang.blaze3d.matrix.MatrixStack;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.mob.SpiritEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;

public class RenderSpiritMob extends LivingRenderer<SpiritEntity, ModelSpirit> implements IEntityRenderer<SpiritEntity, ModelSpirit> {
   	public static final ResourceLocation SPIRIT_TEXTURE = new ResourceLocation(EtiMod.MODID, "textures/entity/spirit.png");
   	
   	public RenderSpiritMob(EntityRendererManager rendererManager) {
   		this(rendererManager, new ModelSpirit(), 0.4f);
   	}
   	
   	public RenderSpiritMob(EntityRendererManager rendererManager, ModelSpirit entityModelIn, float shadowSizeIn) {
		super(rendererManager, entityModelIn, shadowSizeIn);
	}
   	
	@Override
	public ResourceLocation getTextureLocation(SpiritEntity entity) {
		return SPIRIT_TEXTURE;
	}
	
	protected void scale(SpiritEntity spirit, MatrixStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(ModelSpirit.WIDTH_MOD, ModelSpirit.HEIGHT_MOD, ModelSpirit.WIDTH_MOD);
	}
	
	@Override
	public void render(SpiritEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
		scale(p_225623_1_, p_225623_4_, 0);
		super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
	}
	
	public static class RenderFactory implements IRenderFactory<SpiritEntity> {

		@Override
		public EntityRenderer<? super SpiritEntity> createRenderFor(EntityRendererManager manager) {
			return new RenderSpiritMob(manager);
		}
		
	}
	
	private static float getFacingAngle(Direction facingIn) {
	      switch(facingIn) {
	      case SOUTH:
	         return 90.0F;
	      case WEST:
	         return 0.0F;
	      case NORTH:
	         return 270.0F;
	      case EAST:
	         return 180.0F;
	      default:
	         return 0.0F;
	      }
	   }
	
	protected void superApplyRotations(LivingEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
	      Pose pose = entityLiving.getPose();
	      if (pose != Pose.SLEEPING) {
	         // matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
	      }

	      if (entityLiving.deathTime > 0) {
	         float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
	         f = MathHelper.sqrt(f);
	         if (f > 1.0F) {
	            f = 1.0F;
	         }

	         matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * 90F));
	      } else if (entityLiving.isAutoSpinAttack()) {
	         matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.xRot));
	         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(((float)entityLiving.tickCount + partialTicks) * -75.0F));
	      } else if (pose == Pose.SLEEPING) {
	         Direction direction = entityLiving.getBedOrientation();
	         float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
	         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
	         matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90F));
	         matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270.0F));
	      } else if (entityLiving.hasCustomName() || entityLiving instanceof PlayerEntity) {
	         String s = TextFormatting.stripFormatting(entityLiving.getName().getString());
	         if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity) || ((PlayerEntity)entityLiving).isModelPartShown(PlayerModelPart.CAPE))) {
	            matrixStackIn.translate(0.0D, (double)(entityLiving.getBbHeight() + 0.1F), 0.0D);
	            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
	         }
	      }

	   }
	
	
	public void applyRotationsPlayer(LivingEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
      float f = entityLiving.getSwimAmount(partialTicks);
      if (entityLiving.isFallFlying()) {
         superApplyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
         float f1 = (float)entityLiving.getFallFlyingTicks() + partialTicks;
         float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!entityLiving.isAutoSpinAttack()) {
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entityLiving.xRot)));
         }
         
         Vector3d vector3d = entityLiving.getLookAngle();
         Vector3d vector3d1 = entityLiving.getDeltaMovement();
         double d0 = Entity.getHorizontalDistanceSqr(vector3d1);
         double d1 = Entity.getHorizontalDistanceSqr(vector3d);
         if (d0 > 0.0D && d1 > 0.0D) {
            double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
            double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
            matrixStackIn.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
         }
      } else if (f > 0.0F) {
         superApplyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
         float f3 = entityLiving.isInWater() ? -90.0F - entityLiving.xRot : -90.0F;
         float f4 = MathHelper.lerp(f, 0.0F, f3);
         matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f4));
         //if (entityLiving.isActualySwimming()) {
         //   matrixStackIn.translate(0.0D, -1.0D, (double)0.3F);
         //}
      } else {
         superApplyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
      }

   }
	
	@Override
	public void setupRotations(SpiritEntity spirit, MatrixStack stack, float age, float yaw, float partial) {
		applyRotationsPlayer(spirit, stack, age, yaw, partial);
	}
	
    
    /*
    public RenderSpiritMob(final RenderManager manager, final ModelBase model, final float shadowSize) {
        super(manager, model, shadowSize);
    }
    
    @Override
    protected void preRenderCallback(final EntityLivingBase entity, final float f) {
        this.precall((EntitySpiritMob)entity, f);
    }
    
    protected void precall(final EntitySpiritMob ent, final float f) {
        GL11.glScalef(ModelSpirit.SCALE_MOD, ModelSpirit.SCALE_MOD, ModelSpirit.SCALE_MOD);
    }
    
    protected ResourceLocation getEntityTexture(EntitySpiritMob entity) {
        return RenderSpiritMob.mobTextures;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return RenderSpiritMob.mobTextures;
    }
    */
}
