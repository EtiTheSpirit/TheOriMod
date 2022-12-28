package etithespirit.orimod.common.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.world.FluidRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class DecayPoisonFluid extends ForgeFlowingFluid {
	
	protected DecayPoisonFluid(Properties properties) {
		super(properties);
	}
	
	public static final ForgeFlowingFluid.Properties createProperties() {
		return new ForgeFlowingFluid.Properties(FluidRegistry.DECAY_POISON_TYPE, FluidRegistry.DECAY_FLUID_STATIC, FluidRegistry.DECAY_FLUID_FLOWING)
			.block(FluidRegistry.DECAY_POISON)
			.bucket(ItemRegistry.POISON_BUCKET)
			.levelDecreasePerBlock(2)
			.tickRate(1);
	}
	
	public static final Supplier<FluidType> DECAY_POISON_FLUID_TYPE = () -> new FluidType(
			FluidType.Properties.create()
				.supportsBoating(true)
				.canHydrate(false)
				.canDrown(true)
				.canConvertToSource(true)
				.canExtinguish(true)
				.canSwim(true)
			) {
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
	
	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluidIn, Direction direction) {
		// Based on the water implementation, may need to be overriden for mod fluids that shouldn't behave like water.
		// return direction == Direction.DOWN && !isSame(fluidIn);
		return false; //!isSame(fluidIn);
	}
	
	public static class Flowing extends DecayPoisonFluid {
		public Flowing() {
			super(createProperties());
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}
		
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}
		
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}
		
		public boolean isSource(FluidState state) {
			return false;
		}
	}
	
	public static class Source extends DecayPoisonFluid {
		public Source() {
			super(createProperties());
		}
		
		public int getAmount(FluidState state) {
			return 8;
		}
		
		public boolean isSource(FluidState state) {
			return true;
		}
	}
}
