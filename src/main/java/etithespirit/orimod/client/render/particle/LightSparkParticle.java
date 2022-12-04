package etithespirit.orimod.client.render.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import etithespirit.orimod.GeneralUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public abstract class LightSparkParticle extends Particle {
	
	private static final ParticleRenderType TRANSLUCENT_SOLID_COLOR = new ParticleRenderType() {
		
		private static final ResourceLocation FORGE_WHITE = new ResourceLocation("forge:textures/white.png");
		
		@Override
		public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
			RenderSystem.depthMask(true);
			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}
		
		@Override
		public void end(Tesselator pTesselator) {
			RenderSystem.enableTexture();
			pTesselator.end();
		}
	};
	
	protected LightSparkParticle(ClientLevel worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}
	
	protected LightSparkParticle(ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
	}
	
	protected LightSparkParticle(ClientLevel worldIn, Vec3 pos, Vec3 speed) {
		super(worldIn, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
	}
	
	@Override
	public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
		Vec3 vec3 = camera.getPosition();
		float xMovement = (float)(Mth.lerp((double)partialTicks, this.xo, this.x) - vec3.x());
		float yMovement = (float)(Mth.lerp((double)partialTicks, this.yo, this.y) - vec3.y());
		float zMovement = (float)(Mth.lerp((double)partialTicks, this.zo, this.z) - vec3.z());
		Quaternion quaternion;
		if (this.roll == 0.0F) {
			quaternion = camera.rotation();
		} else {
			quaternion = new Quaternion(camera.rotation());
			float f3 = Mth.lerp(partialTicks, this.oRoll, this.roll);
			quaternion.mul(Vector3f.ZP.rotation(f3));
		}
		
		Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
		vector3f1.transform(quaternion);
		Vector3f[] positions = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float f4 = this.getQuadSize(partialTicks);
		
		for(int i = 0; i < 4; ++i) {
			Vector3f vector3f = positions[i];
			vector3f.transform(quaternion);
			vector3f.mul(f4);
			vector3f.add(xMovement, yMovement, zMovement);
		}
		
		buffer.vertex((double)positions[0].x(), (double)positions[0].y(), (double)positions[0].z()).uv(1, 1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(GeneralUtils.FULL_BRIGHT_LIGHT).endVertex();
		buffer.vertex((double)positions[1].x(), (double)positions[1].y(), (double)positions[1].z()).uv(1, 0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(GeneralUtils.FULL_BRIGHT_LIGHT).endVertex();
		buffer.vertex((double)positions[2].x(), (double)positions[2].y(), (double)positions[2].z()).uv(0, 0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(GeneralUtils.FULL_BRIGHT_LIGHT).endVertex();
		buffer.vertex((double)positions[3].x(), (double)positions[3].y(), (double)positions[3].z()).uv(0, 1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(GeneralUtils.FULL_BRIGHT_LIGHT).endVertex();
	}
	
	public abstract float getQuadSize(float partialTicks);
	
	@Override
	public ParticleRenderType getRenderType() {
		return TRANSLUCENT_SOLID_COLOR;
	}
	
	public static class SpiritArcArrowImpactParticle extends LightSparkParticle {
		
		private static final Random RNG = new Random();
		
		public SpiritArcArrowImpactParticle(ClientLevel worldIn, Vec3 at, Vec3 normal) {
			super(worldIn, at, addRandomTo(normal, 0.5, 2));
			this.lifetime = RNG.nextInt(7, 10);
			this.rCol = 0.75f;
			this.gCol = 0.875f;
			this.bCol = 1f;
			this.alpha = 0.8f;
		}
		
		private static Vec3 addRandomTo(Vec3 value, double scalarMultFactor, double preMultValue) {
			return (value.multiply(preMultValue, preMultValue, preMultValue).add(RNG.nextDouble(), RNG.nextDouble(), RNG.nextDouble()).multiply(preMultValue, preMultValue, preMultValue));
		}
		
		@Override
		public float getQuadSize(float partialTicks) {
			return 0.125f;
		}
		
	}
}
