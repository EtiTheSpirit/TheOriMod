package etithespirit.orimod.common.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.FluidRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DecayPoisonFluid {
	
	public static final ForgeFlowingFluid.Properties createProperties() {
		return new ForgeFlowingFluid.Properties(FluidRegistry.DECAY_POISON_TYPE, FluidRegistry.DECAY_FLUID_STATIC, FluidRegistry.DECAY_FLUID_FLOWING).block(FluidRegistry.DECAY_POISON);
	}
	
	public static final Supplier<FluidType> DECAY_POISON_FLUID_TYPE = () -> new FluidType(FluidType.Properties.create().supportsBoating(true).canHydrate(false).canDrown(true)) {
		@Override
		public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
			
			consumer.accept(new IClientFluidTypeExtensions() {
				private static final ResourceLocation STILL = new ResourceLocation(OriMod.MODID, "block/decay_poison");
				private static final ResourceLocation FLOWING = new ResourceLocation(OriMod.MODID, "block/decay_poison_flowing");
				
				@Override
				public int getTintColor() {
					return 0x9A8719B8;
				}
				
				@Override
				public ResourceLocation getStillTexture() {
					return STILL;
				}
				
				@Override
				public ResourceLocation getFlowingTexture() {
					return FLOWING;
				}
				
				@Override
				public @Nullable ResourceLocation getOverlayTexture() {
					return IClientFluidTypeExtensions.super.getOverlayTexture();
				}
				
				@Override
				public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc) {
					return IClientFluidTypeExtensions.super.getRenderOverlayTexture(mc);
				}
				
				@Override
				public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
					int color = this.getTintColor();
					return new Vector3f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F);
				}
				
				@Override
				public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
					nearDistance = -8F;
					farDistance = 6F;
					
					if (farDistance > renderDistance) {
						farDistance = renderDistance;
						shape = FogShape.CYLINDER;
					}
					
					RenderSystem.setShaderFogStart(nearDistance);
					RenderSystem.setShaderFogEnd(farDistance);
					RenderSystem.setShaderFogShape(shape);
				}
			});
		}
	};
}
