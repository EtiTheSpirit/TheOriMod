package etithespirit.orimod.common.fluid;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.info.coordinate.SixSidedUtils;
import etithespirit.orimod.registry.BlockRegistry;
import etithespirit.orimod.registry.FluidRegistry;
import etithespirit.orimod.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public abstract class DecayPoisonFluid extends FlowingFluid {
	
	public Fluid getFlowing() {
		return FluidRegistry.DECAY_FLUID_FLOWING.get();
	}
	
	public Fluid getSource() {
		return FluidRegistry.DECAY_FLUID_STATIC.get();
	}
	
	public Item getBucket() {
		return ItemRegistry.POISON_BUCKET.get();
	}
	
	public void animateTick(Level pLevel, BlockPos pPos, FluidState pState, Random pRandom) {
		if (!pState.isSource() && !pState.getValue(FALLING)) {
			if (pRandom.nextInt(64) == 0) {
				pLevel.playLocalSound((double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.5D, (double)pPos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, pRandom.nextFloat() * 0.25F + 0.75F, pRandom.nextFloat() + 0.5F, false);
			}
		} else if (pRandom.nextInt(10) == 0) {
			pLevel.addParticle(ParticleTypes.UNDERWATER, (double)pPos.getX() + pRandom.nextDouble(), (double)pPos.getY() + pRandom.nextDouble(), (double)pPos.getZ() + pRandom.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
		
	}
	
	@Nullable
	public ParticleOptions getDripParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}
	
	protected boolean canConvertToSource() {
		return true;
	}
	
	protected void beforeDestroyingBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
		BlockEntity blockentity = pState.hasBlockEntity() ? pLevel.getBlockEntity(pPos) : null;
		Block.dropResources(pState, pLevel, pPos, blockentity);
	}
	
	public int getSlopeFindDistance(LevelReader pLevel) {
		return 4;
	}
	
	public BlockState createLegacyBlock(FluidState pState) {
		return BlockRegistry.DECAY_POISON.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(pState));
	}
	
	public boolean isSame(Fluid pFluid) {
		return pFluid == getSource() || pFluid == getFlowing();
	}
	
	public int getDropOff(LevelReader pLevel) {
		return 2;
	}
	
	public int getTickDelay(LevelReader p_76454_) {
		return 10;
	}
	
	public boolean canBeReplacedWith(FluidState pFluidState, BlockGetter pBlockReader, BlockPos pPos, Fluid pFluid, Direction pDirection) {
		return pDirection == Direction.DOWN && !pFluidState.is(OriModFluidTags.DECAY);
	}
	
	protected float getExplosionResistance() {
		return 100.0F;
	}
	
	public Optional<SoundEvent> getPickupSound() {
		return Optional.of(SoundEvents.BUCKET_FILL);
	}
	
	@Override
	protected void randomTick(Level level, BlockPos pos, FluidState state, Random rng) {
		if (level instanceof ServerLevel srv) {
			int flags = SixSidedUtils.getFlagsForNeighborsWhere(level, pos, ((neighborBlock, pos2, neighborPos) -> neighborBlock.getFluidState().is(Fluids.WATER)));
			SixSidedUtils.setAllBlocksForFlags(srv, BlockRegistry.DECAY_POISON.get().defaultBlockState(), pos, flags);
		}
	}
	
	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}
	
	/**
	 * Creates the fluid attributes object, which will contain all the extended values for the fluid that aren't part of the vanilla system.
	 * Do not call this from outside. To retrieve the values use {@link Fluid#getAttributes()}
	 */
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
			new ResourceLocation(OriMod.MODID, "block/decay_poison"),
			new ResourceLocation(OriMod.MODID, "block/decay_poison_flowing")
		)
		.color(0x9A8719B8)
		.density(1420)
		.rarity(Rarity.RARE)
		.viscosity(10000000) // assuming water is 1000, honey is what I am basing this on, which is 10k as dense
		.translationKey("orimod.fluid.decay")
		.temperature(305)
		.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
		.build(getSource());
	}
	
	public static class Source extends DecayPoisonFluid {
		
		@Override
		public boolean isSource(FluidState pState) {
			return true;
		}
		
		public int getAmount(FluidState pState) {
			return 8;
		}
		
	}
	
	public static class Flowing extends DecayPoisonFluid {
		
		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> pBuilder) {
			super.createFluidStateDefinition(pBuilder);
			pBuilder.add(LEVEL);
		}
		
		@Override
		public boolean isSource(FluidState pState) {
			return false;
		}
		
		public int getAmount(FluidState pState) {
			return pState.getValue(LEVEL);
		}
	}

}
